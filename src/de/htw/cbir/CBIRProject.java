package de.htw.cbir;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import de.htw.cbir.model.Settings;

public class CBIRProject {

	private static String startDirectory = "images";
	
	public static void main(String[] args) throws IOException {
		
		// die Einstellungen im gesamten Projekt
		Settings settings = new Settings();
		
		// in welchem Verzeichnis befinden sich die Bilder
		final File imageDirectory = askDirectory(startDirectory);
		
		// lade die Bilder
		PicManager imageManager = new PicManager();
		imageManager.loadImages(imageDirectory);
		
		// Zeige die GUI an
		new CBIRController(settings, imageManager);
	}
	
	/**
	 * Frage den Anwender nach einem Verzeichnis 
	 * 
	 * @param startDirectory
	 * @return
	 */
	public static File askDirectory(final String dir) {
		final JFileChooser fc = new JFileChooser(dir);

		// Nur komplette Ordner koennen ausgewaehlt werden
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		final int returnVal = fc.showOpenDialog(null);

		if (returnVal != JFileChooser.APPROVE_OPTION)
			System.exit(-1);

		// Liest alle Dateien des Ordners und schreibt sie in ein Array
		return fc.getSelectedFile();
	}
}
