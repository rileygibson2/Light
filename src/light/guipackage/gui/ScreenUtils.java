package light.guipackage.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import light.general.Utils;
import light.guipackage.cli.CLI;
import light.guipackage.general.Point;
import light.guipackage.general.Rectangle;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.general.UnitValue.Unit;
import light.guipackage.gui.components.Component;
import light.guipackage.gui.components.primitives.Image;
import light.guipackage.gui.components.primitives.Label;
import light.guipackage.gui.components.primitives.boxes.SimpleBox;

public class ScreenUtils {
	
	private Rectangle screen;
	
	private Map<Image, BufferedImage> imageCache;
	
	public ScreenUtils(Rectangle screen) {
		this.screen = screen;
		this.imageCache = new HashMap<Image, BufferedImage>();
		loadFonts();
	}
	
	public void updateScreen(Rectangle screen) {this.screen = screen;}
	
	public void clearImageCache() {
		imageCache.clear();
		CLI.debug("Cleared image cache");
	}
	
	public void loadFonts() {
		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			//            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Utils.getInputStream("assets/fonts/neoteric.ttf")));
			//            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Utils.getInputStream("assets/fonts/neoteric-bold.ttf")));
			//            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Utils.getInputStream("assets/fonts/aloevera.ttf")));
			//            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Utils.getInputStream("assets/fonts/blackpast.ttf")));
			// ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, Utils.getInputStream("assets/fonts/suezone.ttf")));
		} catch (Exception e) {CLI.error("Error loading fonts - "+e.getMessage());}
	}
	
	public double getStringWidthAsPerc(Font f, String s) {
		FontRenderContext frc = new FontRenderContext(null, true, true);
		return cWR(f.getStringBounds(s, frc).getWidth());
		//FontMetrics metrics = g.getFontMetrics(f);
		//return cWR(metrics.stringWidth(s));
	}
	
	public double getStringHeightAsPerc(Font f, String s) {
		FontRenderContext frc = new FontRenderContext(null, true, true);
		return cHR(f.getStringBounds(s, frc).getHeight());
		//FontMetrics metrics = g.getFontMetrics(f);
		//return cHR(metrics.getHeight());
	}
	
	public void drawBase(Graphics2D g) {
		fillRect(g, Styles.bg, new Rectangle(0, 0, screen.width, screen.height));
	}
	
	public void drawLabel(Graphics2D g, Label l) {
		doClip(g, l);
		Rectangle r = l.getRealRec();
		Color col = new Color(l.textCol.getRed(), l.textCol.getGreen(), l.textCol.getBlue(), percToCol(l.getOpacity()));
		g.setFont(l.font);
		
		if (l.isTextXCentered()&&l.isTextYCentered()) drawCenteredString(g, l.font, l.getText(), col, r);
		else if (l.isTextXCentered()) drawXCenteredString(g, l.font, l.getText(), col, r);
		else if (l.isTextYCentered()) drawYCenteredString(g, l.font, l.getText(), col, r);
		else drawStringFromPoint(g, l.font, l.getText(), col, new Point(r.x, r.y));

		//Reset clip
		g.setClip(null);
	}
	
	public void drawImage(Graphics2D g, Image i) {
		if (i.getSource()==null) return;
		doClip(g, i);
		Rectangle r = i.getRealRec();
		if (r.width<=0||r.height<=0) return; //Prevent image transformations throwing errors
		
		BufferedImage img = null;
		
		//Check image cache first
		if (!i.getIgnoreCache()&&imageCache.containsKey(i)&&imageCache.get(i)!=null) img = imageCache.get(i);
		else {
			try {
				img = ImageIO.read(Utils.getURL("assets/"+i.getSource()));
				if (i.getMakeImageTransparent()) img = makeImageTransparent(img);
				imageCache.put(i, img);
				i.setIgnoreCache(false);
			}
			catch (IOException | IllegalArgumentException e) {
				CLI.error("ImageIO failed for assets/"+i.getSource());
				return;
			}
		}
		if (i.getOpacity()<100) g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) (i.getOpacity()/100)));
		
		//Scale to rectangle
		double scale = Math.min(r.width/(double) img.getWidth(), r.height/(double) img.getHeight());
		int scaledWidth = (int) (img.getWidth()*scale);
		int scaledHeight = (int) (img.getHeight()*scale);
		
		BufferedImage scaledImg = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		AffineTransform affineTransform = AffineTransform.getScaleInstance(scale, scale);
		AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_BILINEAR);
		affineTransformOp.filter(img, scaledImg);
		g.drawImage(scaledImg, (int) (r.x+(r.width-scaledImg.getWidth())/2), (int) (r.y), null);
		
		//Reset alpha and clip
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		g.setClip(null);
	}
	
	public BufferedImage makeImageTransparent(BufferedImage img) {
		int width = img.getWidth();
		int height = img.getHeight();
		
		// Create a new BufferedImage with ARGB color model
		BufferedImage transparentImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		// Define the black color
		int blackColor = Color.BLACK.getRGB();
		
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				// Calculate the brightness of the pixel
				Color color = new Color(img.getRGB(x, y), true);
				float brightness = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null)[2];
				
				// Set the alpha based on the brightness
				transparentImage.setRGB(x, y, ((255-Math.round(brightness * 255)) << 24) | (0 << 16) | (0 << 8) | 0);
			}
		}
		return transparentImage;
	}
	
	public void drawSimpleBox(Graphics2D g, SimpleBox b) {
		doClip(g, b);
		Rectangle r = b.getRealRec();
		Color col = b.getColor();
		
		if (col!=null&&col.getAlpha()==0) return; //Duck tape fix as this method does net respect the boxe's alpha channel
		if (col==null) col = Color.BLACK;
		col = new Color(col.getRed(), col.getGreen(), col.getBlue(), percToCol(b.getOpacity()));
		
		if (b.isFilled()) {
			if (b.isOval()) fillOval(g, col, r);
			else if (b.isRounded()) {
				if (b.getRoundedCorners()!=null) fillRoundRect(g, col, r, b.getRoundedCorners(), b.getArcSize());
				else fillRoundRect(g, col, r, b.getArcSize());
			}
			else fillRect(g, col, r);
		}
		
		//Border
		if (b.hasBorder()) {			
			if (b.isOval()) drawOval(g, b.getBorderColor(), r, b.getBorderWidth());
			else if (b.isRounded()) {
				if (b.getRoundedCorners()!=null) drawRoundRect(g, b.getBorderColor(), col, r, b.getBorderWidth(),  b.getRoundedCorners(), b.getArcSize());
				else drawRoundRect(g, b.getBorderColor(), r, b.getBorderWidth(), b.getArcSize());
			}
			else drawRect(g, b.getBorderColor(), r, b.getBorderWidth());
			
			if (b.getBorderSides()!=null) knockoutBorderSides(g, b, r);
		}

		//Reset clip
		g.setClip(null);
	}

	private void doClip(Graphics2D g, Element e) {
		g.setClip(null);
		if (!e.hasClippingElement()) return;
		Rectangle r = e.getClippingElement().getBoundingRectangle();
		g.setClip((int) r.x, (int) r.y, (int) r.width, (int) r.height);
	}
	
	public void drawShadow(Graphics2D g, Component c) {
		Rectangle r = c.getRealRec(c.getShadowRec());
		Color start = new Color(70, 70, 70, 255);
		Color end = new Color(Styles.bg.getRed(), Styles.bg.getGreen(), Styles.bg.getBlue(), 0);
		
		double size = 1;
		Rectangle r1 = new Rectangle(r.x-(r.width*(size/2)), r.y-(r.height*(size/2)), r.width*(size+1), r.height*(size+1));
		setGradientRadial(g, start, end, new float[]{0f, 1f}, r1);
		fillRect(g, r1);
	}
	
	private void drawRect(Graphics2D g, Color c, Rectangle r, double strokeW) {
		g.setStroke(new BasicStroke((float) strokeW));
		g.setColor(c);
		g.drawRect((int) r.x, (int) r.y, (int) r.width, (int) r.height);
	}
	
	private void knockoutBorderSides(Graphics2D g, SimpleBox b, Rectangle r) {
		if (b.getBorderSides().length==0) return;
		
		Color col = null;
		if (b.hasColor()) {
			col = b.getColor();
			col = new Color(col.getRed(), col.getGreen(), col.getBlue(), percToCol(b.getOpacity()));
			g.setColor(col);
		}
		/*
		* If this box does not have a color then need to find the next relevant background color
		* to mask in the boxes used to single out the borders required.
		* Search for all parents untill a SimpleBox with a color is found
		*/
		if (!b.hasColor()) {
			SimpleBox par = (SimpleBox) (b.getParentAssignableFrom(SimpleBox.class));
			
			while (par!=null) {
				if (par.hasColor()) {
					col = par.getColor();
					col = new Color(col.getRed(), col.getGreen(), col.getBlue(), percToCol(par.getOpacity()));
					g.setColor(col);
					break;
				}
				par = (SimpleBox) (par.getParentAssignableFrom(SimpleBox.class));
			}
			
			//Last resort
			if (col==null) g.setColor(Color.BLACK);
		}
		
		//Main blockers
		List<Integer> s = Arrays.stream(b.getBorderSides()).boxed().collect(Collectors.toList());
		if (!s.contains(1)) g.fillRect((int) (r.x-b.getBorderWidth()), (int) (r.y+b.getBorderWidth()), (int) (r.width*0.2), (int) (r.height-b.getBorderWidth()*2));
		if (!s.contains(2)) g.fillRect((int) (r.x+b.getBorderWidth()), (int) (r.y+r.height*0.8), (int) (r.width-b.getBorderWidth()*2), (int) (r.height*0.2+b.getBorderWidth()));
		if (!s.contains(3)) g.fillRect((int) (r.x+r.width*0.8), (int) (r.y+b.getBorderWidth()), (int) (r.width*0.2+b.getBorderWidth()), (int) (r.height-b.getBorderWidth()*2));
		if (!s.contains(4)) g.fillRect((int) (r.x+b.getBorderWidth()), (int) (r.y-b.getBorderWidth()), (int) (r.width-b.getBorderWidth()*2), (int) (r.height*0.2));
		
		//Cut out little dots left in corners when more than one corner is blocked
		if (!s.contains(1)&&!s.contains(4)) g.fillRect((int) (r.x-b.getBorderWidth()), (int) (r.y-b.getBorderWidth()), (int) (r.width*0.1), (int) (r.height*0.1));
		if (!s.contains(1)&&!s.contains(2)) g.fillRect((int) (r.x-b.getBorderWidth()), (int) (r.y+r.height*0.9), (int) (r.width*0.1), (int) (r.height*0.1+b.getBorderWidth()));
		if (!s.contains(2)&&!s.contains(3)) g.fillRect((int) (r.x+r.width*0.9), (int) (r.y+r.height*0.9), (int) (r.width*0.1+b.getBorderWidth()), (int) (r.height*0.1+b.getBorderWidth()));
		if (!s.contains(3)&&!s.contains(4)) g.fillRect((int) (r.x+r.width*0.9), (int) (r.y-b.getBorderWidth()), (int) (r.width*0.1+b.getBorderWidth()), (int) (r.height*0.1));
	}
	
	public void fillRect(Graphics2D g, Color c, Rectangle r) {
		g.setColor(c);
		g.fillRect((int) r.x, (int) r.y, (int) r.width, (int) r.height);
	}
	
	private void fillRect(Graphics2D g, Rectangle r) {
		g.fillRect((int) r.x, (int) r.y, (int) r.width, (int) r.height);
	}
	
	private void drawRoundRect(Graphics2D g, Color c, Rectangle r, double strokeW, int arcSize) {
		g.setStroke(new BasicStroke((float) strokeW));
		g.setColor(c);
		g.drawRoundRect((int) r.x, (int) r.y, (int) r.width, (int) r.height, arcSize, arcSize);
	}
	
	private void drawRoundRect(Graphics2D g, Color c, Color knockoutCol, Rectangle r, double strokeW, int[] corners, int arcSize) {
		g.setStroke(new BasicStroke((float) strokeW));
		g.setColor(c);
		g.drawRoundRect((int) r.x, (int) r.y, (int) r.width, (int) r.height, arcSize, arcSize);
		
		if (corners.length==0) return;
		List<Integer> cor = Arrays.stream(corners).boxed().collect(Collectors.toList());
		
		/*
		* If a corner is not present then fill out the rounded edge. Corners
		* go in anti-clockwise order with 1 being top left and 4 being top right.
		*/
		if (!cor.contains(1)) {
			g.setColor(knockoutCol);
			g.fillRect((int) (r.x-strokeW), (int) (r.y-strokeW), (int) (r.width*0.2), (int) (r.height*0.2));
			g.setColor(c);
			g.drawLine((int) r.x, (int) r.y, (int) r.x, (int) (r.y+r.height/2));
			g.drawLine((int) r.x, (int) r.y, (int) (r.x+r.width/2), (int) r.y);
		}
		if (!cor.contains(2)) {
			g.setColor(knockoutCol);
			g.fillRect((int) (r.x-strokeW), (int) (r.y+r.height*0.8+strokeW), (int) (r.width*0.2), (int) (r.height*0.2));
			g.setColor(c);
			g.drawLine((int) r.x, (int) (r.y+r.height/2), (int) r.x, (int) (r.y+r.height));
			g.drawLine((int) r.x, (int) (r.y+r.height), (int) (r.x+r.width/2), (int) (r.y+r.height));
		}
		if (!cor.contains(3)) {
			g.setColor(knockoutCol);
			g.fillRect((int) (r.x+r.width*0.8+strokeW), (int) (r.y+r.height*0.8+strokeW), (int) (r.width*0.2), (int) (r.height*0.2));
			g.setColor(c);
			g.drawLine((int) (r.x+r.width/2), (int) (r.y+r.height), (int) (r.x+r.width), (int) (r.y+r.height));
			g.drawLine((int) (r.x+r.width), (int) (r.y+r.height), (int) (r.x+r.width), (int) (r.y+r.height/2));
		}
		if (!cor.contains(4)) {
			g.setColor(knockoutCol);
			g.fillRect((int) (r.x+r.width*0.8+strokeW), (int) (r.y), (int) (r.width*0.2-strokeW), (int) (r.height*0.2));
			g.setColor(c);
			g.drawLine((int) (r.x+r.width/2), (int) r.y, (int) (r.x+r.width), (int) r.y);
			g.drawLine((int) (r.x+r.width), (int) r.y, (int) (r.x+r.width), (int) (r.y+r.height/2));
		}
	}
	
	public void fillRoundRect(Graphics2D g, Color c, Rectangle r, int arcSize) {
		g.setColor(c);
		g.fillRoundRect((int) r.x, (int) r.y, (int) r.width, (int) r.height, arcSize, arcSize);
	}
	
	private void fillRoundRect(Graphics2D g, Color c, Rectangle r, int[] corners, int arcSize) {
		g.setColor(c);
		g.fillRoundRect((int) r.x, (int) r.y, (int) r.width, (int) r.height, arcSize, arcSize);
		
		if (corners.length==0) return;
		List<Integer> cor = Arrays.stream(corners).boxed().collect(Collectors.toList());
		
		/*
		* If a corner is not present then fill out the rounded edge. Corners
		* go in anti-clockwise order with 1 being top left and 4 being top right.
		*/
		if (!cor.contains(1)) g.fillRect((int) r.x, (int) r.y, (int) (r.width*0.2), (int) (r.height*0.2));
		if (!cor.contains(2)) g.fillRect((int) r.x, (int) (r.y+r.height*0.8), (int) (r.width*0.2), (int) (r.height*0.2));
		if (!cor.contains(3)) g.fillRect((int) (r.x+r.width*0.8), (int) (r.y+r.height*0.8), (int) (r.width*0.2), (int) (r.height*0.2));
		if (!cor.contains(4)) g.fillRect((int) (r.x+r.width*0.8), (int) (r.y), (int) (r.width*0.2), (int) (r.height*0.2));
	}
	
	private void drawOval(Graphics2D g, Color c, Rectangle r, double strokeW) {
		g.setStroke(new BasicStroke((float) strokeW));
		g.setColor(c);
		g.drawOval((int) r.x, (int) r.y, (int) r.width, (int) r.height);
	}
	
	private void fillOval(Graphics2D g, Color c, Rectangle r) {
		g.setColor(c);
		g.fillOval((int) r.x, (int) r.y, (int) r.width, (int) r.height);
	}
	
	public void drawLine(Graphics2D g, Color c, Point p1, Point p2) {
		g.setColor(c);
		g.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
	}
	
	public void drawCenteredString(Graphics2D g, Font f, String s, Color c, Rectangle r) {
		FontMetrics metrics = g.getFontMetrics(f);
		int x = (int) (r.x+(r.width-metrics.stringWidth(s))/2);
		int y = (int) (r.y+((r.height-metrics.getHeight())/2)+metrics.getAscent());
		g.setFont(f);
		g.setColor(c);
		g.drawString(s, x, y);
	}
	
	private void drawXCenteredString(Graphics2D g, Font f, String s, Color c, Rectangle r) {
		FontMetrics metrics = g.getFontMetrics(f);
		int x = (int) (r.x+(r.width-metrics.stringWidth(s))/2);
		int y = (int) (r.y+(-metrics.getHeight()/2))+metrics.getAscent();
		g.setFont(f);
		g.setColor(c);
		g.drawString(s, x, y);
	}
	
	private void drawYCenteredString(Graphics2D g, Font f, String s, Color c, Rectangle r) {
		FontMetrics metrics = g.getFontMetrics(f);
		int y = (int) (r.y+((r.height-metrics.getHeight())/2)+metrics.getAscent());
		g.setFont(f);
		g.setColor(c);
		g.drawString(s, (int) r.x, y);
	}
	
	public void drawStringFromPoint(Graphics2D g, Font f, String s, Color c, Point p) {
		FontMetrics metrics = g.getFontMetrics(f);
		int y = (int) (p.y+(-metrics.getHeight()/2))+metrics.getAscent();
		g.setFont(f);
		g.setColor(c);
		g.drawString(s, (int) p.x, y);
	}
	
	/**
	* Finds the max height of a font that can fit in a specified height
	* @param f
	* @param height
	* @return
	*/
	public Font getMaxFontForHeight(Font f, int maxHeight) {
		String text = "Lorem Ipsum"; // Dummy text
		JLabel label = new JLabel(text);
		
		// Binary search for maximum font size
		int low = 1;
		int high = maxHeight;
		int maxFontSize = 0;
		
		while (low <= high) {
			int mid = (low + high) / 2;
			Font newFont = new Font(f.getName(), f.getStyle(), mid);
			label.setFont(newFont);
			int height = label.getPreferredSize().height;
			if (height > maxHeight) high = mid - 1;
			else {
				maxFontSize = mid;
				low = mid + 1;
			}
		}
		return new Font(f.getName(), f.getStyle(), maxFontSize);
	}
	
	/**
	* Finds the max height of a font that can fit in a specified rectangle
	* @param f
	* @param height
	* @return
	*/
	public Font getMaxFontForRect(Font f, Rectangle r, String text) {
		int maxWidth = (int) r.width;
		int maxHeight = (int) r.height;
		JLabel label = new JLabel(text);
		
		// Binary search for maximum font size
		int low = 1;
		int high = maxWidth;
		if (maxHeight>maxWidth) high = maxHeight;
		int maxFontSize = 0;
		
		while (low <= high) {
			int mid = (low + high) / 2;
			Font newFont = new Font(f.getName(), f.getStyle(), mid);
			label.setFont(newFont);
			Dimension size = label.getPreferredSize();
			if (size.width > maxWidth || size.height > maxHeight) high = mid - 1;
			else {
				maxFontSize = mid;
				low = mid + 1;
			}
		}
		return new Font(f.getName(), f.getStyle(), maxFontSize);
	}
	
	private void setGradientLinear(Graphics2D g, Color start, Color end, Rectangle gR) {
		GradientPaint gr = new GradientPaint((int) gR.x, (int) gR.y, start, (int) gR.width, (int) gR.height, end);
		g.setPaint(gr);
	}
	
	private void setGradientRadial(Graphics2D g, Color start, Color end, float[] fracts, Rectangle gR) {
		Rectangle2D r = new Rectangle2D.Double((int) gR.x, (int) gR.y, (int) gR.width, (int) gR.height);
		Color[] cols = {start, end};
		RadialGradientPaint gr = new RadialGradientPaint(r, fracts, cols, CycleMethod.NO_CYCLE);
		g.setPaint(gr);
	}
	
	private Color getGrad(Color start, Color end, double i, double total) {
		int r, g, b, a;
		r = (int) (start.getRed()+(((end.getRed()-start.getRed())/total)*i));
		g = (int) (start.getGreen()+(((end.getGreen()-start.getGreen())/total)*i));
		b = (int) (start.getBlue()+(((end.getBlue()-start.getBlue())/total)*i));
		a = (int) (start.getAlpha()+(((end.getAlpha()-start.getAlpha())/total)*i));
		
		if (r>255) r = 255; if (r<0) r = 0;
		if (g>255) g = 255; if (g<0) g = 0;
		if (b>255) b = 255; if (b<0) b = 0;
		if (a>255) a = 255; if (a<0) a = 0;
		return new Color(r, g, b, a);
	}
	
	private int percToCol(double p) {
		int c = (int) ((p/100)*255);
		if (c>255) c = 255;
		if (c<0) c = 0;
		return c;
	}
	
	/**
	* Scales a percentage of the screen width to an actual x point
	* @param p
	* @return
	*/
	public int cW(double p) {
		return (int) Math.round(screen.width*((double) p/100));
	}
	
	/**
	* Scales a percentage of the screen height to an actual y point
	* @param p
	* @return
	*/
	public int cH(double p) {
		return (int) Math.round(screen.height*((double) p/100));
	}
	
	/**
	* Scales an actual x point to a percentage of screen width
	* @param p
	* @return
	*/
	public double cWR(double p) {
		return (p/screen.width)*100;
	}
	
	/**
	* Scales an actual y point to a percentage of screen height
	* @param p
	* @return
	*/
	public double cHR(double p) {
		return (p/screen.height)*100;
	}
	
	/**
	* Takes a width percentage value, scales it to root and returns the equivilent percentage
	* value if for the height. Useful for making squares
	* @param c
	* @return realive height percentage
	*/
	public double rHP(Component c, double width, Unit u) {
		UnitRectangle o = new UnitRectangle();
		o.width = new UnitValue(width, u);
		Rectangle r = c.getRealRec(o);
		return cHR(r.width);
	}
	
	/**
	* Takes a UnitValue pair of real units and converts it and it's value to the new real Unit specified.
	* Will not work for percentage unit values.
	* @param p
	* @param u
	* @return
	*/
	public UnitValue translateRealUnitToRealUnit(UnitValue p, Unit u) {
		if (u.isRelative()) return null;
		UnitValue newP = new UnitValue();
		newP.u = u;
		
		switch (u) {
			case px:
			switch (p.u) {
				case px: newP.v = p.v; break;
				case vh: newP.v = cH(p.v); break;
				case vw: newP.v = cW(p.v); break;
			}
			break;
			case vh:
			switch (p.u) {
				case px: newP.v = cHR(p.v); break;
				case vh: newP.v = p.v; break;
				case vw: newP.v = cHR(cW(p.v)); break;
			}
			break;
			case vw:
			switch (p.u) {
				case px: newP.v = cWR(p.v); break;
				case vh: newP.v = cWR(cH(p.v)); break;
				case vw: newP.v = p.v; break;
			}
			break;
		}
		return newP;
	}
}
