package org.skynet.model;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.skynet.model.NetworkStateManager.NetworkState;
import org.skynet.reseau.Arc;
import org.skynet.reseau.Element;
import org.skynet.reseau.Entree;
import org.skynet.reseau.ImageDeFond;
import org.skynet.reseau.Noeud;
import org.skynet.reseau.Reseau;
import org.skynet.reseau.Sortie;
import org.skynet.reseau.Station;
import org.skynet.utils.SkynetImage;
import org.skynet.utils.SkynetImageInfo;
import org.skynet.utils._Point;

public class ControlleurReseau {
	private Reseau reseau;

	NetworkStateManager stateManager;// = new NetworkStateManager();

	// un workaround avant l'implantation de creation de nouveau projet
	private boolean sortieInit = false; 
	
	private String lastFilename = null;
	
	
	
	private Noeud noeud_temp;
	
	private _Point selectionStart = null;
	private _Point selectionFin = null;
	
	private boolean grilleAffiche = false;
	private Dimension taille = null;
	
	ControlleurSelection selection;
	
	public List<ImageDeFond> obtenirImagesDeFond(){
		return reseau.obtenirImagesDeFond();
	}
	public void setBgImageEditingDisabled(boolean isDisabled){
		reseau.setBgImageEditingDisabled(isDisabled);
	}
	public boolean getBgImageEditingDisabled(){
		return reseau.getBgImageEditingDisabled();
	}
	public boolean addImageDeFond(String path){
		try {
			reseau.addImageDeFond(new _Point(0,0),path);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			reseau.ajouterMessageErreurPopup("Erreur d'ajout de l'image: \n"+e.getLocalizedMessage());
		}
		return false;
	}
	public Reseau obtenirReseau(){
		return reseau;
	}
	public boolean obtenirReseauValide(){
		return reseau.obtenirReseauValide();
	}
	public boolean controleValiditeReseau(){
		return reseau.controleValiditeReseau();
	}
	
	public void definirDebutSelection(Point coordonnees){
		selectionStart = metres(coordonnees);
	}
	public _Point obtenirDebutSelection(){
		return selectionStart;
	}
	public void definirFinSelection(Point coordonnees){
		selectionFin = metres(coordonnees);
	}
	public _Point obtenirFinSelection(){
		return selectionFin;
	}
	public List<Station> obtenirListeStationsReseau(){
		//ajout par Alex, pour calcul statistiques
		return reseau.obtenirStations();
	}
	public void ajouterMessageErreurPopup(String message){
		reseau.ajouterMessageErreurPopup(message);
	}
	public String obtenirMessageErreurPopup(){
		return reseau.obtenirMessageErreurPopup();
	}
	public void reinitializerMessageErreurPopup(){
		reseau.reinitializerMessageErreurPopup();
	}
	public void ajouterMessagePopup(String message){
		reseau.ajouterMessageErreurPopup(message);
	}
	public String pickMessageErreurPopup(){
		String message = obtenirMessageErreurPopup();
		reinitializerMessageErreurPopup();
		return message;
	}
	
