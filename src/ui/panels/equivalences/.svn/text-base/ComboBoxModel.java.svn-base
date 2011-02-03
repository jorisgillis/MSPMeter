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
