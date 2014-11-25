package org.skynet.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.skynet.afficheur.AfficheurReseau;
import org.skynet.model.ControlleurEtatInterface;
import org.skynet.model.ControlleurReseau;

@SuppressWarnings({ "serial", "unused" })
public class MainWindowFrame extends JFrame implements java.awt.event.KeyListener {
	private final MainPanel reseauPannel;
	private final ButtonsPanel boutonsPannel;
	private final TopPanel commandsPanel;
	private final StatusBar statusBar;
	private final ControlleurReseau reseau;
	//private final PropertiesPanel propertiesPanel;
	private final ControlleurEtatInterface etatsInterface;

	public MainWindowFrame(ControlleurReseau reseau, AfficheurReseau affichage) {
		super("PerformNet, gestion des reseaux Jackson");
		setLayout(new BorderLayout());
		
		etatsInterface = new ControlleurEtatInterface(reseau);
		
		this.reseau = reseau;

		reseauPannel = new MainPanel(reseau, affichage, etatsInterface);
		//add(reseauPannel, BorderLayout.CENTER);
		ScrollPaneReseau scrollPane = new ScrollPaneReseau();//this,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		//scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.getViewport().add(reseauPannel);
		reseauPannel.addMouseWheelListener(scrollPane);
		//scrollPane.setBounds(0, 0, 100, 100);
		add(scrollPane, BorderLayout.CENTER);
		scrollPane.addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent arg0) {
			}
			
			@Override
			public void componentResized(ComponentEvent arg0) {
				scrollPaneResized(arg0);
			}
			
			@Override
			public void componentMoved(ComponentEvent arg0) {
			}
			
			@Override
			public void componentHidden(ComponentEvent arg0) {
			}
		});

		boutonsPannel = new ButtonsPanel(reseau, reseauPannel, etatsInterface);
		add(boutonsPannel, BorderLayout.WEST);
		
		commandsPanel = new TopPanel(reseau, reseauPannel, etatsInterface);
		add(commandsPanel, BorderLayout.NORTH);

		statusBar = new StatusBar(reseau, reseauPannel, etatsInterface);
		add(statusBar, BorderLayout.SOUTH);
		
		//add(new TopMenu(reseau,etatsInterface,reseauPannel));
		setJMenuBar(new TopMenu(reseau,etatsInterface,reseauPannel));

		//propertiesPanel = new PropertiesPanel();
		//add(propertiesPanel, BorderLayout.EAST);
		
		setPreferredSize(new Dimension(1200, 800));
		//setLocation(300, 200);
		setMinimumSize(new Dimension(800,400));

		setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		addKeyListener(this);
		setFocusable(true);
		
	}

	public void scrollPaneResized(ComponentEvent e){
		if(reseau.dimensionsAffichageChanges(e.getComponent().getSize()))
			etatsInterface.Reset();
	}
	@Override
	public void keyPressed(KeyEvent arg0) {
		System.out.println("Pressed " + arg0.getKeyChar());
		switch(arg0.getKeyCode()){
		case KeyEvent.VK_DELETE:
			reseau.supprimerNoeudsSelectionnees();
			reseauPannel.repaint();
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
