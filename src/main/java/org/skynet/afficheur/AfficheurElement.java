package org.skynet.afficheur;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import org.skynet.model.ControlleurReseau;
import org.skynet.reseau.Element;

public class AfficheurElement {
	interface TexteNoeudBackgroundDrawer {
		void draw(Graphics2D g2);
	}
	public class TexteNoeud {
		Font font;
		String texte;
		Point coordonnees;
		FontMetrics fontMetrics;
		Color color=null;
		TexteNoeudBackgroundDrawer bgDrawer = null;
		TexteNoeud(String texte,Point coordonnees,Font font,FontMetrics fontMetrics,Color color){
			init(texte, coordonnees, font, fontMetrics, color, null);
		}
		TexteNoeud(String texte,Point coordonnees,Font font,FontMetrics fontMetrics,Color color,TexteNoeudBackgroundDrawer bgDrawer){
			init(texte, coordonnees, font, fontMetrics, color, bgDrawer);
		}
		void init(String texte,Point coordonnees,Font font,FontMetrics fontMetrics,Color color,TexteNoeudBackgroundDrawer bgDrawer){
			this.texte = texte;
			this.coordonnees = coordonnees;
			this.font = font;
			this.fontMetrics = fontMetrics;
			this.color = color;
			this.bgDrawer = bgDrawer;
		}
		void draw(Graphics2D g2){
			if(bgDrawer!=null) bgDrawer.draw(g2);
			if(font!=null)
				g2.setFont(font);
			if(color!=null)
				g2.setColor(color);
			g2.drawString(texte, coordonnees.x, coordonnees.y);
		}
		Rectangle2D obtenirRectangle(ControlleurReseau controlleur){
			int width = 0;
			int height = 0;
			int spacing = controlleur.pixels(obtenirSpacing());
			// Calcul des dimensions pour les textes multiligne
			for(String t : texte.split("\r?\n")){
				width = Math.max(width, fontMetrics.stringWidth(t)) + spacing*2;
				height += fontMetrics.getHeight()+spacing;
			}
			return
					new Rectangle2D.Float(
						controlleur.metres(coordonnees.x-spacing)
						, controlleur.metres(coordonnees.y-height)//texte.fontMetrics.getHeight())
						, controlleur.metres(width)//texte.fontMetrics.stringWidth(texte.texte))
						, controlleur.metres(height)//texte.fontMetrics.getHeight())
					);
		}
	}
	
	//protected List<TexteNoeud> textes = new LinkedList<TexteNoeud>();
	public List<Rectangle2D> obtenirRectanglesTexteEnMetres(Graphics2D g2, ControlleurReseau controlleur, Element noeud){
		List<TexteNoeud> textes = preRender(g2, controlleur, noeud);
		List<Rectangle2D> rectangles = new LinkedList<Rectangle2D>();
				
		if(textes!=null){
			for(TexteNoeud texte : textes){
				rectangles.add(texte.obtenirRectangle(controlleur));
			}
		}
		
		return rectangles;
	}
	/*
	 * Cette fonction est utilisee pour precalculer les rectangles avec les textes
	 * pour que les arc n'overlappent pas sur les textes
	 * la fonction obtenirRectanglesTexteMetres va convertir les textes en Rectangles
	 */
	List<TexteNoeud> preRender(Graphics2D g2, ControlleurReseau controlleur, Element noeud){
		return null;
	}
	
	protected int alphaDefaut = 140;
	protected float tailleFont = 0.5f; // en metres
	protected float obtenirSpacing(){
		return tailleFont*.25f;
	}
	protected Color obtenirCouleurAuDessous(){
		return new Color(0x657a02);
	}
	protected Color obtenirCouleurSelectionnee(){
		return new Color(0xc467ff);
	}
	protected Color obtenirCouleurTexte(){
		return new Color(0xFFFFFF);
	}
	protected Color obtenirCouleurTexte2(){
		return new Color(0x7afd7d);
	}
	protected void _afficheHighlight(Graphics2D g2, ControlleurReseau controlleur, Element noeud){
		//_Point coord_reseau = noeud.obtenirCoordonnees();
		Point coord = controlleur.pixels(noeud.obtenirCoordonnees());
		Stroke oldStroke = g2.getStroke();
		g2.setStroke(new BasicStroke(controlleur.pixelsf(.2f)));
		//g2.setStroke(new BasicStroke(controlleur.pixelsf(.2f),BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL, 0, new float[] {controlleur.pixelsf(.2f),controlleur.pixelsf(.2f)/2}, 0));
		Color hColor = null;
		if(controlleur.noeudEstSelectionnee(noeud) && controlleur.noeudHighlighted(noeud)){
			hColor = new Color(1.0f,0,1.0f);
		} else if(controlleur.noeudHighlighted(noeud)){
			//g2.setColor(Color.blue);
			hColor = Color.blue;
			/*g2.drawRect(
				coord.x
				,coord.y
				,controlleur.pixels(noeud.obtenirLargeur())
				,controlleur.pixels(noeud.obtenirHauteur())
			);*/
		} else if(controlleur.noeudEstSelectionnee(noeud)) {
			//g2.setColor(obtenirCouleurSelectionnee());//Color.yellow);
			hColor=obtenirCouleurSelectionnee();
		}
		if(hColor!=null){
			g2.setStroke(new BasicStroke(controlleur.pixelsf(.2f),BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND, 0, new float[] {2,1}, 0));
			g2.setColor(hColor);
			g2.drawRect(
					coord.x
					,coord.y
					,controlleur.pixels(noeud.obtenirLargeur())
					,controlleur.pixels(noeud.obtenirHauteur())
				);
		}
		g2.setStroke(oldStroke);
	}
	protected void _affiche(Graphics2D g2, ControlleurReseau controlleur, Element noeud){
		Point coord = controlleur.pixels(noeud.obtenirCoordonnees());
		//java.awt.Image img = noeud.obtenirImage().getScaledInstance(controlleur.pixels(noeud.obtenirLargeur()), controlleur.pixels(noeud.obtenirHauteur()), java.awt.Image.SCALE_SMOOTH);
		g2.drawImage(
			noeud.obtenirImage()
			,coord.x
			,coord.y
			,controlleur.pixels(noeud.obtenirLargeur())
			,controlleur.pixels(noeud.obtenirHauteur())
			,null
		);
		_afficheHighlight(g2, controlleur, noeud);
	}
}
