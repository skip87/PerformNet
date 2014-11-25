package org.skynet.afficheur;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;

import org.skynet.model.ControlleurReseau;
import org.skynet.ui.MainPanel;

public class AfficheurGrille {
	public void affiche(Graphics2D g2,ControlleurReseau controlleur,MainPanel panneauAffichage){
		if(controlleur.obtenirGrilleAffiche()){
			g2.setColor(Color.gray);//lightGray);
			//g2.setColor(Color.magenta);
			float distance = controlleur.pixels(controlleur.obtenirDistanceGrille());
			
			Dimension dimension = new Dimension(panneauAffichage.getWidth(),panneauAffichage.getHeight());
			Point dimReseau = controlleur.pixels(controlleur.obtenirDimensionReseau());
			if(dimReseau.x>dimension.width){
				dimension.width = dimReseau.x;
			}
			if(dimReseau.y>dimension.height){
				dimension.height = dimReseau.y;
			}
			/*
			for(float i=0; i<dimension.width; i+=distance){
				g2.drawLine((int)i, 0, (int)i, (int)dimension.height);
			}
			for(float i=0; i<dimension.height; i+=distance){
				g2.drawLine(0, (int)i, (int)dimension.width, (int)i);
			}
			*/
			float size = controlleur.pixels(.2f);
			//size = Math.max(Math.min(size, 20),4);
			g2.setStroke(new BasicStroke(Math.max(1,controlleur.pixelsf(.04f))));
			for(float i=0; i<dimension.width; i+=distance){
				for(float j=0; j<dimension.height; j+=distance){
					//g2.fillOval((int)(i-size/2), (int)(j-size/2), (int)size, (int)size);
					g2.drawLine((int)Math.round(i-size/2), (int)j, (int)Math.round(i+size/2), (int)j);
					g2.drawLine((int)i, (int)Math.round(j-size/2), (int)i, (int)Math.round(j+size/2));
				}
			}
		}
	}
}
