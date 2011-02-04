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


package ui;

import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Provides the basic warning mechanismes used in all the windows and panels.
 * @author Joris Gillis
 */
public class Window extends JFrame {
	
	private static final long serialVersionUID = -1030336475457432617L;

	public Window() throws HeadlessException {
		super();
	}
	
	public Window(GraphicsConfiguration gc) {
		super(gc);
	}
	
	public Window(String title) throws HeadlessException {
		super(title);
	}
	
	public Window(String title, GraphicsConfiguration gc) {
		super(title, gc);
	}
	
	/**
	 * Show a warning message.
	 * @param message	message to be shown
	 */
	public void showWarning(String message) {
		JOptionPane.showMessageDialog(this, message, "Warning", JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * Show a warning message.
	 * @param e		exception containing the errormessage
	 */
	public void showWarning(Exception e) {
		JOptionPane.showMessageDialog(this, "A fault occurred: \n"+ e.getMessage(), "Warning", JOptionPane.WARNING_MESSAGE);
	}

}