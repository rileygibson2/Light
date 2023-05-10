package light.general;

import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

import light.executors.Executor.ExecType;
import light.executors.ExecutorCapable;

public class Utils {
	
	public static InputStream getInputStream(String path) {
		return Utils.class.getClassLoader().getResourceAsStream(path);
	}
	
	public static URL getURL(String path) {
		return Utils.class.getClassLoader().getResource(path);
	}
	
	public static boolean validateDMX(double dmx) {return dmx>=0&&dmx<=255;}
	
	public static int castToDMX(double dmx) {
		return (int) (dmx<0 ? 0 : (dmx>255 ? 255 : dmx));
	}
	
	public static String capitaliseFirst(String s) {
		if (s.length()==0) return s;
		if (s.length()==1) return s.toUpperCase();
		return s.substring(0, 1).toUpperCase()+s.substring(1, s.length()).toLowerCase();
	}
	
	public static int hashString(String s) {
		final int prime = 31;
		int result = 1;
		for (char c : s.toCharArray()) result = prime * result + c;
		return result;
	}
	
	public static byte[] combineByteArrays(byte[]... args) {
		int l = 0;
		for (byte[] b : args) l += b.length;
		
		ByteBuffer buff = ByteBuffer.allocate(l);
		for (byte[] b : args) buff.put(b);
		return buff.array();
	}
	
	public static byte[] generateByteArray(byte... args) {
		byte[] result = new byte[args.length];
		for (int i=0; i<result.length; i++) result[i] = args[i];
		return result; //?? just return args maybe
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
