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


package ui.results;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The statusbar under the main window. Based on http://forums.sun.com/thread.jspa?messageID=4278711.
 * @author Joris Gillis
 */
public class StatusBar extends JPanel {
	
	/** For showing the text */
	private JLabel label;
	/** Singleton pattern */
	private static StatusBar statusBar;
	
	
	/** The queue of all the statusmessages */
	private Queue<String> queue;
	
	/** Is there information being actively displayed? */
	private boolean displayed = false;
	
	/** Locking down the variables */
	private ReentrantLock lock = new ReentrantLock();
	
	/**
	 * Getting the instance.
	 * @return	get the only instance.
	 */
	public static StatusBar getInstance() {
		if(statusBar == null)
			statusBar = new StatusBar();
		return statusBar;
	}
	
	/**
	 * Constructing the StatusBar.
	 */
	private StatusBar() {
		//- Doing the GUI
		setPreferredSize(new Dimension(10, 23));
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH; c.anchor = GridBagConstraints.FIRST_LINE_START;
		
		label = new JLabel("");
		add(label, c);
		
		//- Data structures
		queue = new ConcurrentLinkedQueue<String>();
	}
	
	/**
	 * Setting the text of the status bar.
	 * @param text	text
	 */
	private void setText( String text ) {
		this.label.setText(text);
	}
	
	/*
	 * (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		int y = 0;
		g.setColor(new Color(156, 154, 140));
		g.drawLine(0, y, getWidth(), y);
		y++;
		g.setColor(new Color(196, 194, 183));
		g.drawLine(0, y, getWidth(), y);
		y++;
		g.setColor(new Color(218, 215, 201));
		g.drawLine(0, y, getWidth(), y);
		y++;
		g.setColor(new Color(233, 231, 217));
		g.drawLine(0, y, getWidth(), y);

		y = getHeight() - 3;
		g.setColor(new Color(233, 232, 218));
		g.drawLine(0, y, getWidth(), y);
		y++;
		g.setColor(new Color(233, 231, 216));
		g.drawLine(0, y, getWidth(), y);
		y = getHeight() - 1;
		g.setColor(new Color(221, 221, 220));
		g.drawLine(0, y, getWidth(), y);
	}
	
	
	/**
	 * Either sets the given status information or adds it to the queue. Each time
	 * a new piece of status information is added to the statusbar a timer is started
	 * which will check whether the queue is non-empty. If it is non-empty the head
	 * of the queue is displayed in the status bar, otherwise nothing is done.
	 * @param status	new piece of status information
	 */
	public void addStatus( String status ) {
		try {
			lock.lock();
			if( displayable() ) {
				display(status);
			} else
				queue.add(status);
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Doing the actual displaying.
	 * @param status	status information
	 */
	private synchronized void display( String status ) {
		// we are displaying!
		setDisplaying();
		
		// set the status information
		setText(status);
		
		// start a timer, which will display newly arrived information
		Timer checker = new Timer();
		checker.schedule(new checkQueue(), 1000);
	}
	
	
	/**
	 * Returns whether we can display information, or not.
	 * @return	true if we can display, false otherwise
	 */
	private synchronized boolean displayable() {
		return !displayed;
	}
	
	/**
	 * Sets that a piece of information is just being displayed, meaning that
	 * no other piece of information can be displayed until the timer ends and
	 * a new piece of information will be obtained from the queue.
	 */
	private synchronized void setDisplaying() {
		displayed = true;
	}
	
	/**
	 * Sets that a new piece of information can be displayed at any given time now.
	 */
	private synchronized void unsetDisplaying() {
		displayed = false;
	}
	
	
	
	/**
	 * The one that will check for newly arrived pieces of status information.
	 * @author Joris Gillis
	 */
	private class checkQueue extends TimerTask {
		
		/*
		 * (non-Javadoc)
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			try {
				lock.lock();
				if( queue.size() > 0 )
					display(queue.poll());
				else
					unsetDisplaying();
			} finally {
				lock.unlock();
			}
		}
	}
	
	
	/**
	 * The striped lines in the right-down corner of the window, used to adjust the size of the window.
	 * @author balachandran
	 */
	private class AngledLinesWindowsCornerIcon implements Icon {

		//RGB values discovered using ZoomIn
		private final Color WHITE_LINE_COLOR = new Color(255, 255, 255);
		private final Color GRAY_LINE_COLOR = new Color(172, 168, 153);

		//Dimensions
		private static final int WIDTH = 13;
		private static final int HEIGHT = 13;

		public int getIconHeight() {
			return WIDTH;
		}

		public int getIconWidth() {
			return HEIGHT;
		}

		public void paintIcon(Component c, Graphics g, int x, int y) {

			g.setColor(WHITE_LINE_COLOR);
			g.drawLine(0,12, 12,0);
			g.drawLine(5,12, 12,5);
			g.drawLine(10,12, 12,10);

			g.setColor(GRAY_LINE_COLOR);
			g.drawLine(1,12, 12,1);
			g.drawLine(2,12, 12,2);
			g.drawLine(3,12, 12,3);

			g.drawLine(6,12, 12,6);
			g.drawLine(7,12, 12,7);
			g.drawLine(8,12, 12,8);

			g.drawLine(11,12, 12,11);
			g.drawLine(12,12, 12,12);
		}
	}
}
