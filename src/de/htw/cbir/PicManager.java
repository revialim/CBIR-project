package de.htw.cbir;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import de.htw.cbir.model.Pic;

public class PicManager {

	// alle geladenen Bilder
	private Pic[] images;
	
	// welche Kategorien gibt es alle und welche Bilder gehören zu diesen Kategorien
	private HashMap<String, List<Pic>> imageCategories;
	
	public PicManager() {
		this.images = new Pic[0];
		this.imageCategories = new HashMap<String, List<Pic>>();
	}
	
	public Pic[] getImages() {
		return images;
	}
	
	public Set<String> getCategoryNames() {
		return imageCategories.keySet();
	}
	
	public Pic[] getImageInCategory(String categoryName) {
		return imageCategories.get(categoryName).toArray(new Pic[0]);
	}
	
	/**
	 * Lade alles Bilder von der Festplatte. Sollte ein 
	 * Bild eine Exception werfen wird alles abgebrochen.
	 * 
	 * Es sind nur jpg, png und gifs erlaubt. Die Kategorie
	 * wird aus dem Dateinamen ausgelesen z.B. <Kategorie>_rest.jpg
	 * 
	 * @param imageDirectory Verzeichnis mit den Bildern.
	 * @throws IOException 
	 */
	public void loadImages(File imageDirectory) throws IOException {
		
		// besorge alle gültigen Bilddateien aus dem Verzeichnis
		File[] imageFiles = imageDirectory.listFiles((File dir, String name) -> {
			return (name.endsWith("jpg") || name.endsWith("png") || name.endsWith("gif"));
		});
		
		// lade alle Bilder
		images = new Pic[imageFiles.length];
		for (int index = 0; index < imageFiles.length; index++) {
			Pic image = images[index] = loadImage(imageFiles[index]);
			image.setId(index);
			image.setRank(index);
			
			// und in eine Gruppenliste einsortiert
			String key = image.getCategory();
			List<Pic> imageList = imageCategories.get(key);
			if(imageList == null)
				imageCategories.put(key, imageList = new ArrayList<>());
			imageList.add(image);
		}
		
		// wieviele Bilder gibt es pro Gruppe
		for (List<Pic> groupImages : imageCategories.values())
			for (Pic pic : groupImages)
				pic.setCategoryOccurrence(groupImages.size());
	}
	
	/**
	 * Lade und verkleinere das Bild. Lese außerdem die Kategorie
	 * aus dem Dateinamen. <Kategorie>_<Zeichenkette>.<Endung>
	 * 
	 * @param imageFile
	 * @return
	 * @throws IOException 
	 */
	private Pic loadImage(File imageFile) throws IOException {
		BufferedImage image = ImageIO.read(imageFile);
		
		int iw = image.getWidth(null);
		int ih = image.getHeight(null);

		int maxOrigImgSize = Math.max(iw,ih);

		// maximale Kantenlänge
		float thumbSize = 128;

		//skalierungsfaktor bestimmen:
		float scale = (maxOrigImgSize > thumbSize) ? thumbSize/maxOrigImgSize : 1;

		int nw = (int)(iw*scale);
		int nh = (int)(ih*scale);

		// Bild verkleinern
		BufferedImage currBi = new BufferedImage(nw, nh, BufferedImage.TYPE_INT_ARGB);
		Graphics2D big = currBi.createGraphics();
		big.drawImage(image,0,0,nw,nh,null);

		String filename = imageFile.getName().toLowerCase();
		Pic currPic = new Pic();
		currPic.setCategory(filename.split("[_]")[0]);
		currPic.setDisplayImage(currBi);
		currPic.setOrigWidth(iw);
		currPic.setOrigHeight(ih);
		
		return currPic;
	}
}
