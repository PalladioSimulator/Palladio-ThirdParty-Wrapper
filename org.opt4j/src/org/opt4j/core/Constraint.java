/**
 * 
 */
package org.opt4j.core;

/**
 * The definition of a constraint. Besides a unique key, this class has a direction and a limit.
 * 
 * @author noorshams
 *
 */
public abstract class Constraint extends Criterion implements Comparable<Constraint> {
	
	/**
	 * Defines the possible directions a constraint can be defined.
	 */
	public enum Direction {
		less, greater, equal, lessOrEqual, greaterOrEqual;
	}

	protected final Direction direction;
	protected final double limit;

	public Constraint(String name, Direction direction, double limit){
		super(name);
		this.direction = direction;
		this.limit = limit;
	}
	
	/**
	 * @return the direction
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * @return the limit
	 */
	public double getLimit() {
		return limit;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName() + "(" + getDirection() + " " + getLimit() + ")["+ this.getClass() +"]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Constraint other) {
		// Compares mainly keys
		if (this.equals(other)) {
			return 0;
		} else if (other == null) {
			return 1;
		} else {
			return this.getName().compareTo(other.getName());
		}
	}

}
