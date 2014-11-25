package org.skynet.afficheur;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import org.skynet.model.ControlleurEtatInterface;
import org.skynet.model.ControlleurEtatInterface.State;
import org.skynet.model.ControlleurReseau;
import org.skynet.reseau.Arc;
import org.skynet.reseau.Entree;
import org.skynet.reseau.Noeud;
import org.skynet.reseau.Sortie;
import org.skynet.reseau.Station;
import org.skynet.ui.MainPanel;
import org.skynet.utils.Transform;
import org.skynet.utils._Point;

public class AfficheurReseau {
	private final AfficheurStation afficheurStation;
	private final AfficheurEntree afficheurEntree;
	private final AfficheurSortie afficheurSortie;
	private final AfficheurArc afficheurArc;
	private final AfficheurGrille afficheurGrille;
	private final AfficheurImageDeFond afficheurImageDeFond;
	
	public AfficheurReseau() {
		afficheurStation = new AfficheurStation(); 
		afficheurEntree = new AfficheurEntree(); 
		afficheurSortie = new AfficheurSortie(); 
		afficheurArc = new AfficheurArc(); 
		afficheurGrille = new AfficheurGrille(); 
		afficheurImageDeFond = new AfficheurImageDeFond();
	}
	
	public void afficheArcPreview(Graphics2D g2, ControlleurReseau controlleur, ControlleurEtatInterface etatInterface){
		if(etatInterface.obtenirEtat()==State.AJOUTER_ARC_STATION2 && etatInterface.obtenirCoordonneesPreview() != null) {
			Noeud noeud1 = controlleur.obtenirNoeudTemp();
			_Point coord2 = etatInterface.obtenirCoordonneesPreview();
			//if(etatInterface.obtenirCoordonneesPreview()!=null)
			//	g2.setColor(Color.red);g2.drawArc(controlleur.pixels(etatInterface.obtenirCoordonneesPreview().x)-3, controlleur.pixels(etatInterface.obtenirCoordonneesPreview().y)-3, 5, 5, 0, 360);
			
			afficheurArc.affiche(g2, controlleur, noeud1, coord2);
		}
	}
	
	public void afficherSelectionRangee(Graphics g2, ControlleurReseau controlleur, ControlleurEtatInterface etatInterface){
		if(etatInterface.obtenirEtat()==State.SELECTION_RANGEE){
			Point start = controlleur.pixels( controlleur.obtenirDebutSelection() );
			Point end = controlleur.pixels( controlleur.obtenirFinSelection() );
			Rectangle rect = Transform.Rectangle(start,end);
			g2.setColor(new Color(.3f,.3f,.3f,.2f));
			g2.fillRect(rect.x, rect.y, rect.width, rect.height);
			g2.setColor(Color.gray);
			g2.drawRect(rect.x, rect.y, rect.width, rect.height);
		}
	}
	
	public void afficherMessageEtatReseau(Graphics g2, ControlleurReseau controlleur, MainPanel panneauAffichage){
		Font fontBackup = g2.getFont();
		// Afficher le texte "Reseau n'est pas valide"
		if(controlleur.obtenirReseauValide()==false){
			Font fontAffichageGras = fontBackup.deriveFont(Font.BOLD, 18);//new Font(fontBackup.getName(),Font,(int)(18/facteurZoom));
			g2.setFont(fontAffichageGras);
			FontMetrics fontMetricsGras = g2.getFontMetrics();

			g2.setColor(new Color(0xC3,0,0));
			String reseauValideString = "Reseau n'est pas valide";
			g2.drawString(
					reseauValideString
					//, (reseau.transformerPixelsEnMetres(panneauAffichage.getWidth())-fontMetricsGras.stringWidth(reseauValideString))/2
					, (panneauAffichage.getParent().getBounds().width-fontMetricsGras.stringWidth(reseauValideString))/2
					, fontMetricsGras.getHeight()
			);
			g2.setFont(fontBackup);
		}
	}
	public void afficherStationPreview(Graphics2D g2, ControlleurReseau controlleur,ControlleurEtatInterface etatInterface){
		if(etatInterface.obtenirEtat()==State.AJOUTER_STATION && etatInterface.obtenirCoordonneesPreview() != null) {
			//g2.drawArc(controlleur.pixels(etatInterface.obtenirCoordonneesPreview().x)-3, controlleur.pixels(etatInterface.obtenirCoordonneesPreview().y)-3, 5, 5, 0, 360);
			Station station = new Station(controlleur.obtenirReseau(),etatInterface.obtenirCoordonneesPreview());
			affiche(g2,controlleur,station,true);
		}
	}

