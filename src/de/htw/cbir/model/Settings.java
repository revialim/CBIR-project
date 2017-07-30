package de.htw.cbir.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Settings {
	
	private float saturation = 1;
	private int resolution = 4;//16
	private int bins = 4;
	private double alpha = 0.3;
	private double beta = 0.3;
	private int metric = 1;

	public float getSaturation() {
		return saturation;
	}
	
	public void setSaturation(float saturation) {
		this.saturation = saturation;
		fireEvent(new SettingChangeEvent(SettingOption.SATURATION, saturation));
	}
	
	public int getResolution(){
		return resolution;
}

	public void setResolution(int resolution){
		this.resolution = resolution;
		fireEvent(new SettingChangeEvent(SettingOption.RESOLUTION, resolution));
	}

	public int getBins(){
		return bins;
	}

	public void setBins(int bins){
		this.bins = bins;
		fireEvent(new SettingChangeEvent(SettingOption.BINS, bins));
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
		fireEvent(new SettingChangeEvent(SettingOption.ALPHA, alpha));
	}

	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
		fireEvent(new SettingChangeEvent(SettingOption.BETA, beta));
	}

	public int getMetric(){
		return metric;
	}
	
	public void setMetric(int metric){
		this.metric = metric;
		fireEvent(new SettingChangeEvent(SettingOption.METRIC, metric));
	}
	
	
	// --------------------------------------------------------------------------------
	// ---------------------- Event handling and delegation ---------------------------
	// --------------------------------------------------------------------------------
	/**
	 * Alle Einstellungen die über Kontrollelement geändert werden können
	 * brauchen eine SettingOption. Diese Identifiziert das Event.
	 */
	public static enum SettingOption { SATURATION, RESOLUTION, METRIC, BINS, ALPHA, BETA };
	
	/**
	 * SettingOption gibt an bei welchen Events die Listener informiert werden sollen.
	 * Jede Option kann nur einen Listener haben.
	 */
	protected Map<SettingOption, List<SettingChangeEventListener>> eventListeners = new HashMap<>();
		
	/**
	 * Feuer ein Event an allen Listener die sich für eine Änderung
	 * and der <SettingOption> Einstellung interessieren.
	 *
	 */
	private void fireEvent(SettingChangeEvent event) {
		List<SettingChangeEventListener> listeners = eventListeners.get(event.getSetting());
		if(listeners != null) {
			for (SettingChangeEventListener listener : listeners) {			
				listener.settingChanged(event);
			}
		}
	}
	
	/**
	 * Fügt einen Event Listener hinzu der informiert wird wenn die <SettingOption> 
	 * Einstellung von einem UI Element (vom Anwendert) geändert wurde.
	 *
	 */
	public void addChangeListener(SettingOption settingOption, SettingChangeEventListener actionListener) {
		if(actionListener == null) return;
		
		// gibt es andere Listener für die selbe Setting Option
		List<SettingChangeEventListener> listeners = eventListeners.get(settingOption);
		if(listeners == null)
			eventListeners.put(settingOption, listeners = new ArrayList<>());
		listeners.add(actionListener);
	}

	public void removeChangeListeners() {
		eventListeners.clear();
	}
	
	/**
	 * Ein Event welches die Änderungen der Settings beinhalten.
	 * 
	 * @author Nico
	 *
	 */
	public static class SettingChangeEvent {
		protected SettingOption setting;
		protected Number value;
		public SettingChangeEvent(SettingOption setting, Number value) {
			super();
			this.setting = setting;
			this.value = value;
		}
		public SettingOption getSetting() {
			return setting;
		}
		public Number getValue() {
			return value;
		}
	}
	
	/**
	 * Interface für alle Listener von SettingChangeEvents
	 * 
	 * @author Nico
	 *
	 */
	public static interface SettingChangeEventListener {
		public void settingChanged(SettingChangeEvent event);
	}
}
