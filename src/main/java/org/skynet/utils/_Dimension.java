package org.skynet.utils;

import java.awt.geom.Dimension2D;
import java.io.Serializable;

public class _Dimension extends Dimension2D implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3585010956394234476L;
	private double width;
	private double height;
	
	public _Dimension(){
	}
	public _Dimension(double width,double height){
		setSize(width,height);
	}
	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public void setSize(double width, double height) {
		this.width=width;
		this.height=height;
	}

}
