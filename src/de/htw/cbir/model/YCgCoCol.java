package de.htw.cbir.model;

import java.awt.*;

/**
 * Created by lili on 29.07.17.
 */
public class YCgCoCol {
  private final double lum, co, cg;

  public YCgCoCol(double lum, double cg, double co){
    this.lum = lum;
    this.cg = cg;
    this.co = co;
  }

  public YCgCoCol(int rgb){
    int r = (rgb >> 16) & 255;
    int g = (rgb >> 8) & 255;
    int b = (rgb) & 255;

    double rPercent = r/255;
    double gPercent = g/255;
    double bPercent = b/255;

    this.lum = rPercent * 0.25 + gPercent * 0.5 + bPercent * 0.25;
    this.cg = rPercent * (-0.25) + gPercent * 0.5 + bPercent * (-0.25);
    this.co = rPercent * 0.5 + bPercent * (-0.5);
  }

  public double getLum() {
    return lum;
  }

  public double getCg() {
    return cg;
  }

  public double getCo() {
    return co;
  }

  public int getRGB(){
    int r = (int)(lum - cg + co);
    int g = (int)(lum + cg);
    int b = (int)(lum - cg - co);

    return (new Color(r, g, b)).getRGB();
  }
}
