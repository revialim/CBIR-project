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

		//val1 = S1, val2 = S2

		//todo replace val1 and val2 with corresponding signatures for val1 and val2
		float dirDist1 = directedHD(val1, val2);
		float dirDist2 = directedHD(val2, val1);

		//s_i – mean feature vector of i-th cluster
		//w_i – number of features of i-th cluster
		//sum_i – covariance matrix of i-th cluster
		//i = 1, ..., N

		if(dirDist1 > dirDist2){
			return dirDist1;
		} else {
			return dirDist2;
		}
	}

	/**
	 * Directed Hausdorff Distance
	 */
	//todo
	private static float directedHD (float[] val1, float[] val2){
		float dhd = 0;
		int i_max = val1.length;
		int w_i = 1;
		int w_j = 1;
		int mimimumW = Math.min(w_i, w_j);
		float[] covarArr = new float[i_max];

		// ...is the distance between two color features
		float sum = 0;

		for(int i = 0; i < i_max; i++){

			float min = euklideanDist(val1[i], val2[0])/mimimumW;
			for(int j = 1; j < i_max; j++){
				float tmpMin = euklideanDist(val1[i], val2[j])/mimimumW;
				min = (min < tmpMin)? min : tmpMin;
				covarArr[j] = tmpMin;
			}
			sum = sum + min;

		}

		return dhd;
	}

	//Covariance matrix
	//todo find out what covariance matrix is...
	private static float[] covarianceMatrix(float[] val){
		float[] covarianceMatrix = new float[val.length];
		for(int i = 0; i < val.length; i++){
			//todo cal values of covariance matrix
		}
		return covarianceMatrix;
	}

	/**
	 * euklidean distance as defined in paper (PMHD)
	 */
	private static float euklideanDist(float colFeat1, float colFeat2){
		float dist = 0;
		int[] rgb1 = new int[3];
		rgb1[0] = (((int)colFeat1) >> 16) & 255;
		rgb1[1] = (((int)colFeat1) >> 8) & 255;
		rgb1[2] = ((int)colFeat1) & 255;
		int[] rgb2 = new int[3];
		rgb2[0] = (((int)colFeat2) >> 16) & 255;
		rgb2[1] = (((int)colFeat2) >> 8) & 255;
		rgb2[2] = ((int)colFeat2) & 255;

		for(int k = 1; k <= 3; k++){
			dist = dist + (rgb1[k]-rgb1[k])*(rgb2[k]-rgb2[k]);
		}

		return (float) Math.sqrt(dist);
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
