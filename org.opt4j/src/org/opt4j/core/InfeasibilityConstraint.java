/**
 * 
 */
package org.opt4j.core;

/**
 * @author noorshams
 *
 */
public class InfeasibilityConstraint extends Constraint {
	public InfeasibilityConstraint(String name, Direction direction, double limit) {
		super(name, direction, limit);
	}
}
