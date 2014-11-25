package org.skynet.reseau;

import org.skynet.utils._Dimension;
import org.skynet.utils._Point;

public class Sortie extends Noeud {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7178371069367106412L;
	public Sortie(Reseau reseau, _Point coordonnees) {
		super(reseau,coordonnees);
	}
	@Override
	public boolean peutContenirSortie(){
		return false;
	}
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
