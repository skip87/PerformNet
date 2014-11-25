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
import org.skynet.reseau.Sortie;
import org.skynet.utils.Transform;
import org.skynet.utils.Utils;

public class AfficheurSortie extends AfficheurNoeud {
	@Override 
	public Color obtenirCouleurSelectionnee(){
		return Color.red;
	}
	@Override
	public List<TexteNoeud> preRender(Graphics2D g2, ControlleurReseau controlleur, Element _noeud){
		Sortie noeud = (Sortie) _noeud;
		List<TexteNoeud> textes = new LinkedList<TexteNoeud>();
		textes.clear();
		if(noeud.estConnecteEntree()){//controlleur.obtenirReseauValide()){
			Font font;
			g2.setFont(font=g2.getFont().deriveFont(Font.BOLD,controlleur.pixels(tailleFont)));
	
			FontMetrics fontMetrics = g2.getFontMetrics();
			Point coordonnees = controlleur.pixels(noeud.obtenirCoordonnees());
			Point stationStart = new Point(coordonnees.x,coordonnees.y);
			Dimension stationDimension = new Dimension(controlleur.pixels(noeud.obtenirLargeur()),controlleur.pixels(noeud.obtenirHauteur()));
			// Affichage du texte au centre de la station
			float tauxUtilisation = noeud.obtenirTauxSortie();
			String texteCentreStation = Utils.format(tauxUtilisation) + "/h";


			if(Utils.showFullStatistics(controlleur, noeud))// || controlleur.noeudEstSelectionnee(noeud))
				texteCentreStation = "Taux sortie: "+texteCentreStation;
			
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
			
			int offsetBas = 0;
			
			// Affichage nombre entites dans systeme
			String texteBasStation = Utils.format(controlleur.obtenirNombreMEntitesDansSysteme())+" e";
			if(Utils.showFullStatistics(controlleur, noeud))// || controlleur.noeudEstSelectionnee(noeud))
				texteBasStation = "Nombre entites dans syst\u00E9me: "+texteBasStation;
			textes.add(
				new TexteNoeud(
						texteBasStation
					, new Point(
							(int)(
									stationStart.x + stationDimension.width/2
									- fontMetrics.stringWidth(texteBasStation)/2
								)
								,(int)(
									stationStart.y + stationDimension.height
									+ fontMetrics.getHeight()
									+ offsetBas
								)
					)
					, font
					, fontMetrics
					, new Color(0xe83c00)
				)
			);
			offsetBas += fontMetrics.getHeight();

			// Affichage  temps moyen passe par le poste (bas de station)
			String texteBasStation2 = Transform.time(controlleur.obtenirTempsMoyenPrisParUneEntite());
			if(Utils.showFullStatistics(controlleur, noeud))// || controlleur.noeudEstSelectionnee(noeud))
				texteBasStation2 = "Temps moyen pris par une entite: "+texteBasStation2;
			textes.add(
				new TexteNoeud(
						texteBasStation2
					, new Point(
							(int)(
									stationStart.x + stationDimension.width/2
									- fontMetrics.stringWidth(texteBasStation2)/2
								)
								,(int)(
									stationStart.y + stationDimension.height
									+ fontMetrics.getHeight()
									+ offsetBas
								)
					)
					, font
					, fontMetrics
					, new Color(0xe83c00)
				)
			);
			offsetBas += fontMetrics.getHeight();
		
		}
		
		return textes;
	}
	void affiche(Graphics2D g2, ControlleurReseau controlleur, Sortie noeud){
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
