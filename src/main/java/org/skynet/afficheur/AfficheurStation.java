package org.skynet.afficheur;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.LinkedList;
import java.util.List;

import org.skynet.model.ControlleurReseau;
import org.skynet.reseau.Element;
import org.skynet.reseau.Station;
import org.skynet.utils.Transform;
import org.skynet.utils.Utils;

public class AfficheurStation extends AfficheurNoeud {
	private class TexteNoeudBackgroundDrawerRectangle implements TexteNoeudBackgroundDrawer{
		Color fill;
		Color border=null;
		int x,y,width,height;
		@SuppressWarnings("unused")
		public TexteNoeudBackgroundDrawerRectangle(Color fill, Color border,int x,int y,int width,int height){
			this.border = border;
			this.fill = fill;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
		public TexteNoeudBackgroundDrawerRectangle(Color fill,int x,int y,int width,int height){
			this.fill = fill;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		@Override
		public void draw(Graphics2D g2) {
			g2.setColor(fill);
			g2.fillRect(x, y, width, height);
			if(border!=null){
				g2.setColor(border);
				g2.drawRect(x, y, width, height);
			}
		}
	}
	private class TexteNoeudBackgroundDrawerArc implements TexteNoeudBackgroundDrawer{
		Color fill;
		Color border;
		int x,y,width,height,startAngle,arcAngle;
		public TexteNoeudBackgroundDrawerArc(Color fill, Color border,int x,int y,int width,int height,int startAngle,int arcAngle){
			this.border = border;
			this.fill = fill;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.startAngle = startAngle;
			this.arcAngle = arcAngle;
		}

		@Override
		public void draw(Graphics2D g2) {
			g2.setColor(fill);
			g2.fillArc(x, y, width, height, startAngle, arcAngle);
			g2.setColor(border);
			g2.drawArc(x, y, width, height, startAngle, arcAngle);
		}
	}
	public AfficheurStation() {
	}
	@Override
	public List<TexteNoeud> preRender(Graphics2D g2, ControlleurReseau controlleur, Element _noeud){
		int hautOffset = 0;
		//int basOffset = 0;
		Station noeud = (Station) _noeud;
		List<TexteNoeud> textes = new LinkedList<TexteNoeud>();
		textes.clear();
			Font font = g2.getFont().deriveFont(Font.BOLD,controlleur.pixels(tailleFont));
			
			g2.setFont(font);
			
			int alpha = alphaDefaut;
			if(controlleur.noeudHighlighted(noeud) || controlleur.noeudEstSelectionnee(noeud))
				alpha = 255;

			FontMetrics fontMetrics = g2.getFontMetrics();
			Color selectedColor = obtenirCouleurSelectionnee();
			Color fill = new Color(selectedColor.getGreen(), selectedColor.getGreen(), selectedColor.getBlue(), alpha);
				
				
			Point coordonnees = controlleur.pixels(noeud.obtenirCoordonnees());
			//Point lineStart = new Point((int)coordonnees.x,(int)(coordonnees.y+noeud.obtenirHauteur()/2))
			Point lineStart = new Point(
					coordonnees.x-controlleur.pixels(noeud.obtenirDimensionLigneAttente())
					,(coordonnees.y+controlleur.pixels(noeud.obtenirHauteur())/2)
				)
				, lineEnd = new Point(
					coordonnees.x
					,(coordonnees.y+controlleur.pixels(noeud.obtenirHauteur())/2)
				);
			
			// Affichage de l'image de la station
			Point stationStart = coordonnees;//new Point(coordonnees.x,coordonnees.y);
			Dimension stationDimension = new Dimension(controlleur.pixels(noeud.obtenirLargeur()),controlleur.pixels(noeud.obtenirHauteur()));

			// Affichage des erreurs s'ils existent
			LinkedList<String> errorMsg = new LinkedList<String>();
			if(noeud.obtenirTauxUtilisationStation()>=1.0){
				errorMsg.add("Taux d'utilisation est >=100%");
			}
			if(noeud.estConnecteEntree()==false){
				errorMsg.add("La station n'est pas connect\u00E9 \u00E0 l'entr\u00E9e");
			}
			if(noeud.obtenirArcsSortants().size()==0){
				errorMsg.add("La station n'est pas connect\u00E9 \u00E0 la sortie");
			}
			if(errorMsg.size()>0){
				//int offsetTop = fontMetrics.getHeight()*errorMsg.size()/2;
				//int centreX = stationStart.x + stationDimension.width/2;
				//int centreY = stationStart.y + stationDimension.height/2;
				for(String s : errorMsg){
					//g2.drawString()
					
					textes.add(
							new TexteNoeud(
									s
								, new Point(
										(int)(
												stationStart.x + stationDimension.width/2
												- fontMetrics.stringWidth(s)/2
											)
											,(int)(
												stationStart.y
												- controlleur.pixels(obtenirSpacing())
												- hautOffset
											)
								)
								, font
								, fontMetrics
								, new Color(0xFF0000)
							)
						);
					//offsetTop += fontMetrics.getHeight();
					hautOffset += fontMetrics.getHeight();
				}
			}
			
			
			
			
			
			// Affichage du texte en haut-droite de la station
			int nbPostes = noeud.obtenirNombrePostes();
			String hautDroite = "x"+nbPostes+"";
			int arcWidth = (int)(fontMetrics.stringWidth(hautDroite)*3); 
			int arcHeight = (int)(fontMetrics.getHeight()*3); 
			int width = Math.max(arcWidth, arcHeight);
			int height = arcHeight;//width;
			//Color x ;
			TexteNoeudBackgroundDrawer arcDrawer = new TexteNoeudBackgroundDrawerArc(
					fill
					,obtenirCouleurTexte2()
					,stationStart.x + stationDimension.width - width/2
					, stationStart.y - height/2
					, width
					, height
					, 180
					, 90
			);
			textes.add(
				new TexteNoeud(
						hautDroite
					, new Point(
						(int)(
								stationStart.x + stationDimension.width
								- fontMetrics.stringWidth(hautDroite)
								- controlleur.pixels(obtenirSpacing())
						)
						,(int)(
								stationStart.y
								+ fontMetrics.getHeight()
						)
					)
					, font
					, fontMetrics
					, obtenirCouleurTexte2()
					, arcDrawer
				)
			);
				
			g2.setColor(obtenirCouleurAuDessous());
			// Affichage  nom de la station (haut de station)
			String nomStation = noeud.obtenirNom();
			textes.add(
				new TexteNoeud(
						nomStation
					, new Point(
							(int)(
									stationStart.x + stationDimension.width/2
									- fontMetrics.stringWidth(nomStation)/2
								)
								,(int)(
									stationStart.y
									- controlleur.pixels(obtenirSpacing())
									- hautOffset
								)
					)
					, font
					, fontMetrics
					, obtenirCouleurAuDessous()
				)
			);
			hautOffset += fontMetrics.getHeight();

			g2.setColor(obtenirCouleurAuDessous());
			// Affichage  temps moyen passe par le poste (haut de station)
			String texteHautStation = Utils.format(noeud.obtenirCapaciteTraitement())+"/h";
			if(Utils.showFullStatistics(controlleur, noeud))// || controlleur.noeudEstSelectionnee(noeud))
				texteHautStation = "Capacite traitement: "+texteHautStation;
			textes.add(
				new TexteNoeud(
						texteHautStation
					, new Point(
							(int)(
									stationStart.x + stationDimension.width/2
									- fontMetrics.stringWidth(texteHautStation)/2
								)
								,(int)(
									stationStart.y
									- controlleur.pixels(obtenirSpacing())
									- hautOffset
								)
					)
					, font
					, fontMetrics
					, obtenirCouleurAuDessous()
				)
			);
			hautOffset += fontMetrics.getHeight();

			//g2.setColor(new Color(0xe83c00));
			// Affichage  temps moyen passe par le poste (bas de station)
			String texteBasStation = Transform.time(noeud.obtenirVitesseMoyenneTraitementDEntite());//Utils.format(noeud.obtenirTempsMoyenTraitementParPoste());
			if(Utils.showFullStatistics(controlleur, noeud))// || controlleur.noeudEstSelectionnee(noeud))
				texteBasStation = "Vitess moyenne traitement d'entit\u00E9s: "+texteBasStation;
			textes.add(
				new TexteNoeud(
						texteBasStation
					, new Point(
							(int)(
									stationStart.x + stationDimension.width/2
									- fontMetrics.stringWidth(texteBasStation)/2
								)
								,(int)(
									stationStart.y + stationDimension.height
									+ fontMetrics.getHeight()
								)
					)
					, font
					, fontMetrics
					, new Color(0xe83c00)
				)
			);
			

		if(noeud.estConnecteEntree() && noeud.obtenirTauxUtilisationStation()<1.0){//controlleur.obtenirReseauValide()){
			// Affichage du texte au centre de la station
			float tauxUtilisation = noeud.obtenirTauxUtilisationStation()*100;
			String texteCentreStation = Utils.format(tauxUtilisation) + "%";
			if(Utils.showFullStatistics(controlleur, noeud))// || controlleur.noeudEstSelectionnee(noeud))
				texteCentreStation = "Taux utilisation: "+texteCentreStation;
			//int centreX = stationStart.x + stationDimension.width/2;
			//int centreY = stationStart.y + stationDimension.height/2;
			
			// Afficher le background pour que le texte soit visible
			float _blue = .6f;//0.5f + (1-noeud.obtenirTauxUtilisationStation())*.5f;
			float _green = 0;//0.5f + (noeud.obtenirTauxUtilisationStation())*.5f;
			float _red = 0;
			Color fill2 = new Color(_red, _green, _blue, (float)alpha/255);
			// Generer les couleurs pour le texte
			float red = Math.min(1.0f, 0.5f + noeud.obtenirTauxUtilisationStation()*.5f);
			float green = Math.min(1.0f, 0.5f + (1-noeud.obtenirTauxUtilisationStation())*.5f);

			if(Utils.showFullStatistics(controlleur, noeud)){
				int xoffset = Math.max(
						//centreX
						stationStart.x + stationDimension.width
						- fontMetrics.stringWidth(texteCentreStation)
						- controlleur.pixels(obtenirSpacing())
						,stationStart.x
				);
				int xoffset2 = xoffset + controlleur.pixels(obtenirSpacing());
				
				int yoffset = (int)(
						//centreY
						//- fontMetrics.getHeight()/2
						stationStart.y+stationDimension.height-fontMetrics.getHeight()
					);
				int yoffset2 = yoffset + fontMetrics.getHeight()-controlleur.pixels(obtenirSpacing());
				
				TexteNoeudBackgroundDrawer rectDrawer = new TexteNoeudBackgroundDrawerRectangle(
					fill2
					,(int)(xoffset
						/*
						centreX
						- fontMetrics.stringWidth(texteCentreStation)/2
						- controlleur.pixels(obtenirSpacing())
						*/
					)
					, (int)(
							yoffset
					)
					, (int)(fontMetrics.stringWidth(texteCentreStation) + controlleur.pixels(obtenirSpacing())*2)
					, (int)(fontMetrics.getHeight()// + controlleur.pixels(obtenirSpacing())
					)
	
				);
				
				textes.add(
						new TexteNoeud(
							texteCentreStation
							, new Point(
								(int)(
									/*
									centreX
									- fontMetrics.stringWidth(texteCentreStation)/2
									*/
									xoffset2
								)
								,(int)(
									/*centreY
									+ fontMetrics.getHeight()/2*/
										yoffset2
								)
							)
							, font
							, fontMetrics
							, new Color(red,green,0)
							, rectDrawer
						)
					);
			} else {
				// Affichage du texte en haut-droite de la station
				//int nbPostes = noeud.obtenirNombrePostes();
				//String hautDroite = "x"+nbPostes+"";
				int arcWidth2 = (int)(fontMetrics.stringWidth(texteCentreStation)*3); 
				int arcHeight2 = (int)(fontMetrics.getHeight()*3); 
				int width2 = Math.max(arcWidth2, arcHeight2);
				int height2 = arcHeight2;//width;
				//Color x ;
				TexteNoeudBackgroundDrawer arcDrawer2 = new TexteNoeudBackgroundDrawerArc(
						fill2
						,obtenirCouleurTexte2()
						,stationStart.x-width2/2// + stationDimension.width - width2/2
						, stationStart.y + stationDimension.height - height2/2
						, width2
						, height2
						, 0
						, 90
				);
				textes.add(
					new TexteNoeud(
							texteCentreStation
						, new Point(
							(int)(
									stationStart.x// + stationDimension.width
									//- fontMetrics.stringWidth(texteCentreStation)
									//- controlleur.pixels(obtenirSpacing())
									+ 2*controlleur.pixels(obtenirSpacing())
							)
							,(int)(
									stationStart.y + stationDimension.height
									//+ fontMetrics.getHeight()
									- 2*controlleur.pixels(this.obtenirSpacing())
							)
						)
						, font
						, fontMetrics
						, new Color(red,green,0)
						, arcDrawer2
					)
				);
			}
			
			
			
			
			
			
				

				
				
				// Affichage texte haut de la file d'attente
				String texteHaut = Utils.format(noeud.obtenirTauxArrive())+"/h";
				if(Utils.showFullStatistics(controlleur, noeud))// || controlleur.noeudEstSelectionnee(noeud))
					texteHaut = "Taux arriv\u00E9e: "+texteHaut;
				textes.add(
					new TexteNoeud(
						texteHaut
						, new Point(
								(int)(
										Math.min(
												(lineEnd.x + lineStart.x)/2
												- fontMetrics.stringWidth(texteHaut)/2
												,lineEnd.x - fontMetrics.stringWidth(texteHaut)
										)
									)
									,(int)(
										lineStart.y 
										//- fontMetrics.getHeight() 
										- controlleur.pixels(obtenirSpacing())
									)
						)
						, font
						, fontMetrics
						, new Color(0x003080)
					)
				);

				
				// Affichage texte bas de la file d'attente
				String texteBas = Utils.format(noeud.obtenirLongueurMoyenneFileAttente())+" e";
				if(Utils.showFullStatistics(controlleur, noeud))// || controlleur.noeudEstSelectionnee(noeud))
					texteBas = "Longuer moyenne: "+texteBas;
				textes.add(
						new TexteNoeud(
							texteBas
							, new Point(
									(int)(
											Math.min(
													(lineEnd.x + lineStart.x)/2
													- fontMetrics.stringWidth(texteBas)/2
													,lineEnd.x - fontMetrics.stringWidth(texteBas)
											)
										)
										,(int)(
											lineStart.y 
											+ fontMetrics.getHeight() * 2
										)
							)
							, font
							, fontMetrics
							, new Color(0x5e7fb6)
						)
					);
				// Affichage texte bas de la file d'attente
				String texteBas2 = Transform.time(noeud.obtenirTempsMoyenPasseDansFileAttenteStation());
				if(Utils.showFullStatistics(controlleur, noeud))// || controlleur.noeudEstSelectionnee(noeud))
					texteBas2 = "Temps moyen dans file attente: "+texteBas2;
				textes.add(
						new TexteNoeud(
								texteBas2
							, new Point(
									(int)(
											Math.min(
													(lineEnd.x + lineStart.x)/2
													- fontMetrics.stringWidth(texteBas2)/2
													,lineEnd.x - fontMetrics.stringWidth(texteBas2)
											)
										)
										,(int)(
											lineStart.y 
											+ fontMetrics.getHeight()
										)
							)
							, font
							, fontMetrics
							, new Color(0x9d3b00)
						)
					);
		}
		
		
		
		
		return textes;
	}
	void affiche(Graphics2D g2, ControlleurReseau controlleur, Station noeud){
		affiche(g2,controlleur,noeud,false);
	}
	void affiche(Graphics2D g2, ControlleurReseau controlleur, Station noeud, boolean estPreview){
		//g2.setFont(g2.getFont().deriveFont(Font.BOLD,controlleur.transformerMetresEnReseau(tailleFont)));
		g2.setFont(g2.getFont().deriveFont(Font.BOLD,controlleur.pixels(tailleFont)));
		
		
		//_affiche(g2, reseau, noeud);
		// Maintenant il faut afficher la ligne d'attente
		Point coordonnees = controlleur.pixels(noeud.obtenirCoordonnees());
		//Point lineStart = new Point((int)coordonnees.x,(int)(coordonnees.y+noeud.obtenirHauteur()/2))
		Point lineStart = new Point(
				coordonnees.x-controlleur.pixels(noeud.obtenirDimensionLigneAttente())
				,(coordonnees.y+controlleur.pixels(noeud.obtenirHauteur())/2)
			)
			, lineEnd = new Point(
				coordonnees.x
				,(coordonnees.y+controlleur.pixels(noeud.obtenirHauteur())/2)
			);
		
		// Affichage de l'image de la station
		Point stationStart = coordonnees;//new Point(coordonnees.x,coordonnees.y);
		Dimension stationDimension = new Dimension(controlleur.pixels(noeud.obtenirLargeur()),controlleur.pixels(noeud.obtenirHauteur()));

		// Afficher l'erreur si le noeud n'est pas connecte a l'entree/sortie
		//int errors = 0;
		
		// Affichage de la ligne
		g2.setColor(Color.gray);
		g2.drawLine(lineStart.x+5,lineStart.y,lineEnd.x,lineEnd.y);
		// Affichage de l'image de la station
		g2.drawImage(noeud.obtenirImage(),stationStart.x,stationStart.y,stationDimension.width,stationDimension.height,null);
		// Affichage du highlight
		_afficheHighlight(g2, controlleur, noeud);
		
		List<TexteNoeud> textes = preRender(g2, controlleur, noeud);
		if(textes!=null){
			for(TexteNoeud texte : textes){
				texte.draw(g2);
			}
		}
		
	}
}
