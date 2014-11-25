package org.skynet.ui.dialogs;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.SpinnerNumberModel;

import org.skynet.model.ControlleurReseau;
import org.skynet.reseau.Station;

public class DialogEditionStation extends DialogEdition<Station> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected static DialogEditionStation ptr = null;

	protected JLabel nomStationLabel;
	protected JTextField nomStationText;
	protected JLabel nombrePostesLabel;
	protected JSpinner nombrePostes;
	protected SpinnerNumberModel nombrePostesModel;
	protected JLabel vitesseTraitementLabel;
	protected JSpinner vitesseTraitement;
	protected SpinnerNumberModel vitesseTraitementModel;
	//protected JTextField vitesseTraitementText;
	protected JLabel largeurLabel;
	protected JSpinner largeur;
	protected SpinnerNumberModel largeurModel;
	protected JLabel hauteurLabel;
	protected JSpinner hauteur;
	protected SpinnerNumberModel hauteurModel;
	
	protected JLabel imageLabel;
	protected JLabel imagePreview;
	

	protected ControlleurReseau controlleur;
	protected Station station;
	
	protected BufferedImage image;
	protected String path = null;
	ImageToolTip tooltip;
	protected JButton ajouterImage;

	protected DialogEditionStation(){
	    //setLayout(new BoxLayout(this, 1));
	    //setLayout(new GridLayout(0, 2));
	   // setBorder(BorderFactory.createTitledBorder("Edition Station"));
	    /*
	     * Nom station
	     */
		nomStationLabel = new JLabel("Nom station: ");
		add(nomStationLabel);
		nomStationText = new JTextField();
		add(nomStationText);
		
		//add(Box.createVerticalStrut(15));
		/*
		 * Nombre postes */
		nombrePostesLabel = new JLabel("Nombre postes: ");
		add(nombrePostesLabel);
		nombrePostes = new JSpinner();
		nombrePostes.setModel(nombrePostesModel = new SpinnerNumberModel(1,1,100,1));
		add(nombrePostes);
		
		vitesseTraitementLabel = new JLabel("Vitesse de traitement: ");
		add(vitesseTraitementLabel);
		vitesseTraitement = new JSpinner();
		vitesseTraitement.setModel(vitesseTraitementModel = new SpinnerNumberModel(1.0,1.0,10000.0,1.0));
		add(vitesseTraitement);
		//vitesseTraitementText = new JTextField();
		//add(vitesseTraitementText);
		
		//add(Box.createVerticalStrut(15));
			
		
		/* 
		 * Largeur 
		 */
		largeurLabel = new JLabel("Largeur (metres): ");
		add(largeurLabel);
		largeur = new JSpinner();
		largeur.setModel(largeurModel = new SpinnerNumberModel(2.0,1.5,10000.0,1.0));
		add(largeur);
		
		/* 
		 * Hauteur 
		 */
		hauteurLabel = new JLabel("Hauteur (metres): ");
		add(hauteurLabel);
		hauteur = new JSpinner();
		hauteur.setModel(hauteurModel = new SpinnerNumberModel(2.0,1.5,10000.0,1.0));
		add(hauteur);
		
		/* 
		 * Image 
		 */
		imageLabel = new JLabel("Image: ");
		add(imageLabel);
		
		//JPanel imagePanel = new JPanel();
		//imagePreview = new JLabel("");
		imagePreview= new JLabel() {
			private static final long serialVersionUID = 1L;
			public JToolTip createToolTip() {
				return createTooltip();
			}
		};
		imagePreview.setToolTipText("preview");
		imagePreview.setIcon(null);
		add(imagePreview);
		
		add(new JLabel(""));
		add(ajouterImage=new JButton("Ajouter image"));
		ajouterImage.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ajouterImageClicked();
			}
		});
		
	}
	void setImage(BufferedImage image){
		int width = image.getWidth();
		int height = image.getHeight();
		float aspect = 35.0f/height;
		width = (int)(width*aspect);
		height = (int)(height*aspect);
		imagePreview.setIcon(new ImageIcon( image.getScaledInstance(width, height, Image.SCALE_SMOOTH)));
		if(tooltip!=null)
			tooltip.setImage(image);
	}
	void ajouterImageClicked(){
		File file = DialogSaveAs.Open();
		if(file!=null){
			try {
				image = ImageIO.read(file);
				path = file.getPath();
				setImage(image);
			} catch (IOException e) {
				e.printStackTrace();
				showErrorMessage("Error encountered while opening file");
			}
		}
	}
	public JToolTip createTooltip(){
		return tooltip = new ImageToolTip(image);
	}
	public boolean save(){
		LinkedList<String> erreurs = new LinkedList<String>();
		String nomStation = obtenirNomStation();
		if(nomStation.equals(""))
			erreurs.add("Nom station est requis");
		int nombrePostes = obtenirNombrePostes();
		if(nombrePostes<1)
			erreurs.add("Nombre postes doit etre superieur a 1");
		float vitesse = obtenirVitesseTraittement();
		if(vitesse<1)
			erreurs.add("Vitesse de traitement doit etre superieure a 1");
		if(erreurs.size()>0){
			showErrorMessage(erreurs);
		    return false;
		}
		
		controlleur.modifierStation(
				station
				, nomStation
				, vitesse
				, nombrePostes
				, largeurModel.getNumber().floatValue()
				, hauteurModel.getNumber().floatValue()
				, path
		);
		return true;
	}
	public void init(Station station,ControlleurReseau controlleur){
		this.controlleur = controlleur;
		this.station = station;
		nomStationText.setText(station.obtenirNom());
		nombrePostesModel.setValue(station.obtenirNombrePostes());
		//vitesseTraitementText.setText(Float.toString(station.obtenirVitesseMoyenneTraitementParPoste()));
		vitesseTraitementModel.setValue(station.obtenirVitesseMoyenneTraitementParPoste());
		largeurModel.setValue(station.obtenirLargeur());
		hauteurModel.setValue(station.obtenirHauteur());
		image = station.obtenirImage();
		path=null;
		setImage(image);
	}
	void iconMouseExited(MouseEvent e){
		tooltip.setVisible(false);
	}
	void iconMouseEntered(MouseEvent e){
		tooltip = new ImageToolTip(image);
		tooltip.setVisible(true);
	}
	public int obtenirNombrePostes(){
		return nombrePostesModel.getNumber().intValue();//(int)nombrePostes.getValue();
	}
	public String obtenirNomStation(){
		return nomStationText.getText();
	}
	public float obtenirVitesseTraittement(){
		return vitesseTraitementModel.getNumber().floatValue();//Float.parseFloat(vitesseTraitementText.getText());
	}

	public static DialogEditionStation getSingleton(){
		if(ptr==null)
			ptr = new DialogEditionStation();
		return ptr;
	}
	public static DialogEditionStation Open(Station station,ControlleurReseau controlleur){
		DialogEditionStation ptr = getSingleton();
		ptr.init(station, controlleur);
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
