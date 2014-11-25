package org.skynet.model;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.skynet.reseau.Arc;
import org.skynet.reseau.Element;
import org.skynet.reseau.Entree;
import org.skynet.reseau.ImageDeFond;
import org.skynet.reseau.Noeud;
import org.skynet.reseau.Sortie;
import org.skynet.reseau.Station;
import org.skynet.ui.MainPanel;
import org.skynet.ui.dialogs.DialogEditionArc;
import org.skynet.ui.dialogs.DialogEditionImageDeFond;
import org.skynet.ui.dialogs.DialogEditionSortie;
import org.skynet.ui.dialogs.DialogEditionStation;
import org.skynet.ui.dialogs.DialogSaveAs;
import org.skynet.ui.dialogs.DialogsEditionEntree;
import org.skynet.ui.dialogs.SkynetFileFilter;
import org.skynet.utils._Point;

public class ControlleurEtatInterface {
	private List<EtatInterfaceListener> listeners = new LinkedList<EtatInterfaceListener>(); 
	
	boolean isFake = false;
	public enum State {
		NORMAL, AJOUTER_STATION, AJOUTER_ARC, AJOUTER_ARC_STATION2
		, STATION_SELECTIONNEE, SELECTION_RANGEE
		, DEPLACER_ARC_DEBUT, DEPLACER_ARC_FIN, AJOUT_IMAGE_FOND
	}
	private State etat = State.NORMAL;
	
	private _Point coordonnees_preview = null;

	private Point coordonnees_souris = null;
	boolean dragged = false; // il faut emuler le dragging state car eclipse ne le nous fournis pas
	final ControlleurReseau reseau;
	Point prevCoord;
	
	private boolean mouseInPanel = false;
	
	boolean nodesMoved = false;
	
	boolean _addedNew = false; // une emulation pour l'ajout de la station dans la liste des selections
	
