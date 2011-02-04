/*
 * Copyright 2010 MSPMeter
 *
 * Licensed under the EUPL, Version 1.1 or Ð as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl.html
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package dataflow;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import msp.RestrictionViolation;
import msp.data.DataFaultException;
import msp.data.ImpossibleCalculationException;

import org.apache.log4j.Logger;

import ui.results.ProgressController;
import dataflow.datastructures.Cell;

/**
 * The Grid, this class contains all the cells and ensures that the values of the
 * the cells are updated according to the grid. 
 * @author Joris Gillis
 */
public class Grid implements ProgressProvider {
	
	/** The collection of cells. */
	protected HashMap<String, Cell> cells = new HashMap<String, Cell>();
	/** The flows between the cells. */
	protected HashMap<String, Vector<String>> flows = new HashMap<String, Vector<String>>();
	/** Children of cells. */
	protected HashMap<String, Vector<String>> children = new HashMap<String, Vector<String>>();
	/** Keeps track of the closure of the flows */
	protected HashMap<String, Vector<String>> closure = new HashMap<String, Vector<String>>();
	
	/** The collection of cells that need to be updated instanteously if a new
	 * value arrives to one of its predecessors. */
	protected HashMap<String, Cell> hotCells = new HashMap<String, Cell>();
	/** Set of all cells that need to be propagated immediately. */
	protected Set<String> hotPredecessor = new HashSet<String>(); 
	
	/** Has something been changed? */
	protected boolean changed = false;
	
	/** The singleton instance of the Grid. */
	protected static Grid instance = null;
	
	
	/** Which cells have to be recalculated? */
	protected PriorityBlockingQueue<String> cellQueue = null;
	/** Which cells have to be recalculated right now? */
	protected PriorityBlockingQueue<String> hotCellQueue = null;
	
	/** List of objects monitoring the progress. */
	protected Vector<ProgressController> controllers = new Vector<ProgressController>();
	
	/** Lock to prevent two threads to recompute on the same moment. */
	protected ReentrantLock calculationLock = new ReentrantLock( true );
	
	// Logger
	private static Logger logger = Logger.getLogger(Grid.class);
	
	
	//===========================================================================
	// INSTANCE MANAGEMENT
	//===========================================================================
	/**
	 * Construct a new Grid.
	 */
	private Grid() {
	}
	
	/**
	 * Returns the only existing instance of the Grid.
	 * @return	singleton Grid
	 */
	public static Grid instance() {
		if( instance == null )
			instance = new Grid();
		return instance;
	}
	
	
	//===========================================================================
	// PROGRESS MONITORING
	//===========================================================================
	/* (non-Javadoc)
	 * @see dataflow.ProgressProvider#addProgressController(ui.results.ProgressController)
	 */
	public void addProgressController( ProgressController controller ) {
		if( !controllers.contains(controller) )
			controllers.add(controller);
	}
	
	/* (non-Javadoc)
	 * @see dataflow.ProgressProvider#startProgressing()
	 */
	public void startProgressing() {
		for( int i = 0; i < controllers.size(); i++ ) 
			controllers.get(i).startProgressing();
	}
	
	/* (non-Javadoc)
	 * @see dataflow.ProgressProvider#stopProgressing()
	 */
	public void stopProgressing() {
		for( int i = 0; i < controllers.size(); i++ )
			controllers.get(i).stopProgressing();
	}
	
	
	//===========================================================================
	// BUILDING NETWORK STRUCTURE
	//===========================================================================
	/**
	 * Adds a cell the grid with the given name.
	 * @param name	name of the cell
	 * @param cell	the cell
	 */
	public void addCell( Cell cell ) {
		if( !isCellPresent(cell.getName()) ) 
			cells.put(cell.getName(), cell);
	}
	
	/**
	 * Adds a hot cell to the grid. The name of the cell is 
	 * inside the cell. 
	 * @param cell	the cell
	 */
	public void addHotCell( Cell cell ) {
		if( !isCellPresent(cell.getName()) ) {
			hotCells.put(cell.getName(), cell);
			hotPredecessor.add(cell.getName());
		}
	}
	
