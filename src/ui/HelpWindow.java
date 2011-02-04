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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;

/**
 * Window containing all the help information.
 * @author Joris Gillis
 */
@SuppressWarnings("serial")
public class HelpWindow extends JFrame {
	
	/**
	 * Construct a new HelpWindow.
	 */
	public HelpWindow() {
		// Setting the title
		super("MSPMeter - Help");
		
		// layouting
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1; c.weighty = 1; c.fill = GridBagConstraints.BOTH;
		
		// components
		JTree topics = new JTree();
		JEditorPane text = new JEditorPane();
		text.setEditable(false);
		JScrollPane editorPane = new JScrollPane(text);
		
		topics.setPreferredSize(new Dimension(175, 450));
		editorPane.setPreferredSize(new Dimension(500, 550));
		
		
		// doing the layout
		c.gridx = 0; c.gridy = 0;
		add( topics, c );
		c.gridx = 1; c.weightx = 3;
		add( editorPane, c );
		
		// framing stuff
		pack();
	}
	
}
