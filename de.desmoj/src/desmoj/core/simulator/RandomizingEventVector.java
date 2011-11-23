package desmoj.core.simulator;

import java.util.Random;

/**
 * A specialized Event vector providing random order for concurrent Event notes.
 * Random order is achieved by computing a random insert position within the
 * range of simultaneous (concurrent) events. Existing connections between
 * events are maintained, i.e. a new event-note will never be inserted between
 * two connected event-notes. Connections are only possible between to
 * successive concurrent Event notes where one of the notes was inserted by call
 * of the insertBefore() or the insertAfter() method. Most of the methods
 * inherited from the super class
 * {@link desmoj.core.simulator.EventVectorList EventVector}are only overwritten to
 * keep track of the existing connections.
 * 
 * @version DESMO-J, Ver. 2.3.3 copyright (c) 2011
 * @author Ruth Meyer
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
public class RandomizingEventVector extends EventVectorList {

	// -------------------------------------------------------------------
	// Fields

	/** the random position generator. */
	private Random _positionGenerator;

	// -------------------------------------------------------------
	// Constructors

	/**
	 * Constructs a new randomizing Event vector. Initializes the event vector
	 * and the random position generator.
	 */
	public RandomizingEventVector() {
		super();
		_positionGenerator = new Random();
	}

	// ------------------------------------------------------------------
	// Methods

	/**
	 * Inserts the given new event-note directly before the specified Event
	 * note. Registers <code>where</code> as connected to <code>newNote</code>.
	 * 
	 * @param where :
	 *            the event-note before which the new note shall be inserted
	 * @param newNote :
	 *            the new event-note to be inserted
	 */
	void insertBefore(EventNote where, EventNote newNote) {
		super.insertBefore(where, newNote);
		// insertBefore means a "backward" connection: newNote is connected to
		// its successor where
		// this is translated to the "forward" connection: where is connected to
		// its predecessor newNote
		int i = this.eVector.indexOf(where);
		if (i >= 0)
			where.setConnected(true);
	}

	/**
	 * Inserts the given new event-note directly behind the specified Event
	 * note. Registers <code>newNote</code> as connected to <code>where</code>.
	 * 
	 * @param where :
	 *            the event-note after which the new note shall be inserted
	 * @param newNote :
	 *            the new event-note to be inserted
	 */
	void insertAfter(EventNote where, EventNote newNote) {
		super.insertAfter(where, newNote);
		// insertAfter means a "forward" connection: newNote is connected to its
		// predecessor where
		int i = this.eVector.indexOf(newNote);
		if (i >= 0)
            newNote.setConnected(true);
	}

	/**
	 * Inserts the given event-note at the front of the event vector.
	 * 
	 * @param newNote
	 *            EventNote : the new event-note to be inserted as first note.
	 */
	void insertAsFirst(EventNote newNote) {
		super.insertAsFirst(newNote);
        newNote.setConnected(false);
	}

	/**
	 * Inserts the given event-note at the back of the event vector.
	 * 
	 * @param newNote
	 *            EventNote : the new event-note to be inserted as last note.
	 */
	void insertAsLast(EventNote newNote) {
		super.insertAsLast(newNote);
        newNote.setConnected(false);
	}

	/**
	 * Removes the given note from the event vector.
     * A connection between the note's previous and next note
     * is established if and only if the given note was 
     * connnect to both the previous and next node.
	 * 
	 * @param note
	 *            EventNote : the event-note to be removed
	 */
	void remove(EventNote note) {
		int i = this.eVector.indexOf(note);
		if (i >= 0) {
            EventNote prev = this.prevNote(note);
            EventNote next = this.nextNote(note);
            if (prev != null && next != null) {
                if (note.isConnected() && next.isConnected()) 
                    next.setConnected(true); 
                else 
                    next.setConnected(false);
            }
			super.remove(note);
		}
	}

	/**
	 * Removes the first event-note (if any).
	 */
	void removeFirst() {
		if (!this.isEmpty()) {
			super.removeFirst();
            if (this.isEmpty())
                this.firstNote().setConnected(false);
		}
	}

	/**
	 * Inserts the given event-note into the event vector. Overwrites the
	 * inherited insert() method to achieve random insert for concurrent Events.
	 * Takes possible connections between existing event-notes into account,
	 * i.e. will not insert the new note between connected events. Connections
	 * may only exist between two events of the same time where one of the
	 * events has been inserted via insertBefore() or insertAfter().
	 * 
	 * @param newNote
	 *            EventNote : the event-note to be inserted
	 */
	//TODO:
	void insert(EventNote newNote) {
		if (isEmpty()) {
			super.insert(newNote);
            newNote.setConnected(false);
			// notes inserted via insert() are not connected to other notes
			return; // no need to continue
		}
		// use binary search to determine first event-note with same time
		TimeInstant refTime = newNote.getTime();
		int firstIndexForInsert, lastIndexForInsert;
		int left = 0;
		int right = eVector.size();
		while (left < right) {
			int middle = (left + right) / 2;
			if (TimeInstant.isBefore(((EventNote) eVector.get(middle)).getTime(),
					refTime)) {
				left = middle + 1;
			} else {
				right = middle;
			}
		}
		if (right < eVector.size()
				&& TimeInstant.isEqual(((EventNote) eVector.get(right)).getTime(),
						refTime)) {
			// same time found
			firstIndexForInsert = right;
			// look for last event-note with same time; last position to insert
            // is AFTER last concurrent note 
			lastIndexForInsert = findLastIndex(firstIndexForInsert) + 1;
		} else {
			// same time not found, but right still holds the insert position
			firstIndexForInsert = right;
			lastIndexForInsert = firstIndexForInsert;
		}
		// do we need to generate a random insert position?
		if (firstIndexForInsert != lastIndexForInsert) {
			// yeah, so here we go
			firstIndexForInsert += _positionGenerator.nextInt(lastIndexForInsert - firstIndexForInsert + 1);
			// defer in case connection violated
			while (firstIndexForInsert < this.eVector.size() && ((EventNote) eVector.get(firstIndexForInsert)).isConnected()) firstIndexForInsert++;
		}
		// at last do the actual inserting
		this.eVector.add(firstIndexForInsert, newNote);
        newNote.setConnected(false);
	}

	/**
	 * This helper method determines the position of the last event-note with
	 * the same time as the event-note at position firstIndex doing a simple
	 * linear search from firstIndex.
	 */
	//TODO:
	protected int findLastIndex(int firstIndex) {
		TimeInstant refTime = ((EventNote) eVector.get(firstIndex)).getTime();
		int lastIndex = firstIndex + 1;
		while (lastIndex < eVector.size()
				&& TimeInstant.isEqual(refTime,
						((EventNote) eVector.get(lastIndex)).getTime())) {
			lastIndex++;
		}
		return lastIndex - 1;
	}

} /* end of class RandomizingEventVector */