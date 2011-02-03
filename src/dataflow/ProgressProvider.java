package dataflow;

import ui.results.ProgressController;

/**
 * The one providing all the progress information
 * @author Joris Gillis
 *
 */
public interface ProgressProvider {
	
	/**
	 * Adds a new progress controller to the list of controllers.
	 * @param controller	the new controllers
	 */
	public abstract void addProgressController(ProgressController controller);
	
	/**
	 * Makes the controllers start progressing.
	 */
	public abstract void startProgressing();
	
	/**
	 * Makes the controllers stop progressing.
	 */
	public abstract void stopProgressing();
	
}