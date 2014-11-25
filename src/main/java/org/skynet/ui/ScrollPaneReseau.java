package org.skynet.ui;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JScrollPane;

public class ScrollPaneReseau extends JScrollPane implements MouseWheelListener {
	boolean listenerCalled = false;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ScrollPaneReseau(){
		super();
		this.setWheelScrollingEnabled(true);
		this.addMouseWheelListener(this);
		
	}
	
	@Override
	protected void processMouseWheelEvent(MouseWheelEvent e) {
		listenerCalled = true;
		if(!e.isControlDown()){
			super.processMouseWheelEvent(e);
		} else {
		}
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(!listenerCalled){
			listenerCalled = true;
			processMouseWheelEvent(e);
		}
		listenerCalled = false;
	}
}
