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


package ui.panels.version;

import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * A slider and a textfield. The slider reacts to changes of the textfield and
 * vice versa. 
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class JTextSlider extends JPanel implements ChangeListener, DocumentListener {
	
	/** The slider */
	protected JSlider slider;
	/** The textfield */
	protected JTextField text;
	
	
	/** Minimum value of the range */
	protected int min;
	/** Maximum value of the range */
	protected int max;
	/** Default value of the slider */
	protected int d;
	/** Scaling factor */
	protected double scale;
	
	/** All the listeners */
	protected List<ChangeListener> changeListeners = new LinkedList<ChangeListener>();
	
	
	/**
	 * Creates a new JTextSlider with default range of [1,1].
	 */
	public JTextSlider() {
		this(1, 1);
	}
	
	/**
	 * Creates a non-default JTextSlider with a given range [min, max].
	 * @param min	start point of the interval
	 * @param max	end point of the interval
	 */
	public JTextSlider( int min, int max ) {
		this(min, max, (int)(((double)min+max)/2));
	}
	
	/**
	 * Creates a non-default JTextSlider with the given range [min, max] and
	 * default value d.
	 * @param min	start point of the interval
	 * @param max	end point of the interval
	 * @param d		default
	 */
	public JTextSlider( int min, int max, int d ) {
		this(min, max, d, 1.0);
	}
	
	/**
	 * Creates a non-defaiult JTextSlider with the given range [min, max],
	 * a default value d and a scaling factor.
	 * @param min	start point of the interval
	 * @param max	end point of the interval
	 * @param d		default value
	 * @param scale	scaling factor
	 */
	public JTextSlider( int min, int max, int d, double scale ) {
		super();
		
		// saving the variables
		this.min = min;
		this.max = max;
		this.d = d;
		this.scale = scale;
		
		// Creating the widgets
		slider = new JSlider(min, max);
		slider.setValue(d);
		text = new JTextField(""+ (d/scale), 7);
		
		// listening to changes
		slider.addChangeListener(this);
		text.getDocument().addDocumentListener(this);
		
		// displaying the lot
		this.setLayout(new BorderLayout());
		this.add(text, BorderLayout.EAST);
		this.add(slider, BorderLayout.WEST);
	}
	
	/**
	 * Returns the value.
	 * @return	value
	 */
	public synchronized double getValue() {
		return getSliderValue();
	}
	
	/**
	 * Sets the value.
	 * @param value	new value
	 */
	public synchronized void setValue( double value ) {
		slider.setValue((int)(value*scale));
	}
	
	/**
	 * Returns the value of the slider.
	 * @return	value of the slider
	 */
	private synchronized double getSliderValue() {
		return slider.getValue() / scale;
	}
	
	/**
	 * Returns the value of the textfield.
	 * @return	value of the textfield
	 */
	private synchronized double getTextValue() {
		if( text.getText().equals("") )
			return 0.0;
		else
			return Double.parseDouble(text.getText());
	}
	
	/**
	 * Sets the minimum of the range.
	 * @param min	minimum of the range
	 */
	public synchronized void setMinimum( int min ) {
		this.min = min;
		slider.setMinimum(min);
	}
	
	/**
	 * Sets the maximum of the range.
	 * @param max the max to set
	 */
	public synchronized void setMaximum( int max ) {
		this.max = max;
		slider.setMaximum(max);
	}
	
	/**
	 * Sets the default value
	 * @param d the default
	 */
	public synchronized void setDefault( int d ) {
		this.d = d;
		slider.setValue(d);
	}
	
	/**
	 * Sets the scale of the values.
	 * @param scale the scale to set
	 */
	public synchronized void setScale( double scale ) {
		this.scale = scale;
	}
	
	
	/**
	 * Checks whether the given double is within the expected bounds.
	 * @param d	double
	 * @return	within bounds?
	 */
	private boolean withinBounds( double d ) {
		return min <= d && d <= max;
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		slider.setEnabled(enabled);
		text.setEnabled(enabled);
	}
	
	
	//===========================================================================
	// Reactional
	//===========================================================================
	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public synchronized void stateChanged(ChangeEvent e) {
		if( unequal(getTextValue(), getSliderValue()) )
			text.setText("" + getSliderValue());
		notifyChangeListeners();
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	public synchronized void changedUpdate(DocumentEvent e) {
		try {
			if( unequal(getTextValue(), getSliderValue()) && withinBounds(getTextValue() * scale) )
				slider.setValue((int)(getTextValue()*scale));
			notifyChangeListeners();
		} catch( NumberFormatException exception ) {
			// when the textfield does not contain a valid double.
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	public synchronized void insertUpdate(DocumentEvent e) {
		changedUpdate(e);
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	public synchronized void removeUpdate(DocumentEvent e) {
		changedUpdate(e);
	}
	
	/**
	 * Adds a changeListener to the list of listeners
	 * @param c new changeListener
	 */
	public void addChangeListener( ChangeListener c ) {
		changeListeners.add(c);
	}
	
	/**
	 * Notifies the ChangeListeners of a change
	 */
	private void notifyChangeListeners() {
		for( ChangeListener c : changeListeners )
			c.stateChanged( new ChangeEvent(this) );
	}
	
	
	/**
	 * Are both doubles within close enough range to be equal?
	 * @param d1	double 1
	 * @param d2	double 2
	 * @return		equal?
	 */
	private boolean equal( double d1, double d2 ) {
		return Math.abs(d1 - d2) < 0.0000001;
	}
	
	/**
	 * Are both doubles not equal?
	 * @param d1	double 1
	 * @param d2	double 2
	 * @return		unequal?
	 */
	private boolean unequal( double d1, double d2 ) {
		return !equal(d1, d2);
	}
}
