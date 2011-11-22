package desmoj.core.simulator;

import java.util.Iterator;

/**
 * ProcessQueue provides models with a ready-to-use element to enqueue
 * <code>SimProcess</code>es in. The sort order of the ProcessQueue is
 * determined first by the priorities of the enqueued SimProcesses and second by
 * the given sort order. The default sort order is FIFO (first in, first out)
 * but others like LIFO (last in, first out) can be chosen, too. See the
 * constants in class <code>QueueBased</code> and the derived classes from
 * <code>QueueList</code>. The capacity of the ProcessQueue, that is the
 * maximum number of SimProcesses enqueued, can be chosen, too. Note that in
 * contrast to the 'plain' queue, this ProcessQueue always expects and returns
 * objects that are derived from class <code>SimProcess</code>. When
 * modelling using the process-, activity-, or transaction-oriented paradigm
 * where SimProcesses are used to represent the model's entities, this
 * ProcessQueue can be used instead of the standard Queue to reduce the amount
 * of casts needed otherwise.
 * 
 * @see QueueBased
 * @see QueueList
 * @see QueueListFifo
 * @see QueueListLifo
 * 
 * @version DESMO-J, Ver. 2.3.3 copyright (c) 2011
 * @author Tim Lechler
 * @author modified by Soenke Claassen
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
public class ProcessQueue<P extends SimProcess> extends QueueBased implements Iterable<P> {

	/**
	 * The queue implementation that actually stores the entities
	 */
	private QueueList<P> _ql;

	/**
	 * Counter for the sim-processes which are refused to be enqueued, because
	 * the queue capacity is full.
	 */
	private long _refused;
	

	/**
	 * Constructs a simple priority based waiting-queue for SimProcesses, the
	 * kind of queue implementation (FIFO or LIFO) and the capacity of the queue
	 * can be chosen.
	 * <p>
     * The usage of the generic version <code>ProcessQueue&lt;Type&gt;</code> where 
     * <code>Type</code> is derived from <code>SimProcess</code> is recommended
     * for type safety. Using the raw type <code>ProcessQueue</code> yields a queue
     * in which any <code>SimProcess</code> can be enqueued, potentially requiring
     * type casting on accessing processes enqueued.  
	 * 
	 * @param owner
	 *            Model : The model this ProcessQueue is associated to
	 * @param name
	 *            java.lang.String : The process-queue's name
     * @param sortOrder
     *            int : determines the sort order of the underlying queue
     *            implementation. Choose a constant from <code>QueueBased</code>:
     *            <code>QueueBased.FIFO</code>, <code>QueueBased.LIFO</code> or
     *            QueueBased.Random.
     * @param qCapacity
	 *            int : The capacity of the ProcessQueue, that is how many
	 *            processes can be enqueued. Zero (0) means unlimited capacity.
	 * @param showInReport
	 *            boolean : Flag if process-queue should produce a report
	 * @param showInTrace
	 *            boolean : Flag for process-queue to produce trace messages
	 */
	public ProcessQueue(Model owner, String name, int sortOrder, int qCapacity,
			boolean showInReport, boolean showInTrace) {

		super(owner, name, showInReport, showInTrace); // create the QBased
		// object
		reset();

        // determine the queueing strategy
        switch (sortOrder) {
        case QueueBased.FIFO :
            _ql = new QueueListFifo<P>(); break;
        case QueueBased.LIFO :
            _ql = new QueueListLifo<P>(); break;
        case QueueBased.RANDOM :
            _ql = new QueueListRandom<P>(); break;
        default :
            sendWarning(
                    "The given sortOrder parameter " + sortOrder + " is not valid! "
                            + "A queue with Fifo sort order will be created.",
                    "ProcessQueueQueue : "
                            + getName()
                            + " Constructor: ProcessQueue(Model owner, String name, "
                            + "int sortOrder, long qCapacity, boolean showInReport, "
                            + "boolean showInTrace)",
                    "A valid positive integer number must be provided to "
                            + "determine the sort order of the queue.",
                    "Make sure to provide a valid positive integer number "
                            + "by using the constants in the class QueueBased, like "
                            + "QueueBased.FIFO, QueueBased.LIFO or QueueBased.RANDOM.");
            _ql = new QueueListFifo<P>(); 
        }
        
        // give the QueueList a reference to this QueueBased
        _ql.setQueueBased(this);

		// set the capacity of the queue
		queueLimit = qCapacity;

		// check if it the capacity does make sense
		if (qCapacity < 0) {
			sendWarning(
					"The given capacity of the queue is negative! "
							+ "A queue with unlimited capacity will be created instead.",
					"ProcessQueue : "
							+ getName()
							+ " Constructor: ProcessQueue(Model owner, String name, "
							+ "int sortOrder, long qCapacity, boolean showInReport, "
							+ "boolean showInTrace)",
					"A negative capacity for a queue does not make sense.",
					"Make sure to provide a valid positive capacity "
							+ "for the queue.");
			// set the capacity to the maximum value
			queueLimit = Integer.MAX_VALUE;
		}

		// check if qCapacity is zero (that means unlimited capacity)
		if (qCapacity == 0) {
			// set the capacity to the maximum value
			queueLimit = Integer.MAX_VALUE;
		}

	}

	/**
	 * Constructs a simple priority and FIFO based waiting-queue for
	 * Sim-processes with unlimited capacity of the queue.
     * <p>
     * The usage of the generic version <code>ProcessQueue&lt;Type&gt;</code> where 
     * <code>Type</code> is derived from <code>SimProcess</code> is recommended
     * for type safety. Using the raw type <code>ProcessQueue</code> yields a queue
     * in which any <code>SimProcess</code> can be enqueued, potentially requiring
     * type casting on accessing processes enqueued.  
	 * 
	 * @param owner
	 *            Model : The model this process-queue is associated to
	 * @param name
	 *            java.lang.String : The process-queue's name
	 * @param showInReport
	 *            boolean : Flag if process-queue should produce a report
	 * @param showInTrace
	 *            boolean : Flag for process-queue to produce trace messages
	 */
	public ProcessQueue(Model owner, String name, boolean showInReport,
			boolean showInTrace)
	{
		super(owner, name, showInReport, showInTrace); // create the QBased
		// object
		reset();

		// make the queue with Fifo queueing discipline and unlimited capacity
		_ql = new QueueListFifo<P>();
		_ql.setQueueBased(this);
		
	}

	/**
	 * Returns a process-queue-reporter to produce a report about this
	 * process-queue.
	 * 
	 * @return desmoj.report.Reporter : The reporter for this process-queue
	 */
	public desmoj.core.report.Reporter createReporter() 
	{

		return new desmoj.core.report.ProcessQueueReporter(this);

	}

	/**
	 * Returns the first SimProcess queued in this process-queue or
	 * <code>null</code> in case the queue is empty.
	 * 
	 * @return desmoj.SimProcess : The first SimProcess in the process-queue or
	 *         <code>null</code> if the process-queue is empty
	 */
	public P first() {

		return _ql.first(); // straight design

	}

	/**
	 * Returns the first SimProcess queued in this process-queue that applies to
	 * the given condition. The process-queue is searched from front to end and
	 * the first SimProcess that returns <code>true</code> when the condition
	 * is applied to it is returned by this method. If no SimProcess applies to
	 * the given condition or the process-queue is empty, <code>null</code>
	 * will be returned.
	 * 
	 * @return desmoj.core.SimProcess : The first process queued in this
	 *         process-queue applying to the given condition or <code>null</code>
	 * @param c
	 *            Condition : The condition that the sim-process returned must
	 *            confirm
	 */
	public P first(Condition<P> c) {

		if (c == null) {
			sendWarning(
					"Can not return first SimProcess complying to condition!",
					"ProcessQueue : " + getName()
							+ " Method: void first(Condition c)",
					"The Condition 'c' given as parameter is a null reference!",
					"Check to always have valid references when querying Queues.");
			return null; // no proper parameter
		}
		if (_ql.isEmpty())
			return null; // nobody home to be checked
		for (P tmp = _ql.first(); tmp != null; tmp = _ql.succ(tmp)) {
			if (c.check(tmp))
				return tmp;
		}

		// if no SimProcess complies to the condition just return null
		return null;

	}
	
	/**
	 * Returns the <code>SimProcess</code> queued at the named position.
	 * The first position is 0, the last one size()-1.
	 * 
	 * @return int :The position of the process as an <code>int</code>. 
	 * 				Returns -1 if no such position exists.
	 */
	public int get(P p)  
	{
		
		return _ql.get(p);
		
	}

	/**
	 * Returns the <code>SimProcess</code> queued at the named position.
	 * The first position is 0, the last one size()-1.
	 * 
	 * @return desmoj.core.SimProcess : The <code>SimProcess</code> at the position of
	 *         <code>int</code> or <code>null</code> if no such position exists.
	 */
	public P get(int index) {
		return _ql.get(index);
	}

    /**
     * Returns the underlying queue implementation, providing access to the
     * QueueList implementation, e.g. to add PropertyChangeListeners.
     * 
     * @return desmoj.core.simulator.QueueList : The underlying queue implementation of this
     *         ProcessQueue.
     */
    public QueueList<P> getQueueList() {

        return _ql; // that's all
    }

	/**
	 * Returns the implemented queueing discipline of the underlying queue as a
	 * String, so it can be displayed in the report.
	 * 
	 * @return String : The String indicating the queueing discipline.
	 */
	public String getQueueStrategy() {

		return _ql.getAbbreviation(); // that's it

	}

	/**
	 * Returns the number of entities refused to be enqueued in the queue,
	 * because the capacity limit is reached.
	 * 
	 * @return long : The number of entities refused to be enqueued in the
	 *         queue.
	 */
	public long getRefused() {

		return _refused; // that's it
	}

	/**
	 * Enters a new SimProcess into the ProcessQueue. If the capacity of the
	 * ProcessQueue is full, the entity will not be enqueued and
	 * <code>false</code> will be returned. The sim-process will be stored in
	 * the ProcessQueue until method <code>remove(SimProcess e)</code> is
	 * called with this specific SimProcess. Simprocesses are ordered according
	 * to their priority. Higher priorities are sorted in front of lower
	 * priorities. Simprocesses with same priority are orderer according to the
	 * strategy specified in the constructor. The first SimProcess inside the
	 * process-queue will always be the one with the highest priority.
	 * 
	 * @return boolean : Is <code>true</code> if insertion was successful,
	 *         <code>false</code> otherwise (i.e. capacity limit is reached).
	 * @param e
	 *            desmoj.SimProcess : The sim-process to be added to the
	 *            ProcessQueue
	 */
	public boolean insert(P e) {

		if (e == null) { // null returns with warning
			sendWarning("Can not insert SimProcess!", "ProcessQueue : "
					+ getName() + " Method: boolean insert" + "(SimProcess e)",
					"The sim-process given as parameter is a null reference!",
					"Check to always have valid references when enqueueing "
							+ "Entities");
			return false; // no proper parameter
		}

		if (!isModelCompatible(e)) {
			sendWarning("Can not insert SimProcess!", "ProcessQueue : "
					+ getName() + " Method: boolean insert" + "(SimProcess e)",
					"The sim-process given as parameter is not compatible to "
							+ "the model this process-queue belongs to!",
					"Check if your submodels are allowed to mingle with other "
							+ "model's components.");
			return false; // not of my model type!!!
		}

		if (queueLimit <= length()) {

			if (currentlySendDebugNotes()) { 
				sendDebugNote("refuses to insert " + e.getQuotedName()
					+ " because the "
					+ "capacity limit is reached. ProcessQueue:<br>"
					+ _ql.toString());
			}

			if (currentlySendTraceNotes()) {
				sendTraceNote("is refused to be enqueued in "
					+ this.getQuotedName() + "because the capacity limit ("
					+ getQueueLimit() + ") of this "
					+ "ProcessQueue is reached");
			}

			_refused++; // count the refused ones

			return false; // capacity limit is reached
		}

		_ql.insert(e); // that's it

		if (currentlySendDebugNotes()) {
			sendDebugNote("inserts " + e.getQuotedName()
					+ " in the ProcessQueue:<br>" + _ql.toString());
		}

		// produce trace output
		if (currentlySendTraceNotes()) {
			if (e == currentEntity() && currentEntityAll().size() == 1) {
				sendTraceNote("inserts itself into " + this.getQuotedName());
			} else {
				sendTraceNote("inserts " + e.getName() + " into "
						+ this.getQuotedName());
			}
		}

		return true;
	}

	/**
	 * Enters a new SimProcess into the process-queue and places it after the
	 * given SimProcess. If the capacity of the ProcessQueue is full, the entity
	 * will not be enqueued and <code>false</code> will be returned. Make sure
	 * that the sim-process given as reference is already queued inside the
	 * process-queue, else the sim-process will not be enqueued and
	 * <code>false</code> will be returned. The sim-process will be stored in
	 * the ProcessQueue until method <code>remove(SimProcess e)</code> is
	 * called with this specific SimProcess.
	 * 
	 * @return boolean : Is <code>true</code> if insertion was successful,
	 *         <code>false</code> otherwise (i.e. capacity limit is reached).
	 * @param e
	 *            SimProcess : The sim-process to be added to the process-queue
	 * @param after
	 *            SimProcess : The sim-process after which SimProcess 'e' is to
	 *            be inserted
	 */
	public boolean insertAfter(P e, P after) {

		if (e == null) {
			sendWarning(
					"Can not insert SimProcess!",
					"ProcessQueue : "
							+ getName()
							+ " Method: boolean insertAfter(SimProcess e, SimProcess after)",
					"The sim-process -e- given as parameter is a null reference!",
					"Check to always have valid references when enqueueing Entities");
			return false; // no proper parameter
		}

		if (after == null) {
			sendWarning(
					"Can not insert SimProcess!",
					"ProcessQueue : "
							+ getName()
							+ " Method: boolean insertAfter(SimProcess e, SimProcess after)",
					"The sim-process -after- given as parameter is a null reference!",
					"Check to always have valid references when enqueueing Entities");
			return false; // no proper parameter
		}

		if (!isModelCompatible(e)) {
			sendWarning(
					"Can not insert SimProcess!",
					"ProcessQueue : "
							+ getName()
							+ " Method: boolean insertAfter(SimProcess e, SimProcess after)",
					"The sim-process given as parameter is not compatible to "
							+ "the model this process-queue belongs to!",
					"Check if your submodels are allowed to mingle with other "
							+ "model's components.");
			return false; // not of my model type!!!
		}

		if (queueLimit <= length()) {

			if (currentlySendDebugNotes()) { 
				sendDebugNote("refuses to insert " + e.getQuotedName()
					+ " because the "
					+ "capacity limit is reached. ProcessQueue:<br>"
					+ _ql.toString());
			}

			if (currentlySendTraceNotes()) {
				sendTraceNote("is refused to be enqueued in "
					+ this.getQuotedName() + "because the capacity limit ("
					+ getQueueLimit() + ") of this "
					+ "ProcessQueue is reached");
			}

			_refused++; // count the refused ones

			return false; // capacity limit is reached
		}

		boolean successful = _ql.insertAfter(e, after); // elegantly done...
		
		if (currentlySendDebugNotes()) {
			sendDebugNote("inserts " + e.getQuotedName() + " after "
					+ after.getQuotedName() + " in the ProcessQueue:<br>"
					+ _ql.toString());
		}

		// produce trace output
		if (currentlySendTraceNotes()) {
			if (e == currentEntity() && currentEntityAll().size() == 1) {
				sendTraceNote("inserts itself into " + this.getQuotedName()
						+ " after " + after.getName());
			} else {
				sendTraceNote("inserts " + e.getName() + " into "
						+ this.getQuotedName() + " after " + after.getName());
			}
		}

		return successful;

	}

	/**
	 * Enters a new SimProcess into the ProcessQueue and places it in front of
	 * the given SimProcess. If the capacity of the ProcessQueue is full, the
	 * Entity will not be enqueued and <code>false</code> will be returned.
	 * Make sure that the sim-process given as reference is already queued inside
	 * the ProcessQueue, else the sim-process will not be enqueued and
	 * <code>false</code> will be returned. The sim-process will be stored in
	 * the ProcessQueue until method <code>remove(SimProcess e)</code> is
	 * called with this specific SimProcess.
	 * 
	 * @return boolean : Is <code>true</code> if insertion was successful,
	 *         <code>false</code> otherwise (i.e. capacity limit is reached).
	 * @param e
	 *            SimProcess : The sim-process to be added to the processqQueue
	 * @param before
	 *            SimProcess : The sim-process before which the sim-process 'e' is
	 *            to be inserted
	 */
	public boolean insertBefore(P e, P before) {

		if (e == null) {
			sendWarning(
					"Can not insert SimProcess!",
					"ProcessQueue : "
							+ getName()
							+ " Method: boolean insertBefore(SimProcess e, SimProcess before)",
					"The sim-process -e- given as parameter is a null reference!",
					"Check to always have valid references when enqueueing Entities");
			return false; // no proper parameter
		}

		if (before == null) {
			sendWarning(
					"Can not insert SimProcess!",
					"ProcessQueue : "
							+ getName()
							+ " Method: boolean insertBefore(SimProcess e, SimProcess before)",
					"The sim-process -before- given as parameter is a null reference!",
					"Check to always have valid references when enqueueing Entities");
			return false; // no proper parameter
		}

		if (!isModelCompatible(e)) {
			sendWarning(
					"Can not insert SimProcess!",
					"ProcessQueue : "
							+ getName()
							+ " Method: boolean insertBefore(SimProcess e, SimProcess before)",
					"The sim-process given as parameter is not compatible to "
							+ "the model this process-queue belongs to!",
					"Check if your submodels are allowed to mingle with other "
							+ "model's components.");
			return false; // not of my model type!!!
		}

		if (queueLimit <= length()) {

			if (currentlySendDebugNotes()) {
				sendDebugNote("refuses to insert " + e.getQuotedName()
					+ " because the "
					+ "capacity limit is reached. ProcessQueue:<br>"
					+ _ql.toString());
			}

			if (currentlySendTraceNotes()) {
				sendTraceNote("is refused to be enqueued in "
					+ this.getQuotedName() + "because the capacity limit ("
					+ getQueueLimit() + ") of this "
					+ "ProcessQueue is reached");
			}

			_refused++; // count the refused ones

			return false; // capacity limit is reached
		}

		boolean successful = _ql.insertBefore(e, before); // elegantly done...
		
		if (currentlySendDebugNotes()) {
			sendDebugNote("inserts " + e.getQuotedName() + " before "
					+ before.getQuotedName() + " in the ProcessQueue:<br>"
					+ _ql.toString());
		}

		// produce trace output
		if (currentlySendTraceNotes()) {
			if (e == currentEntity() && currentEntityAll().size() == 1) {
				sendTraceNote("inserts itself into " + this.getQuotedName()
						+ " before " + before.getName());
			} else {
				sendTraceNote("inserts " + e.getName() + " into "
						+ this.getQuotedName() + " before " + before.getName());
			}
		}

		return successful;
	}

	/**
	 * Returns a boolean value indicating if the process-queue is empty or if any
	 * number of SimProcess is currently enqueued in it.
	 * 
	 * @return boolean : Is <code>true</code> if the process-queue is empty,
	 *         <code>false</code> otherwise
	 */
	public boolean isEmpty() {

		return _ql.isEmpty();

	}

	/**
	 * Returns the last SimProcess queued in this process-queue or
	 * <code>null</code> in case the process-queue is empty.
	 * 
	 * @return desmoj.SimProcess : The last SimProcess in the process-queue or
	 *         <code>null</code> if the process-queue is empty
	 */
	public P last() {

		return _ql.last(); // straight design again

	}

	/**
	 * Returns the last SimProcess queued in this process-queue that applies to
	 * the given condition. The process-queue is searched from end to front and
	 * the first SimProcess that returns <code>true</code> when the condition
	 * is applied to it is returned by this method. If no SimProcess applies to
	 * the given condition or the process-queue is empty, <code>null</code>
	 * will be returned.
	 * 
	 * @return desmoj.SimProcess : The last SimProcess queued in this
	 *         process-queue applying to the given condition or <code>null</code>
	 * @param c
	 *            Condition : The condition that the sim-process returned must
	 *            comply to
	 */
	public P last(Condition<P> c) {

		if (c == null) {
			sendWarning(
					"Can not insert SimProcess!",
					"ProcessQueue : " + getName()
							+ " Method: SimProcess last(Condition c)",
					"The Condition -c- given as parameter is a null reference!",
					"Check to always have valid references when querying Queues.");
			return null; // no proper parameter
		}

		if (_ql.isEmpty())
			return null; // nobody home to be checked

		for (P tmp = _ql.last(); tmp != null; tmp = _ql.pred(tmp)) {
			if (c.check(tmp))
				return tmp;
		}

		// if no SimProcess complies to the condition just return null
		return null;

	}

	/**
	 * Returns the sim-process enqueued directly before the given SimProcess in
	 * the process-queue. If the given SimProcess is not contained in this
	 * process-queue or is at the first position thus having no possible
	 * predecessor, <code>null</code> is returned.
	 * 
	 * @return desmoj.SimProcess : The sim-process directly before the given
	 *         SimProcess in the process-queue or <code>null</code>.
	 * @param e
	 *            desmoj.SimProcess : An SimProcess in the process-queue
	 */
	public P pred(P e) {

		if (e == null) {
			sendWarning(
					"Can not find predecessor of SimProcess in Queue!",
					"ProcessQueue : " + getName()
							+ " Method: SimProcess pred(SimProcess e)",
					"The sim-process 'e' given as parameter is a null reference!",
					"Check to always have valid references when querying for Entities");
			return null; // no proper parameter
		}

		return _ql.pred(e);

	}

	/**
	 * Returns the sim-process enqueued before the given SimProcess in the
	 * process-queue that also complies to the condition given. If the given
	 * Sim-process is not contained in this process-queue or is at the first
	 * position thus having no possible predecessor, <code>null</code> is
	 * returned. If no other SimProcess before the given one complies to the
	 * condition, <code>null</code> is returned, too.
	 * 
	 * @return desmoj.SimProcess : The sim-process before the given SimProcess in
	 *         the process-queue complying to the condition or <code>null</code>.
	 * @param e
	 *            SimProcess : A sim-process in the process-queue
	 * @param c
	 *            Condition : The condition that the preceeding SimProcess has
	 *            to comply to
	 */
	public P pred(P e, Condition<P> c) {

		if (e == null) {
			sendWarning(
					"Can not find predecessor of SimProcess in Queue!",
					"ProcessQueue : "
							+ getName()
							+ " Method: SimProcess pred(SimProcess e, Condition c)",
					"The sim-process 'e' given as parameter is a null reference!",
					"Check to always have valid references when querying for Entities");
			return null; // no proper parameter
		}

		if (c == null) {
			sendWarning(
					"Can not return previous SimProcess complying to condition!",
					"ProcessQueue : "
							+ getName()
							+ " Method: SimProcess pred(SimProcess e, Condition c)",
					"The Condition 'c' given as parameter is a null reference!",
					"Check to always have valid references when querying Queues.");
			return null; // no proper parameter
		}

		for (P tmp = pred(e); tmp != null; tmp = pred(tmp)) {
			if (c.check(tmp))
				return tmp;
		}

		return null; // obviously not found here, empty or doesn't comply

	}

	/**
	 * Removes the given SimProcess from the process-queue. If the given
	 * Sim-process is not in the process-queue, a warning will be issued but
	 * nothing else will be changed.
	 * 
	 * @param e
	 *            SimProcess : The sim-process to be removed from the
	 *            process-queue
	 */
	public void remove(SimProcess e) {

		if (e == null) {
			sendWarning(
					"Can not remove SimProcess from Queue!",
					"ProcessQueue : " + getName()
							+ " Method:  void remove(SimProcess e)",
					"The sim-process 'e' given as parameter is a null reference!",
					"Check to always have valid references when removing "
							+ "Entities");
			return; // no proper parameter
		}
		
		if (!_ql.remove((P)e)) 
		{ // watch out, removes SimProcess as a side
			// effect!!!
			sendWarning("Can not remove SimProcess from Queue!",
					"ProcessQueue : " + getName()
							+ " Method:  void remove(SimProcess e)",
					"The sim-process 'e' given as parameter is not enqueued in "
							+ "this queue!",
					"Make sure the sim-process is inside the queue you want it "
							+ "to be removed.");
			return; // not enqueued here
		}
		else // done
		{

		}

		if (currentlySendDebugNotes()) {
			sendDebugNote("remove " + e.getQuotedName() + "<br>"
					+ _ql.toString());
		}

		// produce trace output
		if (currentlySendTraceNotes()) {
			if (e == currentEntity() && currentEntityAll().size() == 1) {
				sendTraceNote("removes itself from " + this.getQuotedName());
			} else {
				sendTraceNote("removes " + e.getQuotedName() + " from "
						+ this.getQuotedName());
			}
		}

	}
	
    /**
     * Removes the process queued at the given position.
     * The first position is 0, the last one length()-1.
     * 
     * @return : The method returns <code>true</code> if a <code>SimProcess</code>
     *           exists at the given position or <code>false></code> if otherwise.
     */
	public boolean remove(int index)  
	{
		if (index < 0 || index >= this.length()) return false;
	    
	    P p = get(index);
		if (p == null) {
		    return false;
		} else {
		    remove(p);
		    return true;
		}
	}		

	/**
	 * Resets all statistical counters to their default values. The mininum and
	 * maximum length of the queue are set to the current number of queued
	 * objects. The counter for the entities refused to be enqueued will be
	 * reset.
	 */
	public void reset() {

		super.reset(); // reset of QueueBased

		_refused = 0;

	}

	/**
	 * Sets the queue capacity to a new value. Only if the new capacity is equal
	 * or larger than the current length of the queue!
	 * 
	 * @param newCapacity
	 *            int : The new capacity of this ProcessQueue.
	 */
	public void setQueueCapacity(int newCapacity) {

		// check if the new capacity is negative or larger than the current
		// length
		// of the queue
		if (newCapacity < length() || newCapacity < 0) {
			sendWarning(
					"The new capacity is negative or would be smaller than the "
							+ "number of entities already enqueued in this ProcessQueue. The capacity "
							+ "will remain unchanged!",
					getClass().getName() + ": " + getQuotedName()
							+ ", Method: "
							+ "void setQueueCapacity(int newCapacity)",
					"The ProcessQueue already contains more entities than the new capacity "
							+ "could hold. What should happen to the remaining entities?",
					"Make sure to change the capacity only to a non negative value larger "
							+ "than the current length of this ProcessQueue.");

			return; // ignore that rubbish and just return
		}

		// set the capacity of the queue to the new value
		queueLimit = newCapacity;

	}

	/**
	 * Sets the sort order of this ProcessQueue to a new value and makes this
	 * ProcessQueue use another <code>QueueList</code> with the specified
	 * queueing discipline. Please choose a constant from
	 * <code>QueueBased</code> (<code>QueueBased.FIFO</code>, 
	 * <code>QueueBased.FIFO</code> or <code>QueueBased.Random</code>)
	 * The sort order of a ProcessQueue can only be changed if the queue is empty.
	 * 
	 * @param sortOrder
	 *            int : determines the sort order of the underlying
	 *            <code>QueueList</code> implementation (<code>QueueBased.FIFO</code>, 
     * <code>QueueBased.FIFO</code> or <code>QueueBased.Random</code>)
	 */
	public void setQueueStrategy(int sortOrder) {

		// check if the queue is empty
		if (!isEmpty()) {
			sendWarning(
					"The ProcessQueue for which the queueing discipline should be "
							+ "changed is not empty. The queueing discipline will remain unchanged!",
					getClass().getName() + ": " + getQuotedName()
							+ ", Method: "
							+ "void setQueueStrategy(int sortOrder)",
					"The ProcessQueue already contains some processes ordered according a "
							+ "certain order.",
					"Make sure to change the sort order only for an empty ProcessQueue.");

			return; // ignore that rubbish and just return
		}

        // determine the queueing strategy
        switch (sortOrder) {
        case QueueBased.FIFO :
            _ql = new QueueListFifo<P>(); break;
        case QueueBased.LIFO :
            _ql = new QueueListLifo<P>(); break;
        case QueueBased.RANDOM :
            _ql = new QueueListRandom<P>(); break;
        default :
            sendWarning(
                    "The given sortOrder parameter is negative or too big! "
                            + "The sort order of the ProcessQueue will remain unchanged!",
                    getClass().getName() + ": " + getQuotedName()
                            + ", Method: "
                            + "void setQueueStrategy(int sortOrder)",
                    "A valid positive integer number must be provided to "
                            + "determine the sort order of the queue.",
                    "Make sure to provide a valid positive integer number "
                            + "by using the constants in the class QueueBased, like "
                            + "QueueBased.FIFO, QueueBased.LIFO or QueueBased.RANDOM.");
            return;
        }
        _ql.setQueueBased(this);

	}

	/**
	 * Sets the number of entities refused to be enqueued in the queue because
	 * the capacity limit is reached to a new value.
	 * 
	 * @param n
	 *            long : the new number of entities refused to be enqueued in
	 *            the queue because the capacity limit is reached.
	 */
	public void setRefused(long n) {
		// check if n is negative
		if (n < 0) {
			sendWarning(
					"Attempt to set the number of entities refused to enqueue in "
							+ "the ProcessQueue to a negative value. The attempted action "
							+ "is ignored!", "ProcessQueue : " + getName()
							+ " Method: void setRefused(long n)",
					"The number given as parameter n is negative! That makes no "
							+ "sense!",
					"Make sure to provide only positive numbers as parameter n.");
			return;
		}

		this._refused = n; // save the new value

	}

	/**
	 * Returns the sim-process enqueued directly after the given SimProcess in
	 * the process-queue. If the given SimProcess is not contained in this
	 * process-queue or is at the last position thus having no possible
	 * successor, <code>null</code> is returned.
	 * 
	 * @return desmoj.SimProcess : The sim-process directly after the given
	 *         SimProcess in the ProcessQueue or <code>null</code>
	 * @param e
	 *            desmoj.SimProcess : A sim-process in the process-queue
	 */
	public P succ(P e) {

		if (e == null) {
			sendWarning(
					"Can not find successor of SimProcess in Queue!",
					"ProcessQueue : " + getName()
							+ " Method: SimProcess succ(SimProcess e)",
					"The sim-process 'e' given as parameter is a null reference!",
					"Check to always have valid references when querying for "
							+ "Entities");
			return null; // no proper parameter
		}

		return _ql.succ(e);

	}

	/**
	 * Returns the sim-process enqueued after the given SimProcess in the
	 * process-queue that also complies to the condition given. If the given
	 * Sim-process is not contained in this process-queue or is at the last
	 * position thus having no possible successor, <code>null</code> is
	 * returned. If no other SimProcess after the given one complies to the
	 * condition, <code>null</code> is returned, too.
	 * 
	 * @return desmoj.SimProcess : The sim-process after the given SimProcess in
	 *         the process-queue complying to the condition or <code>null</code>.
	 * @param e
	 *            SimProcess : A sim-process in the process-queue
	 * @param c
	 *            Condition : The condition that the succeeding SimProcess has
	 *            to comply to
	 */
	public P succ(P e, Condition<P> c) {

		if (e == null) {
			sendWarning(
					"Can not find predecessor of SimProcess in Queue!",
					"ProcessQueue : "
							+ getName()
							+ " Method: SimProcess succ(SimProcess e, Condition c)",
					"The sim-process 'e' given as parameter is a null reference!",
					"Check to always have valid references when querying for Entities");
			return null; // no proper parameter
		}

		if (c == null) {
			sendWarning(
					"Can not return previous SimProcess complying to condition!",
					"ProcessQueue : "
							+ getName()
							+ " Method: SimProcess succ(SimProcess e, Condition c)",
					"The Condition 'c' given as parameter is a null reference!",
					"Check to always have valid references when querying Queues.");
			return null; // no proper parameter
		}

		for (P tmp = succ(e); tmp != null; tmp = succ(tmp)) {
			if (c.check(tmp))
				return tmp;
		}

		return null; // obviously not found here, empty or doesn't comply

	}
	
    /**
     * Returns an iterator over the processes enqueued.
     *
     * @return java.lang.Iterator&lt;P&gt; : An iterator over the processes enqueued.
     */
    public Iterator<P> iterator() {
        return new ProcessQueueIterator(this);
    }

    /**
     * Private queue iterator, e.g. required for processing all queue elements in a 
     * for-each-loop.
     */
    private class ProcessQueueIterator implements Iterator<P> {
        
        ProcessQueue<P> clientQ; 
        P next, lastReturned;
        
        public ProcessQueueIterator(ProcessQueue<P> clientQ) {
            this.clientQ = clientQ;
            next = clientQ.first();
            lastReturned = null;
        }
        public boolean hasNext() {
            return next != null;
        }
        public P next() {
            lastReturned = next;
            next = clientQ.succ(next);
            return lastReturned;
        }
        public void remove() {
            clientQ.remove(lastReturned);
        }
    }
}