	public Point obtenirCoordonneesSouris(){
		return coordonnees_souris;
	}
	public _Point obtenirCoordonneesSourisReseau(){
		return coordonnees_souris != null ? reseau.metres(coordonnees_souris) : null;
	}
	public ControlleurEtatInterface(ControlleurReseau reseau){
		this.reseau = reseau;
	}
	public boolean obtenirSourisEstDansReseau(){
		return mouseInPanel;
	}
	/*
	 * La fonction Reset sera appele lors de reinitialisation du reseau (new/load/...)
	 */
	public void Reset(){
		reseau.controleValiditeReseau();
		dragged = false;
		_addedNew = false;
		//etat = State.NORMAL;
		definirEtat(State.NORMAL);
		for(EtatInterfaceListener listener : listeners){
			listener.onReset(this);
		}
	}
	public State obtenirEtat(){
		return etat;
	}
	public void addListener(EtatInterfaceListener listener){
		listeners.add(listener);
	}
	public void definirEtat(State etat){
		this.etat = etat;
		for(EtatInterfaceListener listener : listeners){
			listener.onStateChanged(this,etat);
		}
	}
	public boolean mouseClicked(MouseEvent arg0) {
		boolean repaint = false;
		Noeud noeudHighlight = null;
		Arc arcHightlight = null;
		switch(obtenirEtat()){
		case NORMAL:
			Noeud _noeud = reseau.obtenirNoeud(arg0.getPoint());
			ImageDeFond _image = reseau.obtenirImage(arg0.getPoint());
			if(arg0.getClickCount() == 2){
				if(_noeud!=null){
					if(reseau.estStation(_noeud)){
						DialogEditionStation dialog = DialogEditionStation.Open((Station)_noeud, reseau);
						// Si le dialog n'est pas null, alors ok est selectionne
						if(dialog!=null){
							/*reseau.modifierStation(
								(Station)_noeud
								,dialog.obtenirNomStation()
								,dialog.obtenirVitesseTraittement()
								,dialog.obtenirNombrePostes()
							);*/
							repaint=true;
							definirEtat(State.NORMAL);
						}
					} else if(reseau.estEntree(_noeud)){
						DialogsEditionEntree dialog2 = DialogsEditionEntree.Open((Entree)_noeud, reseau);
						// Si le dialog n'est pas null, alors ok est selectionne
						if(dialog2!=null){

								repaint=true;
								definirEtat(State.NORMAL);
						
							
						}
						System.out.println("Entrée");
					} else if(reseau.estSortie(_noeud)){
						System.out.println("Sortie");
						DialogEditionSortie dialog4 = DialogEditionSortie.Open((Sortie)_noeud, reseau);
						// Si le dialog n'est pas null, alors ok est selectionne
						if(dialog4!=null){

								repaint=true;
								definirEtat(State.NORMAL);
						}
					} 
				} else {
					Arc _arc = reseau.trouverArc(arg0.getPoint());
					if(reseau.trouverArc(arg0.getPoint())!=null){
						System.out.println("Arc");
						 
						DialogEditionArc dialog3 = DialogEditionArc.Open((Arc)_arc, reseau);
						// Si le dialog n'est pas null, alors ok est selectionne
						if(dialog3!=null){
							
							repaint=true;
							definirEtat(State.NORMAL);
						}
					}
					
					 if(reseau.estImage(_image)){
						System.out.println("ImageDeFond");
						DialogEditionImageDeFond dialog6 = DialogEditionImageDeFond.Open((ImageDeFond)_image, reseau);
						// Si le dialog n'est pas null, alors ok est selectionne
						if(dialog6!=null){

								repaint=true;
								definirEtat(State.NORMAL);
						}
					}
				}
			} else {
				// C'est un click ordinaire, il faut retirer l'element de la liste selectionnee 
				// si l'element n'est pas ajoute dans mouse pressed et le bouton CTRL et down
				if((arg0.getModifiers()&ActionEvent.CTRL_MASK)!=0){
					if(_noeud!=null){
						if(!_addedNew){
							reseau.ajouterSelection(_noeud,true);
						}
					} else if(_image!=null) {
						reseau.ajouterSelection(_image);
					}
				} else {
					reseau.reinitialiserSelection();
					if(_noeud!=null){
						reseau.ajouterSelection(_noeud);
						//reseau.ajouterHighlight(_noeud);
						noeudHighlight = _noeud;
					} else{
						arcHightlight = reseau.obtenirArcHighlight(arg0.getPoint());
						if(arcHightlight!=null){
							reseau.selectionnerArc(arg0.getPoint());
						} else if(_image!=null) {
							reseau.ajouterSelection(_image);
						}
					}
				}
			}
			repaint=true;
			break;
		case DEPLACER_ARC_DEBUT: // C'est pas un deplacement, c'est un click seulement
		case DEPLACER_ARC_FIN:
			reseau.selectionnerArc(arg0.getPoint());
			definirEtat(State.NORMAL);
			arcHightlight = reseau.obtenirArcHighlight(arg0.getPoint());
			repaint=true;
			break;
		default:
			break;
		}
		
		reseau.reinitialiserHighlight();
		if(noeudHighlight!=null)
			reseau.ajouterHighlight(noeudHighlight);
		if(arcHightlight!=null){
			reseau.ajouterHighlight(arcHightlight);
		}
		return repaint;
	}

	
	@SuppressWarnings("incomplete-switch")
	public boolean mouseEntered(MouseEvent arg0) {
		mouseInPanel = true;
		coordonnees_souris = arg0.getPoint();
		boolean repaint = false;
		switch(obtenirEtat()){
		case SELECTION_RANGEE:
			reseau.definirHighlight();
			repaint = true;
			break;
		case AJOUTER_STATION:
		case AJOUTER_ARC_STATION2:
			activerStationPreview(arg0.getPoint());
			repaint=true;
			break;
		}
		return repaint;
	}

	
	@SuppressWarnings("incomplete-switch")
	public boolean mouseExited(MouseEvent arg0) {
		mouseInPanel = true;
		coordonnees_souris = null;
		reseau.reinitialiserHighlight();
		switch(obtenirEtat()){
		case AJOUTER_STATION:
		case AJOUTER_ARC_STATION2:
			desactiverStationPreview();
			break;
		}
		return true; // repaint required
	}

