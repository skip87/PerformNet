package org.skynet.ui.dialogs;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JOptionPane;

//import org.skynet.model.ControlleurReseau;

public abstract class DialogEdition<T> extends javax.swing.JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected DialogEdition(){
		setLayout(new GridLayout(0, 2));
	}
	protected String getDialogTitle(){
		return "Edition";
	}
	abstract protected boolean save();
	public void showErrorMessage(List<String> erreurs){
	    StringBuilder builder = new StringBuilder();
	    builder.append( erreurs.remove(0));

	    for( String s : erreurs) {
	        builder.append( "\n");
	        builder.append( s);
	    }
	    showErrorMessage(builder.toString());
	}
	public void showErrorMessage(String message){
		JOptionPane.showMessageDialog(this, message);
	}
	protected boolean openDialog(){
		Object[] options = {
			"Sauvegarder",
            "Annuler"
		};
		while(JOptionPane.showOptionDialog(
			null
			,this
			,getDialogTitle()
			,JOptionPane.YES_NO_OPTION
			,JOptionPane.PLAIN_MESSAGE
			,null
			,options
			,options[0]
		)==JOptionPane.OK_OPTION){
			if(save())
				return true;
		}
		return false;
	}
}
