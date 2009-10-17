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
package net.sf.hajdbc.balancer;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import net.sf.hajdbc.Database;

/**
 * Thread-safe abstract balancer implementation that implements most of the Balancer interface, except {@link Balancer#next()}.
 * Uses A copy-on-write algorithm for {@link #add(Database)}, {@link #remove(Database)}, and {@link #clear()}.
 * Calls to {@link #all()} are non-blocking.
 * 
 * @author  Paul Ferraro
 * @param <D> either java.sql.Driver or javax.sql.DataSource
 */
public abstract class AbstractBalancer<Z, D extends Database<Z>> implements Balancer<Z, D>
{
	protected abstract Set<D> getDatabaseSet();
	
	/**
	 * @see net.sf.hajdbc.balancer.Balancer#all()
	 */
	@Override
	public Iterator<D> iterator()
	{
		return Collections.unmodifiableSet(this.getDatabaseSet()).iterator();
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object database)
	{
		return this.getDatabaseSet().contains(database);
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> databases)
	{
		return this.getDatabaseSet().containsAll(databases);
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.Set#isEmpty()
	 */
	@Override
	public boolean isEmpty()
	{
		return this.getDatabaseSet().isEmpty();
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.Set#size()
	 */
	@Override
	public int size()
	{
		return this.getDatabaseSet().size();
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.Set#toArray()
	 */
	@Override
	public Object[] toArray()
	{
		return this.getDatabaseSet().toArray();
	}

	/**
	 * {@inheritDoc}
	 * @see java.util.Set#toArray(T[])
	 */
	@Override
	public <T> T[] toArray(T[] array)
	{
		return this.getDatabaseSet().toArray(array);
	}
}