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

import org.skynet.reseau.Sortie;



public class DialogEditionSortie extends DialogEdition<Sortie> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected static DialogEditionSortie ptr = null;

	protected JLabel nomEntreeLabel;
	protected JTextField nomEntreeText;
	protected JLabel tauxEntreeLabel;
	protected JSpinner tauxEntree;
	protected SpinnerNumberModel tauxEntreeModel;
	
	
	protected JLabel largeurLabel;
	protected JSpinner largeur;
	protected SpinnerNumberModel largeurModel;
	protected JLabel hauteurLabel;
	protected JSpinner hauteur;
	protected SpinnerNumberModel hauteurModel;
	
	protected JLabel imageLabel;
	protected JLabel imagePreview;
	

	protected ControlleurReseau controlleur;
	
	
	protected BufferedImage image;
	protected String path = null;
	ImageToolTip tooltip;
	protected JButton ajouterImage;

	private Sortie sortie;

	protected DialogEditionSortie(){
	    //setLayout(new BoxLayout(this, 1));
	    //setLayout(new GridLayout(0, 2));
	   // setBorder(BorderFactory.createTitledBorder("Edition Station"));
	    /*
	     * Nom station
	     */
		
		
		
		//vitesseTraitementText = new JTextField();
		//add(vitesseTraitementText);
		
		//add(Box.createVerticalStrut(15));
			
		
		/* 
		 * Largeur 
		 */
		
		
		/* 
		 * Hauteur 
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
		
		
		if(erreurs.size()>0){
			showErrorMessage(erreurs);
		    return false;
		}
		
		controlleur.modifierSortie(
				sortie
				, largeurModel.getNumber().floatValue()
				, hauteurModel.getNumber().floatValue()
				,path
		);
		return true;
	}
	public void init(Sortie sortie,ControlleurReseau controlleur ){
		this.controlleur = controlleur;
		this.sortie = sortie;
		
		//vitesseTraitementText.setText(Float.toString(station.obtenirVitesseMoyenneTraitementParPoste()));
		largeurModel.setValue(sortie.obtenirLargeur());
		hauteurModel.setValue(sortie.obtenirHauteur());
		image = sortie.obtenirImage();
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
	
	

	public static DialogEditionSortie getSingleton(){
		if(ptr==null)
			ptr = new DialogEditionSortie();
		return ptr;
	}
	public static DialogEditionSortie Open(Sortie sortie,ControlleurReseau controlleur){
		DialogEditionSortie ptr = getSingleton();
		ptr.init(sortie, controlleur);
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
