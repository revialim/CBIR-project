package de.htw.cbir.feature;

import de.htw.cbir.model.Pic;
import de.htw.cbir.model.Settings;
import de.htw.cbir.model.YCgCoCol;

import java.awt.image.BufferedImage;
import java.rmi.UnexpectedException;

public class ColorHistogramYCgCo extends FeatureFactory {
  public ColorHistogramYCgCo(Settings settings) {
    super(settings);
  }

  @Override
  public BufferedImage getFeatureImage(Pic image) {
    return null;
  }

  @Override
  public float[] getFeatureVector(Pic image) {
    return new float[0];
  }

  @Override
  public float getDistance(float[] fv1, float[] fv2) {
    return 0;
  }

  @Override
  public String getName() {
    return "ColorHistogramYCgCo";
  }


  private int getIndex(YCgCoCol ycgco, int bins){//TODO
    double binStepLum = 1.0 / bins;
    double binStepAngle = 360 / bins;

    int lumIndex = (int) ( ycgco.getLum()      /binStepLum);
    lumIndex = (lumIndex == bins) ? lumIndex-1 : lumIndex;

    double angle = getAngle(ycgco);
    int cgcoIndex = (int) (angle / binStepAngle);

    return cgcoIndex + bins * lumIndex;
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
}
