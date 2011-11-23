package desmoj.core.util;


/**
 * A listener to the sim clock of an experiment running in an experiment runner.
 * 
 * @version DESMO-J, Ver. 2.3.3 copyright (c) 2011
 * @author Nicolas Knaak
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You
 * may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */

public interface SimClockListener extends SimRunListener {

	/**
	 * Called when the simulation clock this listener listens to is advanced.
	 * 
	 * @param e
	 *            a SimRunEvent containing information about the experiment
	 *            model and simulation time of the clock advance
	 */
	public void clockAdvanced(SimRunEvent e);
}