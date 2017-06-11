package de.htw.cbir.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Visualisiert den Mean Average Precision Graph in einem Diagram
 */
public class DiagramGraph 
{ 

	private final int width=500, height=400, margin_top=20, margin_right=350, margin_bottom=80, margin_left=20;
	private final int p=0, r=1;
	private Vector<float[][]> graphs = new Vector<float[][]>();
	private Vector<String> titles = new Vector<String>();
	private Color[] colors = {Color.BLACK, Color.BLUE, Color.GREEN, Color.ORANGE, Color.CYAN};
	private JFrame frame = new JFrame();
	
	public DiagramGraph ()	{ 
		
		GraphPanel graphPanel = new GraphPanel();
		JButton clearButton = new JButton("Clear graph");
		clearButton.addActionListener((ActionEvent e) -> { 
			graphs.clear(); 
			titles.clear();
			frame.repaint();
		});
		
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(clearButton, BorderLayout.NORTH);
		contentPane.add(graphPanel, BorderLayout.CENTER);		
		
		frame.setContentPane(contentPane);		
		frame.setTitle("Precision-Recall-Graph");
		frame.setSize(width+margin_left+margin_right, height+margin_top+margin_bottom);
		frame.setLocation(CBIRView.getFrameSizeX(), 0);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(false); 
	}

	public void addGraph(float[][] pUeberR, String titel)
	{
		graphs.add(pUeberR);
		titles.add(titel);
		frame.setVisible(true);
		frame.toFront();
		frame.requestFocus();
		frame.repaint();
	}
		
	class GraphPanel extends JPanel 
	{
		private static final long serialVersionUID = 5827852254290989616L;

		@Override
		public void paint(Graphics graphics)	{ 
			paintCoordinates(graphics);			

			for (int i = 0; i < graphs.size(); i++) {
				Color c; 
				if(i >= 5) {
					int r = (i*200)%256;
					int g = (i*95)%256;
					int b = (i*35)%256;
					c = new Color(r, g, b);
				}
				else
					 c = colors[i];
				paintGraphs(graphs.elementAt(i), c, graphics, i, titles.elementAt(i));
			}
		} 

		public void paintGraphs(float[][] pUeberR, Color c, Graphics g, int z, String titel){
			int x1, x2, y1, y2;
			g.setColor(c);
			g.drawString(titel, margin_left+width+10, margin_top+z*15);
			for (int i = 1; i < pUeberR[p].length; i++) {
				x1 = margin_left + (int)(pUeberR[r][i-1]*width+0.5);
				x2 = margin_left + (int)(pUeberR[r][i]*width+0.5);
				
				y1 = height + margin_top - (int)(pUeberR[p][i-1]*height+0.5);
				y2 = height + margin_top - (int)(pUeberR[p][i]*height+0.5);
				
				g.drawLine(x1, y1, x2, y2);
			}

		}

		private void paintCoordinates(Graphics g) {			
			g.setColor(Color.white);
			Rectangle bounds = this.getBounds();
			g.fillRect( 0, 0, (int) bounds.getWidth(), (int) bounds.getHeight());
			
			g.setColor(Color.black);
			g.drawLine(margin_left, margin_top, margin_left, height+margin_top);
			g.drawLine(margin_left, height+margin_top, width+margin_left, height+margin_top);
			g.drawString("P", 7, margin_top+10);
			g.drawString("R", margin_left+width-15, margin_top+height+12);
			
			int hl = 10; // 4 -> Hilfslinie alle viertel
			for(int z=0; z<=hl; z++) {
				//Hilfslinien zeichnen
				//vertikal
				g.drawLine(margin_left+z*width/hl, margin_top, margin_left+z*width/hl, height+margin_top);
				//horizontal
				g.drawLine(margin_left, margin_top+z*height/hl, width+margin_left, margin_top+z*height/hl);
			}
		}
	}
}






