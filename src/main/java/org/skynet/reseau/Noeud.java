package org.skynet.reseau;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.skynet.utils._Point;

public abstract class Noeud extends Element implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3178467320166480146L;
	private String nom = "";
	boolean _estConnecte;
	transient boolean _estConnecteEntree=false;
	transient boolean _estConnecteSortie=false;
	// C'est la liste que sera concatinnee lors d'affichage de l'arc pour ne pas faire overlap sur les textes
	protected List<Rectangle2D> listeAdditionnelDesRectangles = null;
	// nous avons seulement le setter, pas besoin du getter
	public void definirListesRectangles(List<Rectangle2D> listeAdditionnelDesRectangles){
		this.listeAdditionnelDesRectangles = listeAdditionnelDesRectangles;
	}
	
	public void definirNom(String nom){
		this.nom = nom;
	}
	public String obtenirNom(){
		return nom;
	}
	protected Reseau reseau;
	
	public Noeud(Reseau reseau,_Point coordonnees){
		super(coordonnees);
		this.reseau = reseau;
	}
	
	//protected ArrayList<Arc> arcsEntrants = new ArrayList<Arc>();
	//protected ArrayList<Arc> arcsSortants = new ArrayList<Arc>();
	
	protected boolean _faitPartieDeCycle = false;
	
	public _Point obtenirCoordonneesCentre(){
		return new _Point(obtenirCoordonnees().x + obtenirLargeur()/2,obtenirCoordonnees().y+obtenirHauteur()/2);
	}
	public boolean estConnecte(){
		return _estConnecte;
	}
	public void estConnecte(boolean estConnecte){
		this._estConnecte = estConnecte;
	}
	public boolean estConnecteEntree(){
		return _estConnecteEntree;
	}
	public void estConnecteEntree(boolean estConnecteEntree){
		this._estConnecteEntree = estConnecteEntree;
	}
	public boolean estConnecteSortie(){
		return _estConnecteSortie;
	}
	public void estConnecteSortie(boolean estConnecteSortie){
		this._estConnecteSortie = estConnecteSortie;
	}
	public boolean faitPartieDeCycle(){
		return _faitPartieDeCycle;
	}
	public void faitPartieDeCycle(boolean faitPartieDeCycle){
		this._faitPartieDeCycle = faitPartieDeCycle;
	}
	public List<Arc> obtenirArcsEntrants(){
		//Alex
		return reseau.obtenirArcsEntrants(this);//arcsEntrants;
	}
	
	public List<Arc> obtenirArcsSortants(){
		//Alex
		return reseau.obtenirArcsSortants(this);//arcsSortants;
	}
	
	public void supprimer(){
	}
	public boolean peutContenirEntree(){
		return true;
	}
	public boolean peutContenirSortie(){
		return true;
	}
	/*
	public Noeud(_Point coordonnees){
		this.coordonnees = coordonnees;
	}
	*/
	private boolean _existeCommeNoeudConnecte(Noeud noeud){
		if(noeud==this) return true;
		// Pour tous les arcs entrants il faut voir si le noeud entrant n'est pas le noeud recherche
		for(Arc arc : obtenirArcsEntrants()){
			if(arc.obtenirDepart()._existeCommeNoeudConnecte(noeud))
				return true;
		}
		return false;
	}
	public boolean existeCommeNoeudConnecte(Noeud noeud){
		// Premier check il faut faire sur tout les noeud sortants pous qu'ils soient pas egaux au noeud en question
		for(Arc arc : obtenirArcsSortants()){
			if(arc.obtenirArrivee()==noeud)
				return true;
		}
		return _existeCommeNoeudConnecte(noeud);
	}
	
	public float obtenirTauxArrive(){
		float tauxArrivee = 0;
		for(Arc arc : obtenirArcsEntrants()){
			tauxArrivee += arc.obtenirTauxEntitesPassant();//.tauxPassageRelative();
		}
		
		return tauxArrivee;
	}
	public float obtenirTauxSortie(){
		return obtenirTauxArrive();
	}
	
	/*
	 * Cette fonction est utilisee pour retourner le rectangle d'affichage du noeud
	 */
	protected Rectangle2D obtenirRectangleAffichage(){
		_Point coord1 = obtenirCoordonnees();
		return new Rectangle2D.Float(coord1.x,coord1.y,(float)obtenirLargeur(),(float)obtenirHauteur());
	}
	/*
	 * Cette fonction est utilisee pour retourner le rectangle de tous les elements dans le noeud
	 * Par exemple pour la station la file d'attente sera ajoute
	 * Cette fonction est utilisee pour collision detection de l'arc avec les noeud
	 */
	public List<Rectangle2D> obtenirRectanglesIntersection(){
		List<Rectangle2D> rectangles = new LinkedList<Rectangle2D>();
		rectangles.add(obtenirRectangleAffichage());
		if(listeAdditionnelDesRectangles!=null){
			for(Rectangle2D rect : listeAdditionnelDesRectangles){
				rectangles.add(rect);
			}
		}
		return rectangles;
	}
}
