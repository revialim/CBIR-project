package de.htw.cbir.model;

import java.awt.*;

/**
 * Created by lili on 29.07.17.
 */
public class YCbCrCol {

  final static double kr = 0.299;//todo find out how to calculate/define theses values
  final static double kg = 0.587;
  final static double kb = 0.114;

  final double lum, cb, cr;

  YCbCrCol(double lum, double cb, double cr) {
    this.lum = lum;
    this.cb = cb;
    this.cr = cr;
  }

  public YCbCrCol(int rgb) {
    int red = (rgb >> 16) & 255;
    int green = (rgb >> 8) & 255;
    int blue = (rgb) & 255;

    double rPercent = red / 255;
    double gPercent = green / 255;
    double bPercent = blue / 255;


    this.lum = kr * rPercent + kg * gPercent + kb * bPercent;
    this.cb = 0.5 * ((bPercent - lum) / (1 - kb));
    this.cr = 0.5 * ((rPercent - lum) / (1 - kr));
  }

  public double getLum() {
    return lum;
  }

  public double getCb() {
    return cb;
  }

  public double getCr() {
    return cr;
  }

  private int getRGB() {
    double rPercent = cr * 2 * (1 - kr) + lum;
    double bPercent = cb * 2 * (1 - kb) + lum;

    double gPercent = (lum - kr * rPercent - kb * bPercent) / kg;

    Color c = new Color((int) rPercent * 255, (int) gPercent * 255, (int) bPercent * 255);
    return c.getRGB();
  }
}
