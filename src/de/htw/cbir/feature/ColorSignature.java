package de.htw.cbir.feature;

import de.htw.cbir.model.Pic;
import de.htw.cbir.model.Settings;

import java.awt.*;
import java.awt.image.BufferedImage;
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
    //use resolution as number of colors for signature

    float[] featureVector = image.getFeatureVector();
    int numPixel = image.getOrigHeight() * image.getOrigWidth();


    int width = (int) Math.sqrt(featureVector.length) +1; //enough space to draw signature colors
    int height = width;
    System.out.println("width: "+ width +", featureVector.length: "+ featureVector.length);

    BufferedImage bi = new BufferedImage(width , height, BufferedImage.TYPE_INT_ARGB);

    for(int x = 0; x < width; x++){
      for(int y = 0; y < height; y++){
        if((x + y * width) < featureVector.length){
          bi.setRGB(x, y, (int) featureVector[x + y * width]);
        }
        //todo get correct position in featureVector for setting bi color
      }
    }

    //bi.setRGB(0, 0, Color.WHITE.getRGB());


    return bi;
  }

  @Override
  public float[] getFeatureVector(Pic image) {
    BufferedImage bi = image.getDisplayImage();
    int res = settings.getResolution();
    //use resolution as number of colors for signature

    //int bins = res;
    float[] featureVector = callMedianCut(bi, res);

    return featureVector;
  }

  @Override
  public float getDistance(float[] fv1, float[] fv2) {
    if(settings.getMetric() == 1){
      return getL1Distance(fv1, fv2);
    } else if(settings.getMetric() == 2) { //metric == 2
      return getL2Distance(fv1, fv2);
    } else {  //metric == 3
      return getEarthMoversDistance(fv1, fv2);
    }
  }

  @Override
  public String getName() {
    return "ColorSignature";
  }


  // ===== helper functions ===

  float[] convertColorTriples(List<ColorTriple> featureColors){
    float[] featureVector = new float[featureColors.size()];

    for(int i = 0; i < featureColors.size(); i++){
      //System.out.println("featureColors: "+featureColors.get(i).red +", "+ featureColors.get(i).green+", "+ featureColors.get(i).blue);
      Color col = new Color(featureColors.get(i).red, featureColors.get(i).green, featureColors.get(i).blue);
      featureVector[i] = col.getRGB();
    }
    return featureVector;
  }

  float[] callMedianCut(BufferedImage image, int numberOfColors){
    List<ColorTriple> pixels = new ArrayList<>();
    List<ColorTriple> signatureColors = new ArrayList<>();

    // put all the pixels in a pixels bucket
    for(int x = 0; x < image.getWidth(); x++){
      for(int y = 0; y < image.getHeight(); y++){
        int col = image.getRGB(x,y);
        int red = (col >> 16) & 255;
        int green = (col >> 8) & 255;
        int blue = col & 255;
        pixels.add(new ColorTriple(red, green, blue));
        //works til here...
        //System.out.println("pixels at ("+x +", "+ y +"): red: "+red +", green: "+ green +", blue: "+blue);
      }
    }
    signatureColors = medianCut(numberOfColors, pixels);
    System.out.println("numberOfColors: "+numberOfColors);
    System.out.println("pixels at 0: "+ pixels.get(0).red +", "+ pixels.get(0).green +", "+ pixels.get(0).blue);
    System.out.println("signatureColors at 0: "+ signatureColors.get(0).red +", "+ signatureColors.get(0).green +", "+ signatureColors.get(0).blue);

    //turn list<colortriple> into float[] which will be the feature vector
    //and return the float array
    return convertColorTriples(signatureColors);
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
      int rangeRed = rangeOfChannel(ColChannel.R, pixels);
      int rangeGreen = rangeOfChannel(ColChannel.G, pixels);
      int rangeBlue = rangeOfChannel(ColChannel.B, pixels);

      if (rangeRed > rangeGreen && rangeRed > rangeBlue) {
        //sort (and cut) along red channel dimension
        pixels = sortAccordingToChannel(ColChannel.R, pixels);
      }
      if (rangeGreen > rangeRed && rangeGreen > rangeBlue) {
        //sort (and cut) along green channel dimension
        pixels = sortAccordingToChannel(ColChannel.G, pixels);
      }
      if (rangeBlue > rangeRed && rangeBlue > rangeGreen) {
        //sort (and cut) along blue channel dimension
        pixels = sortAccordingToChannel(ColChannel.B, pixels);
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


  List<ColorTriple> sortAccordingToChannel(ColChannel channel, List<ColorTriple> pixels){
    if(channel.equals(ColChannel.R)) {
      return pixels.stream().sorted((a, b) -> Integer.compare(a.red, b.red)).collect(Collectors.toList());
    }
    if(channel.equals(ColChannel.G)) {
      return pixels.stream().sorted((a, b) -> Integer.compare(a.green, b.green)).collect(Collectors.toList());
    }
    if(channel.equals(ColChannel.B)) {
      return pixels.stream().sorted((a, b) -> Integer.compare(a.blue, b.blue)).collect(Collectors.toList());
    }
    throw new IllegalArgumentException("channel needs to be 'R', 'G' or 'B' from ColChannel. ");
  }
  /**
   * returns the range of the pixels according to
   * a given channel.
   *
   * @param channel use r for red, g for green, b for blue as a valid channel
   * @param pixels List of ColorTriple containing the pixels
   * @return int range
   */
  int rangeOfChannel(ColChannel channel, List<ColorTriple> pixels){

    if(channel == ColChannel.R){
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

    if(channel == ColChannel.G){
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

    if(channel == ColChannel.B){
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

    throw new IllegalArgumentException("channel needs to be 'R', 'G' or 'B' from ColChannel. ");
  }


  ColorTriple meanColorTriple(List<ColorTriple> pixels){
    //todo problems can occur when pixels is too big
    float red = 0;
    float green = 0;
    float blue = 0;

    for(int i = 0; i < pixels.size(); i++){
      red = red + pixels.get(i).red;
      green = green + pixels.get(i).green;
      blue = blue + pixels.get(i).blue;

      //System.out.println("i = "+i +": "+ pixels.get(i).red +", "+ pixels.get(i).red / pixels.size()  );
    }

    red = red / pixels.size();
    green = green / pixels.size();
    blue = blue / pixels.size();

    System.out.println("meanColorTriple: "+ red +", "+ green +", "+ blue);

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

