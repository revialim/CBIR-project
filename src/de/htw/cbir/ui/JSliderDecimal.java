package de.htw.cbir.ui;

import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.plaf.UIResource;

public class JSliderDecimal extends JSlider {

	private static final long serialVersionUID = -2305958479725115074L;

	private int digitsBehindDecimal;
	private DecimalFormat df;
	
    public static JSliderDecimal createDoubleJSlider(double min, double max, double value, int digitsBehindDecimal) {
    	
    	int precision = (int) Math.pow(10, digitsBehindDecimal);
    	
    	final int imin = (int)(min*precision);
    	final int imax = (int)(max*precision);
    	final int ivalue = (int)(value*precision);

        return new JSliderDecimal(imin, imax, ivalue, digitsBehindDecimal);  
    }
    
    private JSliderDecimal(int min, int max, int value, int digitsBehindDecimal) {
    	super(HORIZONTAL, min, max, value);
    	this.digitsBehindDecimal = digitsBehindDecimal;
    	
    	df = new DecimalFormat("#0.00");
    	df.setMaximumFractionDigits(digitsBehindDecimal);
    }
    
    @Override
    public void setMajorTickSpacing(int n) {
    	int precision = (int) Math.pow(10, digitsBehindDecimal);
        super.setMajorTickSpacing(n * precision);
    }
    
	public void setMajorTickSpacing(double d) {
		int precision = (int) Math.pow(10, digitsBehindDecimal);
		super.setMajorTickSpacing((int)(d * precision));
	}
	
    @Override
    public void setMinorTickSpacing(int n) {
    	int precision = (int) Math.pow(10, digitsBehindDecimal);
        super.setMinorTickSpacing(n * precision);
    }    
   
	public void setMinorTickSpacing(double d) {
		int precision = (int) Math.pow(10, digitsBehindDecimal);
		super.setMinorTickSpacing((int)(d * precision));
	}
    
    @SuppressWarnings("rawtypes")
	@Override
    public Hashtable createStandardLabels( int increment, int start ) {
        if ( start > getMaximum() || start < getMinimum() ) {
            throw new IllegalArgumentException( "Slider label start point out of range." );
        }

        if ( increment <= 0 ) {
            throw new IllegalArgumentException( "Label incremement must be > 0" );
        }
        
        final JSlider that = this;
        final Dictionary labelTable = getLabelTable();
        
        @SuppressWarnings("serial")
		class SmartHashtable extends Hashtable<Object, Object> implements PropertyChangeListener {
            int increment = 0;
            int start = 0;
            boolean startAtMin = false;
            

            class LabelUIResource extends JLabel implements UIResource {
                public LabelUIResource( String text, int alignment ) {
                    super( text, alignment );
                    setName("Slider.label");
                }

                public Font getFont() {
                    Font font = super.getFont();
                    if (font != null && !(font instanceof UIResource)) {
                        return font;
                    }
                    return that.getFont();
                }

                public Color getForeground() {
                    Color fg = super.getForeground();
                    if (fg != null && !(fg instanceof UIResource)) {
                        return fg;
                    }
                    if (!(that.getForeground() instanceof UIResource)) {
                        return that.getForeground();
                    }
                    return fg;
                }
            }

            public SmartHashtable( int increment, int start ) {
                super();
                this.increment = increment;
                this.start = start;
                startAtMin = start == getMinimum();
                createLabels();
            }

            public void propertyChange( PropertyChangeEvent e ) {
                if ( e.getPropertyName().equals( "minimum" ) && startAtMin ) {
                    start = getMinimum();
                }

                if ( e.getPropertyName().equals( "minimum" ) ||
                     e.getPropertyName().equals( "maximum" ) ) {

                    Enumeration keys = getLabelTable().keys();
                    Hashtable<Object, Object> hashtable = new Hashtable<Object, Object>();

                    // Save the labels that were added by the developer
                    while ( keys.hasMoreElements() ) {
                        Object key = keys.nextElement();
                        Object value = labelTable.get(key);
                        if ( !(value instanceof LabelUIResource) ) {
                            hashtable.put( key, value );
                        }
                    }

                    clear();
                    createLabels();

                    // Add the saved labels
                    keys = hashtable.keys();
                    while ( keys.hasMoreElements() ) {
                        Object key = keys.nextElement();
                        put( key, hashtable.get( key ) );
                    }

                    ((JSlider)e.getSource()).setLabelTable( this );
                }
            }

            void createLabels() {
                for ( int labelIndex = start; labelIndex <= getMaximum(); labelIndex += increment ) {
                	
                	int precision = (int) Math.pow(10, digitsBehindDecimal);
                	double num = (double)labelIndex / precision;
                	String numStr = df.format(num);
                	
                    put( Integer.valueOf( labelIndex ), new LabelUIResource(numStr, JLabel.CENTER ) );
                }
            }
        }

        SmartHashtable table = new SmartHashtable( increment, start );

        if (labelTable != null && (labelTable instanceof PropertyChangeListener)) {
            removePropertyChangeListener((PropertyChangeListener) labelTable);
        }

        addPropertyChangeListener( table );

        return table;
    }

    
    public float getDecimalValue() {
    	int precision = (int) Math.pow(10, digitsBehindDecimal);
    	return (float)getValue() / precision;
    }
}
