package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import util.Debug;

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
		
		Debug.setDebug(false);
		
		//realizarExperimentos();
		//experimentar(tamPoblacion);

	}
	
	public static void realizarExperimentos(){
		PrintWriter pw;
		try {
			pw = new PrintWriter(new File("experimentos.csv"));		
			pw.println("Experimento\tParmámetro\tMejor individuo\tTiempo en milisegundos\tIteraciones");
			
			//Probar tamaño de la población
			pw.println("Experimentando con la población");
			for (tamPoblacion = 10; tamPoblacion <= 400; tamPoblacion += 10){				
				pw.println(experimentar(tamPoblacion));
			}
			tamPoblacion = 50;
			
			//Probar probabilidad de cruce
			pw.println("Experimentando con la probabilidad de cruce");
			for (probabilidadCruce = 0.7; probabilidadCruce <= 0.9; probabilidadCruce +=0.005){		
				pw.println(experimentar(probabilidadCruce));				
			}
			probabilidadCruce = 0.8;
			
			//Probar probabilidad de mutación
			pw.println("Experimentando con la probabilidad de mutación");
			for (probabilidadMutacion = 1/tamPoblacion; probabilidadMutacion <= 5.0/tamPoblacion; probabilidadMutacion += 0.115/tamPoblacion){		
				pw.println(experimentar(probabilidadMutacion));				
			}
			probabilidadMutacion = 1.0/tamPoblacion;
			
			//Probar elitismo
			pw.println("Experimentando con el elitismo");
			for (elitismo = 1; elitismo <= 40; elitismo ++){		
				pw.println(experimentar(elitismo));				
			}
			elitismo = 1;
			
			//Probar iteraciones sin mejora
			pw.println("Experimentando con el número de iteraciones sin mejora");
			for (maxIteracionesSinMejora = 0; maxIteracionesSinMejora <= 200; maxIteracionesSinMejora += 5){		
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
		linea += "\t" + String.valueOf(prob.getMinCoste()) + "\t" + ((tiempoActual - tiempoAnterior)) + "\t" + prob.getIteraciones() + "\t";

		tiempoAnterior = System.currentTimeMillis();
		prob.resolver(Problema.BALDWINIANA);
		tiempoActual = System.currentTimeMillis();
		linea += "\t" + String.valueOf(prob.getMinCoste()) + "\t" + ((tiempoActual - tiempoAnterior)) + "\t" + prob.getIteraciones() + "\t";

		tiempoAnterior = System.currentTimeMillis();
		prob.resolver(Problema.LAMARCKIANA);
		tiempoActual = System.currentTimeMillis();
		linea += "\t" + String.valueOf(prob.getMinCoste()) + "\t" + ((tiempoActual - tiempoAnterior)) + "\t" + prob.getIteraciones() + "\t";
		
		return linea;
	}

	public static void experimentar(){		
		Problema prob = new Problema ("src/datos/tai256c.dat", tamPoblacion, semilla2, maxIteraciones, maxIteracionesSinMejora, probabilidadCruce, probabilidadMutacion, elitismo);			
		prob.resolver(Problema.ESTANDAR);
		prob.resolver(Problema.BALDWINIANA);
		prob.resolver(Problema.LAMARCKIANA);}
	
}
