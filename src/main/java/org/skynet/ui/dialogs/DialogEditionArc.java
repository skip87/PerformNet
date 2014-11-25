package org.skynet.ui.dialogs;



import java.util.LinkedList;


import javax.swing.JLabel;
import javax.swing.JSpinner;


import javax.swing.SpinnerNumberModel;

import org.skynet.model.ControlleurReseau;
import org.skynet.reseau.Arc;



public class DialogEditionArc extends DialogEdition<Arc> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected static DialogEditionArc ptr = null;

	
	protected JLabel PoidLabel;
	protected JSpinner Poid;
	protected SpinnerNumberModel PoidModel;
	
	
	protected JLabel imageLabel;
	protected JLabel imagePreview;
	

	protected ControlleurReseau controlleur;
	protected Arc arc;
	
	protected DialogEditionArc(){
	    //setLayout(new BoxLayout(this, 1));
	    //setLayout(new GridLayout(0, 2));
	   // setBorder(BorderFactory.createTitledBorder("Edition Station"));
	    /*
	     * Nom station
	     */
		
		//add(Box.createVerticalStrut(15));
		/*
		 * Nombre postes */
		PoidLabel = new JLabel("Poids: ");
		add(PoidLabel);
		Poid = new JSpinner();
		Poid.setModel(PoidModel = new SpinnerNumberModel(1,0.1,100,1));
		add(Poid);
		
		
	}
	
	public boolean save(){
		LinkedList<String> erreurs = new LinkedList<String>();
		
		float Poid = obtenirPoid();
		if(Poid<=0 || Poid >= 1 )
			erreurs.add("le taux 'entree doit etre superieur a 0.1 et inférieur a 100");
		
		if(erreurs.size()>0){
			showErrorMessage(erreurs);
		    return false;
		}
		
		controlleur.modifierArc(
				arc
				, Poid
		);
		return true;
	}
	public void init(Arc arc,ControlleurReseau controlleur ){
		this.controlleur = controlleur;
		this.arc = arc;
		
		PoidModel.setValue(arc.obtenirTauxPassage());
		//vitesseTraitementText.setText(Float.toString(station.obtenirVitesseMoyenneTraitementParPoste()));		
		
	}
	
	public float obtenirPoid(){
		return PoidModel.getNumber().floatValue();//(int)nombrePostes.getValue();
	}
	
	public static DialogEditionArc getSingleton(){
		if(ptr==null)
			ptr = new DialogEditionArc();
		return ptr;
	}
	public static DialogEditionArc Open(Arc arc,ControlleurReseau controlleur){
		DialogEditionArc ptr = getSingleton();
		ptr.init(arc, controlleur);
		/*
		Object[] options = {"Sauvegarder",
                "Annuler"};
		while(JOptionPane.showOptionDialog(
				null
				,ptr
				,"Edition station"
				,JOptionPane.YES_NO_OPTION
				,JOptionPane.PLAIN_MESSAGE
				,null
				,options
				,options[0]
		)==JOptionPane.OK_OPTION)
			return ptr;
		return null;
		*/
		if(ptr.openDialog())
			return ptr;
		return null;
	}
}
