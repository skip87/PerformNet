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
import org.skynet.reseau.Entree;



public class DialogsEditionEntree extends DialogEdition<Entree> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected static DialogsEditionEntree ptr = null;

	protected JLabel nomEntreeLabel;
	protected JTextField nomEntreeText;
	protected JLabel tauxEntreeLabel;
	protected JSpinner tauxEntree;
	protected SpinnerNumberModel tauxEntreeModel;
	
	
	protected JLabel imageLabel;
	protected JLabel imagePreview;
	

	protected ControlleurReseau controlleur;
	protected Entree entree;
	
	
	protected JLabel largeurLabel;
	protected JSpinner largeur;
	protected SpinnerNumberModel largeurModel;
	protected JLabel hauteurLabel;
	protected JSpinner hauteur;
	protected SpinnerNumberModel hauteurModel;
	
	protected BufferedImage image;
	protected String path = null;
	ImageToolTip tooltip;
	protected JButton ajouterImage;

	protected DialogsEditionEntree(){
	    //setLayout(new BoxLayout(this, 1));
	    //setLayout(new GridLayout(0, 2));
	   // setBorder(BorderFactory.createTitledBorder("Edition Station"));
	    /*
	     * Nom station
	     */
		nomEntreeLabel = new JLabel("Nom Entrée: ");
		add(nomEntreeLabel);
		nomEntreeText = new JTextField();
		add(nomEntreeText);
		
		//add(Box.createVerticalStrut(15));
		/*
		 * Nombre postes */
		tauxEntreeLabel = new JLabel("taux d'entree: ");
		add(tauxEntreeLabel);
		tauxEntree = new JSpinner();
		tauxEntree.setModel(tauxEntreeModel = new SpinnerNumberModel(1,1,100000,1));
		add(tauxEntree);
		
		
		//vitesseTraitementText = new JTextField();
		//add(vitesseTraitementText);
		
		//add(Box.createVerticalStrut(15));
			
		
		/* 
		 * Largeur 
		 */
		
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
		 * Hauteur 
		 */
		
		
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
		String nomEntree = obtenirNomEntree();
		if(nomEntree.equals(""))
			erreurs.add("Nom Entree est requis");
		float tauxEntree = obtenirtauxEntree();
		if(tauxEntree<1|| tauxEntree>100000 )
			erreurs.add("le taux 'entree doit etre superieur a 1 et inférieur a 100 000");
		
		if(erreurs.size()>0){
			showErrorMessage(erreurs);
		    return false;
		}
		
		controlleur.modifierEntree(
				entree
				, tauxEntree
				, largeurModel.getNumber().floatValue()
				, hauteurModel.getNumber().floatValue()
				, path
				
		);
		return true;
	}
	public void init(Entree entree,ControlleurReseau controlleur ){
		this.controlleur = controlleur;
		this.entree = entree;
		nomEntreeText.setText(entree.obtenirNom());
		tauxEntreeModel.setValue(entree.obtenirTauxArrive());
		//vitesseTraitementText.setText(Float.toString(station.obtenirVitesseMoyenneTraitementParPoste()));
		largeurModel.setValue(entree.obtenirLargeur());
		hauteurModel.setValue(entree.obtenirHauteur());
		image = entree.obtenirImage();
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
	public float obtenirtauxEntree(){
		return tauxEntreeModel.getNumber().floatValue();//(int)nombrePostes.getValue();
	}
	public String obtenirNomEntree(){
		return nomEntreeText.getText();
	}
	

	public static DialogsEditionEntree getSingleton(){
		if(ptr==null)
			ptr = new DialogsEditionEntree();
		return ptr;
	}
	public static DialogsEditionEntree Open(Entree entree,ControlleurReseau controlleur){
		DialogsEditionEntree ptr = getSingleton();
		ptr.init(entree, controlleur);
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
