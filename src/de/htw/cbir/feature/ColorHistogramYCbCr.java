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
    return null;
  }

  @Override
  public float[] getFeatureVector(Pic image) {
    BufferedImage bi = image.getDisplayImage();
    int res = settings.getResolution();
    int bins = res; //TODO get numbers of bins from additional UI Slider

    //TODO transform image from RGB to YCbCr

    return new float[0];
  }

  @Override
  public float getDistance(float[] fv1, float[] fv2) {
    return 0;
  }

  @Override
  public String getName() {
    return null;
  }

  private BufferedImage getYCbCrImg(BufferedImage bufferedImage){
    //int[] rgbArr = bufferedImage.getRGB(
    //    0,
    //    0,
    //    bufferedImage.getWidth(),
    //    bufferedImage.getHeight(),
    //    null,
    //    0, bufferedImage.getWidth()
    //);

    int width = bufferedImage.getWidth();
    int height = bufferedImage.getHeight();

    for(int x = 0; x < width; x++){
      for(int y = 0; y < height; y++){
        int rgb = bufferedImage.getRGB(x,y);





      }
    }


    return null;
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
