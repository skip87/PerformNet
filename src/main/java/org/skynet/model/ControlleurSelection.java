package org.skynet.model;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.skynet.reseau.Arc;
import org.skynet.reseau.Element;
import org.skynet.reseau.ImageDeFond;
import org.skynet.reseau.Noeud;
import org.skynet.utils._Point;

/*
 * Classe qui gere les highlight noeuds/arcs, selection noeuds/arcs
 */
public class ControlleurSelection {
	private ArrayList<Element> highlight = new ArrayList<Element>();
	private ArrayList<Element> noeudsSelectionnees = new ArrayList<Element>();
	//private List<ImageDeFond> highlightImages = new LinkedList<ImageDeFond>();
	//private List<ImageDeFond> imagesSelectionnes = new LinkedList<ImageDeFond>();
	private LinkedList<Arc> highlightArcs = new LinkedList<Arc>();
	private Arc arcSelectionne = null;

	
	// Visible seulement dans le mm package
	ControlleurSelection(){
	}
	
	public void resetImages(){
		//System.out.println("in resetImages()");
		for (int count = 0;count < noeudsSelectionnees.size();count++){
			if (noeudsSelectionnees.get(count) instanceof ImageDeFond){
				noeudsSelectionnees.remove(count);
			}
		}
	}
	
	public List<Element> obtenirNoeudsHightlight(){
		return highlight;
	}
	public boolean noeudHighlighted(Element noeud){
		return highlight.contains(noeud);
	}
	
	public Arc obtenirArcSelectionne(){
		return arcSelectionne;
	}
	public boolean arcHighlighted(Arc arc){
		return highlightArcs.contains(arc);
	}
	
	public void reinitialiserSelection(){
		noeudsSelectionnees.clear();
		arcSelectionne=null;
	}
	public void selectionnerArc(Arc arc){
		arcSelectionne=arc;
	}
	public boolean contientUneSeuleSelection(){
		return noeudsSelectionnees.size()==1;
	}
	public boolean ajouterSelection(Element noeud,boolean deselectionnerSiExiste){
		if(!noeudsSelectionnees.contains(noeud)){ 
			noeudsSelectionnees.add(noeud);
			return true;
		} else if(deselectionnerSiExiste)
			noeudsSelectionnees.remove(noeud);
		return false;
	}
	public void ajouterSelection(List<Element> noeuds){
		for(Element noeud : noeuds){
			ajouterSelection(noeud,false);
		}
	}
	public List<Element> obtenirNoeudsSelectionnees(){
		return noeudsSelectionnees;
	}
	public void clearNoeuds(){
		noeudsSelectionnees.clear();
		highlight.clear();
	}
	public void clearArcs(){
		arcSelectionne=null;
		highlightArcs.clear();
	}
	public void clear(){
		//reinitialiserSelection();
		//reinitialiserHighlight();
		clearNoeuds();
		clearArcs();
	}
	public void reinitialiserHighlight(){
		highlight.clear();
		highlightArcs.clear();
	}
	public boolean noeudEstSelectionnee(Element noeud){
		return noeudsSelectionnees.contains(noeud);
	}
	public Rectangle obtenirRectangleSelection(){
		if(noeudsSelectionnees.size()==0)
			return null;
		_Point min = new _Point(), max = new _Point(), cmax = new _Point();
		
		int i=0;
		
		for(Element noeud : noeudsSelectionnees){
			_Point coordonnees = noeud.obtenirCoordonnees();
			
			cmax.x = coordonnees.x + noeud.obtenirLargeur();
			cmax.y = coordonnees.y + noeud.obtenirHauteur();
			
			if(i==0){
				min.x = coordonnees.x;
				max.x = cmax.x;
				
				min.y = coordonnees.y;
				max.y = cmax.y;
			} else {
				min.x = Math.min( coordonnees.x, min.x );
				min.y = Math.min( coordonnees.y, min.y );
				
				max.x = Math.max( cmax.x, max.x );
				max.y = Math.max( cmax.y, max.y );
			}
			i++;
		}
		
		return new Rectangle((int)min.x,(int)min.y,(int)(max.x - min.x),(int)(max.y - min.y));
	}
	public void definirHighlight(List<Element> noeuds){
		reinitialiserHighlight();
		for(Element noeud : noeuds){
			ajouterHighlight(noeud);
		}
	}
	public void ajouterHighlight(Element noeud){
		if(noeud != null && !highlight.contains(noeud))
			highlight.add(noeud);
	}
	public void ajouterHighlight(ImageDeFond image){
		if(image != null && !highlight.contains(image))
			highlight.add(image);
	}
	public void ajouterHighlight(Noeud noeud){
		if(noeud != null && !highlight.contains(noeud))
			highlight.add(noeud);
	}
	public void ajouterHighlight(Arc arc){
		if(arc != null && !highlightArcs.contains(arc))
			highlightArcs.add(arc);
	}
	public boolean arcEstSelectionne(Arc arc){
		return arcSelectionne==arc;
	}
	public List<Arc> obtenirArcsHightlight(){
		return highlightArcs;
	}
}