	public boolean dimensionsAffichageChanges(Dimension taille){
		boolean activated = false;
		// un hack que va falloir changer apres avoir creer l'ecran d'introduction
		if(!sortieInit && taille!=null){
			activated = true;
			reseau.obtenirEntree().definirCoordonnees(0,metres(taille.height/2)-reseau.obtenirEntree().obtenirHauteur()/2);
			//sortie.definirCoordonnees(transformerPixelsEnMetres(new Point(taille.width-sortie.obtenirLargeur(),taille.height/2)));
			reseau.obtenirSortie().definirCoordonnees(
				metres(taille.width-10)-reseau.obtenirSortie().obtenirLargeur()
				,metres(taille.height/2)-reseau.obtenirSortie().obtenirHauteur()/2
			);
			sortieInit = true;
			reseau.dimensionChange();
			stateManager.replaceState(0, "Initialisation initiale", reseau);
		}
		reseau.dimensionsAffichageChanges(taille);
		this.taille = taille;
		return activated;
	}
	public void definirNoeudTemp(Noeud noeud){
		noeud_temp = noeud;
	}
	public Noeud obtenirNoeudTemp(){
		return noeud_temp;
	}
	public ImageDeFond obtenirImage(_Point coordonnees){
		return reseau.obtenirImage(coordonnees);
	}
	public ImageDeFond obtenirImage(Point coordonnees){
		return obtenirImage(metres(coordonnees));
	}
	public List<Arc> obtenirArcs(){
		return reseau.obtenirArcs();
	}
	public Arc ajouterArc(Noeud noeud1, Noeud noeud2){
		return reseau.ajouterArc(noeud1, noeud2);
	}
	public boolean noeudHighlighted(Element noeud){
		return selection.noeudHighlighted(noeud);
	}
	protected void deplacerArcDebut(Arc arc,Point coordonnees){
		reseau.deplacerArcDebut(arc, metres(coordonnees));
	}
	protected void deplacerArcFin(Arc arc,Point coordonnees){
		reseau.deplacerArcFin(arc, metres(coordonnees));
	}
	public void notifyNoeudsDeplaces(){
		reseau.notifyNoeudsDeplaces();
	}
	public void deplacerArcDebut(Point coordonnees){
		Arc arcSelectionne = selection.obtenirArcSelectionne();
		if(arcSelectionne!=null)
			deplacerArcDebut(arcSelectionne,coordonnees);
	}
	public void deplacerArcFin(Point coordonnees){
		Arc arcSelectionne = selection.obtenirArcSelectionne();
		if(arcSelectionne!=null)
			deplacerArcFin(arcSelectionne,coordonnees);
	}
	public boolean arcHighlighted(Arc arc){
		return selection.arcHighlighted(arc);
	}
	public void reinitialiserSelection(){
		selection.reinitialiserSelection();
	}
	public void selectionnerArc(Arc arc){
		selection.selectionnerArc(arc);
	}
	public void selectionnerArc(Point coordonnees){
		selectionnerArc(trouverArc(coordonnees));
	}
	public boolean contientUneSeuleSelection(){
		return selection.contientUneSeuleSelection();
	}
	public Arc trouverArc(Point coordonnees){
		_Point reseau_coord = metres(coordonnees);
		return reseau.trouverArc(reseau_coord);
	}
	public boolean ajouterSelection(Element noeud){
		return ajouterSelection(noeud,false);
	}
	public boolean ajouterSelection(Element noeud,boolean deselectionnerSiExiste){
		return selection.ajouterSelection(noeud, deselectionnerSiExiste);
	}
	public Point deplacerNoeudsSelectionnees(Point diff){
		return pixels(reseau.deplacerNoeuds(selection.obtenirNoeudsSelectionnees(),metres(diff)));
	}
	public void supprimerNoeud(Element noeud){
		supprimerNoeud(noeud,false);
	}
	private void supprimerArc(Arc arc){
		reseau.supprimerArc(arc);
	}
	private void supprimerNoeud(Element noeud,boolean nullifyDansListe){
		reseau.supprimerNoeud(noeud, nullifyDansListe);
	}
	public void supprimerNoeudsSelectionnees(){
		//ArrayList<>
		for(Element noeud : selection.obtenirNoeudsSelectionnees()){
			supprimerNoeud(noeud);
		}
		//selection.clear();
		selection.clearNoeuds();
	}
	public void supprimerArcsSelectionnees(){
		Arc arcSelectionne = selection.obtenirArcSelectionne();
		if(arcSelectionne!=null)
			supprimerArc(arcSelectionne);
		//selection.clear();
		selection.clearArcs();
	}
	public Element obtenirNoeudSelectionne(){
		List<Element> noeuds = obtenirNoeudsSelectionne();
		return noeuds.size()>0 ? noeuds.get(0) : null; 
	}
	public List<Element> obtenirNoeudsSelectionne(){
		return selection.obtenirNoeudsSelectionnees(); 
	}
	public boolean noeudEstSelectionnee(Element noeud){
		return selection.noeudEstSelectionnee(noeud);
	}
	public Rectangle obtenirRectangleSelection(){
		return selection.obtenirRectangleSelection();
	}
	public void definirHighlight(){
		definirHighlight(obtenirDebutSelection(), obtenirFinSelection());
	}
	public void definirHighlight(_Point start,_Point end){
		selection.definirHighlight(reseau.obtenirNoeudsDansRegion(start,end));
	}
	public void selectionnerNoeudsRegion(){
		selectionnerNoeudsRegion(obtenirDebutSelection(), obtenirFinSelection());
	}
	public void selectionnerNoeudsRegion(_Point start,_Point end){
		List<Element> noeuds = reseau.obtenirNoeudsDansRegion(start,end);
		selection.ajouterSelection(noeuds);
	}
	public void reinitialiserHighlight(){
		selection.reinitialiserHighlight();
	}
	public void ajouterHighlight(ImageDeFond image){
		selection.ajouterHighlight(image);
	}
	public void ajouterHighlight(Noeud noeud){
		selection.ajouterHighlight(noeud);
	}
	public void ajouterHighlight(Arc arc){
		selection.ajouterHighlight(arc);
	}
	public boolean arcEstSelectionne(Arc arc){
		return selection.arcEstSelectionne(arc);
	}
	public Arc obtenirArcHighlight(Point coordonnees){
		return trouverArc(coordonnees);
	}
	public void rechercheHightlight(Point coordonnees){
		Noeud noeud = obtenirNoeud(coordonnees);
		ajouterHighlight(noeud);
		if(noeud==null){
			Arc arc = obtenirArcHighlight(coordonnees);
			if(arc!=null){
				ajouterHighlight(arc);
			} else {
				ImageDeFond image = obtenirImageHighlight(coordonnees);
				ajouterHighlight(image);
			}
		}
	}
	public Arc obtenirHighlightArc(){
		List<Arc> highlightArcs = selection.obtenirArcsHightlight();
		return highlightArcs.size() > 0 ? highlightArcs.get(0) : null;
	}
	public Element obtenirHighlight(){
		List<Element> highlight = selection.obtenirNoeudsHightlight();
		return highlight.size() > 0 ? highlight.get(0) : null;
	}
	/*
	public _Point transformerPixelsEnReseau(Point coordonnees){
		return new _Point(transformerPixelsEnReseau(coordonnees.x),transformerPixelsEnReseau(coordonnees.y));
	}
	*/
	public void modifierStation(Station station,String nomStation,float vitesse, int nombrePostes, float largeur, float hauteur, String pathImage){
		reseau.modifierStation(station,nomStation,vitesse,nombrePostes,largeur,hauteur,pathImage);
	}
	public void modifierEntree(Entree entree,float f, float g, float h, String path){
		reseau.modifierEntree(entree,f,g,h, path);
	}
	public void modifierArc(Arc arc, float Poid) {
		reseau.modifierArc(arc,Poid);
		
	}
	public void modifierSortie(Sortie sortie, float f, float g, String path) {
		reseau.modifierSortie(sortie,f,g, path);
	}
	public void modifierImageDeFond(org.skynet.reseau.ImageDeFond imageDeFond,
			float f, float g, String path) {
		reseau.modifierImageDeFond(imageDeFond,f,g, path);	
	}
	
