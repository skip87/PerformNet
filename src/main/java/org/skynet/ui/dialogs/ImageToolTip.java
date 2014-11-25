package org.skynet.ui.dialogs;

import java.awt.image.BufferedImage;

import javax.swing.JToolTip;

public class ImageToolTip extends JToolTip {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ImageTooltipUI ui;
	public void setImage(BufferedImage image){
		ui.setImage(image);
	}
	public ImageToolTip(BufferedImage image) {
		 setUI(ui=new ImageTooltipUI(image));
	}
}
