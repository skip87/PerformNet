package org.skynet.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

public class SkynetImageInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3532571576702988064L;
	public transient BufferedImage image;
	public transient boolean used = false;
	public String path = null;
	public String extension = null;
	public SkynetImageInfo(String path) throws IOException{
		extension = path.substring(path.lastIndexOf('.')+1).toLowerCase();
		image = ImageIO.read(new File(path));
	}
	public SkynetImageInfo(BufferedImage image) throws IOException{
		this.image = image;
	}
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        if(!ImageIO.write(image, extension != null ? extension : "png", out)){ // png is lossless
        	ImageIO.write(image, "png", out);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        image=ImageIO.read(in);
    }	
}