	protected Station ajouterStation(Point coordonnees){
		Station station = reseau.ajouterStation(coordonnees);//reseau.obtenirCoordonneesPreview());
		desactiverStationPreview();
		definirEtat(State.NORMAL);
		reseau.reinitialiserSelection();
		return station;
	}
	
	public boolean mousePressed(MouseEvent arg0) {
		nodesMoved = false;
		dragged = false;
		boolean repaint = false;
		Noeud forceNoeud = null;
		isFake = false;
		if(javax.swing.SwingUtilities.isRightMouseButton(arg0)
			&& obtenirEtat()==State.NORMAL){
			if(reseau.obtenirNoeud(arg0.getPoint())==null
					&& reseau.obtenirNoeudClosest(arg0.getPoint())==null
					){
				Noeud noeud = ajouterStation(arg0.getPoint());
				reseau.reinitialiserHighlight();
				reseau.ajouterHighlight(noeud);
				forceNoeud = noeud;
				isFake = true;
			}
			definirEtat(State.AJOUTER_ARC);
		}
		switch(obtenirEtat()){
		case AJOUTER_STATION:
			ajouterStation(arg0.getPoint());
			repaint = true;
			break;
		case AJOUTER_ARC:
			{
				Noeud noeud1 = forceNoeud!=null ? forceNoeud : reseau.obtenirNoeud(arg0.getPoint());//reseau.obtenirHighlight();
				if(noeud1==null)
					noeud1=reseau.obtenirNoeudClosest(arg0.getPoint());
				if(noeud1==reseau.obtenirSortie()){
					definirEtat(State.NORMAL);
					reseau.ajouterMessagePopup("Sortie ne peut pas contenir arcs sortants");
				} else {
					reseau.definirNoeudTemp(noeud1);
					if(noeud1!=null){
						definirEtat(State.AJOUTER_ARC_STATION2);
					}
					activerStationPreview(arg0.getPoint());
					//System.out.println("Arc drag start");
				}
				repaint = true;
			}
			break;
		case AJOUTER_ARC_STATION2:
			Noeud noeud = reseau.obtenirNoeud(arg0.getPoint());//reseau.obtenirHighlight();
			if(noeud!=null){
				Arc arc = reseau.ajouterArc(reseau.obtenirNoeudTemp(),noeud);
				reseau.reinitialiserSelection();
				if(arc!=null) reseau.selectionnerArc(arc);
				definirEtat(State.NORMAL);
			} else {
				definirEtat(State.NORMAL);
			}
			repaint = true;
		case NORMAL:
			{
				Noeud noeud1 = reseau.obtenirNoeud(arg0.getPoint());
				reseau.definirNoeudTemp(noeud1);
				//noeud1.obtenirCoordonnees();
	
				_addedNew = false;
				Noeud _noeud = reseau.obtenirNoeud(arg0.getPoint());
				if(_noeud!=null){// Nous sommes en train de commencer deplacer element(s)
					if(reseau.noeudEstSelectionnee(_noeud)==false){ // Le noeud n'existe parmis les noeud selectionnees
						if((arg0.getModifiers()&ActionEvent.CTRL_MASK)==0){
							// Le CTRL n'est pas appuye, il faut reinitialiser la selection
							reseau.reinitialiserSelection();
						}// else {
							// sinon il faut l'ajouter dans la liste des selections
							_addedNew=reseau.ajouterSelection(_noeud);
						//}
					}
				} else {
					Arc arc = reseau.trouverArc(arg0.getPoint());
					boolean imageFound = false;
					if(arc!=null){
						_Point coord[] = arc.obtenirCoordonneesArc();
						_Point coord_reseau = reseau.metres(arg0.getPoint());
						float dist1 = (coord[0].x-coord_reseau.x)*(coord[0].x-coord_reseau.x) + (coord[0].y-coord_reseau.y)*(coord[0].y-coord_reseau.y); 
						float dist2 = (coord_reseau.x-coord[1].x)*(coord_reseau.x-coord[1].x) + (coord_reseau.y-coord[1].y)*(coord_reseau.y-coord[1].y);
						
						if(Math.min(dist1, dist2)<reseau.metres(30)*reseau.metres(30)){
							if(dist1<dist2){
								definirEtat(State.DEPLACER_ARC_DEBUT);
							} else {
								definirEtat(State.DEPLACER_ARC_FIN);
							}
							reseau.reinitialiserSelection();
							reseau.selectionnerArc(arc);
							//prevCoord = arg0.getPoint();
							activerStationPreview(arg0.getPoint());
							repaint = true;
						} else {
							// TODO : add control point on the actual arc...   significant changes will be required
							arc = null;
						}
					} else {
						ImageDeFond image = reseau.obtenirImage(arg0.getPoint());
						if(image!=null){
							imageFound = true;
							if(reseau.noeudEstSelectionnee(image)==false){ // Le noeud n'existe parmis les noeud selectionnees
								if((arg0.getModifiers()&ActionEvent.CTRL_MASK)==0){
									// Le CTRL n'est pas appuye, il faut reinitialiser la selection
									reseau.reinitialiserSelection();
								}// else {
								// sinon il faut l'ajouter dans la liste des selections
								_addedNew=reseau.ajouterSelection(image);
							}
						}
					}
					if(arc==null && !imageFound){
						if((arg0.getModifiers()&ActionEvent.CTRL_MASK)==0){
							reseau.reinitialiserSelection();
						} else {
							
						}
						reseau.definirDebutSelection(arg0.getPoint());
						reseau.definirFinSelection(arg0.getPoint());
						definirEtat(State.SELECTION_RANGEE);
					}
				}
			}			
			break;
		default:
			break;
		}
		prevCoord = arg0.getPoint();
		repaint = true;
		return repaint;
	}

	
	public boolean mouseReleased(MouseEvent arg0) {
		boolean repaint = false;
		switch(obtenirEtat()){
		case DEPLACER_ARC_DEBUT: // C'est pas un deplacement, c'est un click seulement
		case DEPLACER_ARC_FIN:
			if(dragged){
				if(obtenirEtat()==State.DEPLACER_ARC_DEBUT){
					reseau.deplacerArcDebut(arg0.getPoint());
				} else {
					reseau.deplacerArcFin(arg0.getPoint());
				}
				// C'est un drag
				reseau.reinitialiserHighlight();
				definirEtat(State.NORMAL);
				repaint = true;
			}
			break;
		case SELECTION_RANGEE:
			reseau.selectionnerNoeudsRegion();
			reseau.reinitialiserHighlight();
			definirEtat(State.NORMAL);
			repaint = true;
			break;
		case AJOUTER_ARC_STATION2:
			Noeud noeud = reseau.obtenirNoeud(arg0.getPoint());//reseau.obtenirHighlight();
			if(noeud==null)
				noeud = reseau.obtenirNoeudClosest(arg0.getPoint());
			//System.out.println(noeud);
			if(noeud!=reseau.obtenirNoeudTemp()){
				// Un petit hack pour ajouter la station en draggant l'arc
				boolean noeudCree = false;
				if(noeud==null){
					definirEtat(State.AJOUTER_STATION);
					mousePressed(arg0);
					noeud = reseau.obtenirNoeud(arg0.getPoint());
					reseau.reinitialiserSelection();
					if(noeud!=null){
						reseau.ajouterSelection(noeud);
						noeudCree = true;
					}
				}
				if(noeud!=null){
					Arc arc = reseau.ajouterArc(reseau.obtenirNoeudTemp(),noeud);
					if(noeudCree!=true){
						reseau.reinitialiserSelection();
						if(arc!=null) reseau.selectionnerArc(arc);
					}
					definirEtat(State.NORMAL);
				}
			} else if(isFake){
				definirEtat(State.NORMAL);
			}
			repaint=true;
		case NORMAL:
			if(nodesMoved){
				reseau.notifyNoeudsDeplaces();
				// il faut notifier tous le monde que les elements sont deplacees
				this.definirEtat(State.NORMAL);
			}
			break;
		default:
			break;
		}
		return repaint;
	}

	
	public boolean mouseDragged(MouseEvent arg0) {
		coordonnees_souris = arg0.getPoint();
		dragged = true;
		boolean repaint = false;
		switch(obtenirEtat()){
		case DEPLACER_ARC_DEBUT:
		case DEPLACER_ARC_FIN:
			/*activerStationPreview(arg0.getPoint());
			this.repaint();
			reseau.reinitialiserHighlight();
			reseau.ajouterHighlight(reseau.obtenirNoeud(arg0.getPoint()));
			break;*/
		case AJOUTER_ARC_STATION2:
			activerStationPreview(arg0.getPoint());
			repaint = true;
			reseau.reinitialiserHighlight();
			reseau.ajouterHighlight(reseau.obtenirNoeud(arg0.getPoint()));
			break;
		case NORMAL:
			Point pt = arg0.getPoint();
			if(pt.x<0) pt.x=0;
			if(pt.y<0) pt.y=0;
			//System.out.println("before: "+new Point(pt.x - prevCoord.x,pt.y - prevCoord.y));
				//System.out.println("noeuds selectionnees");//reseau.notifyNoeudsDeplaces();
			Point unused = reseau.deplacerNoeudsSelectionnees(new Point(pt.x - prevCoord.x,pt.y - prevCoord.y));
			prevCoord = new Point(pt.x-unused.x,pt.y-unused.y);
			if(reseau.obtenirNoeudsSelectionne().size()>0)
				nodesMoved=true;
			//System.out.println("unused: "+unused);
			//System.out.println("after: "+prevCoord);
			repaint = true;
			break;
		case SELECTION_RANGEE:
			reseau.definirFinSelection(arg0.getPoint());
			reseau.definirHighlight();
			repaint = true;
			break;
		default:
			break;
		}
		return repaint;
	}

	
	public boolean mouseMoved(MouseEvent arg0) {
		//System.out.println(reseau.obtenirNoeudClosest(arg0.getPoint()));
		coordonnees_souris = arg0.getPoint();
		boolean repaint = true;//false;
		Element prevHighlight = reseau.obtenirHighlight();
		Arc prevHighlightArc = reseau.obtenirHighlightArc();
		reseau.reinitialiserHighlight();
		switch(obtenirEtat()){
		case AJOUTER_STATION:
			activerStationPreview(arg0.getPoint());
			repaint = true;
			break;
		case AJOUTER_ARC_STATION2:
			//System.out.println("Arc moved...");
			activerStationPreview(arg0.getPoint());
			repaint = true;
		case NORMAL:
		case AJOUTER_ARC:
			reseau.rechercheHightlight(arg0.getPoint());
			//reseau.ajouterHighlight(reseau.obtenirNoeud(arg0.getPoint()));
			if(prevHighlight!=reseau.obtenirHighlight() || prevHighlightArc!=reseau.obtenirHighlightArc())//.obtenirNoeud(arg0.getPoint()))
				repaint=true;
			break;
		default:
			break;
		}
		return repaint; 
	}


	
	public boolean keyPressed(KeyEvent arg0) {
		boolean repaint = false;
		float diff = (arg0.getModifiers() & KeyEvent.SHIFT_MASK)!=0 ? 5 : ((arg0.getModifiers() & KeyEvent.CTRL_MASK)!=0 ? 2 : 1);
		if(arg0.getKeyCode()==KeyEvent.VK_DELETE){
			// Il est possible d'effacer les elements seulement dans l'etat normal afind d'eviter les bogues
			if(obtenirEtat()==State.NORMAL){
				reseau.supprimerNoeudsSelectionnees();
				reseau.supprimerArcsSelectionnees();
				reseau.reinitialiserSelection();
				repaint = true;
				definirEtat(State.NORMAL);
			}
		} else if(arg0.getKeyCode()==KeyEvent.VK_A && ((arg0.getModifiers() & KeyEvent.CTRL_MASK) != 0)){
			// CTRL + a
			if(obtenirEtat()==State.NORMAL){
				reseau.selectionnerNoeudsRegion(
						new _Point(-100000,-100000)
						,new _Point(100000,100000)
				);
				repaint = true;
				definirEtat(State.NORMAL);
			}
		} else if(arg0.getKeyCode()==KeyEvent.VK_LEFT) {
			if(obtenirEtat()==State.NORMAL){
				reseau.deplacerNoeudsSelectionnees(new Point(reseau.pixels(-diff),0));
				reseau.notifyNoeudsDeplaces();
				repaint = true;
			}
		} else if(arg0.getKeyCode()==KeyEvent.VK_RIGHT) {
			if(obtenirEtat()==State.NORMAL){
				reseau.deplacerNoeudsSelectionnees(new Point(reseau.pixels(diff),0));
				reseau.notifyNoeudsDeplaces();
				repaint = true;
			}
		} else if(arg0.getKeyCode()==KeyEvent.VK_UP) {
			if(obtenirEtat()==State.NORMAL){
				reseau.deplacerNoeudsSelectionnees(new Point(0,reseau.pixels(-diff)));
				reseau.notifyNoeudsDeplaces();
				repaint = true;
			}
		} else if(arg0.getKeyCode()==KeyEvent.VK_DOWN) {
			if(obtenirEtat()==State.NORMAL){
				reseau.deplacerNoeudsSelectionnees(new Point(0,reseau.pixels(diff)));
				reseau.notifyNoeudsDeplaces();
				repaint = true;
			}
		}
		return repaint;
	}

	
	public boolean keyReleased(KeyEvent arg0) {
		if(arg0.getKeyCode()==KeyEvent.VK_F5)
			reseau.controleValiditeReseau();
		else if(arg0.getKeyCode()==KeyEvent.VK_F6){
			Station.dimensionLigneAttenteDefaut += reseau.metres(1);
			return true;
		}
		return false;
	}

	
	public boolean keyTyped(KeyEvent arg0) {
		return false;
	}
	public boolean mouseWheelMoved(MouseWheelEvent e) {
		if(e.isControlDown()){
			reseau.definirPixelsParMetre(reseau.obtenirPixelsParMetre()-(float)e.getWheelRotation());
		}
		return true;
	}