	/**
	 * Adds a flow between the source and the sink. This means that when the source
	 * changes, the sink will be notified.
	 * @param source	the source of the flow
	 * @param sink		the sink of the flow
	 */
	public void setFlow( String source, String sink ) {
		if( isCellPresent(source) && isCellPresent(sink) ) {
			// adding the sink to the successors
			if( !flows.containsKey(source) )
				flows.put(source, new Vector<String>());
			flows.get(source).add(sink);
			
			// adding the source to the children
			if( !children.containsKey(sink) )
				children.put(sink, new Vector<String>());
			children.get(sink).add(source);
			
			// updating closure
			updateClosure( sink, null );
			
			// updating hotPredecessors
			updateHotPredecessors();
		}
	}
	
	/**
	 * Updates the closure data structure based on the list of descendants.
	 * @param anc			ancestor
	 * @param descendants	list of all the descendants
	 */
	private void updateClosure( String anc, Vector<String> descendants ) {
		// add the current node to the list of descendants, thus first copy it, or make a new vector
		Vector<String> descs;
		
		if( descendants != null ) {
			descs = new Vector<String>(descendants);
			
			// update the hashmap
			if( !closure.containsKey(anc) )
				closure.put(anc, new Vector<String>());
			
			for( int i = 0; i < descendants.size(); i++ )
				if( !closure.get(anc).contains(descendants.get(i)) )
					closure.get(anc).add(descendants.get(i));
		} else
			descs = new Vector<String>();
		
		// add the current node to the descendants list
		descs.add(anc);
		
		// getting the ancestors and continuing along
		Vector<String> ancs = getAncestors(anc);
		for( int i = 0; i < ancs.size(); i++ )
			updateClosure( ancs.get(i), descs );
	}
	
	/**
	 * Updates the hot predecessors, by looking at the ancestors of
	 * all the cells. 
	 */
	private void updateHotPredecessors() {
		//- 1. Cells
		for( String cell : cells.keySet() ) {
			Vector<String> successors = closure.get(cell);
			if( successors != null ) 
				for( String successor : successors )
					if( isHot( successor ) ) {
						hotPredecessor.add(cell);
						break;
					}
		}
		
		//- 2. Hot Cells can be hot predecessors as well!
		for( String cell : hotCells.keySet() ) {
			Vector<String> successors = closure.get(cell);
			if( successors != null )
				for( String successor : successors )
					if( isHot( successor ) ) {
						hotPredecessor.add(cell);
						break;
					}
		}
	}
	
	//===========================================================================
	// QUERYING NETWORK STRUCTURE
	//===========================================================================
	/**
	 * Checks if the given cell (identified by its name) is in the grid.  
	 * @param cell	cell
	 * @return		in the grid?
	 */
	protected boolean isCellPresent( String cell ) {
		return cells.containsKey( cell ) || hotCells.containsKey( cell );
	}
	
	/**
	 * Checks if the given cell (identified by its name) is HOT.
	 */
	protected boolean isHot( String cell ) {
		return hotCells.containsKey(cell);
	}
	
	/**
	 * Returns true if the first cell is an ancestor of the second cell. 
	 * (Second cell is a predecessor of the first cell?)
	 * @param c1	first cell
	 * @param c2	second cell
	 * @return		first a ancestor of the second
	 */
	public boolean ancestor( String c1, String c2 ) {
		boolean v = closure.containsKey(c1) && 
				closure.get(c1) != null && 
				closure.get(c1).contains(c2);
		return v;
	}
	
	/**
	 * Finds all the ancestors of a given cell in the flow graph.
	 * @param cellName	the given cell
	 * @return			the list of all ancestors
	 */
	private Vector<String> getAncestors( String cellName ) {
		Vector<String> ancs = new Vector<String>();
		
		Iterator<String> it = flows.keySet().iterator();
		while( it.hasNext() ) {
			String anc = it.next();
			if( flows.get(anc).contains(cellName) )
				ancs.add(anc);
		}
		
		return ancs;
	}
	
	/**
	 * Returns the list of children of the given cell. 
	 */
	private Vector<String> getChildren( String cellName ) {
		return children.get( cellName );
	}
	
	/**
	 * All changes have been saved.
	 */
	public synchronized void saved() {
		changed = false;
	}
	
