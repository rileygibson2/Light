package guipackage.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
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
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

import guipackage.cli.CLI;
import guipackage.general.Point;
import guipackage.general.Rectangle;
import guipackage.general.UnitRectangle;
import guipackage.general.UnitValue;
import guipackage.general.UnitValue.Unit;
import guipackage.general.Utils;
import guipackage.gui.components.Component;
import guipackage.gui.components.basecomponents.Image;
import guipackage.gui.components.basecomponents.Label;
import guipackage.gui.components.basecomponents.SimpleBox;

public class ScreenUtils {

	private Rectangle screen;

	public ScreenUtils(Rectangle screen) {
		this.screen = screen;
		loadFonts();
	}

	public void updateScreen(Rectangle screen) {this.screen = screen;}

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
		fillRect(g, GUI.bg, new Rectangle(0, 0, screen.width, screen.height));
	}

	public void drawLabel(Graphics2D g, Label l) {
		Rectangle r = l.getRealRec();
		Color col = new Color(l.col.getRed(), l.col.getGreen(), l.col.getBlue(), percToCol(l.getOpacity()));
		g.setFont(l.font);

		if (l.isTextXCentered()&&l.isTextYCentered()) drawCenteredString(g, l.font, l.getText(), col, r);
		else if (l.isTextXCentered()) drawXCenteredString(g, l.font, l.getText(), col, r);
		else if (l.isTextYCentered()) drawYCenteredString(g, l.font, l.getText(), col, r);
		else drawStringFromPoint(g, l.font, l.getText(), col, new Point(r.x, r.y));
	}

	public void drawImage(Graphics2D g, Image i) {
		Rectangle r = i.getRealRec();

		BufferedImage img = null;
		try {img = ImageIO.read(Utils.getURL("assets/"+i.src));}
		catch (IOException | IllegalArgumentException e) {
			CLI.error("ImageIO failed for assets/"+i.src);
			return;
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

		//Reset alpha
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
	}

	public void drawSimpleBox(Graphics2D g, SimpleBox b) {
		Rectangle r = b.getRealRec();

		if (b.isFilled()) {
			Color col = b.getColor();
			col = new Color(col.getRed(), col.getGreen(), col.getBlue(), percToCol(b.getOpacity()));
			if (b.getColor().getAlpha()==0) return; //Duck tape fix as this method does net respect the boxe's alpha channel

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
			else if (b.isRounded()) drawRoundRect(g, b.getBorderColor(), r, b.getBorderWidth(), b.getArcSize());
			else drawRect(g, b.getBorderColor(), r, b.getBorderWidth());
		}
	}

	public void drawShadow(Graphics2D g, Component c) {
		Rectangle r = c.getRealRec(c.getShadowRec());
		Color start = new Color(70, 70, 70, 255);
		Color end = new Color(GUI.bg.getRed(), GUI.bg.getGreen(), GUI.bg.getBlue(), 0);

		double size = 1;
		Rectangle r1 = new Rectangle(r.x-(r.width*(size/2)), r.y-(r.height*(size/2)), r.width*(size+1), r.height*(size+1));
		setGradientRadial(g, start, end, new float[]{0f, 1f}, r1);
		fillRect(g, r1);
	}

	public void drawRect(Graphics2D g, Color c, Rectangle r, double strokeW) {
		g.setStroke(new BasicStroke((float) strokeW));
		g.setColor(c);
		g.drawRect((int) r.x, (int) r.y, (int) r.width, (int) r.height);
	}

	public void fillRect(Graphics2D g, Color c, Rectangle r) {
		g.setColor(c);
		g.fillRect((int) r.x, (int) r.y, (int) r.width, (int) r.height);
	}

	public void fillRect(Graphics2D g, Rectangle r) {
		g.fillRect((int) r.x, (int) r.y, (int) r.width, (int) r.height);
	}

	public void drawRoundRect(Graphics2D g, Color c, Rectangle r, double strokeW, int arcSize) {
		g.setStroke(new BasicStroke((float) strokeW));
		g.setColor(c);
		g.drawRoundRect((int) r.x, (int) r.y, (int) r.width, (int) r.height, arcSize, arcSize);
	}

	public void fillRoundRect(Graphics2D g, Color c, Rectangle r, int arcSize) {
		g.setColor(c);
		g.fillRoundRect((int) r.x, (int) r.y, (int) r.width, (int) r.height, arcSize, arcSize);
	}

	public void fillRoundRect(Graphics2D g, Color c, Rectangle r, int[] corners, int arcSize) {
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

	public void drawOval(Graphics2D g, Color c, Rectangle r, double strokeW) {
		g.setStroke(new BasicStroke((float) strokeW));
		g.setColor(c);
		g.drawOval((int) r.x, (int) r.y, (int) r.width, (int) r.height);
	}

	public void fillOval(Graphics2D g, Color c, Rectangle r) {
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

	public void drawXCenteredString(Graphics2D g, Font f, String s, Color c, Rectangle r) {
		FontMetrics metrics = g.getFontMetrics(f);
		int x = (int) (r.x+(r.width-metrics.stringWidth(s))/2);
		int y = (int) (r.y+(-metrics.getHeight()/2))+metrics.getAscent();
		g.setFont(f);
		g.setColor(c);
		g.drawString(s, x, y);
	}

	public void drawYCenteredString(Graphics2D g, Font f, String s, Color c, Rectangle r) {
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
	public Font getMaxFontForHeight(Font f, Element e) {
		int maxHeight = (int) e.getRealRec().height;
		String text = "Lorem Ipsum"; // Dummy text
		JLabel label = new JLabel(text);

        // Binary search for maximum font size
        int low = 1;
        int hight = maxHeight;
   		int maxFontSize = 0;

        while (low <= hight) {
            int mid = (low + hight) / 2;
            Font newFont = new Font(f.getName(), f.getStyle(), mid);
            label.setFont(newFont);
            int height = label.getPreferredSize().height;
            if (height > maxHeight) {
                hight = mid - 1;
            } else {
                maxFontSize = mid;
                low = mid + 1;
            }
        }
		return new Font(f.getName(), f.getStyle(), maxFontSize);
	}

	public void setGradientLinear(Graphics2D g, Color start, Color end, Rectangle gR) {
		GradientPaint gr = new GradientPaint((int) gR.x, (int) gR.y, start, (int) gR.width, (int) gR.height, end);
		g.setPaint(gr);
	}

	public void setGradientRadial(Graphics2D g, Color start, Color end, float[] fracts, Rectangle gR) {
		Rectangle2D r = new Rectangle2D.Double((int) gR.x, (int) gR.y, (int) gR.width, (int) gR.height);
		Color[] cols = {start, end};
		RadialGradientPaint gr = new RadialGradientPaint(r, fracts, cols, CycleMethod.NO_CYCLE);
		g.setPaint(gr);
	}

	public Color getGrad(Color start, Color end, double i, double total) {
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

	public int percToCol(double p) {
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
