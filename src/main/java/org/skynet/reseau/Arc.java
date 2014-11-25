package org.skynet.reseau;

import java.io.Serializable;

import org.skynet.utils.Utils;
import org.skynet.utils._Point;

public class Arc implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5147674548571524776L;
	private float tauxPassage=1;
	private Noeud depart;
	private Noeud arrivee;
	private boolean actif = true;
	public void estActif(boolean actif){
		this.actif = actif;
	}
	public boolean estActif(){
		return actif;
	}
	public Arc(Noeud depart, Noeud arrivee, float tauxPassage){
		init(depart,arrivee,tauxPassage);
	}
	public Arc(Noeud depart, Noeud arrivee){
		init(depart,arrivee,1);
	}
	protected void init(Noeud depart, Noeud arrivee, float tauxPassage){
		this.depart = depart;
		this.arrivee = arrivee;
		this.tauxPassage = tauxPassage;
	}
	public void supprimer(){
		//depart.enleverArcSortant(this);
		//arrivee.enleverArcEntrant(this);
	}
	public Noeud obtenirDepart(){
		return depart;
	}
	public Noeud obtenirArrivee(){
		return arrivee;
	}
	public void definirDepart(Noeud depart){
		this.depart = depart;
	}
	public void definirArrivee(Noeud arrivee){
		this.arrivee = arrivee;
	}
	public void definirTauxPassage(float tauxPassage){
		this.tauxPassage = tauxPassage;
	}
	public float obtenirTauxPassage(){
		//ajout par Alex pour calcul statistiques
		return tauxPassage;
	}
	public float tauxPassageRelative(){
		// TODO: ajouter le taux passage relative pendant les calculs
		float poidsArcsAbsolut = 0;
		for(Arc arc : obtenirDepart().obtenirArcsSortants()){
			poidsArcsAbsolut += arc.obtenirTauxPassage();
		}

		return obtenirTauxPassage()/poidsArcsAbsolut;//tauxPassageRelative;
	}
	public float obtenirTauxEntitesPassant(){
		return obtenirDepart().obtenirTauxSortie()*tauxPassageRelative();
	}
	public boolean intersecte(_Point coordonnees){
		//Polygon poly = new Polygon();
		
		_Point start = obtenirDepart().obtenirCoordonneesCentre();
		_Point end = obtenirArrivee().obtenirCoordonneesCentre();
		
		double dx = (end.x-start.x)
				,dy = (end.y-start.y);
		double length = Math.sqrt(dx*dx + dy*dy);
		if(length>0){
			dx /= length;
			dy /= length;
		}
		java.awt.geom.Line2D l = new java.awt.geom.Line2D.Float(start.x,start.y,end.x,end.y);
		return l.ptSegDist(coordonnees.x, coordonnees.y) < 0.5;
		//l.ptLineDist(pt)
		
		/*
		float sizeHightlight = 0.5f;//reseau.transformerPixelsEnReseau(10);
		poly.addPoint((int)(sizeHightlight*dy + start.x), (int)(-sizeHightlight*dx + start.y));// left top
		poly.addPoint((int)(-sizeHightlight*dy + start.x), (int)(sizeHightlight*dx + start.y));// left bottom
		poly.addPoint((int)(-sizeHightlight*dy + end.x), (int)(sizeHightlight*dx + end.y));// right bottom
		poly.addPoint((int)(sizeHightlight*dy + end.x), (int)(-sizeHightlight*dx + end.y));// right top
		
		return poly.contains((int)coordonnees.x,(int)coordonnees.y);
		*/
	}
	public _Point[] obtenirCoordonneesArc(){
		//_Point xx[] = new _Point[2];
		
		Noeud noeud1 = obtenirDepart();
		Noeud noeud2 = obtenirArrivee();
		
		//_Point coord1 = noeud1.obtenirCoordonnees();
		//_Point coord2 = noeud2.obtenirCoordonnees();

		_Point coord[] = Utils.calculerCoordonnees(
				noeud1.obtenirRectanglesIntersection()//new Rectangle2D.Float(coord1.x,coord1.y,(float)noeud1.obtenirLargeur(),(float)noeud1.obtenirHauteur())
				,noeud2.obtenirRectanglesIntersection()//new Rectangle2D.Float(coord2.x,coord2.y,(float)noeud2.obtenirLargeur(),(float)noeud2.obtenirHauteur())
				,noeud1.obtenirCoordonneesCentre()
				,noeud2.obtenirCoordonneesCentre()
		);

		return coord;
	}
	public boolean modifier(float poid) {
		boolean estModifie = false;
		if(poid != obtenirTauxPassage()){
			estModifie=true;
			definirTauxPassage( poid);
		}
		return estModifie;
	}
}
