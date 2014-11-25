package org.skynet.utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;

/*
 * C'est le classe que nous allons utiliser pour sauvegarder toutes les images
 * l'avantage sera que toutes les images vont etre duplique seulement au fur et au mesure du besoin
 * et l'image aura seulement le path comme attribut, qui est beaucoup plus efficace que les pixels ;)
 */
public class SkynetImage implements Serializable {
	/*
	 * Nous avons besoin de cette classe pour pouvoir sauvegardeer l'image dans le format original (png, jpg, ...)
	 */
	/**
	 * 
	 */
	private static final long serialVersionUID = 5142232221380349597L;
	
	public transient static Map<String,SkynetImageInfo> cachedImages = new Hashtable<String,SkynetImageInfo>();
	
	private transient SkynetImageInfo image;
	@SuppressWarnings("unused")
	private String path = null;
	private String hash = "";
	
	public SkynetImage(BufferedImage image) throws IOException{
		try {
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] digest = null;
	        ImageIO.write(image, "png", outputStream);
	        byte[] data = outputStream.toByteArray();
	
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        md.update(data);
	        digest = md.digest();
	        for (int i = 0; i < digest.length; i++) {
	        	hash += String.format("%02x", digest[i]);
	        }
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		this.image=cachedImages.get(hash);
		if(this.image==null){
			//image = ImageIO.read(new File(path));
			this.image = new SkynetImageInfo(image);//path);
			cachedImages.put(hash,this.image);
		}
	}
	protected void initImage() throws IOException{
	}
	public SkynetImage(String path) throws IOException{
		this.path = path;
		
		try {
			MessageDigest md;
			byte[] digest = null;
			md = MessageDigest.getInstance("MD5");
			try {
				InputStream is = Files.newInputStream(Paths.get(path));
				@SuppressWarnings("unused")
				DigestInputStream dis = new DigestInputStream(is, md);
				// Read stream to EOF as normal...
				int numBytes;
				byte []bytes = new byte[1024];
				while ((numBytes = is.read(bytes)) != -1) {
					md.update(bytes, 0, numBytes);
				}			} catch(IOException e) {
				e.printStackTrace();
			}
			digest = md.digest();
	        for (int i = 0; i < digest.length; i++) {
	        	hash += String.format("%02x", digest[i]);
	        }
	        System.out.println("Image hash created: "+hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		//initImage();
		image=cachedImages.get(hash);
		if(image==null){
			//image = ImageIO.read(new File(path));
			image = new SkynetImageInfo(path);
			cachedImages.put(hash,image);
		}
	}
	
	public BufferedImage getImage(){
		return image.image;
	}
	
	/*
	 * Il faut marker les images que nous allons utiliser, pour pouvoir les sauvegarder 
	 */
	private void writeObject (ObjectOutputStream out) throws IOException{
		out.defaultWriteObject();
		
	}
	/*
	 * Nous allons utiliser cette fonction pour sauvegarder l'image
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		image=cachedImages.get(hash);
	}
}
