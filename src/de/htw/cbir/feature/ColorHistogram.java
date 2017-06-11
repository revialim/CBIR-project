package de.htw.cbir.feature;

import java.awt.Color;
import java.awt.image.BufferedImage;

import de.htw.cbir.model.Pic;
import de.htw.cbir.model.Settings;

public class ColorHistogram extends FeatureFactory {

	public ColorHistogram(Settings settings) {
		super(settings);	
	}

	@Override
	public BufferedImage getFeatureImage(Pic image) {
		float[] featureVector = image.getFeatureVector();
		int width = featureVector.length;
		int height = (int) getMaxValue(featureVector);
		
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		//Color histCol = new Color(1,1,1);
		
		for(int i = 0; i < width; i++){
			for(int j = 0; j< (int) featureVector[i]; j++){
				bi.setRGB(i, j, Color.WHITE.getRGB());
			}
			for(int j = (int) featureVector[i]; j < height; j++){
				bi.setRGB(i, j, Color.BLACK.getRGB());
			}
		}
		
		return bi;
	}

	@Override
	public float[] getFeatureVector(Pic image) {
		BufferedImage bi = image.getDisplayImage();
		int bins = 4; //TODO settings.numberOfColorBins;
		
		float[] featureVector = new float[bins*bins*bins];// r, g, b
	
		int width = bi.getWidth();
		int height = bi.getHeight();
		
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				int col = bi.getRGB(x, y);
				int red = (col >> 16) & 255;
				int green = (col >> 8) & 255;
				int blue = col & 255;
				
				int binsPos = getBinsPosition(red, green, blue, bins);
				featureVector[binsPos]++;
			}
		}
		
		System.out.println("featureVector at i=0: "+featureVector[0]);
		return featureVector;
	}

	@Override
	public float getDistance(float[] fv1, float[] fv2) {
		if(settings.getMetric() == 1){
			return getL1Distance(fv1, fv2);
		} else { //metric == 2
			return getL2Distance(fv1, fv2);
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	// ===== helper functions === 
	
	static int getBinsPosition(int red, int green, int blue, int bins){
		int binStep = (int) 256/bins;
		
		int rIndex = red/binStep;//implicit rounding to floor
		int gIndex = green/binStep;
		int bIndex = blue/binStep;
		
		int index = bIndex + gIndex*bins + rIndex*bins*bins;
		
		return index;
	}
	
	static float getMaxValue(float[] arr){
		float max = 0;
		for(int i = 0; i<arr.length; i++){
			if(arr[i] > max){
				max = arr[i];
			}
		}
		return max;
	}
}
