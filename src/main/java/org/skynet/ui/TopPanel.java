package org.skynet.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;

import org.skynet.model.ControlleurEtatInterface;
import org.skynet.model.ControlleurReseau;
import org.skynet.model.EtatInterfaceListener;
import org.skynet.model.ControlleurEtatInterface.State;



public class TopPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private final ControlleurReseau controlleur;
	private final MainPanel displayPanel;
	private final ControlleurEtatInterface etatInterface;
	private final JToggleButton creerStation;
	private final JToggleButton creerArc;
	
	private final JPanel gridPannel;
	private final JPanel bgImagePanel;
	private final JComboBox<String> distanceGrille;

	private final JToggleButton grilleMagnetique;
	private final JToggleButton affichageGrille;
	
	private final JToggleButton affichageBgImages;
	
	private final JPanel statsCompletedPannel;
	private final JComboBox<String> statistiquesComplets;

	public TopPanel(ControlleurReseau controlleur, MainPanel displayPanel, ControlleurEtatInterface etatInterface){
		super(new FlowLayout(FlowLayout.LEFT));

		int buttonsHeight = 30;

		this.controlleur = controlleur;
		this.displayPanel = displayPanel;
		this.etatInterface = etatInterface;

		this.setAlignmentY(0);
		add(new JLabel("PerformNet"));
		
		//*
		JButton save = new JButton(new ImageIcon(
				new ImageIcon( TopPanel.class.getResource("/images/save.png"))
					.getImage()
					.getScaledInstance(
							buttonsHeight
							,buttonsHeight
							,java.awt.Image.SCALE_SMOOTH
					)
			));
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveClicked(e);
			}
		});
		add(save);

		JButton open = new JButton(new ImageIcon(
				new ImageIcon( TopPanel.class.getResource("/images/open.png"))
					.getImage()
					.getScaledInstance(
							buttonsHeight
							,buttonsHeight
							,java.awt.Image.SCALE_SMOOTH
					)
			));
		open.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				loadClicked(e);
			}
		});
		add(open);

		JButton export = new JButton(new ImageIcon(
				new ImageIcon( TopPanel.class.getResource("/images/pdf.png"))
					.getImage()
					.getScaledInstance(
							buttonsHeight
							,buttonsHeight
							,java.awt.Image.SCALE_SMOOTH
					)
			));
		export.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				exporterPdf(e);
			}
		});
		add(export);
		
		
		JButton addBackgroundImage = new JButton(new ImageIcon(
				new ImageIcon( TopPanel.class.getResource("/images/background.png"))
					.getImage()
					.getScaledInstance(
							buttonsHeight
							,buttonsHeight
							,java.awt.Image.SCALE_SMOOTH
					)
			));
		addBackgroundImage.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				addBackgroundImageClicked(e);
			}
		});
		add(addBackgroundImage);
		
		JSeparator x;
		add(x=new JSeparator(JSeparator.VERTICAL));
		x.setPreferredSize(new Dimension(3,50));
		
		bgImagePanel = new JPanel();
		add(bgImagePanel);
		//bgImagePanel.add(new JLabel("Edition Images: "));
		
		bgImagePanel.add(affichageBgImages=new JToggleButton(
				new ImageIcon(
						new ImageIcon( TopPanel.class.getResource("/images/Lock-icon.png"))
							.getImage()
							.getScaledInstance(
									buttonsHeight
									,buttonsHeight
									,java.awt.Image.SCALE_SMOOTH
							)
					)
		));
		//*/
		add(creerStation=new JToggleButton(
				//"Creer station"
				new ImageIcon(
						new ImageIcon( TopPanel.class.getResource("/images/station-icon.png"))
							.getImage()
							.getScaledInstance(
									buttonsHeight
									,buttonsHeight
									,java.awt.Image.SCALE_SMOOTH
							)
					)
		));
		creerStation.setToolTipText("Creer station");
		creerStation.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				creerStationClicked(e);
			}
		});
		

		//actionsPanel.add(Box.createVerticalStrut(5));
		add(creerArc=new JToggleButton(
				//"Creer arc"
				new ImageIcon(
						new ImageIcon( TopPanel.class.getResource("/images/arc-icon.png"))
							.getImage()
							.getScaledInstance(
									buttonsHeight
									,buttonsHeight
									,java.awt.Image.SCALE_SMOOTH
							)
					)
		));
		creerArc.setToolTipText("Creer arc");
		creerArc.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				creerArcClicked(e);
			}
		});

		
		add(x=new JSeparator(JSeparator.VERTICAL));
		x.setPreferredSize(new Dimension(3,50));
		
		gridPannel = new JPanel();
		add(gridPannel);
		gridPannel.add(new JLabel("Grille: "));
		
		gridPannel.add(
				grilleMagnetique=new JToggleButton(
						new ImageIcon(
							new ImageIcon( TopPanel.class.getResource("/images/magnet.png"))
								.getImage()
								.getScaledInstance(
										buttonsHeight
										,buttonsHeight
										,java.awt.Image.SCALE_SMOOTH
								)
						)
				)
		);
		//grilleMagnetique.setPreferredSize(new Dimension(distanceGrille.getPreferredSize().height,distanceGrille.getPreferredSize().height));
		grilleMagnetique.setSelected(controlleur.obtenirGrilleActive());
		grilleMagnetique.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				grilleMagnetiqueClicked(arg0);
			}
		});

		//actionsPanel.add(Box.createVerticalStrut(5));
		gridPannel.add(affichageGrille=new JToggleButton(
				new ImageIcon(
						new ImageIcon( TopPanel.class.getResource("/images/view_grid.png"))
							.getImage()
							.getScaledInstance(
									buttonsHeight
									,buttonsHeight
									,java.awt.Image.SCALE_SMOOTH
							)
					)
		));
		affichageGrille.setSelected(controlleur.obtenirGrilleAffiche());
		affichageGrille.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				grilleAffichageClicked(arg0);
			}
		});
		
		
		affichageBgImages.setSelected(false);
		affichageBgImages.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				affichageBgImagesClicked(arg0);
			}
		});
		
		String[] choixGrille = { "0.5", "1", "1.5", "2", "2.5", "3", "4" };
		gridPannel.add(distanceGrille=new JComboBox<String>(choixGrille));
		distanceGrille.setSelectedIndex(1);
		distanceGrille.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				distanceGrilleClicked(e);
			}
		});
		
		add(x=new JSeparator(JSeparator.VERTICAL));
		x.setPreferredSize(new Dimension(3,50));
		
		statsCompletedPannel = new JPanel();
		add(statsCompletedPannel);
		
		String[] choixStats = { "Jamais", "On hover" , "Toujours" };
		statsCompletedPannel.add(new JLabel("Stats: "));
		statsCompletedPannel.add(
				statistiquesComplets = new JComboBox<String>(choixStats)
		);
		statistiquesComplets.setSelectedItem("On hover");
		statistiquesComplets.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				statistiquesCompletsClicked(arg0);
			}
		});
		
		
		etatInterface.addListener(new EtatInterfaceListener() {
			
			@Override
			public void onStateChanged(ControlleurEtatInterface etatInterface,
					State state) {
				etatInterfaceChange(etatInterface,state);
				
			}

			@Override
			public void onReset(ControlleurEtatInterface etatInterface) {
				etatInterfaceReset(etatInterface);
			}
		});
		
		
	}
	protected void statistiquesCompletsClicked(ActionEvent arg0){
		controlleur.setFullStatistics(statistiquesComplets.getSelectedIndex());
		displayPanel.requestRepaint();
	}
	protected void etatInterfaceReset(ControlleurEtatInterface etatInterface){
		grilleMagnetique.setSelected(controlleur.obtenirGrilleActive());
		affichageGrille.setSelected(controlleur.obtenirGrilleAffiche());
		//distanceGrille.setSelectedItem();
		int selectedIndex = 0;
		float d = controlleur.obtenirDistanceGrille();
		for(int i=0; i<distanceGrille.getItemCount(); i++){
			String s = distanceGrille.getItemAt(i);
			float f =Float.parseFloat(s);
			if(f==d){
				selectedIndex = i;
				break;
			} else if(f>d) {
				if(i>0)
					selectedIndex = i-1;
				break;
			}
		}
		distanceGrille.setSelectedIndex(selectedIndex);
		//System.out.println(controlleur.obtenirDistanceGrille());
		statistiquesComplets.setSelectedIndex(controlleur.getFullStatistics());
		
		this.repaint();
	}
	protected void grilleMagnetiqueClicked(ActionEvent e){
		controlleur.definirGrilleActive(grilleMagnetique.isSelected());
		displayPanel.requestRepaint();
	}
	protected void grilleAffichageClicked(ActionEvent e){
		controlleur.definirGrilleAffiche(affichageGrille.isSelected());
		displayPanel.requestRepaint();
	}
	protected void affichageBgImagesClicked(ActionEvent e){
		//System.out.println("VALUE:"+affichageBgImages.isSelected());
		//controlleur.reinitialiserSelection();
		controlleur.resetBgImages();
		controlleur.setBgImageEditingDisabled(affichageBgImages.isSelected());
	}
	protected void distanceGrilleClicked(ActionEvent e){
		controlleur.definirDistanceGrille(Float.parseFloat((String)distanceGrille.getSelectedItem()));
		displayPanel.requestRepaint();
	}
	public void etatInterfaceChange(ControlleurEtatInterface etatInterface, State state){
		this.creerStation.setSelected(etatInterface.obtenirEtat()==State.AJOUTER_STATION);
		this.creerArc.setSelected(etatInterface.obtenirEtat()==State.AJOUTER_ARC || etatInterface.obtenirEtat()==State.AJOUTER_ARC_STATION2);
	}
	public void exporterPdf(ActionEvent e){
		etatInterface.exporterPdf(e, displayPanel);
		/*
		java.io.FileOutputStream fileOut;
		try {
			controlleur.reinitialiserHighlight();
			controlleur.reinitialiserSelection();
			etatInterface.Reset();
			fileOut = new java.io.FileOutputStream("test-output.pdf");

	        java.awt.Point dimension = controlleur.pixels(controlleur.obtenirDimensionReseau());
			gnu.jpdf.PDFJob job = new gnu.jpdf.PDFJob(fileOut);
	        
			java.awt.print.PageFormat pf = new java.awt.print.PageFormat();
			if(dimension.y>dimension.x)
				pf.setOrientation(java.awt.print.PageFormat.PORTRAIT);
			else
				pf.setOrientation(java.awt.print.PageFormat.LANDSCAPE);
	        java.awt.Graphics pdfGraphics = job.getGraphics(pf);
	        

	        java.awt.Rectangle clip = pdfGraphics.getClipBounds();
	        ((java.awt.Graphics2D)pdfGraphics).translate(clip.x, clip.y);
	        System.out.println(clip);
	        //pdfGraphics.setClip(-20, 200, clip.width, clip.height);
	        //java.awt.Dimension pdfDim = job.getPageDimension();
	        float pbackup = controlleur.obtenirPixelsParMetre();
	        controlleur.definirPixelsParMetre(
	        		Math.min(
        				(clip.width-clip.x)/controlleur.obtenirDimensionReseau().x
        				,(clip.height-clip.y)/controlleur.obtenirDimensionReseau().y
	        		)
	        		,true
	        );
	        
	        //dimension.x
	        
	        displayPanel.affiche(pdfGraphics, controlleur, etatInterface);
	        pdfGraphics.translate(0, 0);
	        pdfGraphics.dispose();
	        job.end();
	        fileOut.close();
	        
	        controlleur.definirPixelsParMetre(pbackup);
		} catch (FileNotFoundException e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			//  Auto-generated catch block
			e1.printStackTrace();
		}
		*/
	}
	public void loadClicked(ActionEvent e){
		if(etatInterface.loadClicked(e))
			displayPanel.requestRepaint();
	}
	public void saveAsClicked(ActionEvent e){
		etatInterface.saveAsClicked(e);
	}
	public void saveClicked(ActionEvent e){
		etatInterface.saveClicked(e);
	}
	public void addBackgroundImageClicked(ActionEvent e){
		//System.out.println("ADDBG IMAGE CLICKED");
		
		etatInterface.loadImageClicked(e);
		//System.out.println(file);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
	}
	void creerStationClicked(ActionEvent e){
		if(etatInterface.obtenirEtat()==State.AJOUTER_STATION)
			etatInterface.definirEtat(State.NORMAL);
		else
			etatInterface.definirEtat(State.AJOUTER_STATION);
	}
	void creerArcClicked(ActionEvent e){
		if(etatInterface.obtenirEtat()==State.AJOUTER_ARC || etatInterface.obtenirEtat()==State.AJOUTER_ARC_STATION2)
			etatInterface.definirEtat(State.NORMAL);
		else
			etatInterface.definirEtat(State.AJOUTER_ARC);
		//etatInterface.definirEtat(State.AJOUTER_ARC);
	}

}
