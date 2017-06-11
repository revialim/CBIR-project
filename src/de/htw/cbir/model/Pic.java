package de.htw.cbir.model;

import java.awt.image.BufferedImage;

public class Pic extends Object{
	
	private int id;							// Id bzw Index für das Bild
	private String category;				// Name der Kategorie für das Bild
	private int categoryOccurrence;			// wie viele Bilder haben die selbe Kategorie
	private int rank;                 		// Position bei sortierter 1D-Reihenfolge	
	private float[] featureVector;			// Feature Vektor für das Bild
	
	// Originalgröße des Bildes
	private int origWidth; 
	private int origHeight;
	
	// Zeichenpositionen 
	private int xStart = 0;
	private int xLen = 0;
	private int yStart = 0;
	private int yLen = 0;
	
	// zur Visualisierung
	private BufferedImage bImage;
	private BufferedImage featureImage;

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public int getCategoryOccurrence() {
		return categoryOccurrence;
	}

	public void setCategoryOccurrence(int typeOccurrence) {
		this.categoryOccurrence = typeOccurrence;
	}

	public float[] getFeatureVector() {
		return featureVector;
	}

	public void setFeatureVector(float[] featureVector) {
		this.featureVector = featureVector;
	}

	public int getOrigWidth() {
		return origWidth;
	}

	public void setOrigWidth(int origWidth) {
		this.origWidth = origWidth;
	}

	public int getOrigHeight() {
		return origHeight;
	}

	public void setOrigHeight(int origHeight) {
		this.origHeight = origHeight;
	}

	public int getxStart() {
		return xStart;
	}

	public void setxStart(int xStart) {
		this.xStart = xStart;
	}

	public int getxLen() {
		return xLen;
	}

	public void setxLen(int xLen) {
		this.xLen = xLen;
	}

	public int getyStart() {
		return yStart;
	}

	public void setyStart(int yStart) {
		this.yStart = yStart;
	}

	public int getyLen() {
		return yLen;
	}

	public void setyLen(int yLen) {
		this.yLen = yLen;
	}

	public BufferedImage getDisplayImage() {
		return bImage;
	}

	public void setDisplayImage(BufferedImage bImage) {
		this.bImage = bImage;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BufferedImage getFeatureImage() {
		return featureImage;
	}

	public void setFeatureImage(BufferedImage featureImage) {
		this.featureImage = featureImage;
	}	
}
