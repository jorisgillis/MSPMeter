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
