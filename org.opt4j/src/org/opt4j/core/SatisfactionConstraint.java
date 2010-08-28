/**
 * 
 */
package org.opt4j.core;


/**
 * @author noorshams
 *
 */
public class SatisfactionConstraint extends Constraint {
	
	protected final Objective objective;
	
	/**
	 * @return the objective
	 */
	public Objective getObjective() {
		return objective;
	}

	public SatisfactionConstraint(String name, Direction direction, double limit, Objective objective) {
		super(name, direction, limit);
		this.objective = objective;
	}
}
