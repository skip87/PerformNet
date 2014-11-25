package org.skynet.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.skynet.model.ControlleurEtatInterface;
import org.skynet.model.EtatInterfaceListener;
import org.skynet.model.NetworkStateManager;
import org.skynet.model.ControlleurEtatInterface.State;
import org.skynet.model.ControlleurReseau;
import org.skynet.model.NetworkStateManager.NetworkState;

@SuppressWarnings("serial")
public class ButtonsPanel extends JPanel implements ActionListener, EtatInterfaceListener, ListSelectionListener {
	/*private final JButton creerStation;
	private final JButton creerArc;*/
	
	//private final JButton zoomIn, zoomOut, zoomIn2, zoomOut2, zoomReset;
	
	/*private final JCheckBox grilleMagnetique;
	private final JCheckBox affichageGrille;*/
	
	private final ControlleurReseau reseau;
	private final MainPanel displayPanel;
	
	private final ControlleurEtatInterface etatInterface;
	private final JList<String> statesList;
	private final DefaultListModel<String> statesListModel;
	//private AbstractButton calculerStats;
	
	private boolean disableStateListener = false;

	public ButtonsPanel(ControlleurReseau reseau, MainPanel displayPanel, ControlleurEtatInterface etatInterface) {
		this.reseau = reseau;
		this.displayPanel = displayPanel;
		this.etatInterface = etatInterface;

		setLayout(new GridLayout(10, 1));
		setLayout(new FlowLayout());
		//this.setLayout(new java.awt.GridBagLayout());//new BoxLayout(this, BoxLayout.Y_AXIS));
		
		java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();
		
		JPanel actionsPanel = new JPanel();
		add(actionsPanel);
		
		actionsPanel.setLayout(new GridLayout(5, 1));
		actionsPanel.setPreferredSize(new Dimension(150,10));
		
        this.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		setPreferredSize(new Dimension(200, 400));
		
		//JPanel t = new JPanel();
		//t.

		/*
		actionsPanel.add(creerStation=new SideButton("Creer station"));
		creerStation.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				creerStationClicked(e);
			}
		});

		//actionsPanel.add(Box.createVerticalStrut(5));
		actionsPanel.add(creerArc=new SideButton("Creer arc"));
		creerArc.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				creerArcClicked(e);
			}
		});
		*/
		
		/*
		//actionsPanel.add(Box.createVerticalStrut(5));
		actionsPanel.add(calculerStats=new SideButton("Calculer Statistiques"));
			calculerStats.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				calculerStatsClicked(e);
			}
		});
		//*/
		
		/*
		//actionsPanel.add(Box.createVerticalStrut(5));
		actionsPanel.add(grilleMagnetique=new JCheckBox("Grille magnetique"));
		grilleMagnetique.setSelected(reseau.obtenirGrilleActive());
		grilleMagnetique.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				grilleMagnetiqueChange(e);
			}
		});

		//actionsPanel.add(Box.createVerticalStrut(5));
		actionsPanel.add(affichageGrille=new JCheckBox("Afficher la grille"));
		affichageGrille.setSelected(reseau.obtenirGrilleAffiche());
		affichageGrille.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				grilleAffichageChange(e);
			}
		});
		*/
		
		//c.gridwidth=2;
		c.gridx=0;
		c.gridy=0;
		//c.ipady = 40;
		c.fill=GridBagConstraints.HORIZONTAL;
		statesList = new JList<String>();
		statesListModel=new DefaultListModel<String>();
		statesList.setModel(statesListModel);
		//statesListModel.addElement("test");
		//statesListModel.addElement("test 2");
		//add(statesList,c);
		statesList.setSelectedIndex(0);
		//statesList.setPreferredSize(new Dimension(150,200));
		statesList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		statesList.setLayoutOrientation(JList.VERTICAL);
		javax.swing.JScrollPane listScroller = new javax.swing.JScrollPane(statesList);
		listScroller.setPreferredSize(new Dimension(200, 150));
		add(listScroller,c);
		statesList.addListSelectionListener(this);
		reinitStatesList();
		//statesList.setMinimumSize(new Dimension(200,200));

		c.gridy=1;
		//c.ipady = 0;
		
		
		int buttonsHeight = 30;
		JButton undo = new JButton(new ImageIcon(
				new ImageIcon( TopPanel.class.getResource("/images/revert1.png"))
					.getImage()
					.getScaledInstance(
							buttonsHeight
							,buttonsHeight
							,java.awt.Image.SCALE_SMOOTH
					)
			));
		undo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				undoClicked(e);
			}
		});
		
		add(undo,c);
		
		JButton redo = new JButton(new ImageIcon(
				new ImageIcon( TopPanel.class.getResource("/images/restaure.png"))
					.getImage()
					.getScaledInstance(
							buttonsHeight
							,buttonsHeight
							,java.awt.Image.SCALE_SMOOTH
					)
			));
		redo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				redoClicked(e);
			}
		});
		
		add(redo,c);
		
		
		/*
		//add(Box.createVerticalGlue());

		//ButtonGroup group = new ButtonGroup();
		JPanel zoomPanel = new JPanel();
		//zoomPanel.setPreferredSize(new Dimension(300,50));
		add(zoomPanel);
		//add(new JSeparator(30));
		zoomPanel.add(zoomIn2=new JButton("++"));
		zoomIn2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//reseau.zoomIn();
				boutonZoomInClicked(true);
			}
		});
		
		zoomPanel.add(zoomIn=new JButton("+"));
		zoomIn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//reseau.zoomIn();
				boutonZoomInClicked();
			}
		});
		
		zoomPanel.add(zoomReset=new JButton("100%"));
		zoomReset.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//reseau.zoomIn();
				boutonZoomResetClicked();
			}
		});
		
		zoomPanel.add(zoomOut=new JButton("-"));
		zoomOut.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//reseau.zoomIn();
				boutonZoomOutClicked();
			}
		});
		zoomPanel.add(zoomOut2=new JButton("--"));
		zoomOut2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//reseau.zoomIn();
				boutonZoomOutClicked(true);
			}
		});
		*/
		
		etatInterface.addListener(this);
	}
	
	void grilleAffichageChange(ItemEvent e){
		reseau.definirGrilleAffiche(e.getStateChange() == ItemEvent.SELECTED);
		displayPanel.requestRepaint();
	}
	void grilleMagnetiqueChange(ItemEvent e){
		reseau.definirGrilleActive(e.getStateChange() == ItemEvent.SELECTED);
	}
	void boutonZoomInClicked(){
		boutonZoomInClicked(false);
	}
	void boutonZoomInClicked(boolean accelerer){
		reseau.zoomIn(accelerer);
		displayPanel.requestRepaint();
	}
	void boutonZoomResetClicked(){
		reseau.zoomReset();
		displayPanel.requestRepaint();
	}
	void boutonZoomOutClicked(boolean accelerer){
		reseau.zoomOut(accelerer);
		displayPanel.requestRepaint();
	}
	void boutonZoomOutClicked(){
		boutonZoomOutClicked(false);
	}
	void creerStationClicked(ActionEvent e){
		etatInterface.definirEtat(State.AJOUTER_STATION);
	}
	void creerArcClicked(ActionEvent e){
		etatInterface.definirEtat(State.AJOUTER_ARC);
	}
	/*
	void calculerStatsClicked(ActionEvent e){
		reseau.calculerToutesLesStatistiques();
	}
	*/

	@Override
	public void actionPerformed(ActionEvent e) {
		displayPanel.requestRepaint();
	}

	protected void reinitStatesList(){
		disableStateListener = true;
		statesListModel.clear();
		for(NetworkState hstate : reseau.getHistoryStates()){
			statesListModel.add(0,hstate.getName());
		}
		disableStateListener = false;
		statesList.setSelectedIndex(reseau.getHistoryStates().size()-reseau.getActiveHistoryState()-1);
	}
	@Override
	public void onStateChanged(ControlleurEtatInterface etatInterface,
			State state) {
		//statesList.getSelectedIndex()
		if(reseau.isHistoryStateChanged()){
			reinitStatesList();
		}
	}

	@Override
	public void onReset(ControlleurEtatInterface etatInterface) {
		
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if(!disableStateListener){
			if(e.getValueIsAdjusting()==false){
				//System.out.println("Listener called");
				//System.out.println(e.getFirstIndex());
				//System.out.println(statesList.getSelectedIndex());
				reseau.activateHistory(reseau.getHistoryStates().size()-statesList.getSelectedIndex()-1);//statesList.getSelectedIndex());
				etatInterface.Reset();
				displayPanel.requestRepaint();
			}
		}
	}
	
	public void undoClicked(ActionEvent e){
		reseau.undo();
	}
	
	public void redoClicked(ActionEvent e){
		reseau.redo();
	}
}
