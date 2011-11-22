package desmoj.core.simulator;

import java.util.Vector;

import desmoj.core.exception.SimAbortedException;
import desmoj.core.report.ErrorMessage;

/**
 * Implementation of the interface <code>EventList</code> using a
 * <code>java.util.Vector</code> as a container for the event-notes. Using Vector
 * as a container has the advantage of using tested and thread-safe code. Being
 * based on an array it allows inserting new entries faster using binary search
 * for the right position, which would not be possible on containers based on
 * linked lists. The obvious disadvantage is the slow speed of execution of the
 * thread-safe code of the Vector.
 * 
 * @see java.util.Vector
 * @see EventNote
 * 
 * @version DESMO-J, Ver. 2.3.3 copyright (c) 2011
 * @author Tim Lechler, modified by Ruth Meyer
 * 
 *         Licensed under the Apache License, Version 2.0 (the "License"); you
 *         may not use this file except in compliance with the License. You may
 *         obtain a copy of the License at
 *         http://www.apache.org/licenses/LICENSE-2.0
 * 
 *         Unless required by applicable law or agreed to in writing, software
 *         distributed under the License is distributed on an "AS IS" BASIS,
 *         WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *         implied. See the License for the specific language governing
 *         permissions and limitations under the License.
 * 
 */
public class EventVectorList extends EventList {

	/**
	 * The vector container used to store the event-notes.
	 */
	protected Vector<EventNote> eVector; // The Vector containing all Event notes.

	/**
	 * Constructs an empty event-list with a default size of 100 for the
	 * underlying vector. The standard initial size of a vector is 10, requiring
	 * the size to be doubled when element nnumber 11 is added. This duplication
	 * of the allovated memory takes some time. To reduce the amount of
	 * duplications necessary, the vector is created with an initial size of 100
	 * to improve performance by avoiding 4 cycles of doubling Vector's
	 * capacity. Choosing a larger initial number would not significantly fasten
	 * processing speeds but waste otherwise unused memory, depending on the
	 * behaviour of the model simulated.
	 */
	EventVectorList() {

		eVector = new Vector<EventNote>(100);

	}

	/**
	 * Creates a new event-note with the initial values given as parameters. This
	 * resembles the factory method design pattern described in [Gamm95] p. 107.
	 * This design pattern is used to ensure that always the appropriate
	 * implementation of Event notes is used together with an individual
	 * implementation of an event-list. EventVector does not need any special
	 * implementation of Event notes and thus simply passes the construction
	 * through to the default implementation of EventNote.
	 * 
	 * @param who
	 *            Entity : the entity or process associated with the event-note
	 * @param what
	 *            Event : the event or external event associated with the
	 *            EventNote
	 * @param when
	 *            TimeInstant : the point of simulation time associated with the
	 *            EventNote
	 * @see EventNote
	 */
	EventNote createEventNote(Entity who, Event<Entity> what, TimeInstant when) {

		return new EventNote(who, what, when);

	}

	/**
	 * Returns the first event-note in the event-list. It is the event-note with
	 * the lowest (nearest) associated point of simulation time of all
	 * Event notes contained in the evnet-list. Note that the event-note is not
	 * removed from the event-list.
	 * 
	 * @return EventNote : the event-note to be processed next in the order of
	 *         time. Returns <code>null</code> if the event-list is empty.
	 */
	EventNote firstNote() {

		if (isEmpty())
			return null; // nothing there, nothing returned
		else
			return eVector.firstElement(); // return but not remove

	}

