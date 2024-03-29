package light.guipackage.dom;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.JPanel;

import light.guipackage.general.Point;
import light.guipackage.general.Rectangle;
import light.guipackage.gui.ScreenUtils;

public class DOMViewer extends JPanel {

	private static final long serialVersionUID = 8826256830984099915L;
	public static JFrame frame;
	public static Rectangle screen = new Rectangle(0, 0, 1200, 500);

	DOM dom;
	ScreenUtils sU;
	boolean isVisible;

	int maxDepth;
	int widths[];

	public DOMViewer(DOM dom) {
		this.dom = dom;
		sU = new ScreenUtils(screen);
		isVisible = false;
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Font f = new Font("Geneva", Font.ROMAN_BASELINE, 10);
		sU.fillRect(g2, new Color(50, 50, 50), new Rectangle(sU.cW(screen.x), sU.cH(screen.y), sU.cW(screen.width), sU.cH(screen.height))); //Base

		maxDepth = dom.maxDepth(dom.getRoot());
		widths = new int[maxDepth];

		for (int i=0; i<maxDepth; i++) {
			int width = dom.widthAtDepth(dom.getRoot(), i+1);
			widths[i] = width;
		}
		
		sU.drawStringFromPoint(g2, f, "Depth: "+maxDepth, new Color(220, 220, 220), new Point(sU.cW(2), sU.cH(5)));
		sU.drawStringFromPoint(g2, f, "Widths: "+Arrays.toString(widths), new Color(220, 220, 220), new Point(sU.cW(2), sU.cH(8)));

		if (maxDepth==0) return;
		
		Point bounds = new Point(0, 100);
		Rectangle nodeR = getNodeRec(g2, dom.getRoot(), 0, bounds);
		traverseAndDraw(g2, dom.getRoot(), 0, 0, bounds, new Point(nodeR.x+nodeR.width/2, nodeR.y+nodeR.height));
	}
	
	public void traverseAndDraw(Graphics2D g, DOMNode node, int cDepth, int cWidth, Point bounds, Point linePoint) {
		if (node==null) return;
		
		Rectangle nodeR = getNodeRec(g, node, cDepth, bounds); 
		drawNode(g, node, nodeR, linePoint);

		cWidth = 0;
		int totWidth = node.getChildren().size();
		
		for (DOMNode child : node.getChildren()) {
			double bX = bounds.x+((bounds.y-bounds.x)/totWidth)*cWidth;
			double bY = bounds.x+((bounds.y-bounds.x)/totWidth)*(cWidth+1);
			Point boundsC = new Point(bX, bY);
			
			traverseAndDraw(g, child, cDepth+1, cWidth, boundsC, new Point(nodeR.x+nodeR.width/2, nodeR.y+nodeR.height));
			cWidth++;
	    }
	}
	
	public Rectangle getNodeRec(Graphics2D g, DOMNode node, int depth, Point xBound) {
		Font f = new Font("Geneva", Font.ROMAN_BASELINE, 10);
		Point yBound = new Point((100/maxDepth)*depth, (100/maxDepth)*(depth+1));
		
		
		double w = sU.getStringWidthAsPerc(f, node.getLabel())+2;
		double h = sU.getStringHeightAsPerc(f, node.getLabel())+2;;
		double x = xBound.x+((xBound.y-xBound.x)/2)-(w/2);
		double y = yBound.x+((yBound.y-yBound.x)/2)-(h/2);
		
		//screenUtils.fillRect(g, Color.RED, new Rectangle(xBound.x, y, 0.5, h));
		//screenUtils.fillRect(g, Color.RED, new Rectangle(xBound.y, y, 0.5, h));
		
		return new Rectangle(x, y, w, h);
	}

	public void drawNode(Graphics2D g, DOMNode node, Rectangle r, Point linePoint) {
		Font f = new Font("Geneva", Font.ROMAN_BASELINE, 10);
		
		r = new Rectangle(sU.cW(r.x), sU.cH(r.y), sU.cW(r.width), sU.cH(r.height));
		
		//Draw node
		sU.fillRoundRect(g, new Color(100, 100, 100), r, 10);
		sU.drawCenteredString(g, f, node.getLabel(), new Color(220, 220, 220), r);
		//Draw line
		sU.drawLine(g, new Color(200, 200, 200), new Point(sU.cW(linePoint.x), sU.cH(linePoint.y)), new Point(r.x+r.width/2, r.y));
	}

	public static DOMViewer initialise(DOM d) {
		DOMViewer panel = new DOMViewer(d);
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Initialise
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				frame = new JFrame();
				panel.setPreferredSize(new Dimension((int) screen.width, (int) screen.height));
				frame.getContentPane().add(panel);
				frame.setBounds(0, 350, (int) screen.width, (int) screen.height);

				//Label and build
				frame.setTitle("DOM Visualiser");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				//Finish up
				frame.setVisible(false);
				frame.pack();
			}
		});
		return panel;
	}
}
