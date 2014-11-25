package org.skynet.utils;

import java.awt.Point;
import java.io.Serializable;
/*
 * C'est le classe qui sera utilise pour toutes les coordonnees du reseau
 * Autrement dit, si quelque part vous voyez le "_Point", alors se sont les valeurs normalises
 * Si vous voyez "Point" le plus probable que ce sont les coordonnees en pixels
 */
public class _Point implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1166940453317036097L;
	public float x = 0;
	public float y = 0;
	
	public _Point(){
	}
	public _Point(Point point){
		this.x=point.x;
		this.y=point.y;
	}
	public _Point(float x, float y){
		this.x=x;
		this.y=y;
	}
	public Point toPoint(){
		return new Point((int)x,(int)y);
	}
	@Override
	public String toString(){
		return "org.skynet.model._Point[x="+x+",y="+y+"]";
	}
}
