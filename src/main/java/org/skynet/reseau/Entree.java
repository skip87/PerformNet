package org.skynet.reseau;

import org.skynet.utils._Dimension;
import org.skynet.utils._Point;

public class Entree extends Noeud {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3198194432316293656L;
	private float tauxDArivee = 90;
	public Entree(Reseau reseau, _Point coordonnees){
		super(reseau,coordonnees);
	}
	@Override
	public boolean peutContenirEntree(){
		return false;
	}
	@Override
	public float obtenirTauxArrive(){
		return tauxDArivee;
	}
	public void definirTauxArrive(float tauxDArivee){
		this.tauxDArivee = tauxDArivee;
	}
	public boolean modifier(float tauxArrivee, float largeur, float hauteur, String pathImage) {
		boolean estModifie = false;
		if(tauxArrivee != obtenirTauxArrive()){
			estModifie=true;
			definirTauxArrive( tauxArrivee);
		}
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
