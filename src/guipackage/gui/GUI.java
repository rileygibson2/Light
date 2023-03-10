package guipackage.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

import guipackage.cli.CLI;
import guipackage.dom.DOM;
import guipackage.general.Pair;
import guipackage.general.Rectangle;
import guipackage.general.UnitRectangle;
import guipackage.gui.components.Component;
import guipackage.gui.components.basecomponents.MessageBox;
import guipackage.gui.components.complexcomponents.CommandLineGUI;
import guipackage.gui.components.complexcomponents.PoolGUI;
import guipackage.gui.components.complexcomponents.UDAGUI;
import light.Pool;
import light.stores.Preset;
import light.uda.UDA;
import light.uda.UDAZone;
import light.uda.commandline.CommandLine;
import light.uda.guiinterfaces.GUIInterface;


public class GUI extends JPanel {

	private static final long serialVersionUID = 1L;
	private Object parent; // The creator of this GUI
	private static GUI singleton;
	public static Rectangle screen;
	public static JFrame frame;
	
	private static ScreenUtils screenUtils;
	private IO io;

	private Set<Element> roots; //Roots of all the views loaded
	private RootElement currentRoot;
	private static Pair<UDAGUI, UDA> uda;

	private List<MessageBox> messages;
	
	DOM dom;
	
	//Styles
	public final static Color bg = new Color(15, 15, 15);
	public final static Color fg = new Color(50, 50, 50);
	public final static Color focus = new Color(70, 70, 70);
	public final static Color focus2 = new Color(90, 90, 90);

	public final static Color focusOrange = new Color(245, 185, 66);

	public final static Color textMain = new Color(220, 220, 220);
	public final static Color textDull = new Color(100, 100, 100);

	private boolean antiAlias;
	
	public final static String baseFont = "Geneva";
	public final static String logoFont = "seuzone";

	private GUI() {
		io = IO.getInstance();
		dom = new DOM();
		screenUtils = new ScreenUtils(screen);
		messages = new ArrayList<MessageBox>();
		antiAlias = false;
		currentRoot = new RootElement();
		
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		addMouseListener(io);
		addMouseMotionListener(io);
		addMouseWheelListener(io);
		addKeyListener(io);
		repaint();
	}
	
	public static GUI getInstance() {
		if (singleton==null) singleton = new GUI();
		return singleton;
	}

	public void setParent(Object parent) {this.parent = parent;}
	
	public GUIInterface addToGUI(Object o, Object... extras) {
		GUIInterface c = null;
		if (o instanceof CommandLine) {
			c = new CommandLineGUI(new UnitRectangle(5, 93, 80, 5));
			currentRoot.addComponent((Component) c);
		}
		else if (o instanceof UDA) {
			c = new UDAGUI(new UnitRectangle(5, 0, 95, 93), (UDA) o);
			currentRoot.addComponent((Component) c);
			uda = new Pair<UDAGUI, UDA>((UDAGUI) c, (UDA) o);
		}
		else if (o instanceof UDAZone) {
			Object zone = ((UDAZone<?>) o).getZone();
			if (zone instanceof Pool) {
				c = new PoolGUI((UDAZone<Pool<?>>) o);
				uda.a.addComponent((Component) c);
			}
		}

		return c;
	}

	public static Pair<UDAGUI, UDA> getUDAPair() {return uda;}

	public void addMessage(String message, Color col) {
		//Find y position message should animate to
		double goalY = messages.size()*12.5+5;
		int hold = 2000;
		if (col.equals(MessageBox.error)) hold = 4000;
		
		MessageBox m = new MessageBox(message, col, goalY, hold);
		messages.add(m);
		currentRoot.addComponent(m);
	}
	
	public void removeMessage(MessageBox m) {
		messages.remove(m);
		
		//Update position of all other messages
//		for (int i=0; i<messages.size(); i++) {
//			messages.get(i).updateGoal((i*12.5)+5);
//		}
	}
	
	public static ScreenUtils getScreenUtils() {return screenUtils;}
	
	public Element getCurrentRoot() {return currentRoot;}
	
	public void changeRoot(RootElement root) { 
		if (root==null) return;
		currentRoot = root;
	}
	
	public void setAntiAliasing(boolean a) {antiAlias = a;}
	
	public boolean getAntiAliasing() {return antiAlias;}

	public void scanDOM(Element e, String indent) {
		if (indent.equals("")) {
			CLI.debug("Scanning DOM:");
			CLI.debug("Screen Dims: "+screen.toString());
		}
		CLI.debug(indent+e.toString());
		for (Component c : e.getComponents()) scanDOM(c, indent+" - ");
	}
	
	@Override
	public void paintComponent(Graphics g) {
		if (antiAlias) ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		screenUtils.drawBase((Graphics2D) g);
		currentRoot.draw((Graphics2D) g);
		if (dom.visualiserVisible()) dom.update(getCurrentRoot());
	}

	public static GUI initialise(Object controller, Rectangle screen) {
		//Full screen check
		if (screen==null) {
			GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			screen = new Rectangle(0, 0, device.getDisplayMode().getWidth()-120, device.getDisplayMode().getHeight()-65);
		}
		GUI.screen = screen;

		//Make GUI
		GUI panel = GUI.getInstance();
		panel.setParent(controller);

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Initialise
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				frame = new JFrame();
				
				panel.setPreferredSize(new Dimension((int) GUI.screen.width, (int) GUI.screen.height));
				frame.getContentPane().add(panel);

				//Label and build
				frame.setTitle("Campfire");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				//Finish up
				frame.setVisible(true);
				frame.pack();
			}
		});
		return panel;
	}
}
