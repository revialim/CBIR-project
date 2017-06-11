package de.htw.cbir.feature;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.htw.cbir.model.Pic;
import de.htw.cbir.model.Settings;


public class ColorMean extends FeatureFactory {

	public ColorMean(Settings settings) {
		super(settings);
	}

	///////////////////////////////////////////
	// visualize the feature data as image
	//
	@Override
	public BufferedImage getFeatureImage(Pic image) {

		int w = 1;
		int h = 1;

		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D big = bi.createGraphics();

		int[] pixels = new int[h * w];

		float[] featureVector = image.getFeatureVector();
		int r = (int) featureVector[0];
		int g = (int) featureVector[1];
		int b = (int) featureVector[2];

		pixels[0] = (0xFF << 24) | (r << 16) | (g << 8) | b;

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
		featureVector[0] = r / sum;
		featureVector[1] = g / sum;
		featureVector[2] = b / sum;
		
		return featureVector;
	}

	@Override
	public float getDistance(float[] fv1, float[] fv2) {
		return getL2Distance(fv1, fv2);
	}

	@Override
	public String getName() {
		return "ColorMean";
	}
}
