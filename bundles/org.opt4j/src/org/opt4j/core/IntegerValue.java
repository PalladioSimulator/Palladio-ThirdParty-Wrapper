/**
 * Opt4J is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * Opt4J is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with Opt4J. If not, see http://www.gnu.org/licenses/. 
 */

package org.opt4j.core;

/**
 * The {@link IntegerValue} is a {@link Value} with an {@link Integer} as value.
 * The {@link Objectives} contains a convenience method that allows to add an
 * {@link IntegerValue} without creating a new instance:
 * {@link Objectives#add(Objective, int)}.
 * 
 * @see Objectives#add(Objective, int)
 * @author lukasiewycz
 * 
 */
public class IntegerValue implements Value<Integer> {

	protected Integer value = null;

	/**
	 * Constructs a {@link IntegerValue}.
	 * 
	 * @param value
	 *            the specified integer
	 */
	public IntegerValue(Integer value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opt4j.core.Value#getDouble()
	 */
	@Override
	public Double getDouble() {
		if (value == null) {
			return null;
		}
		double v = value;
		return v;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opt4j.core.Value#getValue()
	 */
	@Override
	public Integer getValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opt4j.core.Value#setValue(java.lang.Object)
	 */
	@Override
	public void setValue(Integer value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Value<Integer> arg) {
		if (arg == null) {
			return -1;
		}
		Double other = arg.getDouble();
		if (value == null) {
			if (other == null) {
				return 0;
			}
			return 1;
		} else if (other == null) {
			return -1;
		}
		return getDouble().compareTo(other);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof IntegerValue)) {
			return false;
		}
		IntegerValue other = (IntegerValue) obj;
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return value.toString();
	}

}
