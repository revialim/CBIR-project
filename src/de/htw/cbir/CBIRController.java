package de.htw.cbir;

import java.util.Arrays;
import java.util.Locale;

import de.htw.cbir.evaluation.CBIREvaluation;
import de.htw.cbir.evaluation.PrecisionRecallTable;
import de.htw.cbir.feature.*;
import de.htw.cbir.model.Pic;
import de.htw.cbir.model.PicPair;
import de.htw.cbir.model.Settings;
import de.htw.cbir.model.Settings.SettingChangeEvent;
import de.htw.cbir.ui.CBIRView;

public class CBIRController {

	private Settings settings;
	private PicManager imageManager;
	
	private CBIRView ui;
	private FeatureFactory featureFactory;
	
	public CBIRController(Settings settings, PicManager imageManager) {
		this.settings = settings;
		this.imageManager = imageManager;
		
		// UI Elemente
		this.ui = new CBIRView(this);
		
		// Default Feature Factory
		changeFeatureFactory(getFeatureFactoryNames()[0]);
	}

	public PicManager getImageManager() {
		return imageManager;
	}
	
	public Settings getSettings() {
		return settings;
	}
	
	/**
	 * Berechne die Distanz aller Bilder zum Query Bild.
	 * Sortiere die Bilder von der kleinsten zur größten Distanz. 
	 * 
	 * @param queryImage
	 */
	public void sortByImage(Pic queryImage) {

		// wurde kein ein Sortieralgorithmus ausgewählt
		if(featureFactory == null) {
			System.out.println("No sorting algorithm selected");
			return;
		}
		
		Pic[] allImages = imageManager.getImages();
		long milliSec = System.currentTimeMillis();
		
		// Sortere das alle Bilder nach dem Querybild
		PicPair[] sortedArray = new PicPair[allImages.length];
		for (int i = 0; i < allImages.length; i++) {
			Pic searchImage = allImages[i];
			float distance = featureFactory.getDistance(queryImage.getFeatureVector(), searchImage.getFeatureVector());
			sortedArray[i] = new PicPair(queryImage, searchImage, distance);
		}
		
		// sortiere die Suchbilder nach der Distance zum Query Bild
		Arrays.sort(sortedArray);
		
		// berechne die mean average precision
		float ap = PrecisionRecallTable.calcAveragePrecision(sortedArray);

		// logge die Ergebnisse
		System.out.println("Feature Factory: "+featureFactory.getName());		
		System.out.printf(Locale.ENGLISH, "AP: %2.4f took %6dms for feature factory %s\n\n", ap, (System.currentTimeMillis() - milliSec), featureFactory.getName());
		
		// wende die Reihnfolge an und zeige sie dem Benutzer
		for (int i = 0; i < sortedArray.length; i++) {
			sortedArray[i].getSearchImage().setRank(i);
		}
		ui.repaint();
	}

	/**
	 * Berechne die Mean Average Precision und zeichne dessen Graphen. 
	 * 
	 * @param category
	 */
	public void triggerTests(String category) {
		
		// wurde bereits ein Sortieralgorithmus ausgewählt
		if(featureFactory == null) {
			System.out.println("No sorting algorithm selected");
			return;
		}
		
		// evaluiere (durch MAP Wert) den Sortieralgorithmus
		Pic[] allImages = imageManager.getImages();
		CBIREvaluation eval = new CBIREvaluation(featureFactory, allImages);
		
		// welche Teste sollen durchgeführt werden
		Pic[] queryImages = (category.equals("All")) ? allImages : imageManager.getImageInCategory(category);
		long milliSec = System.currentTimeMillis();
		float map = eval.test(queryImages, true, category);
		System.out.printf(Locale.ENGLISH, "MAP: %2.4f took %6dms for feature factory %s and Category %s\n", map, (System.currentTimeMillis() - milliSec), featureFactory.getName(), category);
	}
	
	/**
	 * die Namen aller Feature Factories die im Menu auswählbar sein sollen
	 * 
	 * @return
	 */
	public String[] getFeatureFactoryNames() {
		return new String[] { "ColorMean", "ColorMeanSaturation", "ColorMeanThumbnail", "ColorHistogram", "ColorSignature", "YCbCrHistogram", "ColorHistogramYCgCo" };
	}
	