	public void activerStationPreview(Point coordonnees){
		coordonnees_preview = reseau.metres(coordonnees);//new _Point(reseau.transformerPixelsEnReseau(coordonnees.x),reseau.transformerPixelsEnReseau(coordonnees.y));
		if(etat==State.AJOUTER_STATION)
			coordonnees_preview = reseau.collerALaGrille(coordonnees_preview);
		//System.out.println("x="+coordonnees_preview.x+";y="+coordonnees_preview.y);
	}
	public _Point obtenirCoordonneesPreview(){
		return coordonnees_preview;
	}
	public void desactiverStationPreview(){
		coordonnees_preview = null;
	}


	public void loadImageClicked(ActionEvent e){
		//this.definirEtat(State.AJOUT_IMAGE_FOND);
		List<org.skynet.ui.dialogs.SkynetFileFilter> filters = new LinkedList<org.skynet.ui.dialogs.SkynetFileFilter>();
		//SkynetFileFilter filter = new SkynetFileFilter("PDF Documents","pdf");
		filters.add(new org.skynet.ui.dialogs.SkynetFileFilter() {
			 
		    public String getDescription() {
		        return "JPEG Images (*.jpg,*.jpeg)";
		    }
		 
		    public boolean accept(File f) {
		        if (f.isDirectory()) {
		            return true;
		        } else {
		            return f.getName().toLowerCase().endsWith(".jpg") || f.getName().toLowerCase().endsWith(".jpeg");
		        }
		    }

			@Override
			public File addExtension(File file) {
				return new File(file.getPath()+".jpg");
			}
		});
		filters.add(new org.skynet.ui.dialogs.SkynetFileFilter() {
			 
		    public String getDescription() {
		        return "PNG Images (*.png)";
		    }
		 
		    public boolean accept(File f) {
		        if (f.isDirectory()) {
		            return true;
		        } else {
		            return f.getName().toLowerCase().endsWith(".png");
		        }
		    }

			@Override
			public File addExtension(File file) {
				return new File(file.getPath()+".png");
			}
		});
		JFileChooser fileChooser = new JFileChooser();
		//fileChooser.setAcceptAllFileFilterUsed(false);
		for(SkynetFileFilter filter : filters){
			fileChooser.addChoosableFileFilter(filter);
		}
		switch(fileChooser.showOpenDialog(null)){
		case JFileChooser.APPROVE_OPTION:
			File file = fileChooser.getFileFilter() instanceof SkynetFileFilter ? ((SkynetFileFilter)fileChooser.getFileFilter()).testExtension(fileChooser.getSelectedFile()) : fileChooser.getSelectedFile();
			
				if (file.exists() && !file.isDirectory()){
					//System.out.println("FILE"+file);
					//ImageDeFond tmpImage = new ImageDeFond(new _Point(0,0), ImageIO.read(file));
					//reseau.obtenirReseau().addImageDeFond(tmpImage);
					reseau.addImageDeFond(file.getPath());
				}
		default:
			break;
		}
	}
	public boolean loadClicked(ActionEvent e){
		File file = DialogSaveAs.Open(reseau.obtenirLastFilename());
		if(file!=null){
			if(!reseau.load(file.getPath()))
				JOptionPane.showMessageDialog(null,
						"Une erreur rencontrï¿½e lors de l'ouverture du fichier",
					    "Erreur ouverture",
					    JOptionPane.ERROR_MESSAGE);
			this.Reset();
			//displayPanel.requestRepaint();
			return true;
		}
		return false;
	}
	public boolean save(String path){
		if(!reseau.save(path)){
			JOptionPane.showMessageDialog(null,
				"Une erreur est survenue lors de la sauvegarde",
			    "Erreur sauvegarde",
			    JOptionPane.ERROR_MESSAGE);
			return false;
		}
		return true;
	}
	public boolean saveAsClicked(ActionEvent e){
		return saveAsClicked(e,null);
	}
	public File getFileFromDialog(){
		return getFileFromDialog((File)null,null);
	}
	public File getFileFromDialog(String existingFile,List<org.skynet.ui.dialogs.SkynetFileFilter> filters){
		return getFileFromDialog(existingFile!=null ? new File(existingFile):(File)null,filters);
	}
	public File getFileFromDialog(File existingFile){
		return getFileFromDialog(existingFile,null);
	}
	public File getFileFromDialog(File existingFile,List<org.skynet.ui.dialogs.SkynetFileFilter> filters){
		File file = existingFile != null ? DialogSaveAs.Open(existingFile,filters) : DialogSaveAs.Open(reseau.obtenirLastFilename(),filters);
		if(file!=null){
			if(file.exists()){
				switch(JOptionPane.showConfirmDialog(null, "Le fichier \""+file.getName()+"\" existe deja\nEtes-vous sur de vouloir l'\u00E9craser")){
				case JOptionPane.YES_OPTION:
					//save(file.getAbsolutePath());//"reseau.txt");
					break;
				case JOptionPane.NO_OPTION:
					return getFileFromDialog(file,filters);
				default:
					return null;
				}
			}
		}
		return file;
	}
	public boolean saveAsClicked(ActionEvent e,File existingFile){
		File file = getFileFromDialog(existingFile);
		/*
		File file = existingFile != null ? DialogSaveAs.Open(existingFile) : DialogSaveAs.Open(reseau.obtenirLastFilename());
		if(file!=null){
			if(file.exists()){
				switch(JOptionPane.showConfirmDialog(null, "Le fichier \""+file.getName()+"\" existe dï¿½jï¿½\nEtes-vous sï¿½r de vouloir l'ï¿½craser")){
				case JOptionPane.YES_OPTION:
					//save(file.getAbsolutePath());//"reseau.txt");
					break;
				case JOptionPane.NO_OPTION:
					saveAsClicked(e,file);
					return;
				default:
					return;
				}
			}
			save(file.getAbsolutePath());
		}
		*/
		if(file!=null)
			return save(file.getAbsolutePath());
		return false;
	}
	public boolean saveClicked(ActionEvent e){
		String path = reseau.obtenirLastFilename();
		if(path==null)
			return saveAsClicked(e);
		return save(path);
	}
	public void newClicked(ActionEvent e){
		boolean cnew = true;
		int n = JOptionPane.showConfirmDialog(
			    null,
			    "Do you want to save your current project before creating a new one ?",
			    "Save before exit ?",
			    JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			if(!saveClicked(e)){
				cnew=false;
			}
		}
		if(cnew){
			reseau.newReseau();
			Reset();
		}
	}
	public void exporterPdf(ActionEvent e,MainPanel displayPanel){
		List<org.skynet.ui.dialogs.SkynetFileFilter> filters = new LinkedList<org.skynet.ui.dialogs.SkynetFileFilter>();
		//SkynetFileFilter filter = new SkynetFileFilter("PDF Documents","pdf");
		filters.add(new org.skynet.ui.dialogs.SkynetFileFilter() {
			 
		    public String getDescription() {
		        return "PDF Documents (*.pdf)";
		    }
		 
		    public boolean accept(File f) {
		        if (f.isDirectory()) {
		            return true;
		        } else {
		            return f.getName().toLowerCase().endsWith(".pdf");
		        }
		    }

			@Override
			public File addExtension(File file) {
				return new File(file.getPath()+".pdf");
			}
		});
		File file = getFileFromDialog(reseau.obtenirLastFilename(),filters);
		if(file!=null){
			java.io.FileOutputStream fileOut;
			try {
				reseau.reinitialiserHighlight();
				reseau.reinitialiserSelection();
				Reset();
				String path = file.getAbsolutePath();
				if(!path.toLowerCase().endsWith(".pdf"))
					path += ".pdf";
				fileOut = new java.io.FileOutputStream( path);
	
		        java.awt.Point dimension = reseau.pixels(reseau.obtenirDimensionReseau());
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
		        float pbackup = reseau.obtenirPixelsParMetre();
		        reseau.definirPixelsParMetre(
		        		Math.min(
	        				(clip.width-clip.x)/reseau.obtenirDimensionReseau().x
	        				,(clip.height-clip.y)/reseau.obtenirDimensionReseau().y
		        		)
		        		,true
		        );
		        
		        //dimension.x
		        
		        displayPanel.affiche(pdfGraphics, reseau, this);
		        pdfGraphics.translate(0, 0);
		        pdfGraphics.dispose();
		        job.end();
		        fileOut.close();
		        
		        reseau.definirPixelsParMetre(pbackup);
		        return;
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			JOptionPane.showMessageDialog(null,
					"Une erreur est survenue lors de l'exportation dans PDF",
				    "Erreur exportation",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
}
