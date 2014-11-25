package org.skynet.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.skynet.afficheur.AfficheurReseau;
import org.skynet.model.ControlleurEtatInterface;
import org.skynet.model.ControlleurReseau;
import org.skynet.model.EtatInterfaceListener;
import org.skynet.model.ControlleurEtatInterface.State;
import org.skynet.utils._Point;

public class MainPanel 
	extends JPanel 
	implements MouseListener, MouseMotionListener, KeyListener, MouseWheelListener 
{
	private static final long serialVersionUID = 1L;

	private final AfficheurReseau afficheur;
	private final ControlleurReseau reseau;
	private final ControlleurEtatInterface etatInterface;
	
	public MainPanel(ControlleurReseau reseau, AfficheurReseau affichage, ControlleurEtatInterface etatInterface) {
		this.afficheur = affichage;
		this.reseau = reseau;
		this.etatInterface = etatInterface;
		
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				tailleAffichageChange(e);
			}
		});
		
		this.addKeyListener(this);
		this.addMouseWheelListener(this);
		this.setFocusable(true);
		
		/*reseau.addListener(new ReseauListener() {
			public void dimensionChange(_Point size) {
				dimensionReseauChange(size);
			}
		});*/
		etatInterface.addListener(new EtatInterfaceListener() {
			@Override
			public void onStateChanged(ControlleurEtatInterface etatInterface, State state) {
				etatInterfaceStateChanged(etatInterface,state);
			}

			@Override
			public void onReset(ControlleurEtatInterface etatInterface) {
				etatInterfaceReset(etatInterface);
			}
		});
		
		//this.setBackground(new Color(0x7B,0xE1,0xFF));
		this.setBackground(new Color(0xe5efef));
	}
	
	void requestRepaint(){
		String messagePopup = reseau.pickMessageErreurPopup();
		if(messagePopup!=null){
			JOptionPane.showMessageDialog(null,
					messagePopup,
				    "Erreur reseau",
				    JOptionPane.ERROR_MESSAGE);
		}
		// C'est un hack temporaire (il n y a rien plus persistant que les choses temporaires) :P
		dimensionReseauChange(reseau.obtenirDimensionReseau());
		repaint();
	}
	
	void etatInterfaceStateChanged(ControlleurEtatInterface etatInterface, State state){
		_Point size = reseau.obtenirDimensionReseau();
		dimensionReseauChange(size);
	}
	void etatInterfaceReset(ControlleurEtatInterface etatInterface){
		requestRepaint();
	}
	private void dimensionReseauChange(_Point size){
		Dimension new_size = new Dimension(
			reseau.pixels(size.x)
			,reseau.pixels(size.y)
		);
		this.setPreferredSize(new_size);
		this.revalidate();
	}
	
	private void tailleAffichageChange(ComponentEvent e) {
		//reseau.dimensionsAffichageChanges(e.getComponent().getSize());
	}

	public void affiche(Graphics g,ControlleurReseau controlleur,ControlleurEtatInterface etatInterface){
		afficheur.affiche(g, reseau, null, etatInterface);
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		afficheur.affiche(g, reseau, this, etatInterface);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(etatInterface.mouseClicked(arg0))
			requestRepaint();
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		this.requestFocusInWindow();
		if(etatInterface.mouseEntered(arg0))
			requestRepaint();
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		if(etatInterface.mouseExited(arg0))
			requestRepaint();
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		this.requestFocusInWindow();
		if(etatInterface.mousePressed(arg0))
			requestRepaint();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(etatInterface.mouseReleased(arg0))
			requestRepaint();
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if(etatInterface.mouseDragged(arg0))
			requestRepaint();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		if(etatInterface.mouseMoved(arg0))
			requestRepaint();
	}


	@Override
	public void keyPressed(KeyEvent arg0) {
		if(etatInterface.keyPressed(arg0))
			requestRepaint();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		if(etatInterface.keyReleased(arg0))
			requestRepaint();
	}

	@Override
	public void keyTyped(KeyEvent arg0) {		
		if(etatInterface.keyTyped(arg0))
			requestRepaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(etatInterface.mouseWheelMoved(e))
			requestRepaint();
	}
}
