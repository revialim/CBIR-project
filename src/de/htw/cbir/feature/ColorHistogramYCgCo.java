package de.htw.cbir.feature;

import de.htw.cbir.model.Pic;
import de.htw.cbir.model.Settings;
import de.htw.cbir.model.YCgCoCol;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ColorHistogramYCgCo extends FeatureFactory {
  public ColorHistogramYCgCo(Settings settings) {
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
    int bins = settings.getResolution(); //TODO
    float[] featureVector = new float[bins * bins * 2];

    int width = bi.getWidth();
    int height = bi.getHeight();

    for(int x = 0; x < width; x++){
      for(int y = 0; y < height; y++){
        YCgCoCol ycgco = new YCgCoCol(bi.getRGB(x,y));
        featureVector[getIndex(ycgco,bins)]++;
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
    return "ColorHistogramYCgCo";
  }


  private static int getIndex(YCgCoCol ycgco, int bins){
    double binStepLum = 1.0 / bins;
    double binStepAngle = 360 / (bins*2);

    int lumIndex = (int) (ycgco.getLum() / binStepLum);
    lumIndex = (lumIndex == bins) ? lumIndex-1 : lumIndex;

    double angle = getAngle(ycgco);
    int cgcoIndex = (int) (angle / binStepAngle);

    return lumIndex * (bins*2) + cgcoIndex;
  }

  private static double getAngle(YCgCoCol ycgco){
    double radius = Math.sqrt(ycgco.getCg()*ycgco.getCg() + ycgco.getCo()*ycgco.getCo());

    if(ycgco.getCo() >= 0){
      return Math.acos(ycgco.getCg()/radius);
    } else if(ycgco.getCo() < 0){
      return  -Math.acos(ycgco.getCg()/radius);
    } else {
      throw new IllegalStateException("ycgco.getCo() was neither bigger, equal nor same to zero: "+ycgco.getCo());
    }
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
