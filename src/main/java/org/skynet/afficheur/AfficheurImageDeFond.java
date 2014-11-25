package org.skynet.afficheur;

import java.awt.Graphics2D;
import java.util.List;

import org.skynet.model.ControlleurReseau;
import org.skynet.reseau.ImageDeFond;
import org.skynet.ui.MainPanel;

public class AfficheurImageDeFond extends AfficheurElement {
	//private ArrayList<ImageDeFond> imagesDeFond = new ArrayList<ImageDeFond>();
	public void affiche(Graphics2D g, ControlleurReseau controlleur, MainPanel panneauAffichage){
		List<ImageDeFond> imagesDeFond = controlleur.obtenirImagesDeFond();
		//System.out.println(imagesDeFond);
		for(ImageDeFond img : imagesDeFond){
			//Point coordonnees = controlleur.pixels(img.obtenirCoordonnees());
			//g.drawImage(img.obtenirImage(),coordonnees.x,coordonnees.y,controlleur.pixels(img.obtenirLargeur()),controlleur.pixels(img.obtenirHauteur()),null);
			_affiche(g, controlleur, img);
		}
	}

}
