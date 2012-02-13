package org.opt4j.viewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;


/**
 * The {@link AutoZoomButton} is a specialized {@link JButton} which reactivates
 * the auto zoom feature of a {@link PlotBox}. If the auto zoom feature is
 * enabled, the button is disabled.
 * 
 * @author reimann
 * 
 */
public class AutoZoomButton extends JButton implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a new {@link AutoZoomButton}.
	 */
	public AutoZoomButton() {
		addActionListener(this);
		setEnabled(false);
		setToolTipText("Auto Zoom");
		setPreferredSize(getMinimumSize());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}




}
