package org.skynet.reseau;

import java.awt.geom.Rectangle2D;
import java.util.List;

import org.skynet.utils.Utils;
import org.skynet.utils._Dimension;
import org.skynet.utils._Point;

public class Station extends Noeud {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4594120789661540554L;
	public static int dimensionLigneAttenteDefaut = 2;
	/*
	 * Les valeures de vitesseMTraitementEntitesParPoste et de nombrePostes sont fournies par l'usager lors 
	 * de la creation d'une station.
	 */
	private float vitesseMTraitementEntitesParPoste = 100; // en entites/heure. Float car c'est vitesse MOYENNE
	private int nombrePostes = 1;
	
	
	public float obtenirTauxEntreArrivees(){
		// 1/lambda
		return 1/obtenirTauxArrive();
	}
	/*
	 * L’utilisateur spécifie également la vitesse de traitement moyenne des entités 
	 * par un poste de la station en nombre d’entités par heure. 
	 * ===u===
	 */
	public float obtenirVitesseMoyenneTraitementParPoste(){
		return vitesseMTraitementEntitesParPoste;
	}
	/*
	 * L’inverse (1/u) est donc le temps moyen de traitement d’une entité par le poste 
	 * lorsqu’elle sort de la file d’attente pour être traitée par le poste
	 * ===1/u===
	 */
	public float obtenirVitesseMoyenneTraitementDEntite(){
		return 1/obtenirVitesseMoyenneTraitementParStation();//Poste();
	}
	/*
	 * En régime stationnaire, le taux d’utilisation d’une station 
	 * (% de la capacité utilisée) qui comporte c postes de traitement
	 * ===p===
	 */
	public float obtenirTauxUtilisationStation(){
		/*La formule est p=lambda/(c*u). La valeur retournee est un pourcentage*/
		float tauxArrivee = obtenirTauxArrive();
		float tauxUtilisation = 0;
		int nbPostes = obtenirNombrePostes(); //c 
		float vitesseMTraitementEntiteParPoste = obtenirVitesseMoyenneTraitementParPoste(); //u
		
		tauxUtilisation = tauxArrivee/(vitesseMTraitementEntiteParPoste*nbPostes);
		
		return tauxUtilisation;
	}
	/*
	 * La longueur moyenne de la file d’attente d’une station comptant c postes de traitement
	 * ===L_q===
	 */
	public float obtenirLongueurMoyenneFileAttente(){
		//L_q
		float longueurM = 0;
		
		int nbPostes = obtenirNombrePostes(); //c
		float st_tauxUtilisation = obtenirTauxUtilisationStation();
		
		float cp_exp_c = (float)Math.pow((nbPostes*st_tauxUtilisation),nbPostes); //(cp)^c
		float c_fact_times_1minusp_square2 = Utils.factoriel(nbPostes)*((float)Math.pow((1-st_tauxUtilisation),2));//c!(1-p)^2
		float denominator_sum = 0;
		
		for (int n=0; n<nbPostes; n++){
			if (n==0)
				denominator_sum = 1;
			else {
				try {
					denominator_sum += Math.pow((nbPostes*st_tauxUtilisation),n)/Utils.factoriel(n);
				}
				catch (Exception e){
					e.printStackTrace();
				}
			}
		}
		
		longueurM = (st_tauxUtilisation*cp_exp_c)/(c_fact_times_1minusp_square2);
		longueurM *= 1/((cp_exp_c/((float)Utils.factoriel(nbPostes)*(1-st_tauxUtilisation)))+denominator_sum);
		
		return longueurM;
	}
	/*
	 * Le temps moyen passé par une entité dans la file d’attente de la station
	 * ===W_q===
	 */
	public float obtenirTempsMoyenPasseDansFileAttenteStation(){
		//W_q=L_q/lambda
		return obtenirLongueurMoyenneFileAttente()/obtenirTauxArrive();
	}
	/*
	 * Temps moyen pris par une entite pour passer la station 
	 * (fille d'attente + traitement)
	 * ===W===
	 */
	public float obtenirTempsMoyenPasserStation(){
		//W=W_q+1/u;
		return obtenirTempsMoyenPasseDansFileAttenteStation()+obtenirTempsMoyenTraitementParPoste();
	}
	/*
	 * Le nombre moyen d’entités dans la station (entités en attente et en traitement)
	 * ===L===
	 */
	public float obtenirNombreMoyenEntitesDansStation(){
		//L=lambda*W
		return obtenirTauxArrive()*obtenirTempsMoyenPasserStation();
	}
	public float obtenirCapaciteTraitement(){
		return obtenirVitesseMoyenneTraitementParPoste()*obtenirNombrePostes();
	}
	
	
	public float obtenirVitesseMoyenneTraitementParStation(){
		return vitesseMTraitementEntitesParPoste*obtenirNombrePostes();
	}
	
	public float obtenirTempsMoyenTraitementParPoste(){
		
		return 1/obtenirVitesseMoyenneTraitementParPoste();

	}
	
	public float obtenirTempsMoyenTraitementParStation(){
		
		return 1/obtenirVitesseMoyenneTraitementParStation();
		
	}
	
	
	
	
	
