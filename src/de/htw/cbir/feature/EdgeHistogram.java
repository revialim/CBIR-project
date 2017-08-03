package de.htw.cbir.feature;

import de.htw.cbir.model.Pic;
import de.htw.cbir.model.Settings;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lili on 01.08.17.
 */
public class EdgeHistogram extends FeatureFactory {
  public EdgeHistogram(Settings settings) {
    super(settings);
  }

  @Override
  public BufferedImage getFeatureImage(Pic image) {
    return null;
  }

  @Override
  public float[] getFeatureVector(Pic image) {
    float[] featureVector = new float[5];
    BufferedImage bi = image.getDisplayImage();
    int res = settings.getResolution();
    List<BufferedImage> subImages = getSubImages(bi, res);
    List<float[]> localHistograms = new ArrayList<>();

    for(BufferedImage subImage : subImages){
      localHistograms.add(getLocalEdgeHist(subImage));
    }

    for (float[] hist: localHistograms){
      float[] tmpNorm = normalize(hist);
      for(int i = 0; i < featureVector.length; i++){
        featureVector[i] = featureVector[i] + tmpNorm[i];
      }
    }

    return featureVector;
  }

  // =============

  private float[] getLocalEdgeHist(BufferedImage img){
    float[] localHist = new float[5]; //0: horizontal, 1: vertical, 2: 45 degrees, 3: 135 degrees, 4: non-directional

    int width = img.getWidth();
    int height = img.getHeight();

    int grey1 = getGreyVal(img.getSubimage(0,0, width/2, height/2));
    int grey2 = getGreyVal(img.getSubimage(width/2, 0, width/2, height/2));
    int grey3 = getGreyVal(img.getSubimage(0, height/2, width/2, height/2));
    int grey4 = getGreyVal(img.getSubimage(width/2, height/2, width/2, height/2));

    int meanGrey = getMeanGrey(grey1, grey2, grey3, grey4);

    localHist[getEdgeIndex(meanGrey, grey1, grey2, grey3, grey4)]++;

    return localHist;
  }

  private static int getEdgeIndex(int meanGrey, int grey1, int grey2, int grey3, int grey4){
    if((meanGrey < grey1 && meanGrey < grey2 && meanGrey > grey3 && meanGrey > grey4)
        || (meanGrey > grey1 && meanGrey > grey2 && meanGrey < grey3 && meanGrey < grey4) ){
      //assume horizontal edge
      return 0;
    }
    else if((meanGrey < grey1 && meanGrey > grey2 && meanGrey < grey3 && meanGrey > grey4)
        || (meanGrey > grey1 && meanGrey < grey2 && meanGrey > grey3 && meanGrey < grey4) ){
      //assume vertical edge
      return 1;
    }
    else if(meanGrey > grey1 && meanGrey > grey4){
      //assume 45 degrees diagonal edge
      return 2;
    }
    else if(meanGrey > grey2 && meanGrey > grey3){
      //assume 135 degrees diagonal edge
      return 3;
    }
    else {
      //assume non-directional edge
      return 4;
    }
  }

  private List<BufferedImage> getSubImages(BufferedImage bi, int res) {
    int width = bi.getWidth();
    int height = bi.getHeight();
    int xSteps = width/res;
    int ySteps = height/res;

    List<BufferedImage> subImages = new ArrayList<>();

    for(int x = 0; x < res; x++){
      for(int y = 0; y < res; y++){
        subImages.add(bi.getSubimage(x * xSteps, y * ySteps, xSteps, ySteps));
      }
    }

    return subImages;
  }

  private static int getGreyVal(BufferedImage bi){
    int grey = 0;
    int sum = 0;

    for(int x = 0; x < bi.getWidth(); x++){
      for (int y = 0; y < bi.getHeight(); y++){
        int rgb = bi.getRGB(x, y);
        int red = (rgb >> 16) & 255;
        int green = (rgb >> 8) & 255;
        int blue = rgb & 255;
        grey = grey + (red+green+blue)/3;
        sum ++;
      }
    }

    return grey/sum; //mean grey
  }

  private static int getMeanGrey(int grey1, int grey2, int grey3, int grey4){
    return (grey1+grey2+grey3+grey4)/4;
  }

  // ========
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


  // =========
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
    return "EdgeHistogram";
  }
}
