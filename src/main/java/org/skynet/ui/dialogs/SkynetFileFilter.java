package org.skynet.ui.dialogs;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public abstract class SkynetFileFilter extends FileFilter {
	public File testExtension(File file){
		if(!accept(file)){
			return addExtension(file);
		}
		return file;
	}
    public abstract File addExtension(File f);

}