	/**
	 * Wähle eine andere Feature Factory aus und berechne alle Feature Vektoren mit dieser neu.
	 * 
	 * @param name
	 */
	public void changeFeatureFactory(String name) {
		settings.removeChangeListeners();
		
		// welche Feature Factory wurde ausgewählt
		if(name.equalsIgnoreCase("ColorMean")){
			featureFactory = new ColorMean(settings);
			}
		else if(name.equalsIgnoreCase("ColorMeanSaturation")) {
			featureFactory = new ColorMeanSaturation(settings);
			
			// wenn sich die Saturation ändert berechne alle Feature Vektoren neu 
			settings.addChangeListener(Settings.SettingOption.SATURATION, (SettingChangeEvent event) -> {
				calculateFeatureVectors(featureFactory, imageManager.getImages());
			});
		} 
		else if(name.equalsIgnoreCase("ColorMeanThumbnail")) {
			featureFactory = new ColorMeanThumbnail(settings);
			
			settings.addChangeListener(Settings.SettingOption.RESOLUTION, (SettingChangeEvent event) -> {
				calculateFeatureVectors(featureFactory, imageManager.getImages());
			});
			
			settings.addChangeListener(Settings.SettingOption.METRIC, (SettingChangeEvent event) -> {
				calculateFeatureVectors(featureFactory, imageManager.getImages());
			});
			
			// wenn sich die Saturation ändert berechne alle Feature Vektoren neu 
			settings.addChangeListener(Settings.SettingOption.SATURATION, (SettingChangeEvent event) -> {
				calculateFeatureVectors(featureFactory, imageManager.getImages());
			});
		}
		else if(name.equalsIgnoreCase("ColorHistogram")) {
			featureFactory = new ColorHistogram(settings);

			settings.addChangeListener(Settings.SettingOption.RESOLUTION, (SettingChangeEvent event) -> {
				calculateFeatureVectors(featureFactory, imageManager.getImages());
			});

			settings.addChangeListener(Settings.SettingOption.METRIC, (SettingChangeEvent event) -> {
				calculateFeatureVectors(featureFactory, imageManager.getImages());
			});
		}
		else if(name.equalsIgnoreCase("ColorSignature")) {
			featureFactory = new ColorSignature(settings);

			settings.addChangeListener(Settings.SettingOption.RESOLUTION, (SettingChangeEvent event) -> {
				calculateFeatureVectors(featureFactory, imageManager.getImages());
			});

			settings.addChangeListener(Settings.SettingOption.METRIC, (SettingChangeEvent event) -> {
				calculateFeatureVectors(featureFactory, imageManager.getImages());
			});
		}
		else if(name.equalsIgnoreCase("YCbCrHistogram")) {
			featureFactory = new ColorHistogramYCbCr(settings);

			settings.addChangeListener(Settings.SettingOption.RESOLUTION, (SettingChangeEvent event) -> {
				calculateFeatureVectors(featureFactory, imageManager.getImages());
			});

			settings.addChangeListener(Settings.SettingOption.METRIC, (SettingChangeEvent event) -> {
				calculateFeatureVectors(featureFactory, imageManager.getImages());
			});
		}
		else if(name.equalsIgnoreCase("ColorHistogramYCgCo")) {
			featureFactory = new ColorHistogramYCgCo(settings);

			settings.addChangeListener(Settings.SettingOption.RESOLUTION, (SettingChangeEvent event) -> {
				calculateFeatureVectors(featureFactory, imageManager.getImages());
			});

			settings.addChangeListener(Settings.SettingOption.METRIC, (SettingChangeEvent event) -> {
				calculateFeatureVectors(featureFactory, imageManager.getImages());
			});
		}

		// erzeuge die Feature Vektoren
		if(featureFactory != null)
			calculateFeatureVectors(featureFactory, imageManager.getImages());
	}
	
	protected void calculateFeatureVectors(FeatureFactory featureFactory, Pic[] images) {
		for (Pic image : images) {
			image.setFeatureVector(featureFactory.getFeatureVector(image));
			image.setFeatureImage(featureFactory.getFeatureImage(image));			
		}
		ui.repaint();
	}	
}
