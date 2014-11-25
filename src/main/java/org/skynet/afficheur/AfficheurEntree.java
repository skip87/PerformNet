package org.skynet.afficheur;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import org.skynet.model.ControlleurReseau;
import org.skynet.reseau.Element;
import org.skynet.reseau.Entree;
import org.skynet.utils.Utils;

public class AfficheurEntree extends AfficheurNoeud {
	@Override 
	public Color obtenirCouleurSelectionnee(){
		return Color.green;
	}
	/*
	 * Cette fonction est utilisee pour precalculer les rectangles avec les textes
	 * pour que les arc n'overlappent pas sur les textes
	 */
	@Override
	public List<TexteNoeud> preRender(Graphics2D g2, ControlleurReseau controlleur, Element _noeud){
		Entree noeud = (Entree) _noeud;
		List<TexteNoeud> textes = new LinkedList<TexteNoeud>();
		textes.clear();
		//if(controlleur.obtenirReseauValide()){
			Font font;
			g2.setFont(font=g2.getFont().deriveFont(Font.BOLD,controlleur.pixels(tailleFont)));
	
			FontMetrics fontMetrics = g2.getFontMetrics();
			Point coordonnees = controlleur.pixels(noeud.obtenirCoordonnees());
			Point stationStart = new Point(coordonnees.x,coordonnees.y);
			Dimension stationDimension = new Dimension(controlleur.pixels(noeud.obtenirLargeur()),controlleur.pixels(noeud.obtenirHauteur()));
			// Affichage du texte au centre de la station
			float tauxUtilisation = noeud.obtenirTauxArrive();
			String texteCentreStation = Utils.format(tauxUtilisation) + "/h";
			/*
			int centreX = stationStart.x + stationDimension.width/2;
			int centreY = stationStart.y + stationDimension.height/2;
	
			int alpha = Math.max(alphaDefaut-40,0);
			if(controlleur.noeudHighlighted(noeud) || controlleur.noeudEstSelectionnee(noeud))
				alpha = 255;
	
			// Afficher le background pour que le texte soit visible
			float _blue = 0;//0.5f + (1-noeud.obtenirTauxUtilisationStation())*.5f;
			float _green = .6f;//0.5f + (noeud.obtenirTauxUtilisationStation())*.5f;
			float _red = 0;
			Color fill2 = new Color(_red, _green, _blue, (float)alpha/255);
			g2.setColor(fill2);
			g2.fillRect(
				(int)(
					centreX
					- fontMetrics.stringWidth(texteCentreStation)/2
					- controlleur.pixels(obtenirSpacing())
				)
				, (int)(
					centreY
					- fontMetrics.getHeight()/2
				)
				, (int)(fontMetrics.stringWidth(texteCentreStation) + controlleur.pixels(obtenirSpacing())*2)
				, (int)(fontMetrics.getHeight() + controlleur.pixels(obtenirSpacing()))
			);
			
			
			//g2.setColor(obtenirCouleurTexte());
			g2.setColor(new Color(.2f,0,.8f));
			// Afficher le texte
			g2.drawString(texteCentreStation,
					(int)(
						centreX
						- fontMetrics.stringWidth(texteCentreStation)/2
					)
					,(int)(
						centreY
						+ fontMetrics.getHeight()/2
					)
			);
			*/
			
			if(Utils.showFullStatistics(controlleur, noeud))// || controlleur.noeudEstSelectionnee(noeud))
				texteCentreStation = "Taux entr\u00E9e: "+texteCentreStation;
			
			textes.add(
				new TexteNoeud(
					texteCentreStation
					, new Point(
						(int)(
							stationStart.x + stationDimension.width/2
							- fontMetrics.stringWidth(texteCentreStation)/2
						)
						,(int)(
							stationStart.y
							- controlleur.pixels(obtenirSpacing())
						)
					)
					, font
					, fontMetrics
					, obtenirCouleurAuDessous()
				)
			);
			
			/*
			g2.setColor(obtenirCouleurAuDessous());
			// Affichage  temps moyen passe par le poste (haut de station)
			g2.drawString(texteCentreStation,
					(int)(
						stationStart.x + stationDimension.width/2
						- fontMetrics.stringWidth(texteCentreStation)/2
					)
					,(int)(
						stationStart.y
						- controlleur.pixels(obtenirSpacing())
					)
			);*/
		//}
		
		return textes;
	}
	void affiche(Graphics2D g2, ControlleurReseau controlleur, Entree noeud){
		_affiche(g2, controlleur, noeud);
		
		List<TexteNoeud> textes = preRender(g2, controlleur, noeud);
		if(textes!=null){
			for(TexteNoeud texte : textes){
				texte.draw(g2);
				/*
				if(texte.font!=null)
					g2.setFont(texte.font);
				if(texte.color!=null)
					g2.setColor(texte.color);
				g2.drawString(texte.texte, texte.coordonnees.x, texte.coordonnees.y);
				*/
			}
		}
	}
}
