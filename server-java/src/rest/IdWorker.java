package rest;

public class IdWorker {
	private static int index = 0;
	public static String getId(){
		++IdWorker.index;
		return new Integer(IdWorker.index).toString();
	}
}