	public boolean estEntree(Noeud noeud){
		return reseau.estEntree(noeud);
	}
	public boolean estSortie(Noeud noeud){
		return reseau.estSortie(noeud);
	}
	public boolean estStation(Noeud noeud){
		return reseau.estStation(noeud);
	}
	public boolean estImage(Element element){
		return reseau.estImage(element);
	}
	public ImageDeFond obtenirImageHighlight(Point coordonnees){
		_Point coord_systeme = metres(coordonnees);
		
		return reseau.obtenirImage(coord_systeme);
	}
	public Noeud obtenirNoeud(Point coordonnees){
		_Point coord_systeme = metres(coordonnees);
		
		return reseau.obtenirNoeud(coord_systeme);
	}
	public Noeud obtenirNoeudClosest(Point coordonnees){
		_Point coord_systeme = metres(coordonnees);
		
		return reseau.obtenirNoeudClosest(coord_systeme);
	}
	public ControlleurReseau(){
		// Initialisation du reseau + des images par defaut
		newReseau();
		// Un test trouve sur internet
		/*
			Station ip = reseau.ajouterStation(new _Point(5,2));
			ip.definirVitesseMTraitementEntitesParPoste(6);
			ip.definirNombrePostes(2);
			Station cp = reseau.ajouterStation(new _Point(5,6));
			cp.definirVitesseMTraitementEntitesParPoste(20);
			Station pc = reseau.ajouterStation(new _Point(5,10));
			pc.definirVitesseMTraitementEntitesParPoste(0.857f);
			pc.definirNombrePostes(4);
			
			reseau.obtenirEntree().definirTauxArrive(10);
			
			reseau.ajouterArc(reseau.obtenirEntree(), ip);
			reseau.ajouterArc(ip, cp).definirTauxPassage(.8f);
			reseau.ajouterArc(ip, reseau.obtenirSortie()).definirTauxPassage(.2f);
			reseau.ajouterArc(cp, pc).definirTauxPassage(.4f);
			reseau.ajouterArc(cp, reseau.obtenirSortie()).definirTauxPassage(.6f);
			reseau.ajouterArc(pc, reseau.obtenirSortie()).definirTauxPassage(1);
		*/
	}
	/*
	 * Ajouter station avec les coordonnes ecran (pixels)
	 */
	public Station ajouterStation(Point coordonnees){
		return ajouterStation(new _Point(metres(coordonnees.x),metres(coordonnees.y)));
	}
	/*
	 * Ajouter la station avec les coordonnees reseau
	 */
	public Station ajouterStation(_Point coordonnees){
		return reseau.ajouterStation(coordonnees);
	}
	public List<Station> obtenirStations(){
		return reseau.obtenirStations();
	}
	
