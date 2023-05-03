package light.guipackage.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import light.general.Submitter;
import light.guipackage.cli.CLI;
import light.guipackage.general.Pair;
import light.guipackage.general.Point;
import light.guipackage.threads.GUIThreadController;

public class IO implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
	
	static IO singleton;
	
	private Map<Object, Submitter<KeyEvent>> keyListeners;
	private Submitter<KeyEvent> overrideKeyListener; //Will force key events only to be sent to this submitter
	private Map<KeyEvent, Runnable> keyActions;
	private Point dragPoint;
	
	private static GUIThreadController paint;
	private static Instant paintRequested; //Time at which screen was last interacted with or a paint was requested
	private final static int paintTimeout = 2;
	
	private IO() {
		this.keyListeners = new HashMap<Object, Submitter<KeyEvent>>();
		this.keyActions = new HashMap<KeyEvent, Runnable>();
		dragPoint = null;
		startPaintThread();
	}
	
	public static IO getInstance() {
		if (singleton==null) singleton = new IO();
		return singleton;
	}
	
	public void registerKeyListener(Object listener, Submitter<KeyEvent> s) {keyListeners.put(listener, s);}
	public void deregisterKeyListener(Object listener) {keyListeners.remove(listener);}
	
	/**
	* Will force key events only to be sent to this submitter and block all other
	* registed key listeners from recieving key press events
	* This functionality is designed to be used temporarily and released quickly.
	* @param listener
	* @param s
	*/
	public void setOverrideKeyListener(Submitter<KeyEvent> s) {overrideKeyListener =  s;}
	public boolean hasOverrideKeyListener() {return overrideKeyListener!=null;}
	public void removeOverrideKeyListener() {overrideKeyListener = null;}
	
	public void registerKeyAction(KeyEvent e, Runnable r) {keyActions.put(e, r);}
	public void removeKeyAction(KeyEvent r) {keyActions.remove(r);}
	
	public void finishEvent() {
		requestPaint();
	}
	
	public void startPaintThread() {
		paint = new GUIThreadController() {
			@Override
			public void run() {
				while (isRunning()) {
					if (!paintExpired()) GUI.getInstance().repaint();
					iterate();
				}
			}
		};
		paint.setPaintOnIterate(false);
		paint.setWait(50);
		paint.start();
	}
	
	public void requestPaint() {paintRequested = Instant.now();}
	
	public boolean paintExpired() {
		if (paintRequested==null) return true;
		if ((Instant.now().getEpochSecond()-paintRequested.getEpochSecond())>=paintTimeout) {
			paintRequested = null;
			return true;
		}
		return false;
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		GUI.getInstance().getCurrentRoot().doMove(new Point(e.getX(), e.getY()));
		finishEvent();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (dragPoint==null) dragPoint = new Point(e.getX(), e.getY());
		GUI.getInstance().getCurrentRoot().doDrag(dragPoint, new Point(e.getX(), e.getY()));
		finishEvent();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		dragPoint = null;
		finishEvent();
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		GUI.getInstance().getCurrentRoot().doClick(new Point(e.getX(), e.getY()));
		finishEvent();
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		GUI.getInstance().getCurrentRoot().doScroll(new Point(e.getX(), e.getY()), e.getWheelRotation());
		finishEvent();
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		//Disperse key events to listeners
		if (hasOverrideKeyListener()) overrideKeyListener.submit(e);
		else {
			for (Submitter<KeyEvent> s : keyListeners.values()) s.submit(e);
		}
		
		/*if (e.getExtendedKeyCode()==KeyEvent.VK_C) {
			if (CLI.viewerActive()) CLI.showViewer(false);
			else CLI.showViewer(true);
		}
		if (e.getExtendedKeyCode()==KeyEvent.VK_D) {
			boolean show = true;
			if (GUI.getInstance().dom.visualiserVisible()) show = false;
			GUI.getInstance().dom.showVisualiser(show);
		}
		if (e.getExtendedKeyCode()==KeyEvent.VK_V) {
			CLI.setVerbose(!CLI.isVerbose());
			CLI.getViewer().repaint();
		}*/
		if (e.getExtendedKeyCode()==KeyEvent.VK_S) GUI.getInstance().scanDOM(GUI.getInstance().getCurrentRoot(), "");
		
		for (Map.Entry<KeyEvent, Runnable> m : keyActions.entrySet()) {
			if (e.getExtendedKeyCode()==m.getKey().getExtendedKeyCode()) m.getValue().run();
		} 
		finishEvent();
	}
	
	@Override
	public void mousePressed(MouseEvent e) {}
	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {}
	
	@Override
	public void keyPressed(KeyEvent e) {}
	
	@Override
	public void keyReleased(KeyEvent e) {}
	
}
