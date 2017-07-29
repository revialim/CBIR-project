package de.htw.cbir.feature;

import de.htw.cbir.model.Pic;
import de.htw.cbir.model.Settings;

import java.awt.image.BufferedImage;

/**
 * Created by lili on 29.07.17.
 */
public class ColorHistogramYCoCg extends FeatureFactory {
  public ColorHistogramYCoCg(Settings settings) {
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
    return null;
  }
}
