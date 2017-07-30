package de.htw.cbir.feature;

import de.htw.cbir.model.Pic;
import de.htw.cbir.model.Settings;

import java.awt.*;
import java.awt.image.BufferedImage;


public class FeatureCombination extends FeatureFactory {
  private ColorHistogramYCgCo histogramYCgCo;
  private ColorMeanThumbnail thumbnail;
  private ColorHistogram histogramRGB;

  public FeatureCombination(Settings settings) {
    super(settings);

    histogramYCgCo = new ColorHistogramYCgCo(settings);
    thumbnail = new ColorMeanThumbnail(settings);
    histogramRGB = new ColorHistogram(settings);
  }


  @Override
  public BufferedImage getFeatureImage(Pic image) {
    float[] featureVector = image.getFeatureVector();
    int size = featureVector.length;
    int maxVal = (int) getMaxValue(featureVector);

    BufferedImage bi = new BufferedImage(size , size, BufferedImage.TYPE_INT_ARGB);

    //if(featureVector.length > 1) {
    //  for (int i = 0; i < size; i++) {
    //    int scaledHeight = (int) (featureVector[i] / maxVal * (size - (size/10)));
//
    //    for (int j = 0; j < scaledHeight; j++) {
    //      bi.setRGB(i, size - j - 1, Color.WHITE.getRGB());
    //    }
    //    for (int j = scaledHeight; j < size; j++) {
    //      bi.setRGB(i, size - j - 1, Color.BLACK.getRGB());
    //    }
    //  }
    //} else {
    //  bi.setRGB(0,0,Color.WHITE.getRGB());
    //}

    for(int x = 0; x < size; x ++){
      for(int y = 0; y < size; y ++){
        bi.setRGB(x, y, Color.WHITE.getRGB());
      }
    }

    return bi;
  }

  @Override
  public float[] getFeatureVector(Pic image) {
    //TODO combine both feature vectors
    float[] f1 = normalize(histogramYCgCo.getFeatureVector(image));
    float[] f2 = normalize(thumbnail.getFeatureVector(image));
    float[] f3 = normalize(histogramRGB.getFeatureVector(image));

    //set fvLength to the longest out of the given three feature vectors
    int fvLength = (f1.length < f2.length)? f2.length : f1.length;
    fvLength = (fvLength < f3.length)? f3.length : fvLength;

    float[] combi = new float[fvLength];

    double alpha = settings.getAlpha();
    double beta_val = settings.getBeta();
    double beta = (1 - alpha)* beta_val;
    double gamma = 1 - beta - alpha;//remainder for gamma value

    for(int i = 0; i < combi.length; i++){
      if(f1.length > i && f2.length > i && f3.length > i){
        combi[i] = (float) (f1[i] * alpha + f2[i] * beta + f3[i] * gamma);
      }
      else if(f1.length <= i && f2.length > i && f3.length > i){
        combi[i] = (float) (f2[i] * alpha + f3[i] * (1 - alpha));
      }
      else if(f1.length > i && f2.length <= i && f3.length > i){
        combi[i] = (float) (f1[i] * alpha + f3[i] * (1 - alpha));
      }
      else if(f1.length > i && f2.length > i && f3.length <= i){
        combi[i] = (float) (f1[i] * alpha + f2[i] * (1 - alpha));
      }
      else if(f1.length > i && f2.length <= i && f3.length <= i){
        combi[i] = f1[i];
      }
      else if(f1.length <= i && f2.length > i && f3.length <= i){
        combi[i] = f2[i];
      }
      else if(f1.length <= i && f2.length <= i && f3.length > i){
        combi[i] = f3[i];
      }
      else {
        throw new IllegalStateException("one of the three feature vectors should be same length as combi.length");
      }
    }

    return combi;
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
    return "FeatureCombination";
  }


  private static float[] normalize(float[] vector){
    double length = vectorLength(vector);
    float[] norm = new float[vector.length];

    for(int i = 0; i < vector.length; i++){
      norm[i] = (float) (vector[i] / length);
    }

    return norm;
  }

  private static double vectorLength(float[] vector){
    float tmp = 0;
    for (float a : vector){
      tmp = tmp + (a * a);
    }
    return Math.sqrt(tmp);
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
