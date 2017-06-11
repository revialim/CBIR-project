package de.htw.cbir.feature;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import de.htw.cbir.model.Pic;
import de.htw.cbir.model.Settings;

public class ColorMeanSaturation extends FeatureFactory
{

	public ColorMeanSaturation(Settings settings) {
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
		int l = (int) featureVector[0]; //lum = (r+b+g)/3
		int a = (int) featureVector[1]; // sat * (lum -b)
		int b = (int) featureVector[2]; // sat * (lum -r)

		//System.out.print("l: "+l+" a: "+a+" b: "+b);
		//System.out.println(" sat: "+settings.getSaturation());
		int red, blue, green;
		blue = (l - a);
		red = (l - b);
		
		blue = (blue < 0) ? 0 : blue;
		blue = (blue > 255) ? 255 : blue;
		
		red = (red < 0) ? 0 : red;
		red = (red > 255) ? 255 : red;
		
		green = (3*l - red - blue);
		
		green = (green < 0) ? 0: green;
		green = (green > 255) ? 255 : green;
		
		System.out.println("red: "+red+" blue: "+blue+" green: "+green);


		pixels[0] = (0xFF << 24) | (red << 16) | (green << 8) | blue;
		//pixels[0] = (0xFF << 24) | (l << 16) | (a << 8) | b;

		BufferedImage bThumb = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		bThumb.setRGB(0, 0, w, h, pixels, 0, w);

		//int rgb = bThumb.getRGB(0, 0);
		//int r2 = (rgb >> 16) & 0xff;
		//int g2 = (rgb >>  8) & 0xff;
		//int b2 = (rgb) & 0xff;

		big.drawImage(bThumb, 0, 0, w, h, null);
		big.dispose();
		return bi;
	}

	@Override
	public float[] getFeatureVector(Pic image)  
	{
		BufferedImage bi = image.getDisplayImage();

		int width  = bi.getWidth();
		int height = bi.getHeight();

		int [] rgbValues = new int[width * height];

		bi.getRGB(0, 0, width, height, rgbValues, 0, width);


		// loop over the block
		int r = 0; int g = 0; int b = 0; int sum = 0;

		for(int y=0; y < height; y++) {
			for (int x=0 ; x<width ; x++) {
				int pos = y*width + x;
				r +=  (rgbValues[pos] >> 16) & 255;
				g +=  (rgbValues[pos] >>  8) & 255;
				b +=  (rgbValues[pos]      ) & 255;
				sum++;
			}	
		}

		r /= sum;
		g /= sum;
		b /= sum;	

		//System.out.println("fv red: "+ r +" fv green: "+g +" fv blue: "+b);

		float[] featureVector = new float[3];
		float lum = (r+g+b)/3; 
		featureVector[0] = lum; 
		featureVector[1] = settings.getSaturation()*(lum-b);
		featureVector[2] = settings.getSaturation()*(lum-r);

		return featureVector;
	}

	@Override
	public float getDistance(float[] fv1, float[] fv2) {
		return getL1Distance(fv1, fv2);
	}

	@Override
	public String getName() {
		return "ColorMeanSaturation (Saturation: " + settings.getSaturation() + ")";
	}
}
