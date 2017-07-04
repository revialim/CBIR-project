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
	 * Earth Movers Distance
	 */
	public static float getEarthMoversDistance(float[] val1, float[] val2) {
		float dist = 0;
		//assumption: val1.length same as val2.length
		float tmpDist = val1[0] - val2[0];

		for(int i = 1; i < val2.length; i++){
			//iterate over val1
			tmpDist = val1[i] + tmpDist - val2[i];
			dist = dist + Math.abs(tmpDist);
		}

		return dist;
	}

	/**
	 * Classical Hausdorff Distance
	 */
	public static float getHausdorffDistance(float[] val1, float[] val2){
		float sup1 = getSupOfInf(val1, val2);
		float sup2 = getSupOfInf(val2, val1);

		if(sup1 > sup2){
			return sup1;
		} else {
			return sup2;
		}
	}

	// Get supremum of all infima...
	private static float getSupOfInf(float[] firstVal, float[] secondVal){
		int featLength = firstVal.length;
		float sup = 0;

		//infimum for each temporary distance val1_i
		for(float a : firstVal){
			for(int i = 0; i < featLength; i++){
				float tmpInf = Math.abs(a - secondVal[i]);
				if(tmpInf > sup){
					sup = tmpInf;
				}
			}
		}

		return sup;
	}

	/**
	 * Perceptually Modified Hausdorff Distance
	 */
	public static float getPMHausdorffDistance(float[] val1, float[] val2){

		//todo replace val1 and val2 with corresponding signatures for val1 and val2
		float sup1 = getSupOfInf(val1, val2);
		float sup2 = getSupOfInf(val2, val1);

		if(sup1 > sup2){
			return sup1;
		} else {
			return sup2;
		}
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
