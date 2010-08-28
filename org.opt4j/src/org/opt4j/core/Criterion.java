/**
 * 
 */
package org.opt4j.core;

/**
 * @author noorshams
 *
 */
public abstract class Criterion {
	protected final String name;
	
	protected Criterion(String name){
		this.name = name;
	}
	
	/**Returns the name
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	
}
