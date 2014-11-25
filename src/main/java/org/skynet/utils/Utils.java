package org.skynet.utils;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import org.skynet.model.ControlleurReseau;
import org.skynet.reseau.Noeud;


public class Utils {
	static public float round(float num){
		return (float)Math.round( num*10 ) / 10;
	}
	static public double round(double num){
		return (float)Math.round( num*10 ) / 10;
	}
	static public String format(float num){
		num = Utils.round(num);
		// Detection si c'est une chiffre integrale
		if((int)(num*10) == (int)num*10){
			return String.format("%d", (int)num);
		}
		return String.format("%.1f", num);
	}
	static public String format2(float num){
		if((int)(num*100) == (int)num*100){
			return String.format("%2d", (int)num);
		}
		return String.format("%.2f", num);
	}
	static public int factoriel(int num){
		int numFact = 1;
		
		for(int count = 1; count <= num; count++){
			numFact *= count;
		}
		
		return numFact;
	}
    static public Point2D getIntersectionPoint(Line2D l1, Line2D l2) {
    	return getIntersectionPoint(l1, l2, false);
    }
    static public Point2D getIntersectionPoint(Line2D l1, Line2D l2, boolean debug) {
    	if(debug){
    		System.out.println("Line 1: " + l1.getX1() + ", "+l1.getY1()+", "+l1.getX2()+", "+l1.getY2());
    		System.out.println("Line 2: " + l2.getX1() + ", "+l2.getY1()+", "+l2.getX2()+", "+l2.getY2());
    	}
    	if (! l1.intersectsLine(l2) ) return null;
			double p1x = l1.getX1(),
					p1y = l1.getY1(),
					r1x = l1.getX2()-p1x,
					r1y = l1.getY2()-p1y;
			double p2x = l2.getX1(),
					p2y = l2.getY1(),
					r2x = l2.getX2()-p2x,
					r2y = l2.getY2()-p2y;
	
			double det = r2x*r1y - r2y*r1x;
		if (det == 0) {
			return null;
		}
		double z = (r2x*(p2y-p1y)+r2y*(p1x-p2x))/det;
		if (z==0 ||  z==1) return null;  // intersection at end point!
		return new Point2D.Float((float)(p1x+z*r1x), (float)(p1y+z*r1y));
	}
    static public Point2D getIntersectionPoint(Line2D line, Rectangle2D rect){
		return getIntersectionPoint(line,rect,false);
	}
    static public Point2D getIntersectionPoint(Line2D line, Rectangle2D rect, boolean debug){
		Point2D point = null;
		LinkedList<Point2D> foundPoints = new LinkedList<>();
		/*if(
				(point=getIntersectionPoint(line,new Line2D.Double(rect.getX(),rect.getY(),rect.getX()+rect.getWidth(),rect.getY()),debug))==null // TOP
				&& (point=getIntersectionPoint(line,new Line2D.Double(rect.getX(),rect.getY(),rect.getX(),rect.getY()+rect.getHeight()),debug))==null // LEFT
				&& (point=getIntersectionPoint(line,new Line2D.Double(rect.getX()+rect.getWidth(),rect.getY(),rect.getX()+rect.getWidth(),rect.getY()+rect.getHeight()),debug))==null // RIGHT
				&& (point=getIntersectionPoint(line,new Line2D.Double(rect.getX(),rect.getY()+rect.getHeight(),rect.getX()+rect.getWidth(),rect.getY()+rect.getHeight()),debug))==null // BOTTOM
			);
		*/
		if(
			(point=getIntersectionPoint(line,new Line2D.Double(rect.getX(),rect.getY(),rect.getX()+rect.getWidth(),rect.getY()),debug))!=null // TOP
		) foundPoints.add(point);
		if( 
			(point=getIntersectionPoint(line,new Line2D.Double(rect.getX(),rect.getY(),rect.getX(),rect.getY()+rect.getHeight()),debug))!=null // LEFT
		) foundPoints.add(point);
		if(
				(point=getIntersectionPoint(line,new Line2D.Double(rect.getX()+rect.getWidth(),rect.getY(),rect.getX()+rect.getWidth(),rect.getY()+rect.getHeight()),debug))!=null // RIGHT
		)  foundPoints.add(point);
		if(
			(point=getIntersectionPoint(line,new Line2D.Double(rect.getX(),rect.getY()+rect.getHeight(),rect.getX()+rect.getWidth(),rect.getY()+rect.getHeight()),debug))!=null // BOTTOM
		)  foundPoints.add(point);
		if(foundPoints.size()==1)
			return foundPoints.get(0);
		if(foundPoints.size()==0)
			return null;
		double size = Float.MAX_VALUE;
		for(Point2D point2 : foundPoints){
			double newSize = Math.pow(point2.getX()-line.getX1(),2) + Math.pow(point2.getY()-line.getY1(),2);
			if(newSize<size){
				point = point2;
				size=newSize;
			}
		}
		return point;
	}
	static public _Point calculerOffsetTexte(Graphics g,String texte,double angleNormale){
		FontMetrics fontMetrics = g.getFontMetrics();
		int rectRegion = 0;
		if(angleNormale>=135 && angleNormale<225){
			rectRegion = 2;
		} else if(angleNormale>=225 && angleNormale<315) {
			rectRegion = 3;
		} else if(angleNormale>=45 && angleNormale<135) {
			rectRegion = 1;
		}
		
		double tx2 = 0, ty2 = 0;
		double mulValue = 0.707106781;
		switch(rectRegion){
		case 2:
			tx2 = -1;
			ty2 = -(Math.sin(Math.toRadians(angleNormale))*mulValue + .5);
			//System.out.println(ty2);
			break;
		case 1:
			{
				ty2 = -1;
				tx2 = (Math.cos(Math.toRadians(angleNormale)))*mulValue - .5;
			}
			break;
		case 0:
			tx2 = 0;
			ty2 = -(Math.sin(Math.toRadians(angleNormale))*mulValue + .5);
			break;
		case 3:
			{
				ty2 = 0;
				tx2 = Math.cos(Math.toRadians(angleNormale))*mulValue - .5;//(Math.cos(Math.toRadians(angleNormale))-min)/l;
			}
			break;
		}
		
		tx2 *= fontMetrics.stringWidth(texte);
		ty2 *= fontMetrics.getHeight();
		return new _Point((float)tx2,(float)ty2);
	}
	public static _Point[] calculerCoordonnees(List<Rectangle2D> rects1, List<Rectangle2D> rects2, _Point center1, _Point center2){
		_Point [] new_coord = new _Point[2];
		float x1 = center1.x//(int)(rect1.getX()+rect1.getWidth()/2)
				, y1 = center1.y//(int) (rect1.getY()+rect1.getHeight()/2)
				, x2 = center2.x//(int)(rect2.getX()+rect2.getWidth()/2)
				, y2 = center2.y;//(int) (rect2.getY()+rect2.getHeight()/2);
			
		Line2D line = new Line2D.Float(x1,y1,x2,y2);
		Line2D line_inverse = new Line2D.Float(x2,y2,x1,y1);

		float size = (x2-x1)*(x2-x1) + (y2-y1)*(y2-y1);
		for(Rectangle2D rect1 : rects1){
			if(rect1.getWidth()>0 && rect1.getHeight()>0){
				Point2D pt1 = Utils.getIntersectionPoint(line_inverse,rect1);
				if(pt1!=null){
					float size2 = (float)((x2-pt1.getX())*(x2-pt1.getX()) + (y2-pt1.getY())*(y2-pt1.getY()));
					if(size2<size){
						x1 = (float)pt1.getX();
						y1 = (float)pt1.getY();
						size = size2;
					}
				}
			}
		}
		

		for(Rectangle2D rect2 : rects2){
			if(rect2.getWidth()>0 && rect2.getHeight()>0){
				Point2D pt2 = Utils.getIntersectionPoint(line,rect2);
		
				if(pt2!=null){
					float size2 = (float)((x1-pt2.getX())*(x1-pt2.getX()) + (y1-pt2.getY())*(y1-pt2.getY()));
					if(size2<size){
						x2 = (float)pt2.getX();
						y2 = (float)pt2.getY();
						size = size2;
					}
				}
			}
		}
		new_coord[0] = new _Point(x1,y1);
		new_coord[1] = new _Point(x2,y2);
		return new_coord;
	}
	// TODO: remove this one, cause it should no longer be used
	public static _Point[] calculerCoordonnees(Rectangle2D rect1, Rectangle2D rect2, _Point center1, _Point center2){
		_Point [] new_coord = new _Point[2];
		float x1 = center1.x//(int)(rect1.getX()+rect1.getWidth()/2)
				, y1 = center1.y//(int) (rect1.getY()+rect1.getHeight()/2)
				, x2 = center2.x//(int)(rect2.getX()+rect2.getWidth()/2)
				, y2 = center2.y;//(int) (rect2.getY()+rect2.getHeight()/2);
			
		Line2D line = new Line2D.Float(x1,y1,x2,y2);
		
		if(rect1.getWidth()>0 && rect1.getHeight()>0){
			Point2D pt1 = Utils.getIntersectionPoint(line,rect1);
			if(pt1!=null){
				x1 = (float)pt1.getX();
				y1 = (float)pt1.getY();
			}
		}

		if(rect2.getWidth()>0 && rect2.getHeight()>0){
			Point2D pt2 = Utils.getIntersectionPoint(line,rect2);
	
			if(pt2!=null){
				x2 = (float)pt2.getX();
				y2 = (float)pt2.getY();
			}
		}
		new_coord[0] = new _Point(x1,y1);
		new_coord[1] = new _Point(x2,y2);
		return new_coord;
	}
	public static _Point[] calculerCoordonnees(Rectangle2D rect1, Rectangle2D rect2, _Point center1){
		float x2 = (float)(rect2.getX()+rect2.getWidth()/2)
			, y2 = (float) (rect2.getY()+rect2.getHeight()/2);
		return calculerCoordonnees(rect1,rect2,center1,new _Point(x2,y2));
	}
	public static _Point[] calculerCoordonnees(Rectangle2D rect1, Rectangle2D rect2){
		float x1 = (float)(rect1.getX()+rect1.getWidth()/2)
				, y1 = (float) (rect1.getY()+rect1.getHeight()/2)
				, x2 = (float)(rect2.getX()+rect2.getWidth()/2)
				, y2 = (float) (rect2.getY()+rect2.getHeight()/2);
		return calculerCoordonnees(rect1,rect2,new _Point(x1,y1),new _Point(x2,y2));
	}
	public static boolean showFullStatistics(ControlleurReseau controlleur,Noeud noeud){
		return showFullStatistics(controlleur,controlleur.noeudHighlighted(noeud));
	}
	public static boolean showFullStatistics(ControlleurReseau controlleur,boolean hover){
		int fullStats = controlleur.getFullStatistics();
		if(fullStats==2 || (fullStats==1 && hover)){
			return true;
		}
		return false;
	}
}
