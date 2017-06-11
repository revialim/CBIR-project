package de.htw.cbir.evaluation;

import java.util.Arrays;

import de.htw.cbir.feature.FeatureFactory;
import de.htw.cbir.model.Pic;
import de.htw.cbir.model.PicPair;

public class CBIREvaluation {

	private Pic[] images;
	private FeatureFactory sorter;

	public CBIREvaluation(FeatureFactory sorter, Pic[] images) {
		this.images = images;
		this.sorter = sorter;
	}

	/**
	 * Berechnet die Mean Average Precision über alle Bilder
	 * 
	 * @param sorter
	 * @return
	 */
	public float test(Pic[] queryImages, boolean displayResult, String description) {
		float[][] lookup = createDistanceLookupTable(sorter, this.images, queryImages);
		PrecisionRecallTable table = new PrecisionRecallTable(queryImages.length);

		// berechne die Average Precision für jedes Bild aus
		float MAP = 0;
		for (Pic queryImage : queryImages)
			MAP += test(queryImage, lookup, table);
		MAP /= queryImages.length;

		// beende die Analyse und zeige eventuell Ergebnisse
		if(displayResult)
			table.visualize(sorter.getName(), description);

		return MAP;
	}

	/**
	 * Vergleiche das Query Bild mit allen Bildern im ImageManager und ermittle
	 * die jeweilige Distanz der Bilder über die Lookup Tabelle.
	 * 
	 * Berechne die Average Precision mithilfe der Distanz.
	 * 
	 * @param testImages
	 * @param lookup
	 * @return
	 */
	private float test(Pic queryImage, float[][] lookup, PrecisionRecallTable table) {
		Pic[] allImages = images;
		PicPair[] result = new PicPair[allImages.length];

		// durchlaufe alle Bilder
		for (int i = 0; i < allImages.length; i++) {
			Pic searchImage = allImages[i];
			float distance = lookup[queryImage.getId()][searchImage.getId()];
			result[i] = new PicPair(queryImage, searchImage, distance);
		}

		// sortiere die Suchbilder nach der Distance zum Query Bild
		Arrays.sort(result);

		// berechne die eigentliche Average Precision
		return table.analyse(result);
	}


	/**
	 * Erzeuge eine Lookup Tabelle für alle Distanzen. So das diese nur einmal
	 * berechnet werden müssen.
	 * 
	 * @return
	 */
	private static float[][] createDistanceLookupTable(FeatureFactory sorter, Pic[] allImages, Pic[] queryImages) {
		float[][] lookupTable = new float[allImages.length][allImages.length];
		for (int i = 0; i < lookupTable.length; i++)
			Arrays.fill(lookupTable[i], -1); 
		
		// berechnen zu allen QueryBilder die Abstände zu allen anderen Bildern
		for (int i = 0; i < queryImages.length; i++) {
			Pic p1 = queryImages[i];
			for (int j = 0; j < allImages.length; j++) {
				Pic p2 = allImages[j];		
				float dist = lookupTable[p1.getId()][p2.getId()];
				if(dist == -1)
					dist = sorter.getDistance(p1.getFeatureVector(), p2.getFeatureVector());
				lookupTable[p1.getId()][p2.getId()] = lookupTable[p2.getId()][p1.getId()] = dist;
			}
		}
		return lookupTable;
	}
}