	/*
	 * Cette fonction est utilisee pour retourner le rectangle de tous les elements dans le noeud
	 * Par exemple pour la station la file d'attente sera ajoute
	 * Cette fonction est utilisee pour collision detection de l'arc avec les noeud
	 */
	@Override
	public List<Rectangle2D> obtenirRectanglesIntersection(){
		List<Rectangle2D> rectangles = super.obtenirRectanglesIntersection();//new LinkedList<Rectangle2D>();
		
		_Point coord = obtenirCoordonnees();
		
		float offsetStart = .8f;
		float offsetAdd = 0.8f*2
				+0.8f // c'est le offset pour la deuxieme ligne de statistiques
				;
		//if(!reseau.obtenirReseauValide()){
			offsetStart = .2f;
			offsetAdd = .4f;
		//}
		
		rectangles.add(
				new Rectangle2D.Float(
						coord.x - obtenirDimensionLigneAttente()
						,coord.y + obtenirHauteur()/2 - offsetStart
						,obtenirDimensionLigneAttente()
						,offsetAdd 
				)
		);
		return rectangles;
	}

	
	public void printAll(){
		/*
		System.out.println("vitesseMTraitementEntitesParPoste = "+Float.toString(vitesseMTraitementEntitesParPoste));
		System.out.println("nombrePostes = "+Float.toString(nombrePostes));
		System.out.println("st_tauxPassageTotal = "+Float.toString(st_tauxPassageTotal));
		System.out.println("st_tempsMEntreArrivees = "+Float.toString(st_tempsMEntreArrivees));
		System.out.println("st_tempsMTraitementEntiteParPoste = "+Float.toString(st_tempsMTraitementEntiteParPoste));
		System.out.println("st_tempsMTraitementEntiteParStation = "+Float.toString(st_tempsMTraitementEntiteParStation));
		System.out.println("st_tauxUtilisation = "+Float.toString(st_tauxUtilisation));
		System.out.println("st_longueurM = "+Float.toString(st_longueurM));
		System.out.println("st_tempsMPasseDansFileAttenteStation = "+Float.toString(st_tempsMPasseDansFileAttenteStation));
		System.out.println("st_tempsMPasserStation = "+Float.toString(st_tempsMPasserStation));
		System.out.println("st_nombreMEntitesDansStation = "+Float.toString(st_nombreMEntitesDansStation));
		System.out.println("vitesseMTraitementEntitesParPoste = "+Float.toString(vitesseMTraitementEntitesParPoste));
		*/
	}
	@Override
	public String toString(){
		return Station.class.getPackage()+".Station(vitesseMTraitementEntitesParPoste="+vitesseMTraitementEntitesParPoste+",nombrePostes="+nombrePostes+")";
	}

	
	private int dimensionLigneAttente = -1;

	public Station(Reseau reseau, _Point coordonnees) {
		super(reseau,coordonnees);
	}
	
	@Override
	public _Dimension obtenirTaille(){
		_Dimension taille = (_Dimension) super.obtenirTaille().clone();
		//taille.width += obtenirDimensionLigneAttente();
		return taille;
	}
	public int obtenirDimensionLigneAttente(){
		return dimensionLigneAttente >= 0 ? dimensionLigneAttente : dimensionLigneAttenteDefaut;
	}
	public float obtenirVitesseMTraitementEntitesParPoste(){
		return vitesseMTraitementEntitesParPoste;
	}
	
	public float obtenirVitesseMTraitementEntitesParStation(){
		return vitesseMTraitementEntitesParPoste*nombrePostes;
	}
	
	public int obtenirNombrePostes(){
		return nombrePostes;
	}
	
	public void definirVitesseMTraitementEntitesParPoste(float nombreEntites){
		vitesseMTraitementEntitesParPoste = nombreEntites;
	}
	
	public void definirNombrePostes(int nbPostes){
		nombrePostes = nbPostes;
	}
	

	@Override
	public _Point obtenirCoordonnees(){
		_Point c = super.obtenirCoordonnees();
		return new _Point(c.x,c.y);
	}
	public _Point obtenirCoordonneesCentre(){
		_Point new_coord = super.obtenirCoordonneesCentre();
		// Affichage au debut de la ligne d'attente 
		//return new _Point(new_coord.x - obtenirLargeur()/2 - obtenirDimensionLigneAttente(),new_coord.y);
		// Affichage au point d'entree de la file d'attente dans la station
		//return new _Point(new_coord.x - obtenirLargeur()/2 + reseau.transformerPixelsEnReseau(10),new_coord.y);
		// Affichage de la ligne au centre de la station
		return new_coord;
	}
	
	public boolean modifier(String nom,float vitesse, int nbPostes, float largeur, float hauteur, String pathImage){
		boolean estModifie = false;
		if(!nom.equals(obtenirNom())){
			estModifie=true;
			definirNom(nom);
		}
		if(nbPostes != obtenirNombrePostes()){
			estModifie=true;
			definirNombrePostes(nbPostes);
		}
		if(vitesse!=obtenirVitesseMoyenneTraitementParPoste()){
			estModifie=true;
			definirVitesseMTraitementEntitesParPoste(vitesse);
			
		}
		if(largeur!=obtenirLargeur() || hauteur!=obtenirHauteur()){
			estModifie=true;
			this.definirTaille(new _Dimension(largeur, hauteur));
		}
		if(pathImage!=null){
			if(pathImage.equals("")){
				// Il faut retirer l'image
				if(this.image!=null){
					definirImage((String)null);
					estModifie=true;
				}
			} else {
				definirImage(pathImage);
				estModifie=true;
			}
		}
		return estModifie;
	}
}
