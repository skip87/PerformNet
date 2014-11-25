package org.skynet.ui.dialogs;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalToolTipUI;

public class ImageTooltipUI extends MetalToolTipUI {
	Image image;
	public void setImage(BufferedImage image){
		this.image = image;
		int width = image.getWidth();
		int height = image.getHeight();
		if(width>500 || height>500){
			float a = Math.min(500.0f/width, 500.0f/height);
			this.image = image.getScaledInstance((int)(a*width), (int)(a*height), Image.SCALE_SMOOTH);
		}
	}
	public ImageTooltipUI(BufferedImage image){
		setImage(image);
	}
	public void paint(Graphics g, JComponent c) {
		FontMetrics metrics = c.getFontMetrics(g.getFont());
		g.setColor(c.getForeground());
		g.drawString(((JToolTip) c).getTipText(), 1, 1);
		g.drawImage(image, 1, metrics.getHeight(), c);
	}

	public Dimension getPreferredSize(JComponent c) {
		FontMetrics metrics = c.getFontMetrics(c.getFont());
		String tipText = ((JToolTip) c).getTipText();
		if (tipText == null) {
			tipText = "";
		}
		int width = SwingUtilities.computeStringWidth(metrics, tipText);
		int height = metrics.getHeight() + image.getHeight(c);

		if (width < image.getWidth(c)) {
			width = image.getWidth(c);
		}
		return new Dimension(width, height);
	}
}
