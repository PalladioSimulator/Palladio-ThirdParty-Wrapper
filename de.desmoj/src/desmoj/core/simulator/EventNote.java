package desmoj.core.simulator;

/**
 * Event notes contain any information needed for any type of <Code>Schedulable</Code>, event
 * or process. Since <Code>EventNote</Code> are tightly coupled with the <Code>EventList</Code>, a change
 * of the <Code>EventList</Code> might alo force the <Code>EventNote</Code> to contain other information
 * especially concerning the data structures used in the <Code>EventList</Code> as well. To adopt
 * the <Code>Event notes</Code> extend them to carry the specific data and methods you need
 * for a possible change of the <Code>EventList</Code>. The implementation here includes
 * data and methods for event information only, since the default implementation
 * of class <code>EventVector</code> needs no special information stored
 * inside the <Code>Event notes</Code>.
 * 
 * @version DESMO-J, Ver. 2.3.3 copyright (c) 2011
 * @author Tim Lechler
 * @author modified by Justin Neumann
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
public class EventNote implements Comparable<EventNote>
{
	
	/**
	 * The Entity associated to an event and a point of simulation time. Can be
	 * <code>null</code> in case of an external event associated to this
	 * EventNote.
	 */
	private Entity _myEntity1; // Entity associated with this event-note
	
	/**
	 * The Entity associated to an event and a point of simulation time. Can be
	 * <code>null</code> in case of an external event associated to this
	 * EventNote.
	 */
	private Entity _myEntity2; // Entity associated with this event-note
	
	/**
	 * The Entity associated to an event and a point of simulation time. Can be
	 * <code>null</code> in case of an external event associated to this
	 * EventNote.
	 */
	private Entity _myEntity3; // Entity associated with this event-note

	/**
	 * The event associated to an entity and a point of simulation time. Can be
	 * <code>null</code> in case of a sim-process associated to this event-note.
	 */
	private EventAbstract _myEvent; // type of Event that is going to happen
	
	/**
	 * The point of simulation time associated to an event and an entity. Must
	 * never be <code>null</code> since changes in the state of a model always
	 * happen to a certain discrete point of time.
	 */
	private TimeInstant _myTimeInstant; // time that the event is supposed to happen
    /**
     * If an event-note is connected, <code>RandomizingEventVector</code> or
     * <code>RandomizingEventTreeList</code> will not insert another event-note 
     * (but not explicitly scheduled using
     * <code>scheduleBefore()</code> or <code>scheduelAfter()</code>) 
     * between this event-note and its predecessor scheduled for
     * the same instant.
     */
    private boolean _isConnected; // flag for connection to predecessor
    
	/**
	 * Event notes can only be created if all relevant data can be supplied at
	 * creation time. Note that all relevant associations of the given
	 * Schedulables towards this event-note are built in this constructor
	 * ensuring that each scheduled Schedulable has access to its associated
	 * EventNote.
	 * 
	 * @param who1
	 *            Entity : The Entity that is scheduled to be change during the
	 *            course of the event or the sim-process
	 * @param what
	 *            Event : The type of Event that scheduled to happen to the
	 *            Entity
	 * @param when
	 *            TimeInstant : The point of time that this event-note is supposed to
	 *            be processed by the scheduling mechanism
	 */
	public EventNote(Entity who1, EventAbstract what, TimeInstant when)
	{

		_myEntity1 = who1;
		_myEntity2 = null;
		_myEntity3 = null;
		_myEvent = what;
		_myTimeInstant = when;
			
		if (what != null)
			what.addEventNote(this);

	}
    
	/**
	 * Event notes can only be created if all relevant data can be supplied at
	 * creation time. Note that all relevant associations of the given
	 * Schedulables towards this event-note are built in this constructor
	 * ensuring that each scheduled Schedulable has access to its associated
	 * EventNote.
	 * 
	 * @param who1
	 *            Entity : The first entity that is scheduled to be change during the
	 *            course of the event or the sim-process
	 *            
	 * @param who2
	 *            Entity : The second entity that is scheduled to be change during the
	 *            course of the event or the sim-process
	 *            
	 * @param what
	 *            Event2 : The type of Event that scheduled to happen to the
	 *            entities
	 *            
	 * @param when
	 *            TimeInstant : The point of time that this event-note is supposed to
	 *            be processed by the scheduling mechanism
	 */
	public EventNote(Entity who1, Entity who2, EventAbstract what, TimeInstant when)
	{

		_myEntity1 = who1;
		_myEntity2 = who2;
		_myEntity3 = null;
		_myEvent = what;
		_myTimeInstant = when;
		
		if (what != null)
			what.addEventNote(this);

	}

	/**
	 * Event notes can only be created if all relevant data can be supplied at
	 * creation time. Note that all relevant associations of the given
	 * Schedulables towards this event-note are built in this constructor
	 * ensuring that each scheduled Schedulable has access to its associated
	 * EventNote.
	 * 
	 * @param who1
	 *            Entity : The first entity that is scheduled to be change during the
	 *            course of the event or the sim-process
	 *            
	 * @param who2
	 *            Entity : The second entity that is scheduled to be change during the
	 *            course of the event or the sim-process
	 *            
	 * @param who3
	 *            Entity : The third entity that is scheduled to be change during the
	 *            course of the event or the sim-process
	 *            
	 * @param what
	 *            Event3 : The type of Event that scheduled to happen to the
	 *            Entity
	 *            
	 * @param when
	 *            TimeInstant : The point of time that this event-note is supposed to
	 *            be processed by the scheduling mechanism
	 */
	public EventNote(Entity who1, Entity who2, Entity who3, EventAbstract what, TimeInstant when)
	{

		_myEntity1 = who1;
		_myEntity2 = who2;
		_myEntity3 = who3;
		_myEvent = what;
		_myTimeInstant = when;
		
		if (what != null)
			what.addEventNote(this);

	}
	
	/**
	 * Copies the event-note.
	 * WARNING: No additional reference is set to linked Entity.
	 * 
	 * @return The Entity associated to this event-note
	 */
	public EventNote copy()
	{
		EventNote evn = null;

		if (getNumberOfEntities()<=1)
		{
			evn =  new EventNote(_myEntity1, _myEvent, _myTimeInstant);
			evn._isConnected = this.isConnected();
		}
		else if (getNumberOfEntities()==2)
		{
			evn = new EventNote(_myEntity1, _myEntity2, _myEvent, _myTimeInstant);
			evn._isConnected = this.isConnected();
		}
		else if (getNumberOfEntities()==3)
		{
			evn =  new EventNote(_myEntity1, _myEntity2, _myEntity3, _myEvent, _myTimeInstant);
			evn._isConnected = this.isConnected();
		}
		{
			return evn;
		}
	}
	
	/**
	 * Returns whether this <code>EventNote</code> equals another one or not.
	 * 
	 * @return <code>true</code> or <code>false</code>.
	 */
	public boolean equals(Object object)
	{
	    if (object != null && object instanceof EventNote)
		{
			EventNote note = (EventNote) object;
	        if (this.compareTo(note) == 0)
			{
				if (this.toString().equals(note.toString()))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Compares the given EventNote to this event-note. This method
	 * implements the Comparable<EventNote> Interface
	 * 
	 * @param note The event-note to be compared to this event-note
	 * 
	 * @return Returns a negative integer, zero, or a positive integer as
	 *         this event-note is before, at the same time, or after the given
	 *         EventNote.
	 */
	public int compareTo(EventNote note)
	{
		if (note == null )
		{
			return 0; //TODO?
		}
		if ((note.getTime() == null) && this.getTime() == null)
		{
			return 0; //TODO?
		}
		else if (note == null | note.getTime() == null)
		{
			return -1; //TODO?
		}
		else if (this.getTime() == null)
		{
			return +1; //TODO?
		}
		return this.getTime().compareTo(note.getTime());
	}

	/**
	 * Returns the entity that is associated with this event-note.
	 * 
	 * @return Entity : The Entity associated to this event-note
	 */
	public Entity getEntity1() 
	{

		return _myEntity1;

	}
	
	/**
	 * Returns the entity that is associated with this event-note.
	 * 
	 * @return Entity : The Entity associated to this event-note
	 */
	public Entity getEntity2() 
	{

		return _myEntity2;

	}
	
	/**
	 * Returns the entity that is associated with this event-note.
	 * 
	 * @return Entity : The Entity associated to this event-note
	 */
	public Entity getEntity3() 
	{

		return _myEntity3;

	}

	/**
	 * Returns the event associated with this event-note.
	 * 
	 * @return Event : The event associated with this event-note
	 */
	public EventAbstract getEvent() 
	{

		return _myEvent;

	}
	
	/**
	 * Returns the number of included entities.
	 * 
	 * @return <code>int</code> : The number of entities
	 */
	public long getNumberOfEntities()
	{
		int i = 0;
		if (_myEntity1!=null)
		{
			i++;
		}
		if (_myEntity2!=null)
		{
			i++;
		}
		if (_myEntity3!=null)
		{
			i++;
		}
			
		return i;
		
	}
	
	/**
	 * Returns the point of time associated with this event-note.
	 * 
	 * @return TimeInstant : Point of time in simulation associated with this
	 *         EventNote
	 */
	public TimeInstant getTime() 
	{

		return _myTimeInstant;

	}

	/**
	 * Associates this event-note with the given Entity. This is a package
	 * visibility method for internal framework use only.
	 * 
	 * @param e
	 *            Entity : The Entity to be associated with this event-note
	 */
	void setEntity1(Entity e) 
	{

		_myEntity1 = e;

	}
	
	/**
	 * Associates this event-note with the given Entity. This is a package
	 * visibility method for internal framework use only.
	 * 
	 * @param e
	 *            Entity : The Entity to be associated with this event-note
	 */
	void setEntity2(Entity e) 
	{

		_myEntity2 = e;

	}
	
	/**
	 * Associates this event-note with the given Entity. This is a package
	 * visibility method for internal framework use only.
	 * 
	 * @param e
	 *            Entity : The Entity to be associated with this event-note
	 */
	void setEntity3(Entity e) 
	{

		_myEntity3 = e;

	}

	/**
	 * Associates this event-note with the given event. This is a package
	 * visibility method for internal framework use only.
	 * 
	 * @param e
	 *            Event : The event to be associated with this event-note
	 */
	void setEvent(EventAbstract e) 
	{

		_myEvent = e;

	}
	 
	/**
	 * Sets the point of time for this event-note to the time given as parameter.
	 * This method is to be used by the scheduler to correct the point of time
	 * of an event-note after inserting it relative to some other EventNote to
	 * preserve the temporal order of the event-list. This is a package
	 * visibility method for internal framework use only.
	 * 
	 * @param time
	 *            TimeInstant : the new point of simulation time this event-note is
	 *            associated with.
	 */
	void setTime(TimeInstant time) {

		_myTimeInstant = time;

	}
    
    /**
     * Tests if this event-note is connected to its predecessor.
     * If a connection exists (true), <code>RandomizingEventVector</code> or
     * <code>RandomizingEventTreeList</code> will not insert another event-note scheduled for
     * the same instant (but not explicity scheduled using
     * <code>scheduleBefore()</code> or <code>scheduelAfter()</code>) 
     * between this event-note and its predecessor  scheduled for
     * the same instant. This is a package
     * visibility method for internal framework use only.
     * 
     * @return boolean : Is <code>true</code> if a connection to the predecessor exists, 
     *                   <code>false</code> otherwise.
     */
    boolean isConnected() {
        return _isConnected;
    }

    /**
     * Sets this event node connected to its predecessor (true), so that
     * <code>RandomizingEventVector</code> or
     * <code>RandomizingEventTreeList</code> will not insert another EventNote 
     * (but not explicity scheduled using
     * <code>scheduleBefore()</code> or <code>scheduelAfter()</code>) 
     * between this event-note and its predecessor scheduled for
     * the same instant or removes
     * such a connection (false). This is a package
     * visibility method for internal framework use only.
     * 
     * @param isConnected
     *            boolean : establishes (true) or removes (false) a connection between 
     *            this event-note and its predecessor.
     */
    void setConnected(boolean isConnected) {
        this._isConnected = isConnected;
    }

    /**
	 * Returns a string representing the elements bundled in this event-note. It
	 * calls the <code>toString()</code> methods of every element putting each
	 * in brackets containing one or two letters to indicate the type of
	 * element.
	 * <p>
	 * <ul>
	 * <li>En: Entity, SimProcess or <code>null</code></li>
	 * <li>Ev: Event, external event or <code>null</code></li>
	 * <li>t: simulation time</li>
	 * </ul>
	 * 
	 * @return java.lang.String : String representing the contained elements.
	 */
	public String toString() {
		
		String EntityString = "";
		
		if (getNumberOfEntities()==1)
		{
			EntityString = "En: " + _myEntity1 + " ";
		}
		if (getNumberOfEntities()==2)
		{
			EntityString = "En:" + _myEntity1 + ","  + _myEntity2 + " ";
		}
		if (getNumberOfEntities()==3)
		{
			EntityString = "En:" + _myEntity1 + "," + _myEntity2 + "," + _myEntity3 + " ";
		}

		return (EntityString + "Ev:" + _myEvent + " t:" + _myTimeInstant);

	}

}