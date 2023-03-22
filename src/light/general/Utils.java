package light.general;

import java.io.InputStream;
import java.net.URL;

import light.executors.Executor.ExecType;
import light.executors.ExecutorCapable;

public class Utils {
    
    public static InputStream getInputStream(String path) {
		return Utils.class.getClassLoader().getResourceAsStream(path);
	}
	
	public static URL getURL(String path) {
		return Utils.class.getClassLoader().getResource(path);
	}

    public void runAction(ExecType action, ExecutorCapable element) {
        switch (action) {
			case DoubleRate:
				break;
			case DoubleSpeed:
				break;
			case Flash:
				break;
			case FlashOff:
				break;
			case FlashOn:
				break;
			case Go:
				break;
			case GoBack:
				break;
			case HalfRate:
				break;
			case HalfSpeed:
				break;
			case Master:
				break;
			case Off:
				break;
			case On:
				break;
			case Pause:
				break;
			case Swop:
				break;
			case Temp:
				break;
			case Toggle:
				break;
			default:
				break;
        }
    }
}
