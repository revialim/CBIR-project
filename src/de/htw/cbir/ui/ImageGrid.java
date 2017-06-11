package de.htw.cbir.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import de.htw.cbir.CBIRController;
import de.htw.cbir.model.Pic;

/**
 * Zeigt alle Bilder auf einem Grid an
 */
public class ImageGrid extends JPanel implements ComponentListener, MouseInputListener, MouseWheelListener, KeyListener, FocusListener {

	private static final long serialVersionUID = -3760137980169947493L;

	// 
	private CBIRController controller;
	
	// BufferendImage in das gezeichnet wird
	private BufferedImage bimage;
	
	// sollen die Feature oder die Bilder angezeigt werden
	private boolean drawFeatures = false;
	
	// letzter Zoomfaktor (zur Berechnung der Verschiebung des Bildes bei Zoomaenderung)
	private double zoomFactorLast = 1; 
	private double zoomFactor = 1; 
	
	// diese Variablen steuern die Verschiebung der Ansicht (ueber Mouse-Drag)
	private double xm = 0; 
	private double ym = 0;
	private int xMouseMove;
	private int yMouseMove;
	private int xMouseStartPos;
	private int yMouseStartPos;
	protected int xMousePos;
	protected int yMousePos;

	// Randfaktor 0.9 bedeutet 90% Bild, links und rechts 5% Rand
	private double borderFactor = 0.9; 