	/**
	 * Inserts the new event-note preserving the temporal order of the event-notes
	 * contained in the event-list. It uses binary search to determine the
	 * position where to insert the new event-note to increase performance.
	 * 
	 * @param newNote
	 *            EventNote : the new note to be inserted in the event-list
	 *            keeping the temporal order
	 */
	void insert(EventNote newNote) {
	    
        // code for adding EventNote to all possible entities
        
        Entity who1 = newNote.getEntity1();
        if (who1 != null)
        {
            who1.addEventNote(newNote);
        }
        
        Entity who2 = newNote.getEntity2();
        if (who2 != null)
        {
            who2.addEventNote(newNote);
        }
        
        Entity who3 = newNote.getEntity3();
        if (who3 != null)
        {
            who3.addEventNote(newNote);
        }	
        
        EventAbstract Event = newNote.getEvent();
        if (Event != null) {
            Event.addEventNote(newNote);
        }

		if (isEmpty()) { // no worry about order if it's first
			eVector.addElement(newNote); // easy if empty ;-)
			return; // no need to continue
		} else { // now here comes the binary sorting

			int left = 0; // left border of search partition
			int right = eVector.size() - 1; // right border of search partition
			int index = 0; // current position in vector
			TimeInstant refTime = newNote.getTime();
			// shortcut for call to newNote

			do {
				index = (left + right) / 2; // center on searchable partition
				// check if EventNote at index has smaller or equal time
				if (TimeInstant.isBeforeOrEqual(eVector
						.elementAt(index).getTime(), refTime)) {
					if (index < (eVector.size() - 1)) {
						// is there a note to the right
						if (TimeInstant.isAfter(eVector
								.elementAt(index + 1).getTime(), refTime)) {
							// if note to the right is larger
							eVector.insertElementAt(newNote, index + 1);
							// found position
							return; // everything done, no need to continue
						} else {
							left = index + 1;
							// no hit, so set new boundaries and go on
						}
					} // no Event notes right of the index, so all
					else { // notes are smaller, thus append to end
						eVector.addElement(newNote);
						return; // everything done, get out of here fast!
					}
				} else { // EventNote at index has larger time
					if (index > 0) { // is there a note left of the index?
						if (TimeInstant.isBeforeOrEqual(eVector
								.elementAt(index - 1).getTime(), refTime)) {
							// if note to the left is smallerOrEqual
							eVector.insertElementAt(newNote, index);
							// found position
							return; // everything done, no need to continue
						} else {
							right = index - 1;
							// no hit, so set new boundaries and go on
						}
					} // no Event notes left of the index, so all
					else { // notes are larger, thus insert at pos. 0
						eVector.insertElementAt(newNote, 0);
						return; // everything done, get out of here!
					}
				}
			} while ((left <= right));

			eVector.addElement(newNote);
		}

	}

	/**
	 * Inserts a new event-note after another EventNote specified. Note that to
	 * keep the temporal order of the event-list, the scheduled time will be set
	 * to the same time as the referred "afterNote". Note also, that afterNote
	 * must be contained in the event-list. If the referred "where" is not
	 * contained in the event-list, there is no chance to determine the time
	 * that the new note is intended to be scheduled at. Thus the new event-note
	 * will not be inserted and a <code>EventNotScheduledException</code> will
	 * be thrown, stopping the simulation.
	 * 
	 * @param where
	 *            EventNote : The event-note containing the event after which the
	 *            new note is supposed to be inserted into the event-list.
	 * @param newNote
	 *            EventNote : The new event-note to be inserted after the
	 *            specified EventNote in the event-list.
	 * @throws SimAbortedException
	 *                : if referred EventNote is not contained in the event-list
	 */
	void insertAfter(EventNote where, EventNote newNote) {
	    
        // code for adding EventNote to all possible entities
        Entity who1 = newNote.getEntity1();
        if (who1 != null)
        {
            who1.addEventNote(newNote);
        }
        
        Entity who2 = newNote.getEntity2();
        if (who2 != null)
        {
            who2.addEventNote(newNote);
        }
        
        Entity who3 = newNote.getEntity3();
        if (who3 != null)
        {
            who3.addEventNote(newNote);
        }
        
        EventAbstract Event = newNote.getEvent();
        if (Event != null) {
            Event.addEventNote(newNote);
        }

		int i = eVector.indexOf(where);

		if (i < 0) { // negative index means, that where is not contained
			Model mBuffer = null; // buffer current model
			if (newNote.getEntity1() != null) {
				mBuffer = newNote.getEntity1().getModel();
			}
			if (newNote.getEvent() != null) {
				mBuffer = newNote.getEvent().getModel();
			}
			throw new SimAbortedException(
					new ErrorMessage(
							mBuffer,
							"Can not insert new event-note after given EventNote! "
									+ "Simulation aborted",
							"Internal DESMO-J class : EventVector Method : "
									+ "insertAfter(EventNote where, EventNote newNote)",
							"The event-note to insert the new note after is not contained "
									+ "in the event vector.",
							"This is a fatal error. Contact DESMOJ support",
							newNote.getTime()));
		} else { // if where is contained, put newNote at next position
			newNote.setTime(where.getTime());
			// synchronize times to keep order
			eVector.insertElementAt(newNote, i + 1);
			// everything fine, exit...
			
		}

	}