	public int pixels(float metres){
		return reseau.transformerMetresEnPixels(metres);
	}
	public float pixelsf(float metres){
		return reseau.transformerMetresEnPixelsf(metres);
	}
	public Point pixels(_Point metres){
		return reseau.transformerMetresEnPixels(metres);
	}
	public _Point metres(Point pixels){
		return reseau.transformerPixelsEnMetres(pixels);
	}
	public float metres(int pixels){
		return reseau.transformerPixelsEnMetres(pixels);
	}
	/*
	public float transformerMetresEnReseau(float metres){
		return reseau.transformerMetresEnReseau(metres);
	}
	public float transformerMetresEnPixels(float metres){
		return reseau.transformerMetresEnPixels(metres);
	}
	public float transformerPixelsEnMetres(float pixels){
		return reseau.transformerPixelsEnMetres(pixels);
	}
	public float transformerReseauEnMetres(float coord_reseau){
		return reseau.transformerReseauEnMetres(coord_reseau);
	}
	public Point transformerReseauEnPixels(_Point coordonnees){
		return reseau.transformerReseauEnPixels(coordonnees);
	}
	public float transformerReseauEnPixels(float metres){
		return reseau.transformerReseauEnPixels(metres);
	}
	public float transformerPixelsEnReseau(float pixels){
		//return pixels*metresParPixel;
		return reseau.transformerPixelsEnReseau(pixels);
	}
	*/
	
	public float obtenirPixelsParMetre(){
		return reseau.obtenirPixelsParMetre();
	}
	public void definirPixelsParMetre(float pixels){
		reseau.definirPixelsParMetre(pixels,false);
	}
	public void definirPixelsParMetre(float pixels,boolean nocheck){
		reseau.definirPixelsParMetre(pixels,nocheck);
	}
	
	
	/*public float scale(){
		return obtenirFacteurZoom();
	}
	public float obtenirFacteurZoom(){
		return reseau.obtenirFacteurZoom();//1/metresParPixel;
	}*/
	
	public Entree obtenirEntree(){
		return reseau.obtenirEntree();
	}
	public Sortie obtenirSortie(){
		return reseau.obtenirSortie();
	}
	
	public void zoomReset(){
		reseau.zoomReset();
		//save("test.txt");
	}
	public void zoomIn(boolean accelerer){
		reseau.zoomIn(accelerer);;
	}
	public void zoomOut(boolean accelerer){
		//load("test.txt");
		reseau.zoomOut(accelerer);;
	}
	/*
	public void addListener(ReseauListener listener){
		//listeners.add(listener);
		reseau.addListener(listener);
	}
	*/
	public float obtenirDistanceGrille(){
		return reseau.obtenirDistanceGrille();// = 1*multiplicateur;
	}
	public void definirDistanceGrille(float metres){
		reseau.definirDistanceGrille(metres);
	}
	public boolean obtenirGrilleActive(){
		return reseau.obtenirGrilleActive();
	}
	public void definirGrilleActive(boolean grilleActive){
		reseau.definirGrilleActive(grilleActive);
	}
	public boolean obtenirGrilleAffiche(){
		return grilleAffiche;
	}
	public void definirGrilleAffiche(boolean grilleAffiche){
		this.grilleAffiche=grilleAffiche;
	}
	public _Point obtenirDimensionReseau(){
		return reseau.obtenirDimensionReseau();
	}
	
	public _Point collerALaGrille(_Point coordonnees){
		return reseau.collerALaGrille(coordonnees);
	}
	public Point collerALaGrille(Point coordonnees){
		return pixels(reseau.collerALaGrille(metres(coordonnees)));
	}
	
	public float obtenirNombreMEntitesDansSysteme(){
		return reseau.obtenirNombreMEntitesDansSysteme();
	}
	public float obtenirTempsMoyenPrisParUneEntite(){
		return reseau.obtenirTempsMoyenPrisParUneEntite();
	}
	public void setFullStatistics(int fullStatistics){
		reseau.setFullStatistics(fullStatistics);;
	}
	public int getFullStatistics(){
		return reseau.getFullStatistics();
	}

