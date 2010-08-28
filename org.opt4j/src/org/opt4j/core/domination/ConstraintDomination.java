/**
 * 
 */
package org.opt4j.core.domination;


import java.util.concurrent.ConcurrentHashMap;

import org.opt4j.core.Objectives;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;




/**
 * This class implements {@code DominationStrategy}. The dominance relation is
 * constraint-domination as described in K. Deb, A. Pratap, S. Agarwal, and T. 
 * Meyarivan, "A fast and elitist multiobjective genetic algorithm : Nsga-ii,"
 * Evolutionary Computation, IEEETransactions on, vol. 6, no. 2, pp. 182-197, 
 * August 2002.
 * <br><br>
 * It needs a {@code ConstraintChecker} that checks <em>infeasibility</em> and/or
 * <em>satisfaction</em> constraints.
 * 
 * @author noorshams
 *
 */
@Singleton
public class ConstraintDomination implements DominationStrategy {
	
	protected ConstraintChecker constraintChecker;
	protected DominationStrategy feasibleStrategy;
	
	protected ConcurrentHashMap<Objectives, ConstrainDominationInformation> cache = new ConcurrentHashMap<Objectives, ConstrainDominationInformation>();
	
	@Inject
	public ConstraintDomination(ConstraintChecker checker, @Named("StrategyForFeasibleObjectives") DominationStrategy feasibleStrategy){
		this.constraintChecker = checker;
		this.feasibleStrategy = feasibleStrategy;
	}
	
	/**
	 * {@inheritDoc} The dominance relation is <em>Constraint-Domination</em>.
	 */
	/* (non-Javadoc)
	 * @see dominationStrategy.DominationStrategy#dominates(org.opt4j.core.Objectives, org.opt4j.core.Objectives)
	 */
	@Override
	public boolean dominates(Objectives o1, Objectives o2) {
		boolean feas1;
		boolean feas2;
		Double viol1 = null;
		Double viol2 = null;
		
		if(cache.containsKey(o1)){
			feas1 = cache.get(o1).isFeasible();
			viol1 = cache.get(o1).getConstraintViolation();
		} else {
			feas1 = constraintChecker.isFeasible(o1);
			if(!feas1){
				viol1 = constraintChecker.getConstraintViolation(o1); 
			}
			cache.put(o1, new ConstrainDominationInformation(feas1, viol1));
		}
		if(cache.containsKey(o2)){
			feas2 = cache.get(o2).isFeasible();
			viol2 = cache.get(o2).getConstraintViolation();
		} else {
			feas2 = constraintChecker.isFeasible(o2);
			if(!feas2){
				viol2 = constraintChecker.getConstraintViolation(o2);
			}
			cache.put(o2, new ConstrainDominationInformation(feas2, viol2));
		}
		

		if(feas1 && feas2) { //both feasible, calculate normal dominance	
			
			return this.feasibleStrategy.dominates(o1, o2);
		} else if (!feas1 && !feas2) { // both infeasible
			
			//Does o2 have a greater constraint violation?
			return (viol2 > viol1);			
		} else { // 1 infeasible, 1 feasible			
			// feas1 == true XOR feas2 == true			
			return feas1;
		}
	}

	/**
	 * {@inheritDoc} The dominance relation is <em>Constraint-Domination</em>.
	 */
	/* (non-Javadoc)
	 * @see dominationStrategy.DominationStrategy#weaklyDominates(org.opt4j.core.Objectives, org.opt4j.core.Objectives)
	 */
	@Override
	public boolean weaklyDominates(Objectives o1, Objectives o2) {
		// are equal or o1 dominates o2
		
		double[] a1 = o1.array();
		double[] a2 = o2.array();
		boolean equals = true;
		
		for (int i = 0; i < a1.length; i++) {
			if(a1[i] != a2[i]){
				equals = false;
				break;
			}
		}
		
		return (equals || this.dominates(o1, o2));
	}

}

/**
 * Data structure to save information about objectives in the cache of the strategy class {@code ConstraintDomination}.
 * 
 * @author noorshams
 *
 */
class ConstrainDominationInformation{
	protected final boolean isFeasible;
	protected final Double constraintViolation;
	
	public ConstrainDominationInformation(boolean isFeasible, Double constraintViolation){
		this.isFeasible = isFeasible;
		this.constraintViolation = constraintViolation;
	}
	
	/**
	 * @return the isFeasible
	 */
	public boolean isFeasible() {
		return isFeasible;
	}
	/**
	 * @return the constraintViolation
	 */
	public Double getConstraintViolation() {
		return constraintViolation;
	}
	
	@Override
	public String toString() {
		return "Info: isFeasible: "+isFeasible+", CV: "+constraintViolation;
	}
}
