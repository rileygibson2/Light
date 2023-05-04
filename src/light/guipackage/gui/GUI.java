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

import light.Light;
import light.Pool;
import light.commands.commandcontrol.CommandLine;
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
import light.guipackage.gui.components.complexcomponents.ControlBarGUI;
import light.guipackage.gui.components.complexcomponents.EncodersGUI;
import light.guipackage.gui.components.complexcomponents.FixtureWindowGUI;
import light.guipackage.gui.components.complexcomponents.KeyWindowGUI;
import light.guipackage.gui.components.complexcomponents.PoolGUI;
import light.guipackage.gui.components.complexcomponents.TemporaryGUIInteractions;
import light.guipackage.gui.components.complexcomponents.UDAGUI;
import light.guipackage.gui.components.complexcomponents.ViewGUI;
import light.stores.View;
import light.uda.FixtureWindow;
import light.uda.KeyWindow;
import light.uda.UDA;
import light.uda.UDACapable;
import light.uda.guiinterfaces.GUIInterface;
import light.uda.guiinterfaces.TemporyGUIInteractionsInterface;


public class GUI extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private Object creator; // The creator of this GUI
	private static GUI singleton;
	public static Rectangle screen;
	public static JFrame frame;
	
	private static ScreenUtils screenUtils;
	private IO io;
	
	private RootElement currentRoot;
	private static UDAGUI udaGUI;
	
	private List<MessageBox> messages;
	
	DOM dom;
	
	private boolean antiAlias;
	
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
	
	public void setCreator(Object creator) {this.creator = creator;}
	
	public Object getCreator() {return creator;}
	
	public static UDAGUI getUDAGUI() {return udaGUI;}
	
	public GUIInterface addStaticElementToGUI(Object o) {
		GUIInterface inter = null;
		
		if (o==Light.class) inter = new ControlBarGUI(new UnitRectangle(0, 1, 4.5, 97));
		else if (o==CommandLine.class) inter = new CommandLineGUI(new UnitRectangle(5, 93, 80, 5));
		//Special case for View screen as this does not have a controlling class but is a normal pool, so View class is used as the signifier
		else if (o==View.class) inter = new ViewGUI(new UnitRectangle(95, 0, 5, 100));
		else if (o instanceof UDA) {
			/*
			* UDA is passed as constructor param to prevent stack overflow, as UDA is not finished
			* instantiating when the UDAGUI is created so UDA.getInstance() cannot be called, and
			* UDAGUI constructor needs to access UDA methods.
			*/
			udaGUI = new UDAGUI(new UnitRectangle(5, 0, 90, 93), (UDA) o);
			inter = udaGUI;
		}
		
		currentRoot.addComponent((Component) inter);
		return inter;
	}
	
	public GUIInterface addUDAElementToGUI(UDACapable o, Rectangle udaCells) {
		if (udaGUI==null) return null;
		GUIInterface inter = null;
		
		//Find size
		UnitPoint cellDims = udaGUI.getCellDimensions(); //Dimensions of a cell
		UnitRectangle r = new UnitRectangle();
		r.x = new UnitValue(udaCells.x*cellDims.x.v, cellDims.x.u);
		r.y = new UnitValue(udaCells.y*cellDims.y.v,cellDims.y.u);
		r.width = new UnitValue(udaCells.width*cellDims.x.v, cellDims.x.u);
		r.height = new UnitValue(udaCells.height*cellDims.y.v, cellDims.y.u);
		
		if (o instanceof Pool) inter = new PoolGUI(r, (Pool<?>) o);
		if (o instanceof Encoders) inter = new EncodersGUI(r);
		if (o instanceof FixtureWindow) inter = new FixtureWindowGUI(r, (FixtureWindow) o);
		if (o instanceof KeyWindow) inter = new KeyWindowGUI(r, (KeyWindow) o);
		
		if (inter!=null) udaGUI.addComponent((Component) inter);
		return inter;
	}
	
	public void clearUDAGUI() {
		udaGUI.clear();
	}
	
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

		public TemporyGUIInteractionsInterface getTemporyActionsImplementation() {return new TemporaryGUIInteractions();}
		
		@Override
		public void paintComponent(Graphics g) {
			if (antiAlias) ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			screenUtils.drawBase((Graphics2D) g);
			currentRoot.draw((Graphics2D) g);
			if (dom.visualiserVisible()) dom.update(getCurrentRoot());
		}
		
		public static void initialise(Object creator, Rectangle screen) {
			//Full screen check
			if (screen==null) {
				GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
				screen = new Rectangle(0, 0, device.getDisplayMode().getWidth()-120, device.getDisplayMode().getHeight()-65);
			}
			GUI.screen = screen;
			
			//Make GUI
			GUI.singleton = new GUI();
			GUI.singleton.setCreator(creator);
			
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
