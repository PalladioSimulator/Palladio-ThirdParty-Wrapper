/*******************************************************************************
 * Copyright (c) 2008 ikv++ technologies ag and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     ikv++ technologies ag - initial API and implementation
 *******************************************************************************/

package de.ikv.medini.qvt.execution.debug.events;

import de.ikv.medini.qvt.execution.debug.requests.QVTDebugRequestResume;

/**
 * Event to signal that execution is resumed
 * 
 * @author kiegeland
 * 
 */
public class QVTDebugEventResumed extends QVTDebugEvent {

	public String toString() {
		return "resumed " + (this.isClientRequest ? "client" : "step");
	}

	private boolean isClientRequest;

	/**
	 * 
	 * @param isClientRequest
	 *            true : resumed by client request {@link QVTDebugRequestResume}; false: resumed by step request
	 */
	public QVTDebugEventResumed(boolean isClientRequest) {
		this.isClientRequest = isClientRequest;
	}

	public boolean isClient() {
		return this.isClientRequest;
	}

	public boolean isStep() {
		return !this.isClientRequest;
	}

	public boolean isDrop() {
		return this.isClientRequest;
	}

	/**
	 * Hash code based on {@link #isClientRequest}
	 * 
	 * Generated by Eclipse
	 */
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + (this.isClientRequest ? 1231 : 1237);
		return result;
	}

	/**
	 * Equality based on {@link #isClientRequest}
	 * 
	 * Generated by Eclipse
	 */
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final QVTDebugEventResumed other = (QVTDebugEventResumed) obj;
		if (this.isClientRequest != other.isClientRequest) {
			return false;
		}
		return true;
	}

}