	/**
	 * Inserts the given EventNote at the first position in the event-list. The
	 * Event encapsulated in that EventNote will probably be the next event to
	 * be processed by the scheduler (unless some other calls to this method are
	 * made before). Note that the time of the new event-note is set to the
	 * actual simulation time.
	 * 
	 * @param newNote
	 *            EventNote : The event-note to be inserted at the first position
	 *            in the event-list.
	 */
	void insertAsFirst(EventNote newNote) {

		eVector.insertElementAt(newNote, 0);
		
		Entity who1 = newNote.getEntity1();
		if (who1 != null)
		{
				who1.addEventNote(newNote);
		}
		
		Entity who2 = newNote.getEntity2();
		if (who2 != null)
		{
			who2.addEventNote(newNote);
		}
		
		Entity who3 = newNote.getEntity3();
		if (who3 != null)
		{
			who3.addEventNote(newNote);
		}
		
        EventAbstract Event = newNote.getEvent();
        if (Event != null) {
            Event.addEventNote(newNote);
        }

	}

	/**
	 * Inserts an event-note at the last position in the event-list. Also adapts
	 * the new event-note's scheduled point of time to the same time as the last
	 * elment in the event-list. Time is not changed, if the event-list is
	 * empty.
	 * 
	 * @param newNote
	 *            EventNote : The event-note to be inserted at the last position
	 *            in the event-list.
	 */
	void insertAsLast(EventNote newNote) {

		if (!isEmpty()) // if notes in EventList, set time to time of last note
			newNote.setTime(eVector.lastElement().getTime());

		eVector.addElement(newNote); // always append note to end of
		// EventList
		
		// code for adding EventNote to all possible Entity
		
		Entity who1 = newNote.getEntity1();
		if (who1 != null)
		{
				who1.addEventNote(newNote);
		}
		
		Entity who2 = newNote.getEntity2();
		if (who2 != null)
		{
			who2.addEventNote(newNote);
		}
		
		Entity who3 = newNote.getEntity3();
		if (who3 != null)
		{
			who3.addEventNote(newNote);
		}
		
        EventAbstract Event = newNote.getEvent();
        if (Event != null) {
            Event.addEventNote(newNote);
        }

	}

	/**
	 * Inserts a new event-note before another EventNote specified. Note that
	 * this could disturb the temporal order of the event-list. So this method
	 * should only be used carefully. Note also, that EventNote 'where' must be
	 * contained in the event-list or otherwise an exception will be thrown.
	 * 
	 * @param where
	 *            EventNote : The event-note containing the event before which
	 *            the newNote is supposed to be inserted into the event-list.
	 * @param newNote
	 *            EventNote : The new event-note to be inserted before the
	 *            specified EventNote in the event-list
	 * @throws SimAbortedException
	 *                : if referred EventNote is not contained in the event-list
	 */
	void insertBefore(EventNote where, EventNote newNote) {
	    
        // code for adding EventNote to all possible entities
        
        Entity who1 = newNote.getEntity1();
        if (who1 != null)
        {
            who1.addEventNote(newNote);
        }
        
        Entity who2 = newNote.getEntity2();
        if (who2 != null)
        {
            who2.addEventNote(newNote);
        }
        
        Entity who3 = newNote.getEntity3();
        if (who3 != null)
        {
            who3.addEventNote(newNote);
        }
        
        EventAbstract Event = newNote.getEvent();
        if (Event != null) {
            Event.addEventNote(newNote);
        }

		int i = eVector.indexOf(where);

		if (i < 0) {
			Model mBuffer = null; // buffer current model
			if (newNote.getEntity1() != null) {
				mBuffer = newNote.getEntity1().getModel();
			}
			if (newNote.getEvent() != null) {
				mBuffer = newNote.getEvent().getModel();
			}
			throw new SimAbortedException(
					new ErrorMessage(
							mBuffer,
							"Can not insert new event-note before given EventNote! "
									+ "Simulation aborted",
							"Internal DESMO-J class : EventVector Method : "
									+ "insertBefore(EventNote where, EventNote newNote)",
							"The event-note to insert the new note before is not contained "
									+ "in the event vector.",
							"This is a fatal error. Contact DESMOJ support",
							newNote.getTime()));
		} 
		else 
		{
			newNote.setTime(where.getTime());
			// synchronize times to keep order
			eVector.insertElementAt(newNote, i);
			// insert newN. & push afterN. one up
			
		}

	}

	/**
	 * Tests if there are any scheduled events contained in the event-list. If
	 * the event-list happens to be empty during the run of a simulation, this
	 * is a criterium to stop the simulation, since no further action is
	 * scheduled.
	 * 
	 * @return boolean : True if there are no Event notes contained in the
	 *         event-list, false otherwise.
	 */
	boolean isEmpty() {

		return eVector.isEmpty();
		// simply pass the call through to the Vector.

	}

