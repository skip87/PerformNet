package org.skynet.reseau;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.skynet.utils.SkynetImage;
import org.skynet.utils._Dimension;
import org.skynet.utils._Point;

public class ImageDeFond extends Element {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2637703463417532355L;
	transient public static int pixelsParMetreDefaut = 30;
	transient public static BufferedImage imagePlaceholder = null;
	//private BufferedImage tempImage;
	/*
	protected SkynetImage image = null;
	protected _Point coordonnees;
	protected _Point dimensions = null;
	*/
	protected int zIndex = 0;
	
	public ImageDeFond(_Point coordonnees, String path) throws IOException {
		super(coordonnees);
		//this.tempImage = image;
		this.image = new SkynetImage(path);
		BufferedImage image = this.image.getImage();
		//width = image.getWidth()/pixelsParMetreDefaut;
		//height = image.getHeight()/pixelsParMetreDefaut;
		definirTaille(new _Dimension(image.getWidth()/pixelsParMetreDefaut, image.getHeight()/pixelsParMetreDefaut));
		//taille.setSize(image.getWidth()/pixelsParMetreDefaut, image.getHeight()/pixelsParMetreDefaut);
	}
	/*
	public float getWidth(){
		return width;
	}
	public float getHeight(){
		return height;
	}
	*/
	public BufferedImage obtenirImage(){
		return image != null ? image.getImage() : imagePlaceholder;
	}
	/*
	public _Point obtenirCoordonnees(){
		return coordonnees;
	}
	*/
	//public 
	public boolean modifier(float largeur, float hauteur, String pathImage) {
		boolean estModifie = false;
		
		if(largeur!=obtenirLargeur() || hauteur!=obtenirHauteur()){
			estModifie=true;
			this.definirTaille(new _Dimension(largeur, hauteur));
		}
		
		if(pathImage!=null){
			if(pathImage.equals("")){
				// Il faut retirer l'image
				if(this.image!=null){
					definirImage((String)null);
					estModifie=true;
				}
			} else {
				definirImage(pathImage);
				estModifie=true;
			}
		}
		return estModifie;
		
	}
}
