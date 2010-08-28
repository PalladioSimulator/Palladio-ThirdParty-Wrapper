/**
 * 
 */
package org.opt4j.core.domination;

import org.opt4j.core.Objectives;


/**
 * ...
 * @author noorshams
 *
 */
public interface ConstraintChecker {
	/**
	 * ...
	 * @param o the objectives to check
	 * @return the feasibility
	 */
	boolean isFeasible(final Objectives o);
	/**
	 * ...
	 * @param o the objectives to check
	 * @return the amount of constraint violation
	 */
	double getConstraintViolation(final Objectives o);
}
