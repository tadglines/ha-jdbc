/*
 * HA-JDBC: High-Availability JDBC
 * Copyright 2004-2009 Paul Ferraro
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.hajdbc.durability.coarse;

import net.sf.hajdbc.Database;
import net.sf.hajdbc.ExceptionFactory;
import net.sf.hajdbc.durability.DurabilityListener;
import net.sf.hajdbc.durability.InvocationEvent;
import net.sf.hajdbc.durability.TransactionIdentifier;
import net.sf.hajdbc.durability.none.NoDurability;
import net.sf.hajdbc.sql.InvocationStrategy;
import net.sf.hajdbc.sql.Invoker;
import net.sf.hajdbc.sql.SQLProxy;

/**
 * @author paul
 *
 */
public class CoarseDurability<Z, D extends Database<Z>> extends NoDurability<Z, D>
{
	final DurabilityListener listener;
	
	public CoarseDurability(DurabilityListener listener)
	{
		this.listener = listener;
	}
	
	/**
	 * {@inheritDoc}
	 * @see net.sf.hajdbc.durability.Durability#getInvocationStrategy(net.sf.hajdbc.sql.InvocationStrategy)
	 */
	@Override
	public <T, R, E extends Exception> InvocationStrategy<Z, D, T, R, E> getInvocationStrategy(final InvocationStrategy<Z, D, T, R, E> strategy, final Phase phase, final TransactionIdentifier transactionId, final ExceptionFactory<E> exceptionFactory)
	{
		return new InvocationStrategy<Z, D, T, R, E>()
		{
			@Override
			public R invoke(SQLProxy<Z, D, T, E> proxy, Invoker<Z, D, T, R, E> invoker) throws E
			{
				InvocationEvent event = new InvocationEvent(transactionId, phase);
				
				CoarseDurability.this.listener.beforeInvocation(event);
				
				try
				{
					R result = strategy.invoke(proxy, invoker);
					
					return result;
				}
				catch (Exception e)
				{
					E exception = exceptionFactory.createException(e);
					
					throw exception;
				}
				finally
				{
					CoarseDurability.this.listener.afterInvocation(event);
				}
			}
		};
	}
}