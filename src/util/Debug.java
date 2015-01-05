package util;

public class Debug {
	
	private static boolean debug = true;
	
	public static void setDebug (boolean d){
		debug = d;
	}
	
	//P es para cuando me pilla más a mano la mano izquierda, d para la derecha.
	public static void p(String s){
		if (debug)
			System.out.println(s);
	}
	
	public static void d(String s){
		p(s);
	}
}
