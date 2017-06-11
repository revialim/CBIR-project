package de.htw.cbir.feature;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.htw.cbir.model.Pic;
import de.htw.cbir.model.Settings;

public class ColorMeanThumbnail extends FeatureFactory {

	public ColorMeanThumbnail(Settings settings) {
		super(settings);
	}

	@Override
	public BufferedImage getFeatureImage(Pic image) {

		int res = settings.getResolution();
		int resQuad = res*res;// for example 4, then turn it to 4*4(16)
		
		int w = res;
		int h = res;

		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D big = bi.createGraphics();

		int[] pixels = new int[h * w];//e.g. 16

		float[] featureVector = image.getFeatureVector();
		
		for(int i = 0; i < resQuad; i++){
			int lum = (int) featureVector[i*3];
			int tmp1 = (int) featureVector[i*3+1];
			int tmp2 = (int) featureVector[i*3+2];

			int red, blue, green;
			blue = (lum - tmp1);
			red = (lum - tmp2);
			
			blue = (blue < 0) ? 0 : blue;
			blue = (blue > 255) ? 255 : blue;
			
			red = (red < 0) ? 0 : red;
			red = (red > 255) ? 255 : red;
			
			green = (3*lum - red - blue);
			
			green = (green < 0) ? 0: green;
			green = (green > 255) ? 255 : green;
			
			pixels[i] = (0xFF << 24) | (red << 16) | (green << 8) | blue;
		}

		BufferedImage bThumb = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		bThumb.setRGB(0, 0, w, h, pixels, 0, w);

		big.drawImage(bThumb, 0, 0, w, h, null);
		big.dispose();
		return bi;
	}

	@Override
	public float[] getFeatureVector(Pic image) {
		BufferedImage bi = image.getDisplayImage();

		int width = bi.getWidth();
		int height = bi.getHeight();
		
		int res = settings.getResolution();
		int resQuad = res*res;// for example 4, then turn it to 4*4(16)
		
		float[] featureVector = new float[3*resQuad];
		
		BufferedImage[] subImages = new BufferedImage[resQuad];
		
		for(int i = 0; i< subImages.length; i++){
			subImages[i] = bi.getSubimage((width/res)*(i%res), (height/res)*(i/res), width/res, height/res);
			float[] tmpFeatureVector = getMeanColor(subImages[i]);
			
			featureVector[i*3] = tmpFeatureVector[0];
			featureVector[i*3+1] = tmpFeatureVector[1];
			featureVector[i*3+2] = tmpFeatureVector[2];
		}
		
		return featureVector;
	}

	// method from ColorMean
	private float[] getMeanColor(BufferedImage bi) {
		
		//BufferedImage bi = image.getDisplayImage();

		int width = bi.getWidth();
		int height = bi.getHeight();

		int[] rgbValues = new int[width * height];

		bi.getRGB(0, 0, width, height, rgbValues, 0, width);

		float[] featureVector = new float[3];

		// loop over the block
		int r = 0; int g = 0; int b = 0; int sum = 0;

		for (int y = 0; y < height; y++) 
		{
			for (int x = 0; x < width; x++) 
			{
				int pos = y * width + x;

				r += (rgbValues[pos] >> 16) & 255;
				g += (rgbValues[pos] >>  8)	& 255;
				b += (rgbValues[pos]	  ) & 255;

				sum++;
			}
		}
		// compute the mean color 
		r = r / sum;
		g = g / sum;
		b = b / sum;
		
		float lum = (r+g+b)/3; 
		featureVector[0] = lum; 
		featureVector[1] = settings.getSaturation()*(lum-b);
		featureVector[2] = settings.getSaturation()*(lum-r);
		
		return featureVector;
	}
	
	@Override
	public float getDistance(float[] fv1, float[] fv2) {
		/* TODO return different */
		if(settings.getMetric() == 1){
			return getL1Distance(fv1, fv2);
		} else { //metric == 2
			return getL2Distance(fv1, fv2);
		}
		
		
	}

	@Override
	public String getName() {
		return "ColorMeanThumbnail";
	}

}