	public ImageGrid(CBIRController controller, int width, int height) {
		super();
		this.setSize(width, height);
		this.controller = controller;
		this.bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addKeyListener(this);
		addFocusListener(this);
	}
	
	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);
		
		Graphics2D gi = bimage.createGraphics();

		// Backgroundcolor grau
		gi.setBackground(Color.DARK_GRAY);
		gi.clearRect(0, 0, getWidth(), getHeight());
		//System.out.println("paint: "+getWidth()+" - "+getHeight());

		// zeichnen: Schleife ueber alle Bilder
		for (Pic image : controller.getImageManager().getImages()) {
			BufferedImage bi = (drawFeatures) ? image.getFeatureImage() : image.getDisplayImage();

			int xs = image.getxStart();
			int ys = image.getyStart();
			int xLen = image.getxLen();
			int yLen = image.getyLen();

			gi.drawImage(bi, xs, ys, xLen, yLen, null);
		}

		gi.dispose();
		g.drawImage(bimage, 0, 0, null);
	}

	/**
	 * bestimmt die Zeichenpositionen fuer die Liste der (eindimensional)
	 * sortierten Bilder bei der Sortierung zu einem oder mehreren Vorgabebildern
	 * @param xMousePos
	 * @param yMousePos
	 * @param xMouseMove
	 * @param yMouseMove
	 * @param zoomFactor
	 */
	public void calculateDrawingPositions(int xMousePos, int yMousePos, int xMouseMove, int yMouseMove, double zoomFactor) {

		Pic[] images = controller.getImageManager().getImages();
		int nThumbs = images.length;

		int hCanvas = getHeight();
		int wCanvas = getWidth();
		int h2 = hCanvas / 2;
		int w2 = wCanvas / 2;
		
		// Groesse eines thumbnail-Bereichs
		int thumbSize = (int) Math.sqrt((double) wCanvas * hCanvas / nThumbs);
		while (thumbSize > 0 && (wCanvas / thumbSize) * (hCanvas / thumbSize) < nThumbs)
			--thumbSize;

		int mapPlacesX = wCanvas / thumbSize;
		int mapPlacesY = hCanvas / thumbSize;

		double thumbSizeX = (double) wCanvas / mapPlacesX;
		double thumbSizeY = (double) hCanvas / mapPlacesY;

		// avoid empty lines at the bottom
		while (mapPlacesX * (mapPlacesY - 1) >= nThumbs) {
			mapPlacesY--;
		}
		thumbSizeY = (double) hCanvas / mapPlacesY;

		double scaledThumbSizeX = thumbSizeX * zoomFactor;
		double scaledThumbSizeY = thumbSizeY * zoomFactor;

		double sizeX = scaledThumbSizeX * borderFactor;
		double sizeY = scaledThumbSizeY * borderFactor;
		double size = Math.min(sizeX, sizeY);

		double xDelta = (w2 - xMousePos) * (zoomFactor / zoomFactorLast - 1);
		double yDelta = (h2 - yMousePos) * (zoomFactor / zoomFactorLast - 1);
		zoomFactorLast = zoomFactor;

		double xmLast = xm;
		double ymLast = ym;

		xm -= (xMouseMove + xDelta) / scaledThumbSizeX;
		ym -= (yMouseMove + yDelta) / scaledThumbSizeY;

		int xMinPos = (int) (w2 - xm * scaledThumbSizeX);
		int xMaxPos = (int) (xMinPos + mapPlacesX * scaledThumbSizeX);
		int yMinPos = (int) (h2 - ym * scaledThumbSizeY);
		int yMaxPos = (int) (yMinPos + mapPlacesY * scaledThumbSizeY);

		// disallow to move out of the map by dragging
		if (xMinPos > 0 || xMaxPos < wCanvas - 1) {
			xm = xmLast;
			xMinPos = (int) (w2 - xm * scaledThumbSizeX);
			xMaxPos = (int) (xMinPos + mapPlacesX * scaledThumbSizeX);
		}
		// when zooming out (centered at the mouseposition) it might be
		// necessary to shift the map back to the canvas
		if (xMaxPos < wCanvas - 1) {
			int xMoveCorrection = wCanvas - 1 - xMaxPos;
			xMinPos += xMoveCorrection;
			xm -= xMoveCorrection / scaledThumbSizeX;
		} else if (xMinPos > 0) {
			xm += xMinPos / scaledThumbSizeX;
			xMinPos = 0;
		}

		// same for y
		if (yMinPos > 0 || yMaxPos < hCanvas - 1) {
			ym = ymLast;
			yMinPos = (int) (h2 - ym * scaledThumbSizeY);
			yMaxPos = (int) (yMinPos + mapPlacesY * scaledThumbSizeY);
		}
		if (yMaxPos < hCanvas - 1) {
			int yMoveCorrection = hCanvas - 1 - yMaxPos;
			yMinPos += yMoveCorrection;
			ym -= yMoveCorrection / scaledThumbSizeY;
		} else if (yMinPos > 0) {
			ym += yMinPos / scaledThumbSizeY;
			yMinPos = 0;
		}

		// Zeichenposition errechnen
		for (Pic image : images) {
			
			int w = (drawFeatures) ? 64 : image.getOrigWidth();
			int h = (drawFeatures) ? 64 : image.getOrigHeight();

			// skalierung, keep aspect ratio
			double s = Math.max(w, h);
			double scale = size / s;

			int xLen = (int) (scale * w);
			int yLen = (int) (scale * h);

			int pos = image.getRank();

			int xStart = (int) (xMinPos + (pos % mapPlacesX) * scaledThumbSizeX);
			int yStart = (int) (yMinPos + (pos / mapPlacesX) * scaledThumbSizeY);

			int xs = xStart + (int) ((scaledThumbSizeX - xLen + 1) / 2); // xStart mit Rand
			int ys = yStart + (int) ((scaledThumbSizeY - yLen + 1) / 2);

			image.setxStart(xs);
			image.setxLen(xLen);
			image.setyStart(ys);
			image.setyLen(yLen);
		}
	}
	
	public void doDrawing() {	
		calculateDrawingPositions(xMousePos, yMousePos, xMouseMove, yMouseMove, zoomFactor);
		repaint();
	}
	
	/**
	 * Liefert das Bild zurück dass sich an einer bestimmten
	// Mausposition befindet. Null bedeutet dass unter der Maus kein Bild ist
	 * @param xMouse
	 * @param yMouse
	 * @return
	 */
	public Pic getImage(int xMouse, int yMouse) {
		for (Pic image : controller.getImageManager().getImages()) {
			int xs = image.getxStart();
			int ys = image.getyStart();
			int xLen = image.getxLen();
			int yLen = image.getyLen();

			if (xMouse > xs && xMouse < xs + xLen && yMouse > ys && yMouse < ys + yLen) {
				return image;
			}
		}
		return null; // kein Bild gefunden
	}

	@Override
	public void componentResized(ComponentEvent e) {
		xMouseMove = 0;
		yMouseMove = 0;
		this.bimage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		doDrawing();
	}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {}

	@Override
	public void mouseClicked(MouseEvent me) {
		xMousePos = me.getX();
		yMousePos = me.getY(); 
		Pic image = getImage(xMousePos, yMousePos);
		if (image != null) {

			if (me.getButton()==MouseEvent.BUTTON1) { //linke Maustaste
				if (me.getClickCount() == 2) { //Doppelklick
					if(!image.getCategory().equals("x")) {	
						System.out.println("Teste Bild: "+image.getId());
						controller.sortByImage(image);
						doDrawing();
					}
				}
			}
		}

		xMouseMove = 0; 
		yMouseMove = 0;
		
		requestFocus();
	}

	@Override
	public void mousePressed(MouseEvent me) {
		xMousePos = xMouseStartPos = me.getX();
		yMousePos = yMouseStartPos = me.getY();
	}

	@Override
	public void mouseReleased(MouseEvent me) {
		xMouseMove = 0; 
		yMouseMove = 0;
	}

	@Override
	public void mouseEntered(MouseEvent me) {}

	@Override
	public void mouseExited(MouseEvent me) {}

	@Override
	public void mouseDragged(MouseEvent me) {
		xMousePos = me.getX();
		yMousePos = me.getY();
		xMouseMove = xMousePos - xMouseStartPos; 
		yMouseMove = yMousePos - yMouseStartPos;
		xMouseStartPos = xMousePos;
		yMouseStartPos = yMousePos;
		doDrawing();
	}

	@Override
	public void mouseMoved(MouseEvent me) {
		xMousePos = me.getX();
		yMousePos = me.getY();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int count = e.getWheelRotation();

		if (count < 0) {
			zoomFactor = zoomFactor*1.1;
			if (zoomFactor > 50) 
				zoomFactor = 50;
		}
		else {
			zoomFactor = zoomFactor/1.1;	
			if (zoomFactor < 1) zoomFactor = 1;
		}
		doDrawing();
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("keypress: "+e.getKeyChar());
		
		if (e.getKeyChar() == '+' ) 
			zoomFactor *= 1.1;
	
		if (e.getKeyChar() == '-' ) {
			zoomFactor /= 1.1;
			if(zoomFactor < 1) zoomFactor = 1; 
		}
	
		// Features anzeigen
		if (e.getKeyChar() == 'f' ) 
			drawFeatures = true;
	
		// Bilder anzeigen
		if (e.getKeyChar() == 'b' ) 
			drawFeatures = false;
	
		if (e.getKeyChar() == 'r' ) {
			for (Pic image : controller.getImageManager().getImages())
				image.setRank(image.getId());
		}

		doDrawing();
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void focusGained(FocusEvent e) {
		doDrawing();
	}

	@Override
	public void focusLost(FocusEvent e) {}
}
