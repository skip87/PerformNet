package org.skynet.reseau;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;

import org.skynet.utils.SkynetImage;
import org.skynet.utils._Dimension;
import org.skynet.utils._Point;

public abstract class Element implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2296471839771061547L;
	protected _Point coordonnees; // en metres
	public transient static _Dimension genericSize;
	public transient static BufferedImage genericImage = null;
	protected _Dimension taille = genericSize;// = new Dimension(2,4);
	// C'est l'image que l'usager a telecharge
	protected SkynetImage image = null;
	// C'est l'image que le systeme a defini comme l'image par defaut pour ce type de noeud, exemple entree/sortie
	protected transient BufferedImage defaultImage = null;

	public Element(_Point coordonnees){
		this.coordonnees = coordonnees;
	}
	public void deplacer(_Point diff){
		definirCoordonnees(coordonnees.x+diff.x, coordonnees.y+diff.y);
	}
	public BufferedImage obtenirImage(){
		return image != null ? image.getImage() : (defaultImage!=null ? defaultImage : genericImage);
	}
	public _Dimension obtenirTaille(){
		return taille!=null ? taille : genericSize;
	}
	public void definirTaille(_Dimension taille){
		this.taille = taille;
	}
	public void definirImage(String path){
		this.image = null;
		if(path!=null){
			try {
				this.image = new SkynetImage(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	public void definirImage(BufferedImage image){
		this.defaultImage = image;
	}
	public java.awt.geom.Rectangle2D obtenirRectangle(){
		_Point coordonnees = obtenirCoordonnees();
		return new java.awt.geom.Rectangle2D.Float(coordonnees.x,coordonnees.y,obtenirLargeur(),obtenirHauteur());
	}
	public float obtenirLargeur(){
		return (float)obtenirTaille().getWidth();//(int)(obtenirImage().getWidth() * ControlleurReseau.multiplicateur * .01/* petit fix avant de faire le resize (pas de logiciel) */);
	}
	public float obtenirHauteur(){
		return (float)obtenirTaille().getHeight();//(int)(obtenirImage().getHeight() * ControlleurReseau.multiplicateur * .01/* petit fix avant de faire le resize (pas de logiciel) */);
	}
	public _Point obtenirCoordonnees(){
		return coordonnees;
	}
	public void definirCoordonnees(_Point coordonnees){
		// ne pas permettre place en dehors du reseau
		if(coordonnees.x<0)
			coordonnees.x = 0;
		if(coordonnees.y<0)
			coordonnees.y = 0;
		this.coordonnees = coordonnees;
	}
	public void definirCoordonnees(float x, float y){
		//this.coordonnees = new _Point(x,y);
		definirCoordonnees(new _Point(x,y));
	}
	public boolean intersecte(_Point start,_Point end){
		return intersecte(new Rectangle2D.Float(
				Math.min(start.x, end.x)
				,Math.min(start.y, end.y)
				,Math.abs(start.x-end.x)
				,Math.abs(start.y-end.y)
		));
	}
	public boolean intersecte(Rectangle2D rect){
		_Point coordonnees = obtenirCoordonnees();
		float width = obtenirLargeur()
				,height = obtenirHauteur();
		return rect.intersects(coordonnees.x,coordonnees.y,width,height);
	}
}
