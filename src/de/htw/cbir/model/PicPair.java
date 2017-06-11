package de.htw.cbir.model;

public class PicPair implements Comparable<PicPair> {

	private float distance;
	private Pic searchImage;
	private Pic queryImage;
	
	public PicPair(Pic queryImage, Pic searchImage, float distance) {
		this.distance = distance;
		this.queryImage = queryImage;
		this.searchImage = searchImage;
	}

	public float getDistance() {
		return distance;
	}

	public Pic getSearchImage() {
		return searchImage;
	}

	public Pic getQueryImage() {
		return queryImage;
	}
	
	public boolean isSameCategory() {
		return queryImage.getCategory().equalsIgnoreCase(searchImage.getCategory());
	}

	@Override
	public int compareTo(PicPair pair) {
		int diff = Float.compare(distance, pair.getDistance());
		if(diff == 0)
			return Integer.compare(searchImage.getId(), pair.getSearchImage().getId());	
		return diff;
	}
	
}