	/**
	 * Returns the last EventNote in the event-list. If the event-list is empty,
	 * <code>null</code> will be returned.
	 * 
	 * @return EventNote : the last EventNote in the event-list, null if the
	 *         event-list is empty
	 */
	EventNote lastNote() {

		if (isEmpty())
			return null; // Nothing here, nothign to return...
		else
			return eVector.elementAt(eVector.size());
		// return last

	}

	/**
	 * Returns the next event-note in the event-list relative to the given
	 * EventNote. If the given EventNote is not contained in the event-list or
	 * happens to be the last EventNote in the event-list, null will be
	 * returned.
	 * 
	 * @return EventNote : The event-note following the given EventNote or
	 *         <ocde>null</code> if the given EventNote was last or not found
	 * @param origin
	 *            EventNote : The event-note whose successor is wanted
	 */
	EventNote nextNote(EventNote origin) {

		if (eVector.contains(origin)) {
			if (origin == eVector.lastElement()) {
				return null;
			} else
				return eVector.elementAt(eVector.indexOf(origin) + 1);
		}
		return null;

	}

	/**
	 * Returns the previous EventNote in the event-list relative to the given
	 * EventNote. If the given EventNote is not contained in the event-list or
	 * happens to be the first event-note in the event-list, null will be
	 * returned.
	 * 
	 * @return EventNote : The event-note following the given EventNote or
	 *         <ocde>null</code> if the given EventNote was first or not found
	 * @param origin
	 *            EventNote : The event-note whose predecessor is wanted
	 */
	EventNote prevNote(EventNote origin) {

		if (eVector.contains(origin)) {
			if (origin == eVector.firstElement()) {
				return null;
			}
			return eVector.elementAt(eVector.indexOf(origin) - 1);
		}
		return null;

	}

	/**
	 * Removes the given EventNote from the event-list.
	 * 
	 * Warning: Make sure to tell the entity of the event-note to delete
	 * the Note from its List as well.
	 * 
	 * @param note
	 *            EventNote : The event-note to be removed from the event-list
	 */
	void remove(EventNote note) {

		if (!eVector.contains(note))
			return; // do nothing if it doesn't exist
		else
		{
			eVector.removeElement(note); // go ahead and crunch it!
			
			if (note.getEntity1() != null) // if an entity exists (no external event)
			{
				note.getEntity1().removeEventNote(note); // removes list entry in Entity!
			}
			
			if (note.getEntity2() != null) // if an entity exists (no external event)
			{
				note.getEntity2().removeEventNote(note); // removes list entry in Entity!
			}
			
			if (note.getEntity3() != null) // if an entity exists (no external event)
			{
				note.getEntity3().removeEventNote(note); // removes list entry in Entity!
			}
			
            if (note.getEvent() != null)   // if an event exists
            {
                note.getEvent().removeEventNote(note);      // remove EventNote
            }
		}

	}

	/**
	 * Removes the first event-note from the event-list. Does nothing if the
	 * event-list is already empty.
	 * 
	 * Warning: Make sure to tell the entity of the event-note to delete
	 * the Note from its List as well.
	 */
	void removeFirst() {

		if (!eVector.isEmpty())
		{
			EventNote note = eVector.get(0);
			eVector.removeElementAt(0); // no comment ;-)
			
			if (note.getEntity1() != null) // if an entity exists (no external event)
			{
				note.getEntity1().removeEventNote(note); // removes list entry in Entity!
			}
			
			if (note.getEntity2() != null) // if an entity exists (no external event)
			{
				note.getEntity2().removeEventNote(note); // removes list entry in Entity!
			}
			
			if (note.getEntity3() != null) // if an entity exists (no external event)
			{
				note.getEntity3().removeEventNote(note); // removes list entry in Entity!
			}

			if (note.getEvent() != null)   // if an event exists
            {
                note.getEvent().removeEventNote(note);      // remove EventNote
            }
		}

	}

	/**
	 * Returns a string representing the entries of this vector in a row. The
	 * resulting string includes all Event notes in ascending order as they are
	 * placed inside the event vector.
	 */
	public String toString() {

		StringBuffer textBuffer = new StringBuffer();
		// faster than String and '+'
		java.util.Enumeration<EventNote> notes = eVector.elements(); // get all elements

		while (notes.hasMoreElements()) { // loop through all elements
			textBuffer.append("[");
			textBuffer.append(notes.nextElement());
			textBuffer.append("]"); // compose Note
		}

		return textBuffer.toString();
		// return String representation of StringBuffer

	}
}