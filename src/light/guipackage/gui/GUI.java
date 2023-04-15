package light.guipackage.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import light.Pool;
import light.commands.commandline.CommandLine;
import light.encoders.Encoders;
import light.guipackage.cli.CLI;
import light.guipackage.dom.DOM;
import light.guipackage.general.Rectangle;
import light.guipackage.general.UnitPoint;
import light.guipackage.general.UnitRectangle;
import light.guipackage.general.UnitValue;
import light.guipackage.gui.components.Component;
import light.guipackage.gui.components.basecomponents.MessageBox;
import light.guipackage.gui.components.complexcomponents.CommandLineGUI;
import light.guipackage.gui.components.complexcomponents.EncodersGUI;
import light.guipackage.gui.components.complexcomponents.FixtureWindowGUI;
import light.guipackage.gui.components.complexcomponents.PoolGUI;
import light.guipackage.gui.components.complexcomponents.UDAGUI;
import light.uda.FixtureWindow;
import light.uda.UDA;
import light.uda.UDACapable;
import light.uda.guiinterfaces.GUIInterface;


public class GUI extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private Object parent; // The creator of this GUI
	private static GUI singleton;
	public static Rectangle screen;
	public static JFrame frame;
	
	private static ScreenUtils screenUtils;
	private IO io;
	
	private RootElement currentRoot;
	private static UDAGUI udaGUI;
	
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
		if (singleton==null) throw new Error("This GUI has not been initialised yet");
		return singleton;
	}
	
	public void setParent(Object parent) {this.parent = parent;}
	
	public GUIInterface addToGUI(Object o) {
		GUIInterface c = null;
		if (o instanceof CommandLine) {
			c = new CommandLineGUI(new UnitRectangle(5, 93, 80, 5));
			currentRoot.addComponent((Component) c);
		}
		else if (o instanceof UDA) {
			udaGUI = new UDAGUI(new UnitRectangle(5, 0, 95, 93), (UDA) o);
			c = udaGUI;
			currentRoot.addComponent((Component) c);
		}
		else { //UDA elements
			//Find size
			UDACapable oU = (UDACapable) o;
			UnitPoint cellDims = udaGUI.getCellDims();
			UnitRectangle r = new UnitRectangle();
			r.x = new UnitValue(udaGUI.getUDA().getCells(oU).x*cellDims.x.v, cellDims.x.u);
			r.y = new UnitValue(udaGUI.getUDA().getCells(oU).y*cellDims.y.v,cellDims.y.u);
			r.width = new UnitValue(udaGUI.getUDA().getCells(oU).width*cellDims.x.v, cellDims.x.u);
			r.height = new UnitValue(udaGUI.getUDA().getCells(oU).height*cellDims.y.v, cellDims.y.u);
			
			if (o instanceof Pool) c = new PoolGUI(r, (Pool<?>) o);
			if (o instanceof FixtureWindow) c = new FixtureWindowGUI(r, (FixtureWindow) o);
			if (o instanceof Encoders) c = new EncodersGUI(r);

			if (c!=null) udaGUI.addComponent((Component) c);
		}
		
		return c;
	}
	
	public static UDAGUI getUDAGUI() {return udaGUI;}
	
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
		
		public static void initialise(Object controller, Rectangle screen) {
			//Full screen check
			if (screen==null) {
				GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
				screen = new Rectangle(0, 0, device.getDisplayMode().getWidth()-120, device.getDisplayMode().getHeight()-65);
			}
			GUI.screen = screen;
			
			//Make GUI
			GUI.singleton = new GUI();
			GUI.singleton.setParent(controller);
			
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					//Initialise
					System.setProperty("apple.laf.useScreenMenuBar", "true");
					frame = new JFrame();
					
					GUI.singleton.setPreferredSize(new Dimension((int) GUI.screen.width, (int) GUI.screen.height));
					frame.getContentPane().add(GUI.singleton);
					
					//Label and build
					frame.setTitle("Light");
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					
					//Finish up
					frame.setVisible(true);
					frame.pack();
				}
			});
		}
	}
