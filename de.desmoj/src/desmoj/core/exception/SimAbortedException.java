package desmoj.core.exception;

/**
 * Exception is thrown to indicate that the simulation has been forced to stop
 * becaus of an error that occured. The error forcing a disruption of the
 * simulation is described in the error message attached to this exception.
 * Users must not make use of it nor is this exception supposed to be caught by
 * users. Its use inside the DESMOJ framework is transparent because it is
 * derived from <code>RuntimeException</code> which isthe root for a tree of
 * exceptions that are automatically rethrown by any Java method thus not
 * needing to be rethrown by the user, too.
 * 
 * @version DESMO-J, Ver. 2.3.3 copyright (c) 2011
 * @author Tim Lechler
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
public class SimAbortedException extends DESMOJException {
	/**
	 * Creates a new SimAbortedException declaring the reason for the aborted
	 * simulation run in the contents of the error message given as a parameter.
	 * 
	 * @param message
	 *            desmoj.report.ErrorMessage : The reason for the aborted
	 *            simulation
	 */
	public SimAbortedException(desmoj.core.report.ErrorMessage message) {

		super(message);

	}
}