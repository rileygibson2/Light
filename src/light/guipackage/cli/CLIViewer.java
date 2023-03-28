package light.guipackage.cli;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.swing.JFrame;
import javax.swing.JPanel;

import light.guipackage.general.Point;
import light.guipackage.general.Rectangle;
import light.guipackage.gui.ScreenUtils;

public class CLIViewer extends JPanel implements KeyListener, MouseWheelListener {

	private static final long serialVersionUID = 8826256830984099915L;
	protected static JFrame frame;
	private static Rectangle screen = new Rectangle(0, 0, 700, 400);

	private ScreenUtils sU;
	private boolean isActive;
	Deque<CLIMessage> stack;

	//Adjustments for scroll
	double xAdj;
	double yAdj;
	double maxWidth; //Width of longest message used for scroll

	protected CLIViewer() {
		sU = new ScreenUtils(screen);
		isActive = false;
		stack = new ArrayDeque<CLIMessage>();
		xAdj = 1;
		yAdj = 0;
		maxWidth = 0;

		addMouseWheelListener(this);
	}

	protected boolean isActive() {return isActive;}

	protected void setActive(boolean a) {
		isActive = a;
		frame.setVisible(a);
	}

	protected void print(CLIMessage m) {
		stack.addLast(m);
		if (isActive) repaint();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		yAdj += e.getPreciseWheelRotation();
		repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getExtendedKeyCode()==KeyEvent.VK_LEFT) xAdj += 25;
		if (e.getExtendedKeyCode()==KeyEvent.VK_RIGHT) xAdj -= 25;
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		Font f = new Font("Geneva", Font.ROMAN_BASELINE, 12);
		sU.fillRect(g2, new Color(0, 0, 0), new Rectangle(sU.cW(screen.x), sU.cH(screen.y), sU.cW(screen.width), sU.cH(screen.height))); //Base

		//Account for messages overflowing screen
		double y = 2;
		double totalY = sU.getStringHeightAsPerc(f, "Hello World")*stack.size();
		if (totalY>100) y -= (totalY-100);

		//Verify start positions
		if (maxWidth>100) {
			if (xAdj<-(maxWidth-100)) xAdj = -(maxWidth-100);
		}
		else xAdj = 1;
		if (xAdj>1) xAdj = 1;

		if (totalY<100) yAdj = 0;
		else {
			if (yAdj<-(totalY-100)) yAdj = -(totalY-100);
		}
		if (yAdj>0) yAdj = 0;

		y -= yAdj;

		for (CLIMessage m : stack) {
			if (y>=100) return;
			String header = m.formatHeader();
			double hP = sU.getStringWidthAsPerc(f, header);

			//Update maxWidth
			double w = sU.getStringWidthAsPerc(f, header+ " "+m.message);
			if (w>maxWidth) maxWidth = w;

			sU.drawStringFromPoint(g2, f, header, m.color, new Point(sU.cW(xAdj), sU.cH(y)));
			sU.drawStringFromPoint(g2, f, "  "+m.message, Color.GREEN, new Point(sU.cW(hP+xAdj), sU.cH(y)));

			y += sU.getStringHeightAsPerc(f, header);
		}

		//Verbose symbol
		if (CLI.isVerbose()) {
			double w = sU.getStringWidthAsPerc(f, "Verbose")+3;
			double h = sU.getStringHeightAsPerc(f, "Verbose")+3;
			sU.fillRoundRect(g2, new Color(255, 80, 80), new Rectangle(sU.cW(100-w-5), sU.cH(2), sU.cW(w), sU.cH(h)), 10);
			sU.drawCenteredString(g2, f, "Verbose", Color.WHITE, new Rectangle(sU.cW(100-w-5), sU.cH(2), sU.cW(w), sU.cH(h)));
		}
	}

	protected static CLIViewer initialise() {
		CLIViewer panel = new CLIViewer();
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				//Initialise
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				frame = new JFrame();
				panel.setPreferredSize(new Dimension((int) screen.width, (int) screen.height));
				frame.getContentPane().add(panel);
				frame.setBounds(300, 350, (int) screen.width, (int) screen.height);

				//Label and build
				frame.setTitle("CLI");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.addKeyListener(panel);

				//Finish up
				frame.setFocusable(true);
				frame.setVisible(false);
				frame.pack();
			}
		});
		return panel;
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyReleased(KeyEvent e) {}
}
