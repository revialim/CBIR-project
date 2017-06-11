package de.htw.cbir.feature;

import java.awt.image.BufferedImage;

import de.htw.cbir.model.Pic;
import de.htw.cbir.model.Settings;

public abstract class FeatureFactory {
	
	protected Settings settings;
	
	public FeatureFactory( Settings settings) {
		this.settings = settings;
	}
	
	/**
	 * Manhattan Distanz
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static float getL1Distance(float[] val1, float[] val2) {
		float dist = 0;
		for (int i = 0; i < val2.length; i++)
			dist += Math.abs(val1[i] - val2[i]);
		return dist;
	}	
	
	/**
	 * Euklidische Distanz
	 * 
	 * @param val1
	 * @param val2
	 * @return
	 */
	public static float getL2Distance(float[] val1, float[] val2) {
		float dist = 0;
		for (int i = 0; i < val2.length; i++) {
			float buff = val1[i] - val2[i];
			dist += buff * buff;
		}
		return dist;
	}	
	
	/**
	 * Visualisiere den Feature Vektor für ein Bild
	 * 
	 * @param image
	 * @return
	 */
	public abstract BufferedImage getFeatureImage(Pic image);
	
	/**
	 * Berechne den Feature Vektor für ein Bild
	 * 
	 * @param image
	 * @return
	 */
	public abstract float[] getFeatureVector(Pic image);

	/**
	 * Distanz zwischen zwei Feature Vektoren
	 * 
	 * @param fv1
	 * @param fv2
	 * @return
	 */
	public abstract float getDistance(float[] fv1, float[] fv2); 
	
	/**
	 * Name von der Feature Factory
	 * 
	 * @return
	 */
	public abstract String getName();
}
