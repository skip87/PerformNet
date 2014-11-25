package org.skynet.ui.dialogs;

import java.io.File;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class DialogSaveAs extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static File Open(){
		return Open((File)null,null);
	}
	public static File Open(String path){
		return Open(path!=null ? new File(path) : null,null);
	}
	public static File Open(File file){
		return Open(file,null);
	}
	public static File Open(List<SkynetFileFilter> filters){
		return Open((File)null,filters);
	}
	public static File Open(String path,List<SkynetFileFilter> filters){
		return Open(path!=null ? new File(path) : null,filters);
	}
	public static File Open(File file,List<SkynetFileFilter> filters){
		JFileChooser fileChooser = new JFileChooser();
		if(filters!=null){
			fileChooser.setAcceptAllFileFilterUsed(false);
			for(SkynetFileFilter filter : filters){
				fileChooser.addChoosableFileFilter(filter);
			}
		}
		if(file!=null){
			fileChooser.setCurrentDirectory(file);
		}
		switch(fileChooser.showOpenDialog(null)){
			case JFileChooser.APPROVE_OPTION:
				return fileChooser.getFileFilter() instanceof SkynetFileFilter ? ((SkynetFileFilter)fileChooser.getFileFilter()).testExtension(fileChooser.getSelectedFile()) : fileChooser.getSelectedFile();
			default:
				break;
		}
		return null;
	}
}
