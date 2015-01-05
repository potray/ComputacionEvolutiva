package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Time;
import java.util.Random;

public class Main {		
	
	private static long semilla = 845461;
	private static long semilla2 = System.currentTimeMillis();
	private static int tamPoblacion = 50;
	private static int maxIteraciones = 1000;
	private static int maxIteracionesSinMejora = 20;
	private static double probabilidadCruce = 0.8;
	private static double probabilidadMutacion = 1.0/tamPoblacion;
	private static int elitismo = 1;

	public static void main(String[] args) {		
		PrintWriter pw;
		try {
			pw = new PrintWriter(new File("experimentos3.csv"));		
			pw.println("Experimento\tParm�metro\tMejor individuo\tTiempo\tIteraciones");
			
			//Probar tama�o de la poblaci�n
			pw.println("Experimentando con la poblaci�n");
			for (tamPoblacion = 10; tamPoblacion <= 200; tamPoblacion += 10){				
				pw.println(experimentar(tamPoblacion));
			}
			tamPoblacion = 50;
			
			//Probar probabilidad de cruce
			pw.println("Experimentando con la probabilidad de cruce");
			for (probabilidadCruce = 0.7; probabilidadCruce <= 0.9; probabilidadCruce +=0.01){		
				pw.println(experimentar(probabilidadCruce));				
			}
			probabilidadCruce = 0.8;
			
			//Probar probabilidad de mutaci�n
			pw.println("Experimentando con la probabilidad de mutaci�n");
			for (probabilidadMutacion = 1/tamPoblacion; probabilidadMutacion <= 5.0/tamPoblacion; probabilidadMutacion += 0.25/tamPoblacion){		
				pw.println(experimentar(probabilidadMutacion));				
			}
			probabilidadMutacion = 1.0/tamPoblacion;
			
			//Probar elitismo
			pw.println("Experimentando con el elitismo");
			for (elitismo = 1; elitismo <= 20; elitismo ++){		
				pw.println(experimentar(elitismo));				
			}
			elitismo = 1;
			
			//Probar iteraciones sin mejora
			pw.println("Experimentando con el n�mero de iteraciones sin mejora");
			for (maxIteracionesSinMejora = 0; maxIteracionesSinMejora <= 60; maxIteracionesSinMejora += 3){		
				pw.println(experimentar(maxIteracionesSinMejora));				
			}
			maxIteracionesSinMejora = 20;
			
			System.out.println("Experimentos concluidos");
			
			pw.close();
			

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String experimentar(Object queImprimir){
		String linea = "";
		long tiempoAnterior, tiempoActual;
		
		linea = "";
		Problema prob = new Problema ("src/datos/tai256c.dat", tamPoblacion, semilla2, maxIteraciones, maxIteracionesSinMejora, probabilidadCruce, probabilidadMutacion, elitismo);			
		linea += String.valueOf(queImprimir) + "\t";
		
		tiempoAnterior = System.currentTimeMillis();
		prob.resolver(Problema.ESTANDAR);
		tiempoActual = System.currentTimeMillis();
		linea += String.valueOf(prob.getMinCoste()) + "\t" + ((tiempoActual - tiempoAnterior) / 1000) + "\t";

		tiempoAnterior = System.currentTimeMillis();
		prob.resolver(Problema.LAMARCKIANA);
		tiempoActual = System.currentTimeMillis();
		linea += String.valueOf(prob.getMinCoste()) + "\t" + ((tiempoActual - tiempoAnterior) / 1000) + "\t";

		tiempoAnterior = System.currentTimeMillis();
		prob.resolver(Problema.BALDWINIANA);
		tiempoActual = System.currentTimeMillis();
		linea += String.valueOf(prob.getMinCoste()) + "\t" + ((tiempoActual - tiempoAnterior) / 1000) + "\t";
		
		return linea;
	}

}
