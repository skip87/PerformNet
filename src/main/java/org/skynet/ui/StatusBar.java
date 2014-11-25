package org.skynet.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.skynet.model.ControlleurEtatInterface;
import org.skynet.model.ControlleurReseau;
import org.skynet.utils.Utils;
import org.skynet.utils._Point;

public class StatusBar extends JPanel implements ActionListener, MouseListener, MouseMotionListener {
	private final ControlleurReseau controlleur;
	private final MainPanel displayPanel;
	@SuppressWarnings("unused")
	private final ControlleurEtatInterface etatInterface;
	private final JButton zoomIn, zoomOut, zoomIn2, zoomOut2, zoomReset;
	
	private final JLabel coordSouris;
	private final JLabel infoComposant;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StatusBar(ControlleurReseau controlleur, MainPanel displayPanel, ControlleurEtatInterface etatInterface){
		this.controlleur = controlleur;
		this.displayPanel = displayPanel;
		this.etatInterface = etatInterface;
		
		add(coordSouris = new JLabel(""));
		coordSouris.setPreferredSize(new Dimension(90,40));
		
		add(infoComposant = new JLabel("kjhdsf kdsfjghk fdskgjhskdf gksdf"));
		infoComposant.setPreferredSize(new Dimension(150,40));
		
		displayPanel.addMouseListener(this);
		displayPanel.addMouseMotionListener(this);
		
		
		
		
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
	}
	@Override
	public void actionPerformed(ActionEvent arg0) {

	}
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		coordSouris.setText("");
	}
	@Override
	public void mouseExited(MouseEvent e) {
		coordSouris.setText("");
	}
	@Override
	public void mousePressed(MouseEvent e) {
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}
	@Override
	public void mouseDragged(MouseEvent arg0) {
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {
		_Point metres = controlleur.metres(arg0.getPoint());
		coordSouris.setText("x: "+Utils.format(metres.x)+" y:"+Utils.format(metres.y));
	}

	
	
	void boutonZoomInClicked(){
		boutonZoomInClicked(false);
	}
	void boutonZoomInClicked(boolean accelerer){
		controlleur.zoomIn(accelerer);
		displayPanel.requestRepaint();
	}
	void boutonZoomResetClicked(){
		controlleur.zoomReset();
		displayPanel.requestRepaint();
	}
	void boutonZoomOutClicked(boolean accelerer){
		controlleur.zoomOut(accelerer);
		displayPanel.requestRepaint();
	}
	void boutonZoomOutClicked(){
		boutonZoomOutClicked(false);
	}
	
}
