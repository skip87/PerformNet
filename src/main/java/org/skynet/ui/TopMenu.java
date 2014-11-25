package org.skynet.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.skynet.model.ControlleurEtatInterface;
import org.skynet.model.ControlleurReseau;

public class TopMenu extends JMenuBar {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	MainPanel displayPanel;
	ControlleurReseau controlleur;
	ControlleurEtatInterface etatInterface;
	public TopMenu(ControlleurReseau controlleur,ControlleurEtatInterface etatInterface,MainPanel displayPanel){
		this.controlleur = controlleur;
		this.etatInterface = etatInterface;
		this.displayPanel = displayPanel;
		
		JMenu fileMenu = new JMenu("File");
		add(fileMenu);
		JMenuItem menuItem;
		// !! NEW
		menuItem = new JMenuItem("New", KeyEvent.VK_N);
		fileMenu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				newClicked(arg0);
			}
		});
		// !! SAVE
		menuItem = new JMenuItem("Save", KeyEvent.VK_S);
		fileMenu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveClicked(arg0);
			}
		});
		// SAVE AS
		menuItem = new JMenuItem("Save As", KeyEvent.VK_A);
		fileMenu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveAsClicked(arg0);
			}
		});
		// !! OPEN
		menuItem = new JMenuItem("Open", KeyEvent.VK_O);
		fileMenu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadClicked(arg0);
			}
		});
		// !! OPEN
		menuItem = new JMenuItem("Open", KeyEvent.VK_O);
		fileMenu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				loadClicked(arg0);
			}
		});

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
		displayPanel.requestRepaint();
	}
	protected void newClicked(ActionEvent e){
		//controlleur.newReseau();
		etatInterface.newClicked(e);
	}
}