	/**
	 * Are there changes that are not saved.
	 * @return changes or not?
	 */
	public synchronized boolean changes() {
		return changed;
	}
	
	
	//===========================================================================
	// CHANGING CELLS
	//===========================================================================
	/**
	 * When a cell changes, data begins to flow through the grid.
	 * @param changedCells	array of cells that were changed
	 */
	public void cellChanged( Cell[] changedCells )
		throws DataFaultException, ImpossibleCalculationException, RestrictionViolation {
		// Logging
		logger.debug("Some cells have changed: ");
		for( Cell c : changedCells )
			logger.debug("\t"+ c.getName());
		
		// If the queues do not exist yet
		if( cellQueue == null ) {
			cellQueue = new PriorityBlockingQueue<String>(25, new CellComparator());
			hotCellQueue = new PriorityBlockingQueue<String>(25, new CellComparator());
		}
		
		//- 1. filling the queues
		for( Cell cell : changedCells ) {
			Vector<String> descendants = closure.get(cell.getName());
			if( hotPredecessor.contains(cell.getName()) ) {
				//- a. Hot Predecessor:
				//- All ancestor that are hot predecessors are added to 
				//- the hot queue
				//- All ancestors that are not hot predecessors are added
				//- to the normal queue
				for( String descendant : descendants ) {
					if( hotPredecessor.contains(descendant) )
						hotSchedule(descendant);
					else
						schedule(descendant);
				}
			} else {
				//- b. A normal recalculation
				//- All ancestors are added
				for( String descendant : descendants )
					schedule( descendant );
			}
		}
		
		//- 2. if the hotCellQueue is not empty, it has to be emptied.
		if( !hotCellQueue.isEmpty() ) {
			FlowThread thread = new FlowThread(hotCellQueue);
			thread.start();
		}
	}
	
	/**
	 * Schedules the immediate recalculation of the given cell.
	 * @param cellName	the name of the cell 
	 */
	private void hotSchedule(String cellName) {
		if( !hotCellQueue.contains(cellName) )
			hotCellQueue.add(cellName);
	}
	
	/**
	 * Schedules the recalculation of the given cell. 
	 * @param cellName	the cell
	 */
	private void schedule(String cellName) {
		if( !cellQueue.contains(cellName) )
			cellQueue.add(cellName);
	}
	
	/**
	 * Getting a cell out.
	 * @param name	name of the cell
	 * @return	the requested cell
	 */
	public Cell getCell( String name ) {
		if( cells.containsKey(name) )
			return cells.get(name);
		else 
			return hotCells.get(name);
	}
	
	/**
	 * Starts scheduled recalculations. 
	 */
	public void recalculate() {
		CompleteCalculation thread = new CompleteCalculation();
		thread.start();
	}
	
	
	/**
	 * Goes about recalculating all the cells that are in a queue. 
	 * @author joris
	 */
	private class CompleteCalculation extends Thread {
		public void run() {
			logger.info("Doing complete calculation");
			// first the hot ones
			FlowThread thread = new FlowThread( hotCellQueue );
			thread.run();
			
			// then the rest
			thread = new FlowThread( cellQueue );
			thread.run();
		}
	}
	
	/**
	 * Doing the flowing!
	 * @author Joris Gillis
	 */
	private class FlowThread extends Thread {
		/** Queue that needs to be emptied. */
		protected PriorityBlockingQueue<String> queue;
		
		/**
		 * Constructs a new thread that will empty the given queue. 
		 * @param queue	queue to empty
		 */
		public FlowThread( PriorityBlockingQueue<String> queue ) {
			this.queue = queue;
			changed = true;
		}
		
		/*
		 * (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			flowing();
		}
		
		/**
		 * Make the flow!
		 */
		private void flowing() {
			logger.info("Acquiring lock");
			calculationLock.lock();
			
			try {
				startProgressing();
				logger.info("Starting calculations");
				
				while( !queue.isEmpty() ) {
					try {
						// can we get a cellName?
						// If no cellName is available the function blocks and waits for a new one.
						String cellName = queue.take();
						logger.info("Recalculate: "+ cellName);
						
						// work it!
						Vector<String> children = getChildren(cellName);
						Cell c = getCell(cellName);
						c.recalculate( children );
					} catch( Exception e ) {
						// TODO handling exceptions!
						logger.error("Exception in calculation");
						logger.error(e);
					}
				}
				
				// queue is empty! Shutdown the Progress Monitor
				stopProgressing();
				logger.info("Stopping calculations");
			} catch( Exception e ) {
				logger.error( e );
			} finally {
				logger.info("Releasing lock");
				calculationLock.unlock();
			}
		}
	}
}
