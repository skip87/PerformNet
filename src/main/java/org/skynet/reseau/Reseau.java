package org.skynet.reseau;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import org.skynet.model.ControlleurReseau;
import org.skynet.model.NetworkStateManager;
import org.skynet.utils._Dimension;
import org.skynet.utils._Point;

public class Reseau implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -883544414461393646L;
	public static float dimensionMetresDefaut = 3;
	
	transient protected NetworkStateManager stateManager;// = new NetworkStateManager();

	private int fullStatistics = 1;
	
	private int idStationActuel = 1;
	//private final ControlleurReseau controlleur;

	// Dimension par defaut
	private int defaultPixelParMetre = 30;
	// c'est un workaround pour l'affichage
	// les coordonnees du reseau seront de la mm taille que l'ecran
	// sinon l'affichage sera pas beau
	
	//public static float multiplicateur=1;//1920; 
	private float pixelsParMetre=defaultPixelParMetre;

	
	private Entree entree = new Entree(this, new _Point(1,5));
	private Sortie sortie = new Sortie(this, new _Point(1,5));

	private ArrayList<Station> stations = new ArrayList<Station>();
	private ArrayList<Arc> arcs = new ArrayList<Arc>();
	private ArrayList<ImageDeFond> imagesDeFond = new ArrayList<ImageDeFond>();
	
	private boolean bgImageEditingDisabled = false;
	
	public void setBgImageEditingDisabled(boolean isDisabled){
		bgImageEditingDisabled = isDisabled;
	}
	public boolean getBgImageEditingDisabled(){
		return bgImageEditingDisabled;
	}
	
	public void addImageDeFond(_Point coordonnees,String path) throws IOException{
		imagesDeFond.add(new ImageDeFond(coordonnees, path));
		stateManager.addState("Ajout d'image de fond", this);
	}
	
	public ArrayList<ImageDeFond> obtenirImagesDeFond(){
		return imagesDeFond;
	}

	//private transient List<ReseauListener> listeners = new ArrayList<ReseauListener>();
	
	private boolean reseauValide = false;
	@SuppressWarnings("unused")
	private Dimension tailleEcran;

	@SuppressWarnings("unused")
	private _Point dimensionReseau = new _Point(0,0);

	private float distanceGrille;// = 1*multiplicateur;
	private boolean grilleActive = false;
	
	private transient String messageErreurPopup = null;
	public void setHistoryStateManager(NetworkStateManager stateManager){
		this.stateManager = stateManager;
	}
	List<Arc> obtenirArcsEntrants(Noeud noeud){
		LinkedList<Arc> arcs = new LinkedList<Arc>();
		for(Arc arc : obtenirArcs()){
			if(arc.obtenirArrivee()==noeud)
				arcs.add(arc);
		}
		return arcs;
	}
	List<Arc> obtenirArcsSortants(Noeud noeud){
		LinkedList<Arc> arcs = new LinkedList<Arc>();
		for(Arc arc : obtenirArcs()){
			if(arc.obtenirDepart()==noeud)
				arcs.add(arc);
		}
		return arcs;
	}
	public void structureReseauChange(String stateName){
		stateManager.addState(stateName, this);
		if(controleValiditeReseau()){
		}
	}
	
	public void setFullStatistics(int fullStatistics){
		this.fullStatistics = fullStatistics;
	}
	public int getFullStatistics(){
		return fullStatistics;
	}
	
	public boolean obtenirReseauValide(){
		return reseauValide;
	}
	public boolean controleValiditeReseau(){
		reseauValide = controleContientCycles()==false && controleNoeudsConnectees() && controleTauxUtilisationInferieur1();
		System.out.println("Reseau valide: " + reseauValide);
		return reseauValide;
	}
	@SuppressWarnings("unchecked")
	protected boolean controleContientCycles(){
		entree.estConnecte(true); // ce noeud peut pas avoir des cycles
		sortie.estConnecte(true); // ce noeud peut pas avoir des cycles
		for(Station noeud_actuel : stations){
			// il faut retirer les flags de connexion
			for(Station noeud : stations){
				noeud.estConnecte(false);
			}
			LinkedList<
				AbstractMap.SimpleEntry<
					Noeud
					,LinkedList<Noeud>
				>
			> noeuds = new LinkedList<AbstractMap.SimpleEntry<Noeud,LinkedList<Noeud>>>();
			// ajouter tous les enfants dans le pool, 
			// pour faire le controle de la recursuvite
			//int cnt = noeud_actuel.obtenirNombreArcsEntrants();
			LinkedList<Noeud> path = new LinkedList<Noeud>();
			path.add(noeud_actuel);
			for(Arc arc : noeud_actuel.obtenirArcsEntrants()){
				noeuds.add(
						new AbstractMap.SimpleEntry<>(arc.obtenirDepart(),path)
				);
				
			}
			//noeuds.add(noeud_actuel);
			while(noeuds.size()>0){
				AbstractMap.SimpleEntry<Noeud,LinkedList<Noeud>> noeud_data = noeuds.pollFirst();
				Noeud noeud = noeud_data.getKey();
				// Si le noeud est le meme que le noeud_actuel, alors nous avons un cycle
				if(noeud==noeud_actuel){
					return true;
				}
				path = (LinkedList<Noeud>) noeud_data.getValue().clone();
				path.add(noeud);
				// Si le noeud n'est pas encore connected nous allons proceder a l'analyse de noeud
				if(!noeud.estConnecte()){
					// Activer le flag noeud est connecte
					noeud.estConnecte(true);
					// Ajouter tous les noeuds "entrants"
					for(Arc arc : noeud.obtenirArcsEntrants()){
						//noeuds.add(noeud.obtenirArcEntrant(i).obtenirArrivee());
						noeuds.add(new AbstractMap.SimpleEntry<>(arc.obtenirDepart(),path));
					}
				}
			}
		}

		return false;
	}
	protected boolean controleNoeudsConnectees(){
		for(Noeud noeud : stations){
			noeud.estConnecte(false);
			noeud.estConnecteEntree(false);
		}
		entree.estConnecteEntree(true);
		sortie.estConnecteEntree(false);
		entree.estConnecte(false);
		sortie.estConnecte(false);
		
		boolean valide = true;
		// Creer la liste des noeuds qui sont connecte au reseau
		LinkedList<Noeud> noeuds = new LinkedList<Noeud>();
		// Ajouter le point de sortie comme le noeud de depart
		noeuds.add(entree);
		while(noeuds.size()>0){
			Noeud noeud = noeuds.pollFirst();
			
			// Si le noeud n'est pas connected nous allons proceder a l'analyse de noeud
			if(!noeud.estConnecte()){
				noeud.estConnecte(true);
				noeud.estConnecteEntree(true);
				List<Arc> arcs = noeud.obtenirArcsSortants();
				
				// Faire le check si le noeud contient arcs entants
				// sinon, si le noeud n'est pas une entree, alors le reseau n'est pas valide 
				if(arcs.size()==0 && noeud!=sortie){ 
					valide = false;
					//break;
				} else {
					for(Arc arc : arcs){
						noeuds.add(arc.obtenirArrivee());
					}
				}
			}
		}
		
		// Controle si l'entree est connecte au reseau 
		valide = valide && (sortie.estConnecte()==true);
		// Controle si tous les noeuds sont connecte au reseau
		for(Noeud noeud : stations){
			valide = valide && (noeud.estConnecte()==true);
		}

		return valide;
	}
	/*
	protected boolean controleNoeudsConnectees(){
		for(Noeud noeud : stations){
			noeud.estConnecte(false);
		}
		entree.estConnecte(false);
		sortie.estConnecte(false);
		
		boolean valide = true;
		// Creer la liste des noeuds qui sont connecte au reseau
		LinkedList<Noeud> noeuds = new LinkedList<Noeud>();
		// Ajouter le point de sortie comme le noeud de depart
		noeuds.add(sortie);
		while(noeuds.size()>0){
			Noeud noeud = noeuds.pollFirst();
			
			// Si le noeud n'est pas connected nous allons proceder a l'analyse de noeud
			if(!noeud.estConnecte()){
				noeud.estConnecte(true);
				List<Arc> arcs = noeud.obtenirArcsEntrants();
				
				// Faire le check si le noeud contient arcs entants
				// sinon, si le noeud n'est pas une entree, alors le reseau n'est pas valide 
				if(arcs.size()==0 && noeud!=entree){ 
					valide = false;
					break;
				}
				for(Arc arc : arcs){
					noeuds.add(arc.obtenirDepart());
				}
			}
		}
		
		// Controle si l'entree est connecte au reseau 
		valide = valide && (entree.estConnecte()==true);
		// Controle si tous les noeuds sont connecte au reseau
		for(Noeud noeud : stations){
			valide = valide && (noeud.estConnecte()==true);
		}

		return valide;
	}
	*/
	protected boolean controleTauxUtilisationInferieur1(){
		for(Station station : stations){
			if(station.obtenirTauxUtilisationStation()>=1)
				return false;
		}

		return true;
	}
	public List<Station> obtenirStations(){
		//ajout par Alex, pour calcul statistiques
		return stations;
	}
	public void dimensionsAffichageChanges(Dimension taille){
		tailleEcran = taille;
	}

	public Arc trouverArc(_Point coordonnees){
		for(Arc arc : arcs){
			//if(arc.intersecte(coordonnees, controlleur))
			if(arc.intersecte(coordonnees))
				return arc;
		}
		return null;
	}
	public _Point deplacerNoeuds(List<Element> noeuds,_Point diff){
		float minX=100000, minY=100000;
		// trouver la position minimale du haut gauche
		for(Element noeud : noeuds){
			_Point coordonnees = noeud.obtenirCoordonnees();
			minX = Math.min(coordonnees.x, minX); 
			minY = Math.min(coordonnees.y, minY); 
		}
		_Point unused_pixels = new _Point();
		if(minX<0){// si les coordonnees sont deja plus petits que 0, alors nous ne pouvons plus les deplacer a gauche
			if(diff.x<0){
				unused_pixels.x += diff.x;
				diff.x = 0;
			}
		} else {
			minX += diff.x;
			if(minX<0){// si le resultat est plus petit que 0, alors nous allons deplacer au maximum possible a gauche
				unused_pixels.x += minX;
				diff.x -= minX;
				minX=0;
			}
		}
		if(minY<0){// si les coordonnees sont deja plus petits que 0, alors nous ne pouvons plus les deplacer a gauche
			if(diff.y<0){
				unused_pixels.y += diff.y;
				diff.y = 0;
			}
		} else {
			minY += diff.y;
			if(minY<0){// si le resultat est plus petit que 0, alors nous allons deplacer au maximum possible a gauche
				unused_pixels.y += minY;
				diff.y -= minY;
				minY=0;
			}
		}
		
		if(obtenirGrilleActive()){
			_Point new_coord = collerALaGrille(new _Point(minX,minY));
			float diffx2 = -(new_coord.x-minX);
			float diffy2 = -(new_coord.y-minY);
			unused_pixels.x += diffx2;
			unused_pixels.y += diffy2;
			diff.x -= diffx2;
			diff.y -= diffy2;
		}
		
		for(Element noeud : noeuds){
			deplacerNoeud(noeud, diff);
		}
		return unused_pixels;
	}
	public void deplacerNoeud(Element noeud,_Point diff){ // deplacer noeud, coordonnees du reseau
		noeud.deplacer(diff);
		dimensionChange();
	}
	public void notifyNoeudsDeplaces(){
		stateManager.addState("Noeuds d\u00E9plac\u00E9s", this);
	}
	public Entree obtenirEntree(){
		return entree;
	}
	public Sortie obtenirSortie(){
		return sortie;
	}
	public Station ajouterStation(_Point coordonnees){
		//coordonnees = collerALaGrille(coordonnees);
		if(coordonnees.x<0) coordonnees.x = 0;
		if(coordonnees.y<0) coordonnees.y = 0;
		if(obtenirGrilleActive()){
			coordonnees = collerALaGrille(coordonnees);
		}
		Station station = new Station(this,coordonnees);
		station.definirNom("Station "+idStationActuel);
		idStationActuel++;
		stations.add(station);
		structureReseauChange("Station ajout\u00E9e "+station.obtenirNom());
		dimensionChange();
		return station;
	}
	public int obtenirStationsNombre(){
		return stations.size();
	}
	public Station obtenirStation(int id){
		return stations.get(id);
	}
	/*public void addListener(ReseauListener listener){
		listeners.add(listener);
	}
	*/
    protected void dimensionChange(_Point size) {
        // Notify everybody who might be interested.
        //for (ReseauListener hl : listeners)
        //    hl.dimensionChange(size);
    }
    public _Point obtenirDimensionReseau(){
		_Point max = new _Point();

		_Point coordonnees = entree.obtenirCoordonnees();
		max.x = entree.obtenirLargeur() + coordonnees.x;
		max.y = entree.obtenirHauteur() + coordonnees.y;
		
		coordonnees = sortie.obtenirCoordonnees();
		max.x = Math.max(max.x,sortie.obtenirLargeur() + coordonnees.x);
		max.y = Math.max(max.y,sortie.obtenirHauteur() + coordonnees.y);
		
		for(int i=0; i<stations.size(); i++){
			Noeud noeud = stations.get(i);
			coordonnees = noeud.obtenirCoordonnees();
			
			max.x = Math.max(max.x,noeud.obtenirLargeur() + coordonnees.x);
			max.y = Math.max(max.y,noeud.obtenirHauteur() + coordonnees.y);
		}
		return max;
    }
    // TODO: change it to protected after "new project" will be added, currently it is required for the entry/exit points definition
    public void dimensionChange(){
		
		dimensionChange(dimensionReseau=obtenirDimensionReseau());
	}
	public Arc ajouterArc(Noeud noeud1, Noeud noeud2){
		if(ajouterArcTest(noeud1,noeud2)==false){//noeud1==noeud2 || noeud1.peutContenirSortie()!=true || noeud2.peutContenirEntree()!=true || noeud1.existeCommeNoeudConnecte(noeud2)){
			return null;
		}
		Arc arc = new Arc(noeud1,noeud2);
		//noeud1.ajouterArcSortant(arc);
		//noeud2.ajouterArcEntrant(arc);
		System.out.println("Arc cree: "+noeud1.toString()+" - "+noeud2.toString());
		arcs.add(arc);
		
		this.structureReseauChange("Arc "+noeud1.obtenirNom()+" - "+noeud2.obtenirNom());
		return arc;
	}
	public boolean ajouterArcTest(Noeud noeud1, Noeud noeud2){
		// les controles de base qui ne doivent jamais arriver. on ne retourne aucun message (car ca devra jamais arriver :)
		if(noeud1==null || noeud2==null || noeud1==noeud2)
			return false;
		boolean ret = true;
		//String message = "", nl = "";
		if(noeud1.peutContenirSortie()!=true){
			//message += nl + "Sortie ne peut pas contenir arcs sortants";
			ajouterMessageErreurPopup("Sortie ne peut pas contenir arcs sortants");
			ret = false;
		}
		if(noeud2.peutContenirEntree()!=true){
			//message += nl + "Entree ne peut pas contenir arcs entrants";
			ajouterMessageErreurPopup("Entr\u00E9e ne peut pas contenir arcs entrants");
			ret = false;
		}
		if(noeud1.existeCommeNoeudConnecte(noeud2)){
			//message += nl + "L'arc ne peut etre ajoute pour eveiter les boucles";
			ajouterMessageErreurPopup("L'arc ne peut \u00E9tre ajout\u00E9 pour \u00E9viter les boucles");
			ret = false;
		}
		if(ret==false){
			//messageErreurPopup = message;
			//ajouterMessageErreurPopup(message);
		}
		return ret;
	}
	public void ajouterMessageErreurPopup(String message){
		if(messageErreurPopup==null)
			messageErreurPopup=message;
		else {
			messageErreurPopup += "\n"+message;
		}
	}
	public String obtenirMessageErreurPopup(){
		return messageErreurPopup;
	}
	public void reinitializerMessageErreurPopup(){
		messageErreurPopup = null;
	}
	private boolean pointDansNoeud(_Point coordonnees,Element noeud){
		java.awt.geom.Rectangle2D rect = noeud.obtenirRectangle();
		if(rect.contains(coordonnees.x, coordonnees.y))
			return true;
		return false;
	}
	public Noeud obtenirNoeudClosest(_Point coordonnees){
		//_Point coord_systeme = transformerPixelsEnReseau(coordonnees);
		_Point centre;
		float size;
		// il faut iterer de plus haut a plus bas, pour faire le highlight sur le dernier element affichee
		float closest = Float.MAX_VALUE;
		Noeud cnoeud = null;
		for(Station station : stations){
			centre = station.obtenirCoordonneesCentre();
			size = (centre.x - coordonnees.x)*(centre.x - coordonnees.x) + (centre.y - coordonnees.y)*(centre.y - coordonnees.y);
			if(size<closest){
				cnoeud = station;
				closest = size;
			}
		}
		
		centre = entree.obtenirCoordonneesCentre();
		size = (centre.x - coordonnees.x)*(centre.x - coordonnees.x) + (centre.y - coordonnees.y)*(centre.y - coordonnees.y);
		if(size<closest){
			cnoeud = entree;
			closest = size;
		}
		
		centre = sortie.obtenirCoordonneesCentre();
		size = (centre.x - coordonnees.x)*(centre.x - coordonnees.x) + (centre.y - coordonnees.y)*(centre.y - coordonnees.y);
		if(size<closest){
			cnoeud = sortie;
			closest = size;
		}

		if(cnoeud!=null && closest<(cnoeud.obtenirHauteur()*cnoeud.obtenirHauteur()+cnoeud.obtenirHauteur()*cnoeud.obtenirHauteur())/2)
			return cnoeud;
		return null;
	}
	public boolean estStation(Noeud noeud){
		return noeud instanceof Station;
	}
	public boolean estEntree(Noeud noeud){
		return noeud instanceof Entree;
	}
	public boolean estSortie(Noeud noeud){
		return noeud instanceof Sortie;
	}
	public void modifierSortie(Sortie sortie, float f, float g, String path) {
		if(sortie.modifier(f,g,path)){
			stateManager.addState("Entree modifie", this);
		}
	}
	public void modifierImageDeFond(ImageDeFond ImageDeFond,float f, float g, String path){
		if(ImageDeFond.modifier(f,g,path)){
			stateManager.addState("ImageDeFond modifie", this);
		}
	}
	public void modifierEntree(Entree entree, float tauxArrivee, float g, float h, String path) {
		if(entree.modifier(tauxArrivee,g,h,path)){
			stateManager.addState("Entree modifie", this);
		}
	}
	public void modifierArc(Arc arc, float poid) {
		if(arc.modifier(poid)){
			stateManager.addState("Arc modifie "+arc.obtenirDepart().obtenirNom()+"-"+arc.obtenirArrivee().obtenirNom(), this);
		}
	}
	public void modifierStation(Station station,String nomStation,float vitesse, int nombrePostes,float largeur,float hauteur, String pathImage){
		if(station.modifier(nomStation,vitesse,nombrePostes,largeur,hauteur,pathImage)){
			stateManager.addState("Station modifie "+station.obtenirNom(), this);
		}
	}
	public ImageDeFond obtenirImage(_Point coordonnees){
		if (!bgImageEditingDisabled){
			List<ImageDeFond> images = obtenirImagesDeFond();
			for(int i=images.size()-1; i>=0; i--){
				if(pointDansNoeud(coordonnees,images.get(i)))
					return images.get(i);
			}
		}
		return null;
		
	}
	public Noeud obtenirNoeud(_Point coordonnees){
		//_Point coord_systeme = transformerPixelsEnReseau(coordonnees);

		// il faut iterer de plus haut a plus bas, pour faire le highlight sur le dernier element affichee
		for(int i=stations.size()-1; i>=0; i--){
			if(pointDansNoeud(coordonnees,stations.get(i)))
				return stations.get(i);
		}
		
		if(pointDansNoeud(coordonnees,entree))
			return entree;
		
		if(pointDansNoeud(coordonnees,sortie))
			return sortie;

		return null;
	}
	public void deplacerArcDebut(Arc arc,_Point coordonnees){
		Noeud noeud = obtenirNoeud(coordonnees);
		arc.estActif(false);
		if(ajouterArcTest(noeud, arc.obtenirArrivee())){
			//arc.obtenirDepart().enleverArcSortant(arc);
			arc.definirDepart(noeud);
			//noeud.ajouterArcSortant(arc);
			structureReseauChange("D\u00E9but de l'arc deplac\u00E9");
		}
		arc.estActif(true);
	}
	public void deplacerArcFin(Arc arc,_Point coordonnees){
		Noeud noeud = obtenirNoeud(coordonnees);
		arc.estActif(false);
		if(ajouterArcTest(arc.obtenirDepart(),noeud)){
			//arc.obtenirArrivee().enleverArcEntrant(arc);
			arc.definirArrivee(noeud);
			//noeud.ajouterArcEntrant(arc);
			structureReseauChange("Fin de l'arc deplac\u00E9");
		}
		arc.estActif(true);
	}
	public void supprimerArc(Arc arc){
		String noeudsText = arc.obtenirDepart().obtenirNom() + " - " + arc.obtenirArrivee().obtenirNom();
		arc.supprimer();
		arcs.remove(arc);
		structureReseauChange("Arc supprim\u00E9 "+noeudsText);
	}
	public boolean estNoeud(Element noeud){
		return noeud instanceof Noeud;
	}
	public boolean estImage(Element noeud){
		return noeud instanceof ImageDeFond;
	}
	public void supprimerNoeud(Element noeud,boolean nullifyDansListe){
		if(estNoeud(noeud)){
			supprimerNoeud((Noeud)noeud,nullifyDansListe);
		} else if(estImage(noeud)){
			supprimerElement((ImageDeFond)noeud,nullifyDansListe);
		}
	}
	
	public void supprimerElement(ImageDeFond image,boolean nullifyDansListe){
		int idx = imagesDeFond.indexOf(image);
		if(idx>=0){
			imagesDeFond.remove(idx);
			structureReseauChange("image supprim\u00E9e ");
		}
	}
	public void supprimerNoeud(Noeud noeud,boolean nullifyDansListe){
		int idx = stations.indexOf(noeud);
		if(idx>=0){
			for(Arc arc : noeud.obtenirArcsEntrants()){
				arcs.remove(arc);
			}
			for(Arc arc : noeud.obtenirArcsSortants()){
				arcs.remove(arc);
			}
			noeud.supprimer();
			if(!nullifyDansListe){
				//stations.remove(idx);
				stations.remove(noeud);
			} else {
				stations.set(idx, null);
			}
			structureReseauChange("Noeud supprim\u00E9 "+noeud.obtenirNom());
		}
	}
	protected void init(){
		assert(entree!=null);
		assert(sortie!=null);
		//this.controlleur = controlleur;
		Noeud.genericSize = new _Dimension(dimensionMetresDefaut,dimensionMetresDefaut);
		try {
			Noeud.genericImage = ImageIO.read(ControlleurReseau.class.getResource("/images/station.png"));
			float a1 = dimensionMetresDefaut/Noeud.genericImage.getWidth(),
					a2 = dimensionMetresDefaut/Noeud.genericImage.getHeight();
			float aspect = Math.max(a1, a2);
			//Noeud.genericSize = new Dimension((int)controlleur.transformerPixelsEnReseau(Noeud.genericImage.getWidth()*.03f), (int)controlleur.transformerPixelsEnReseau(Noeud.genericImage.getHeight()*.03f));
			Noeud.genericSize = new _Dimension(
				(aspect*Noeud.genericImage.getWidth())
				,(aspect*Noeud.genericImage.getHeight())
			);
			//entree.definirTaille(taille);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			BufferedImage entry_image = ImageIO.read(ControlleurReseau.class.getResource("/images/entree.png"));
			obtenirEntree().definirImage(entry_image);
			//obtenirEntree().definirTaille(new Dimension((int)controlleur.transformerPixelsEnReseau(entry_image.getWidth()*.3f), (int)controlleur.transformerPixelsEnReseau(entry_image.getHeight()*.3f)));
			float a1 = dimensionMetresDefaut/entry_image.getWidth(),
					a2 = dimensionMetresDefaut/entry_image.getHeight();
			float aspect = Math.max(a1, a2);
			//Noeud.genericSize = new Dimension((int)controlleur.transformerPixelsEnReseau(Noeud.genericImage.getWidth()*.03f), (int)controlleur.transformerPixelsEnReseau(Noeud.genericImage.getHeight()*.03f));
			obtenirEntree().definirTaille(new _Dimension(
				(aspect*entry_image.getWidth())
				,(aspect*entry_image.getHeight())
			));
			//entree.definirTaille(taille);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			BufferedImage exit_image = ImageIO.read(ControlleurReseau.class.getResource("/images/sortie.png"));
			obtenirSortie().definirImage(exit_image);
			//obtenirSortie().definirTaille(new Dimension((int)controlleur.transformerPixelsEnReseau(exit_image.getWidth()*.3f), (int)controlleur.transformerPixelsEnReseau(exit_image.getHeight()*.3f)));
			float a1 = dimensionMetresDefaut/exit_image.getWidth(),
					a2 = dimensionMetresDefaut/exit_image.getHeight();
			float aspect = Math.max(a1, a2);
			obtenirSortie().definirTaille(new _Dimension(
				(aspect*exit_image.getWidth())
				,(aspect*exit_image.getHeight())
			));
			//entree.definirTaille(taille);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		init();
	}
	public Reseau(){
		entree.definirNom("Entree");
		sortie.definirNom("Sortie");
		//Station.dimensionLigneAttenteDefaut = (int)transformerPixelsEnReseau(50);
		init();
	}
	
	/*
	protected void calculerToutesLesStatistiques(){
		calculerNombreMEntitesDansSysteme();
		List<Station> listeStations = obtenirStations();
		
		for(int ind=0; ind<listeStations.size(); ind++){
			listeStations.get(ind).calculerToutesLesStatistiques();
			listeStations.get(ind).toString();
		}
	}
	*/
	
	public float obtenirTempsMoyenPrisParUneEntite(){
		return obtenirNombreMEntitesDansSysteme()/obtenirEntree().obtenirTauxArrive();
	}
	public float obtenirNombreMEntitesDansSysteme(){
		//Somme de L de toutes les stations
		float nbEntitesSysteme = 0;
		
		List<Station> listeStations = obtenirStations();
		
		for(Station station : listeStations){
			if(station.estConnecteEntree())
				nbEntitesSysteme += station.obtenirNombreMoyenEntitesDansStation();
		}
		
		return nbEntitesSysteme;
	}
	
	public List<Arc> obtenirArcs(){
		return arcs;
	}
	public List<Element> obtenirNoeudsDansRegion(_Point start,_Point end){
		
		LinkedList<Element> noeuds = new LinkedList<Element>();
		if(start!=null && end!=null){
			if(entree.intersecte(start,end)){
			//ajouterHighlight(entree);
				noeuds.add(entree);
			}
			if(sortie.intersecte(start,end)){
				//ajouterHighlight(sortie);
				noeuds.add(sortie);
			}
			for(int i=0; i<stations.size(); i++){
				Noeud noeud = stations.get(i);
				if(noeud.intersecte(start,end)){
					//ajouterHighlight(noeud);
					noeuds.add(noeud);
				}
			}
			if (!bgImageEditingDisabled) {
				for(int i=0; i<imagesDeFond.size(); i++){
					ImageDeFond noeud = imagesDeFond.get(i);
					if(noeud.intersecte(start,end)){
						//ajouterHighlight(noeud);
						noeuds.add(noeud);
					}
				}
			}
		}
		return noeuds;
		
	}

	public int transformerMetresEnPixels(float metres){
		return Math.round(metres*pixelsParMetre);
	}
	public float transformerMetresEnPixelsf(float metres){
		return metres*pixelsParMetre;
	}
	public float transformerPixelsEnMetres(float pixels){
		return pixels/pixelsParMetre;
	}
	public _Point transformerPixelsEnMetres(Point pixels){
		return new _Point(transformerPixelsEnMetres(pixels.x),transformerPixelsEnMetres(pixels.y));
	}
	public Point transformerMetresEnPixels(_Point metres){
		return new Point(transformerMetresEnPixels(metres.x),transformerMetresEnPixels(metres.y));
	}

	public float obtenirPixelsParMetre(){
		return pixelsParMetre;
	}
	
	
	public float obtenirFacteurZoom(){
		return pixelsParMetre;//1/metresParPixel;
	}
	public void zoomReset(){
		definirPixelsParMetre(defaultPixelParMetre);
	}
	public void zoomIn(boolean accelerer){
		definirPixelsParMetre(this.pixelsParMetre + (accelerer ? 5 : 1));
	}
	public void zoomOut(boolean accelerer){
		definirPixelsParMetre(this.pixelsParMetre - (accelerer ? 5 : 1));
	}
	public void definirPixelsParMetre(float pixelsParMetre){
		definirPixelsParMetre(pixelsParMetre,false);
	}
	public void definirPixelsParMetre(float pixelsParMetre,boolean nocheck){
		if(!nocheck && pixelsParMetre<15)
			pixelsParMetre = 15;
		this.pixelsParMetre = pixelsParMetre;
		dimensionChange();
	}
	
	public _Point collerALaGrille(_Point coordonnees){
		if(grilleActive){
			coordonnees = new _Point(
				((int)(coordonnees.x/distanceGrille))*distanceGrille
				,((int)(coordonnees.y/distanceGrille))*distanceGrille
			);
		}
		return coordonnees;
	}
	public float obtenirDistanceGrille(){
		return distanceGrille;// = 1*multiplicateur;
	}
	public void definirDistanceGrille(float metres){
		// Definir la taille minimum de la distance grille
		if(metres<0.2f)
			metres=0.2f;
		distanceGrille = metres;
	}
	public boolean obtenirGrilleActive(){
		return grilleActive;
	}
	public void definirGrilleActive(boolean grilleActive){
		this.grilleActive=grilleActive;
	}
	
	
}