	public void afficherBackground(Graphics2D g2,ControlleurReseau controlleur,MainPanel panneauAffichage){
		g2.setColor(Color.white);
		//g2.setColor(Color.magenta);
		float distance = controlleur.pixelsf(.5f);//controlleur.pixels(controlleur.obtenirDistanceGrille());
		//System.out.println(controlleur.pixels(controlleur.obtenirDistanceGrille()));
		//System.out.println(controlleur.pixelsf(.25f));
		
		Dimension dimension = new Dimension(panneauAffichage.getWidth(),panneauAffichage.getHeight());
		Point dimReseau = controlleur.pixels(controlleur.obtenirDimensionReseau());
		if(dimReseau.x>dimension.width){
			dimension.width = dimReseau.x;
		}
		if(dimReseau.y>dimension.height){
			dimension.height = dimReseau.y;
		}
		for(int i=0; i<=(int)(dimension.height/distance); i++){
			for(int j=i%2; j<=(int)(dimension.width/distance); j+=2){
				g2.fillRect((int)(j*distance),(int)(i*distance),(int)distance,(int)distance);
			}
		}
		/*for(float i=0; i<dimension.height; i+=distance){
			for(float j=i%(2*distance); j<dimension.width; j+=distance*2){
				g2.fillRect((int)j,(int)i,(int)distance,(int)distance);
			}
		}*/

	}
	@SuppressWarnings({ "unused" })
	public void affiche(Graphics g, ControlleurReseau controlleur, MainPanel panneauAffichage, ControlleurEtatInterface etatInterface) {
		Graphics2D g2 = (Graphics2D)g;

		// Savegarde des valeurs par defaut
		Font fontBackup = g2.getFont();
		Font fontAffichage = fontBackup;
		
		// Definir le nouveau font d'affichage avec la correction du zoom
		//Font fontAffichage = fontBackup.deriveFont(fontBackup.getSize()/facteurZoom);
		//g2.setFont(fontAffichage);

		if(panneauAffichage!=null)
			afficherBackground(g2, controlleur, panneauAffichage);
		//if(panneauAffichage!=null){
			afficheurImageDeFond.affiche(g2, controlleur, panneauAffichage);
		//}
		// Afficher la grille
		if(panneauAffichage!=null)
			afficheurGrille.affiche(g2, controlleur, panneauAffichage);

		
		controlleur.obtenirEntree().definirListesRectangles(afficheurEntree.obtenirRectanglesTexteEnMetres(g2, controlleur, controlleur.obtenirEntree()));
		controlleur.obtenirSortie().definirListesRectangles(afficheurSortie.obtenirRectanglesTexteEnMetres(g2, controlleur, controlleur.obtenirSortie()));
		for(Station station : controlleur.obtenirStations()){
			station.definirListesRectangles(afficheurStation.obtenirRectanglesTexteEnMetres(g2, controlleur, station));
		}
		for(Arc arc : controlleur.obtenirArcs()){
			affiche(g2,controlleur,arc,etatInterface);
		}
		
		// Afficher l'arc
		afficheArcPreview(g2,controlleur,etatInterface);

		
		affiche(g2,controlleur,controlleur.obtenirEntree());
		affiche(g2,controlleur,controlleur.obtenirSortie());
		
		for(Station station : controlleur.obtenirStations()){
			affiche(g2,controlleur,station);
		}
		
		// Afficher preview de la station
		afficherStationPreview(g2, controlleur, etatInterface);
		// Il faut montrer la selection
		afficherSelectionRangee(g2, controlleur, etatInterface);
		
		if(panneauAffichage!=null)
			afficherMessageEtatReseau(g2, controlleur, panneauAffichage);;
		
		g2.setFont(fontBackup);
		
		//if(etatInterface.obtenirCoordonneesSouris()!=null)
		//	g2.drawArc(etatInterface.obtenirCoordonneesSouris().x-3, etatInterface.obtenirCoordonneesSouris().y-3, 5, 5, 0, 360);
		//if(etatInterface.obtenirCoordonneesPreview()!=null)
		//	g2.drawArc(controlleur.pixels(etatInterface.obtenirCoordonneesPreview().x)-3, controlleur.pixels(etatInterface.obtenirCoordonneesPreview().y)-3, 5, 5, 0, 360);
		
	}
	
	
	private void affiche(Graphics2D g2, ControlleurReseau reseau, Arc arc, ControlleurEtatInterface etatInterface){
		afficheurArc.affiche(g2, reseau, arc, etatInterface);
	}
	private void affiche(Graphics2D g2, ControlleurReseau reseau, Station noeud){
		afficheurStation.affiche(g2, reseau, noeud);
	}
	private void affiche(Graphics2D g2, ControlleurReseau reseau, Station noeud, boolean estPreview){
		afficheurStation.affiche(g2, reseau, noeud, estPreview);
	}
	private void affiche(Graphics2D g2, ControlleurReseau reseau, Entree noeud){
		afficheurEntree.affiche(g2, reseau, noeud);
	}
	private void affiche(Graphics2D g2, ControlleurReseau reseau, Sortie noeud){
		afficheurSortie.affiche(g2, reseau, noeud);
	}
}
