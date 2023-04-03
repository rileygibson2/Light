package light.general;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ThreadController extends Thread {

	private boolean stop;
	private boolean hasStarted; //Stops same thread attempting to run twice and throwing an error
	private int initialDelay;
	private int wait = 20; //Wait for each iteration

	private Object target; //Target object of animation
	protected List<Object> extras; //A list of extra parameters that can be passed in beyond the target if needed
	protected Set<Object> elements; //Generic list of thread created elements a thread can use
	private int i; //Thread clock
	
	private Runnable finishAction;

	public ThreadController() {
		this.stop = false;
		this.hasStarted = false;
		initialDelay = 0;
	}

	@Override
	public void start() {
		if (!hasStarted) {
			hasStarted = true;
			i = 0;
			super.start();
		}
	}
	
	//Internal use only
	
	protected boolean isRunning() {return !stop;} //To determine if it should continue to loop
	 
	//External use
	
	public boolean hasEnded() {return !this.isAlive();}
	
	public boolean isDoomed() {return stop&&!hasEnded();}

	public void setWait(int wait) {this.wait = wait;}
	
	public void setInitialDelay(int d) {initialDelay = d;}

	public boolean hasElements() {return elements!=null;}

	public Set<?> getElements() {return Collections.unmodifiableSet(elements);}

	public Object getTarget() {return target;}
	
	public void setTarget(Object t) {target = t;}
	
	public void setExtras(List<Object> extras) {this.extras = extras;}
	
	public int getIncrement() {return i;}
	
	public void setFinishAction(Runnable r) {finishAction = r;}

	public void doInitialDelay() {
		if (initialDelay>0) {
			try {Thread.sleep(initialDelay);}
			catch (InterruptedException e) {e.printStackTrace();}
		}
	}
	
	public void iterate() {
		i++;
		sleep(wait);
	}

	public void end() {stop = true;}

	/**
	 * End thread and run finish action if applicable
	 */
	protected void finish() {
		end();
		if (finishAction!=null) finishAction.run();
	}
	
	public void sleep(int wait) {
		try {Thread.sleep(wait);}
		catch (InterruptedException e) {e.printStackTrace();}
	}
}
