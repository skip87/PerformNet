package org.skynet;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.skynet.afficheur.AfficheurReseau;
import org.skynet.model.ControlleurReseau;
import org.skynet.ui.MainWindowFrame;
public class Main {
	public static void main(String[] args) {
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
		//new MainWindowFrame(new ControlleurReseau(), new AfficheurReseau());
	    SwingUtilities.invokeLater(new Runnable(){  
	        public void run(){  
	          //new Testing().buildGUI();
	        	new MainWindowFrame(new ControlleurReseau(), new AfficheurReseau());
	        }  
	      });
	}
}
