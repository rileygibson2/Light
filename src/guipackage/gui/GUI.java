package guipackage.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

import guipackage.dom.DOM;
import guipackage.general.Rectangle;
import guipackage.gui.components.Component;
import guipackage.gui.components.basecomponents.MessageBox;
import guipackage.gui.components.complexcomponents.CommandLineGUI;
import guipackage.gui.components.complexcomponents.UDAGUI;
import guipackage.gui.components.complexcomponents.pools.PoolGUI;
import light.zones.Pool;
import light.zones.UDA;
import light.zones.Zone;
import light.zones.commandline.CommandLine;


public class GUI extends JPanel {

	private static final long serialVersionUID = 1L;
	private Object parent; // The creator of this GUI
	private static GUI singleton;
	public static Rectangle screen;
	public static JFrame frame;
	
	private static ScreenUtils screenUtils;
	private IO io;

	private Set<View> layouts;
	private View currentLayout;
	private Map<Zone, Component> zoneMappings;

	private List<MessageBox> messages;
	
	DOM dom;
	
	//Styles
	public final static Color bg = new Color(15, 15, 15);
	public final static Color fg = new Color(50, 50, 50);
	public final static Color focus = new Color(70, 70, 70);
	public final static Color focus2 = new Color(90, 90, 90);

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
		currentLayout = new View(new Rectangle(0, 0, 100, 100));
		zoneMappings = new HashMap<Zone, Component>();
		
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
	
	public void addZoneToView(Zone z, Object... extras) {
		Component c = null;
		if (z instanceof CommandLine) {
			c = new CommandLineGUI(new Rectangle(5, 93, 80, 5));
			currentLayout.addComponent(c);
		}
		else if (z instanceof UDA) {
			c = new UDAGUI(new Rectangle(5, 0, 80, 93), (UDA) z);
			currentLayout.addComponent(c);
		}
		else if (z instanceof Pool) {
			UDAGUI g = (UDAGUI) getGUIOfClass(UDA.class);
			c = new PoolGUI((Pool) z, (UDA) getZoneOfClass(UDA.class), g);
			g.addComponent(c);
		}

		if (c!=null) zoneMappings.put(z, c);
	}

	public Component getZoneGUIElement(Zone z) {return zoneMappings.get(z);}

	public Zone getZoneOfClass(Class c) {
		for (Zone z : zoneMappings.keySet()) {
			if (z.getClass().equals(c)) return z;
		}
		return null;
	}

	public Component getGUIOfClass(Class c) {
		for (Zone z : zoneMappings.keySet()) {
			if (z.getClass().equals(c)) return zoneMappings.get(z);
		}
		return null;
	}

	public void addMessage(String message, Color col) {
		//Find y position message should animate to
		double goalY = messages.size()*12.5+5;
		int hold = 2000;
		if (col.equals(MessageBox.error)) hold = 4000;
		
		MessageBox m = new MessageBox(message, col, goalY, hold);
		messages.add(m);
		currentLayout.addComponent(m);
	}
	
	public void removeMessage(MessageBox m) {
		messages.remove(m);
		
		//Update position of all other messages
//		for (int i=0; i<messages.size(); i++) {
//			messages.get(i).updateGoal((i*12.5)+5);
//		}
	}
	
	public static ScreenUtils getScreenUtils() {return screenUtils;}
	
	public View getView() {return currentLayout;}
	
	public void changeLayout(View v) { 
		if (v==null) return;
		
		if (currentLayout!=null) currentLayout.destroy();
		currentLayout = v;
		repaint();
	}
	
	public void setAntiAliasing(boolean a) {antiAlias = a;}
	
	public boolean getAntiAliasing() {return antiAlias;}
	
	@Override
	public void paintComponent(Graphics g) {
		if (antiAlias) ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		screenUtils.drawBase((Graphics2D) g);
		currentLayout.draw((Graphics2D) g);
		if (dom.visualiserVisible()) dom.update(getView());
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
