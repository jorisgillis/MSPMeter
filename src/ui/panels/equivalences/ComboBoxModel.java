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


package ui.panels.equivalences;

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

@SuppressWarnings("serial")
public class ComboBoxModel extends DefaultComboBoxModel {
	
	/** the elements: sorted */
	protected Vector<String> elements;
	
	/**
	 * Construct a new model.
	 */
	public ComboBoxModel() {
		super();
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.DefaultComboBoxModel#addElement(java.lang.Object)
	 */
	public void addElement(Object element) {
		insertElementAt(element, 0);
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.DefaultComboBoxModel#insertElementAt(java.lang.Object, int)
	 */
	public void insertElementAt(Object element, int index) {
		int size = getSize();
		
		//  Determine where to insert element to keep list in sorted order
		for (index = 0; index < size; index++) {
			Comparable c = (Comparable)getElementAt( index );
			
			if (c.compareTo(element) > 0)
				break;
		}
		
		super.insertElementAt(element, index);
	}
}
