package de.htw.cbir.evaluation;

import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import de.htw.cbir.model.PicPair;
import de.htw.cbir.ui.DiagramGraph;

public class PrecisionRecallTable {

	// Auflösung (in Pixel) von dem Graph. Ändert die Visualisierung nicht den MAP.
	private static int GraphResolution = 500;
	private static DiagramGraph graph = new DiagramGraph();
		
	private float meanAveragePrecision;
	private AtomicInteger currentPosition;
	private float[][] recalls;
	private float[][] precisions;
	
	public PrecisionRecallTable(int numTestRuns) {
		this.meanAveragePrecision = 0;
		this.currentPosition = new AtomicInteger();
		this.recalls = new float[numTestRuns][];
		this.precisions = new float[numTestRuns][];
	}
	
	/**
	 * Berechnet die Average Precision. Gleichzeitig werden sich weitere Informationen gemerkt
	 * um später ein Mean Average Precision Graph mit der visualize Methode zu zeichnen. 
	 * 
	 * @param sortedArray
	 * @return
	 */
	public float analyse(PicPair[] sortedArray) {
		
		// wurde die Anzahl an Testruns noch nicht überschritten
		int index = currentPosition.getAndIncrement();
		if(index > precisions.length) {
			System.out.println("Reached the maximum amount of analysis for this table.");
			return 0;
		}
		
		int numPics = sortedArray.length;
		float[] precision = new float[numPics];
		float[] recall = new float[numPics];
		
		float precisionSum = 0;			// die Summe der Precision Werte aller relevanten Bilder 
        int currRelevantRetrieved = 0;	// wieviele relevante Bilder wurden bereits gefunden
        
        // Analysiere das sortierte Array
		for (int pos = 0; pos < sortedArray.length; pos++) {
	        PicPair imagePair = sortedArray[pos];
	        
	    	// hat das Search und Query Bild die gleiche Kategorie
	        if(imagePair.isSameCategory()) {
            	currRelevantRetrieved++;
            	precisionSum += (float)currRelevantRetrieved / (pos + 1);
            } 
	        
	        // zusätzliche Informationen für den Mean Average Precision Graph
	        precision[pos] = (float) currRelevantRetrieved / (pos + 1);
	        recall[pos] = (float) currRelevantRetrieved / imagePair.getQueryImage().getCategoryOccurrence();
		}
	     
		// speichere in globale Arrays
		precisions[index] = precision;
		recalls[index] = recall;
		meanAveragePrecision += precisionSum / currRelevantRetrieved;

		return precisionSum / currRelevantRetrieved;
	}

	/**
	 * Visualisiere den MeanAveragePrecision Graph für die gemachten Tests.
	 * 
	 * @param featureFactoryName
	 * @param description
	 */
	public void visualize(String featureFactoryName, String description) {	
		int numPics = precisions.length;
		
		int validCases = 0;
		float[][] avgPOverR = new float[2][GraphResolution];
		for (int pic = 0; pic < numPics; pic++) {
			float pLast = 1f;
			float rLast = 0f;
			
			// die "160"er Categorien erzeugen leere Stellen
			if(precisions[pic] == null) continue;

			for (int place = 0; place < precisions[pic].length; place++) {
				float pAct = precisions[pic][place];
				float rAct = recalls[pic][place];

				if (rAct > rLast) {
					float m = (pAct-pLast) / (GraphResolution*(rAct-rLast));

					for (int r = (int)(rLast*GraphResolution), i = 0; r <= (rAct*GraphResolution)-1; r++, i++) {
						avgPOverR[0][r] += pLast + i*m;
						avgPOverR[1][r] = r/(float)GraphResolution;
					}
				}
				pLast = pAct;
				rLast = rAct;
			}
			
			validCases++;
		}
		for (int i = 0; i < GraphResolution; i++)
			avgPOverR[0][i] /= validCases;
			
		// zeichne das Ergebnis als Graph ins Diagramm
		String title = String.format(Locale.ENGLISH,"%s %s mAP=%6.3f", featureFactoryName, description, meanAveragePrecision / validCases);
		graph.addGraph(avgPOverR, title);	
	}

	/**
	 * Berechnet die durchschnittliche Genauerigkeit aller relevanten Bilder (Bilder mit der gleichen Kategorie wie das Querybild)
	 */
	public static float calcAveragePrecision(PicPair[] sortedArray) {
    	float precisionSum = 0;			// die Summe der Precision Werte aller relevanten Bilder 
        int currRelevantRetrieved = 0;	// wieviele relevante Bilder wurden bereits gefunden

        // Analysiere das sortierte Array
        for (int pos = 0; pos < sortedArray.length; pos++) {
        	PicPair imagePair = sortedArray[pos];

        	// hat das Search und Query Bild die gleiche Kategorie
            if(imagePair.isSameCategory()) {
            	currRelevantRetrieved++;
            	precisionSum += (float)currRelevantRetrieved / (pos + 1);
            } 
        }
        
        return precisionSum / currRelevantRetrieved;
	}
}
