package de.htw.cbir.feature;

import de.htw.cbir.model.Pic;
import de.htw.cbir.model.Settings;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Collections;

/**
 * Created by lili on 24.07.17.
 */
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
    int res = settings.getResolution();
    int bins = res; //TODO get numbers of bins from additional UI Slider

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

  static int getBinsPosition(YCbCrCol col, int bins){
    double binStep = 1.0/bins;

    int lumIndex = (int) ( col.lum      /binStep);
    int crIndex =  (int) ((col.cr + 0.5)/binStep);
    int cbIndex =  (int) ((col.cb + 0.5)/binStep);

    lumIndex = (lumIndex == bins) ? lumIndex-1 : lumIndex;
    crIndex = (crIndex == bins) ? crIndex-1 : crIndex;
    cbIndex = (cbIndex == bins) ? cbIndex-1 : cbIndex;

    int index = lumIndex + crIndex*bins + cbIndex*bins*bins;

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

  private class YCbCrCol {

    final static double kr = 0.299;//todo find out how to calculate/define theses values
    final static double kg = 0.587;
    final static double kb = 0.114;

    double lum, cb, cr;
    YCbCrCol(double lum, double cb, double cr){
      this.lum = lum;
      this.cb = cb;
      this.cr = cr;
    }

    YCbCrCol(int rgb){
      int red = (rgb >> 16) & 255;
      int green = (rgb >> 8) & 255;
      int blue = (rgb ) & 255;

      double rPercent = red / 255;
      double gPercent = green / 255;
      double bPercent = blue /255;


      this.lum = kr * rPercent + kg * gPercent + kb * bPercent;
      this.cb = 0.5 * ((bPercent - lum)/(1 - kb));
      this.cr = 0.5 * ((rPercent - lum)/(1 - kr));
    }

    private int getRGB(){
      double rPercent = cr * 2 * (1 - kr) + lum;
      double bPercent = cb * 2 * (1 - kb) + lum;

      double gPercent = (lum - kr * rPercent - kb * bPercent)/kg;

      Color c = new Color((int) rPercent* 255, (int) gPercent*255, (int) bPercent*255);
      return c.getRGB();
    }
  }
}
