package org.skynet.utils;

import java.awt.Point;

public class Transform {
	public static java.awt.Rectangle Rectangle(Point start, Point end){
		return new java.awt.Rectangle(
				Math.min(start.x,end.x)
				,Math.min(start.y,end.y)
				,Math.abs(end.x-start.x)
				,Math.abs(end.y-start.y)
		);
	}
	public static String time(float time){
		/*
		LinkedList<java.util.AbstractMap.SimpleEntry<Integer,String>> time_transform = new LinkedList<>();
		time_transform.add(new SimpleEntry<Integer, String>(60,"sec"));
		time_transform.add(new SimpleEntry<Integer, String>(60,"min"));
		*/
		int seconds = Math.round(time * 3600);
		String out = (seconds%60)+"";
		seconds/=60; // now we have minutes
		if(seconds>0){
			out += "s"; // we store short notation for seconds
			out = (seconds%60)+"m " + out;
			seconds /= 60;
			if(seconds>0){
				out = (seconds%24)+"h " + out;
				seconds /= 24;
				if(seconds>0){
					out = (seconds)+"j " + out;
				}
			}
		} else {
			out += "sec"; // we use long notation
		}
		return out;
	}
}
