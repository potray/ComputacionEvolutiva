package main;

import java.util.Random;

public class Main {

	public static void main(String[] args) {		
		
		long semilla = 845461;
		long semilla2 = System.currentTimeMillis();
		int tamPoblacion = 5;
		int maxIteraciones = 1;
		double probabilidadCruce = 0.7;
		Problema prob = new Problema ("src/datos/nug12.dat", tamPoblacion, semilla, maxIteraciones, probabilidadCruce);
		
		prob.resolver();
		
	}

}
