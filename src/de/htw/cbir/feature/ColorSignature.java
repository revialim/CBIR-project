package de.htw.cbir.feature;

import de.htw.cbir.model.Pic;
import de.htw.cbir.model.Settings;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by lili on 12.06.17.
 */
public class ColorSignature extends FeatureFactory{

  public ColorSignature(Settings settings) {
    super(settings);
  }

  @Override
  public BufferedImage getFeatureImage(Pic image) {
    int res = settings.getResolution();

    float[] featureVector = image.getFeatureVector();
    int numPixel = image.getOrigHeight() * image.getOrigWidth();

    int width = 1;
    int height = width;

    BufferedImage bi = new BufferedImage(width , height, BufferedImage.TYPE_INT_ARGB);

    bi.setRGB(0, 0, Color.WHITE.getRGB());


    return bi;
  }

  @Override
  public float[] getFeatureVector(Pic image) {
    BufferedImage bi = image.getDisplayImage();
    int res = settings.getResolution();

    int bins = res;
    float[] featureVector = new float[bins*bins*bins];

    int width = bi.getWidth();
    int height = bi.getHeight();

    for(int x = 0; x < width; x++){
      for(int y = 0; y < height; y++){
        int col = bi.getRGB(x, y);
        int red = (col >> 16) & 255;
        int green = (col >> 8) & 255;
        int blue = col & 255;

      }
    }

    //System.out.println("featureVector at i=0: "+featureVector[0]);
    return featureVector;
  }

  @Override
  public float getDistance(float[] fv1, float[] fv2) {
    if(settings.getMetric() == 1){
      return getL1Distance(fv1, fv2);
    } else { //metric == 2
      return getL2Distance(fv1, fv2);
    }
  }

  @Override
  public String getName() {
    return "ColorSignature";
  }


  // ===== helper functions ===

  void callMedianCut(BufferedImage image){
    int numberOfColors = 8;
    List<ColorTriple> pixels = new ArrayList<>();

    // put all the pixels in a pixels bucket
    for(int x = 0; x < image.getWidth(); x++){
      for(int y = 0; y < image.getHeight(); y++){
        int col = image.getRGB(x,y);
        int red = (col >> 16) & 255;
        int green = (col >> 8) & 255;
        int blue = col & 255;
        pixels.add(new ColorTriple(red, green, blue));
      }
    }

    //TODO receive colors from medianCut...
    medianCut(numberOfColors, pixels);
  }

  List<ColorTriple> medianCut(int numColors, List<ColorTriple> pixels){
    List<ColorTriple> colors = new ArrayList<>();

    //recursion anchor
    if(numColors <= 1){
      //return meanColor of pixels
      colors.add(meanColorTriple(pixels));
      return colors;
    } else {
      //find out if red, green, or blue has the widest range
      int rangeRed = rangeOfChannel("r", pixels);
      int rangeGreen = rangeOfChannel("g", pixels);
      int rangeBlue = rangeOfChannel("b", pixels);

      if (rangeRed > rangeGreen && rangeRed > rangeBlue) {
        //sort (and cut) along red channel dimension
      }
      if (rangeGreen > rangeRed && rangeGreen > rangeBlue) {
        //sort (and cut) along green channel dimension
      }
      if (rangeBlue > rangeRed && rangeBlue > rangeGreen) {
        //sort (and cut) along blue channel dimension
      }

      //cutttt
      List<ColorTriple> pixelsFirstHalf = new ArrayList<>(pixels.subList(0, pixels.size() / 2 - 1));
      List<ColorTriple> pixelsSecondHalf = new ArrayList<>(pixels.subList(pixels.size() / 2, pixels.size() - 1));
      //recursive call
      colors.addAll(medianCut(numColors / 2, pixelsFirstHalf));
      colors.addAll(medianCut(numColors - numColors / 2, pixelsSecondHalf));

      return colors;
    }
  }


  List<ColorTriple> sortAccordingToChannel(String channel, List<ColorTriple> pixels){
    if(channel.equals("r")) {
      return pixels.stream().sorted((a, b) -> Integer.compare(a.red, b.red)).collect(Collectors.toList());
    }
    if(channel.equals("g")) {
      return pixels.stream().sorted((a, b) -> Integer.compare(a.green, b.green)).collect(Collectors.toList());
    }
    if(channel.equals("b")) {
      return pixels.stream().sorted((a, b) -> Integer.compare(a.blue, b.blue)).collect(Collectors.toList());
    }
    throw new IllegalArgumentException("channel needs to be 'r', 'g' or 'b'. ");
  }
  /**
   * returns the range of the pixels according to
   * a given channel.
   *
   * @param channel use r for red, g for green, b for blue as a valid channel
   * @param pixels List of ColorTriple containing the pixels
   * @return int range
   */
  int rangeOfChannel(String channel, List<ColorTriple> pixels){

    if(channel == "r"){
      int max = pixels.get(0).red;
      int min = pixels.get(0).red;

      for(int i = 1; i < pixels.size(); i++){
        if(pixels.get(i).red > max){
          max = pixels.get(i).red;
        }
        if(pixels.get(i).red < min){
          min = pixels.get(i).red;
        }
      }

      return max - min;
    }

    if(channel == "g"){
      int max = pixels.get(0).green;
      int min = pixels.get(0).green;

      for(int i = 1; i < pixels.size(); i++){
        if(pixels.get(i).green > max){
          max = pixels.get(i).green;
        }
        if(pixels.get(i).green < min){
          min = pixels.get(i).green;
        }
      }

      return max - min;
    }

    if(channel == "b"){
      int max = pixels.get(0).blue;
      int min = pixels.get(0).blue;

      for(int i = 1; i < pixels.size(); i++){
        if(pixels.get(i).blue > max){
          max = pixels.get(i).blue;
        }
        if(pixels.get(i).blue < min){
          min = pixels.get(i).blue;
        }
      }

      return max - min;
    }

    else {
      System.out.println("wrong channel chosen in rangeOfChannel");
      return -1;
    }
  }


  ColorTriple meanColorTriple(List<ColorTriple> pixels){
    float red = 0;
    float green = 0;
    float blue = 0;

    for(int i = 0; i < pixels.size(); i++){
      red = red + pixels.get(i).red / pixels.size();
      green = green + pixels.get(i).green / pixels.size();
      blue = blue + pixels.get(i).blue / pixels.size();
    }

    return new ColorTriple((int)red, (int)green, (int)blue);
  }

  class ColorTriple{
    int red;
    int green;
    int blue;

    ColorTriple(int r, int g, int b){
      red = r;
      green = g;
      blue = b;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ColorTriple that = (ColorTriple) o;

      if (red != that.red) return false;
      if (green != that.green) return false;
      return blue == that.blue;
    }

    @Override
    public int hashCode() {
      int result = red;
      result = 31 * result + green;
      result = 31 * result + blue;
      return result;
    }

  }

  enum ColChannel { R, G, B }
}

