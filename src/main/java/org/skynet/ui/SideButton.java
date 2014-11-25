package org.skynet.ui;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class SideButton extends JButton {
	public static int width = 200;
	public static int height = 50;

	private void _init(){
		//this.setSize(200, 50);
		this.setMinimumSize(new Dimension(width,height));
		this.setMaximumSize(new Dimension(width,height));
	}
	public SideButton(String name){
		super(name);
		_init();
	}
	public SideButton(Icon image){
		this.setIcon(image);
		_init();
	}
	public SideButton(String name,Icon image){
		super(name);
		this.setIcon(image);
		_init();
	}
}