	public String obtenirLastFilename(){
		return lastFilename;
	}
	public boolean save(String path){
	      try
	      {
	    	  //Station item = new Station(reseau,new _Point(100,100));
	         java.io.FileOutputStream fileOut = new java.io.FileOutputStream(path);
	         java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(fileOut);
	         out.writeObject(stateManager);
	         out.writeObject(SkynetImage.cachedImages);
	         out.writeObject(reseau);
	         out.close();
	         fileOut.close();
	         // Si tout a bien fonctionne, alors nous allons sauvegarder
         	lastFilename = path;
	         return true;
	      } catch(java.io.IOException i) {
	         i.printStackTrace();
	      }
	      return false;
	}
	public void newReseau(){
		lastFilename = null;
		reseau = new Reseau();
		stateManager = new NetworkStateManager(reseau);
		reseau.setHistoryStateManager(stateManager);
		stateManager.setChanged();
		selection = new ControlleurSelection();
		definirDistanceGrille(1);
		// We are going to fake in order to place at a good point the entry point and the exit point
		if(taille!=null && sortieInit==true){
			sortieInit=false;
			dimensionsAffichageChanges(taille);
		}
	}
	@SuppressWarnings("unchecked")
	public boolean load(String path){
		Map<String,SkynetImageInfo> imagesBackup = SkynetImage.cachedImages;
	      try {
			java.io.FileInputStream fileIn = new java.io.FileInputStream(path);
			java.io.ObjectInputStream in = new java.io.ObjectInputStream(fileIn);
			Map<String,SkynetImageInfo> cachedImages; 
			NetworkStateManager stateManager;
			stateManager = (NetworkStateManager)in.readObject();
			cachedImages = (Map<String,SkynetImageInfo>)in.readObject();
			SkynetImage.cachedImages = cachedImages;
			Reseau reseau = (Reseau)in.readObject();
			
			if(SkynetImage.cachedImages!=null && stateManager!=null){
				imagesBackup=null;
				this.reseau = reseau;
				reseau.setHistoryStateManager(stateManager);
				this.stateManager = stateManager;
				stateManager.setChanged();
			}
			in.close();
			fileIn.close();
			lastFilename = path;
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(imagesBackup!=null){
				SkynetImage.cachedImages = imagesBackup;
			}
		}
	    return false;
	}
	
	/*
	 * Controle des elements d'historique
	 */
	public boolean hasNextHistoryState(){
		return stateManager.hasNextState();
		//return reseau.hasNexHistoryState();
	}
	public boolean hasPrevHistoryState(){
		return stateManager.hasPreviousState();
		//return reseau.hasPrevHistoryState();
	}
	public void prevHistoryState(){
		Reseau reseauNew = stateManager.prevState();
		//reseau.prevHistoryState();
		if(reseauNew!=null){
			reseau = reseauNew;
			reseau.setHistoryStateManager(stateManager);
			reseau.controleValiditeReseau();//pour enlever le "Reseau n'est pas valide"
		}
	}
	public void nextHistoryState(){
		Reseau reseauNew = stateManager.nextState();
		//reseau.prevHistoryState();
		if(reseauNew!=null){
			reseau = reseauNew;
			reseau.setHistoryStateManager(stateManager);
			reseau.controleValiditeReseau();//pour enlever le "Reseau n'est pas valide"
		}
	}
	public List<NetworkState> getHistoryStates(){
		return stateManager.getStates();//reseau.getHistoryStates();
	}
	public int getActiveHistoryState(){
		return stateManager.getActiveState();//reseau.getActiveHistoryState();
	}
	public boolean isHistoryStateChanged(){
		return stateManager.isChanged();
	}
	public void activateHistory(int index){
		if(getActiveHistoryState()!=index){
			System.out.println("History activation requested "+index);
			Reseau reseauNew = stateManager.changeState(index);//loadState(index);
			//reseau.prevHistoryState();
			if(reseauNew!=null){
				reseau = reseauNew;
				reseau.setHistoryStateManager(stateManager);
			}
			stateManager.setChanged();
		}
	}
	public void undo(){
		if(stateManager.getActiveState()>0)
			activateHistory(stateManager.getActiveState()-1);
	}
	public void redo(){
		if(stateManager.getActiveState()<stateManager.getStates().size())
			activateHistory(stateManager.getActiveState()+1);
	}
	
	public void resetBgImages(){
		selection.resetImages();
	}
}
