package guipackage.gui;

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

import guipackage.cli.CLI;
import guipackage.general.Point;
import guipackage.general.Submitter;
import guipackage.threads.ThreadController;

public class IO implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {
	
	static IO singleton;
	
	private Map<Object, Submitter<KeyEvent>> keyListeners;
	private Map<KeyEvent, Runnable> keyActions;
	private Point dragPoint;
	
	private static ThreadController paint;
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

	public void registerKeyAction(KeyEvent e, Runnable r) {keyActions.put(e, r);}

	public void removeKeyAction(KeyEvent r) {keyActions.remove(r);}
	
	public void finishEvent() {
		requestPaint();
	}
	
	public void startPaintThread() {
		paint = new ThreadController() {
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
		GUI.getInstance().getView().doMove(new Point(e.getX(), e.getY()));
		finishEvent();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (dragPoint==null) dragPoint = new Point(e.getX(), e.getY());
		GUI.getInstance().getView().doDrag(dragPoint, new Point(e.getX(), e.getY()));
		finishEvent();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		dragPoint = null;
		finishEvent();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		GUI.getInstance().getView().doClick(new Point(e.getX(), e.getY()));
		finishEvent();
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		GUI.getInstance().getView().doScroll(new Point(e.getX(), e.getY()), e.getWheelRotation());
		finishEvent();
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		for (Submitter<KeyEvent> s : keyListeners.values()) s.submit(e);

		if (e.getExtendedKeyCode()==KeyEvent.VK_C) {
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
		}

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
