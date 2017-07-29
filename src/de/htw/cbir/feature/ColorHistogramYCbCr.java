package de.htw.cbir.feature;

import de.htw.cbir.model.Pic;
import de.htw.cbir.model.Settings;
import de.htw.cbir.model.YCbCrCol;

import java.awt.*;
import java.awt.image.BufferedImage;


public class ColorHistogramYCbCr extends FeatureFactory{
  public ColorHistogramYCbCr(Settings settings) {
    super(settings);
  }

  @Override
  public BufferedImage getFeatureImage(Pic image) {
    float[] featureVector = image.getFeatureVector();
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
    int bins = settings.getResolution(); //TODO get numbers of bins from additional UI Slider

    float[] featureVector = new float[bins*bins*bins];//lum, cb, cr

    int width = bi.getWidth();
    int height = bi.getHeight();

    for(int x = 0; x < width; x++){
      for(int y = 0; y < height; y++){
        YCbCrCol ycbcrCol = new YCbCrCol(bi.getRGB(x, y));
        int binsPos = getBinsPosition(ycbcrCol, bins);
        //System.out.println("binsPos: "+binsPos);
        featureVector[binsPos]++;
      }
    }

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
    return "YCbCrHistogram";
  }

  private static int getBinsPosition(YCbCrCol col, int bins){
    double binStep = 1.0/bins;

    int lumIndex = (int) ( col.getLum()      /binStep);
    int crIndex =  (int) ((col.getCb() + 0.5)/binStep);
    int cbIndex =  (int) ((col.getCr() + 0.5)/binStep);

    lumIndex = (lumIndex == bins) ? lumIndex-1 : lumIndex;
    crIndex = (crIndex == bins) ? crIndex-1 : crIndex;
    cbIndex = (cbIndex == bins) ? cbIndex-1 : cbIndex;

    return lumIndex + crIndex*bins + cbIndex*bins*bins;
  }

  private static float getMaxValue(float[] arr){
    float max = 0;
    for (float anArr : arr) {
      if (anArr > max) {
        max = anArr;
      }
    }
    return max;
  }

}
