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