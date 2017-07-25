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
		int res = settings.getResolution();

		float[] featureVector = image.getFeatureVector();
		int numPixel = image.getOrigHeight() * image.getOrigWidth();

		int width = featureVector.length;
		int height = width;
		int maxVal = (int) getMaxValue(featureVector);

		
		BufferedImage bi = new BufferedImage(width , height, BufferedImage.TYPE_INT_ARGB);

		if(featureVector.length > 1) {
      for (int i = 0; i < width; i++) {
        int scaledHeight = (int) (featureVector[i] / maxVal * (width - (width/10)));

        for (int j = 0; j < scaledHeight; j++) {
          bi.setRGB(i, height - j - 1, Color.WHITE.getRGB());
        }
        for (int j = scaledHeight; j < height; j++) {
          bi.setRGB(i, height - j - 1, Color.BLACK.getRGB());
        }
      }
    } else {
		  bi.setRGB(0,0,Color.WHITE.getRGB());
    }

		return bi;
	}

	@Override
	public float[] getFeatureVector(Pic image) {
		BufferedImage bi = image.getDisplayImage();
		int res = settings.getResolution();

		int bins = res; //TODO settings.numberOfColorBins;
		
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
				//System.out.println("binsPos for (red:"+red+", green:"+green+", blue"+blue+"): "+binsPos +"; featureVector at binsPos: "+featureVector[binsPos]);
				featureVector[binsPos]++;
			}
		}
		
		//System.out.println("featureVector at i=0: "+featureVector[0]);
		return featureVector;
	}

	@Override
	public float getDistance(float[] fv1, float[] fv2) {
		if(settings.getMetric() == 1){
			return getL1Distance(fv1, fv2);
		} else if(settings.getMetric() == 2){
			return getL2Distance(fv1, fv2);
		} else { //metric == 3
			return getEarthMoversDistance(fv1, fv2);
		}
	}

	@Override
	public String getName() {
		return "ColorHistogram";
	}
	
	
	// ===== helper functions === 
	
	static int getBinsPosition(int red, int green, int blue, int bins){
		int binStep = (int) Math.ceil(256.0/(float)bins);

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
