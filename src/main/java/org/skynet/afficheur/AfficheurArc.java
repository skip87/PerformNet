package org.skynet.afficheur;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import org.skynet.model.ControlleurEtatInterface;
import org.skynet.model.ControlleurEtatInterface.State;
import org.skynet.model.ControlleurReseau;
import org.skynet.reseau.Arc;
import org.skynet.reseau.Noeud;
import org.skynet.utils.Utils;
import org.skynet.utils._Point;

public class AfficheurArc {

	protected void affiche(Graphics2D g, ControlleurReseau controlleur, Arc arc, _Point []from_to){//
		affiche(
			g
			, controlleur
			, arc
			, controlleur.pixels(from_to[0].x)
			, controlleur.pixels(from_to[0].y)
			, controlleur.pixels(from_to[1].x)
			, controlleur.pixels(from_to[1].y)
		);
	}
	protected void affiche(Graphics2D g, ControlleurReseau reseau, Arc arc, int x1, int y1, int x2, int y2){
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(Math.max(2,reseau.pixels(0.08f))));
		//g.setStroke(g.getStroke());
		Color arc_color = Color.gray; 
		arc_color = Color.black;
		if(arc!=null){
			if(reseau.arcHighlighted(arc) && reseau.arcEstSelectionne(arc)){
				arc_color = new Color(0xAA9922);
			} else if(reseau.arcHighlighted(arc)){
				arc_color = Color.blue;
			} else if(reseau.arcEstSelectionne(arc)) {
				arc_color = Color.red;
			}
		}
		g.setColor(arc_color);

		g.drawLine(x1,y1,x2,y2);
		
		double angleDiff = 15;
		double angle = Math.toDegrees(Math.atan2(y1-y2, x1-x2));
		double size = reseau.pixels(.5f);//10;//10/reseau.obtenirFacteurZoom(); // Transformer les pixels dans une nouvelle valeur
		size += (Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1))*.01f);
		// Affiecher les fleches
		//g.drawLine(x2,y2,(int)(x2+size*Math.cos(Math.toRadians(angle + angleDiff))),(int)(y2+size*Math.sin(Math.toRadians(angle + angleDiff))));
		//g.drawLine(x2,y2,(int)(x2+size*Math.cos(Math.toRadians(angle - angleDiff))),(int)(y2+size*Math.sin(Math.toRadians(angle - angleDiff))));
		
		int xpoints [] = new int[3];
		xpoints[0] = x2;
		xpoints[1] = (int)Math.round(x2+size*Math.cos(Math.toRadians(angle + angleDiff)));
		xpoints[2] = (int)Math.round(x2+size*Math.cos(Math.toRadians(angle - angleDiff)));
		
		int ypoints [] = new int[3];
		ypoints[0] = y2;
		ypoints[1] = (int) Math.round(y2+size*Math.sin(Math.toRadians(angle + angleDiff)));
		ypoints[2] = (int)Math.round(y2+size*Math.sin(Math.toRadians(angle - angleDiff)));
		
		g.drawPolygon(xpoints, ypoints, 3);
		g.setColor(Color.white);
		g.fillPolygon(xpoints, ypoints, 3);
	
		// Trouver la normale
		double dx = (x2-x1)
				,dy = y2-y1;
		double length = Math.sqrt(dx*dx + dy*dy);
		if(length>0){
			dx /= length;
			dy /= length;
		}

		// Afficher le texte
		int centerX = (x1+x2)/2;
		int centerY = (y1+y2)/2;
		
		if(arc!=null 
				//&& reseau.obtenirReseauValide()
		){
			// taille entre le texte et le centre de la ligne
			size = 5;//10/reseau.obtenirFacteurZoom();
			//arc.obtenirArrivee().obtenirTauxArrive();
			{
				float tauxPassage = arc.tauxPassageRelative();
				String tauxPassageStr = Utils.format(tauxPassage*100)+" % ("+Utils.format(arc.obtenirTauxPassage())+")";
	
				
				double nx1 = -dy, ny1 = dx;
				
				double angleNormale = angle - 90;
				angleNormale=(-angleNormale+360)%360;
				
				_Point so1 = Utils.calculerOffsetTexte(g,tauxPassageStr,angleNormale);
				
				Color tauxPassageColor = new Color( Math.min(arc_color.getRed()+255,255),Math.min(arc_color.getGreen(),255),Math.max(arc_color.getBlue()-255,0) );
				g.setColor(tauxPassageColor);
				g.drawString(tauxPassageStr,(int)(centerX + nx1*size + so1.x),(int)(centerY + ny1*size + so1.y)+g.getFontMetrics().getHeight());
			}
			if(arc.obtenirDepart().estConnecteEntree()){
				float tauxPassage = arc.obtenirTauxEntitesPassant();
				String tauxPassageStr = Utils.format(tauxPassage)+"/h";
				
				
				double nx1 = dy;
				double ny1 = -dx;
				
				double angleNormale = angle + 90;
				angleNormale=(-angleNormale+360)%360;
				
				_Point so1 = Utils.calculerOffsetTexte(g,tauxPassageStr,angleNormale);
				Color tauxEntitesColor = new Color( Math.min(arc_color.getRed(),180),Math.max(arc_color.getGreen()-100,0),Math.min(arc_color.getBlue()+100,255) );
				g.setColor(tauxEntitesColor);
				g.drawString(tauxPassageStr,(int)(centerX + nx1*size + so1.x),(int)(centerY + ny1*size + so1.y)+g.getFontMetrics().getHeight());
			}
		}
		g.setStroke(oldStroke);
	}
	public void affiche(Graphics2D g, ControlleurReseau reseau, Arc arc, ControlleurEtatInterface etatInterface){
		if(etatInterface.obtenirEtat()==State.DEPLACER_ARC_DEBUT && reseau.arcEstSelectionne(arc)){
			_Point to = etatInterface.obtenirCoordonneesPreview();
			Noeud from = arc.obtenirArrivee();
			_Point coord1 = from.obtenirCoordonnees();

			_Point coord[] = Utils.calculerCoordonnees(
					new Rectangle2D.Float(coord1.x,coord1.y,(float)from.obtenirLargeur(),(float)from.obtenirHauteur())
					,new Rectangle2D.Float(to.x,to.y,0,0)
					,from.obtenirCoordonneesCentre()
			);
			_Point temp = coord[0];
			coord[0]=coord[1];
			coord[1]=temp;
			affiche(g,reseau,null,coord);
		} else if(etatInterface.obtenirEtat()==State.DEPLACER_ARC_FIN && reseau.arcEstSelectionne(arc)){
			affiche(g,reseau,arc.obtenirDepart(),etatInterface.obtenirCoordonneesPreview());
		} else
			affiche(g,reseau,arc,arc.obtenirCoordonneesArc());
	}
	public void affiche(Graphics2D g, ControlleurReseau controlleur, Noeud from, _Point to){
		//Point coord1 = controlleur.pixels( from.obtenirCoordonnees() );
		_Point coord1 = from.obtenirCoordonnees();


		_Point coord[] = Utils.calculerCoordonnees(
				new Rectangle2D.Float(
					coord1.x
					,coord1.y
					, from.obtenirLargeur()
					, from.obtenirHauteur()
				)
				,new Rectangle2D.Float(
						to.x
						,to.y
						,0
						,0
				)
				,from.obtenirCoordonneesCentre()
		);
		affiche(g,controlleur,null,coord);
	}
}
