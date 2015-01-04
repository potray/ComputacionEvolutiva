package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Problema {
	private String path;
	private Scanner s;	
	
	private Random generadorRandom;
	
	private int tamProblema;
	private int tamPoblacion;
	
	private int [][] flujos;
	private int [][] distancias;
	
	private ArrayList <Individuo> poblacion;
	private ArrayList <Individuo> nuevaGeneracion;
	
	private int iteraciones;
	private int maxIteraciones;	
	private double probabilidadCruce;
	
	private int totalCoste;
	private int minCoste;
	private int maxCoste;
	
	public Problema (String path, int tamPoblacion, long semilla, int maxIteraciones, double probabilidadCruce){
		this.path = path;
		this.tamPoblacion = tamPoblacion;
		poblacion = new ArrayList <Individuo> (tamPoblacion);
		nuevaGeneracion = new ArrayList <Individuo> (tamPoblacion);
		generadorRandom = new Random(semilla);
		this.maxIteraciones = maxIteraciones;
		iteraciones = 0;
		this.probabilidadCruce = probabilidadCruce;
	}
	
	/**
	 * Crea un problema a partir de un fichero
	 */
	private void parse (){
		//Abro un fichero y un scanner
		File f = new File (path);
		
		try {
			s = new Scanner (f);
		} catch (FileNotFoundException e) {
			System.out.println("Error: Fichero " + path + " no encontrado");
			e.printStackTrace();
		}
		
		//En la primera línea está el tamaño del problema
		tamProblema = s.nextInt();
		
		//Después están los flujos
		flujos = new int [tamProblema][tamProblema];
		for (int i = 0; i < tamProblema; i++){
			for (int j = 0; j < tamProblema; j++){
				flujos[i][j] = s.nextInt();
			}
		}
		
		//Y por último las distancias
		distancias = new int [tamProblema][tamProblema];
		for (int i = 0; i < tamProblema; i++){
			for (int j = 0; j < tamProblema; j++){
				distancias[i][j] = s.nextInt();
			}
		}		
	}
	
	/**
	 * Inicializa la población utilizando el generador de números aleatorios
	 */
	private void init (){
		System.out.println("Generando población");
		
		for (int i = 0; i < tamPoblacion; i++){
			Solucion s = new Solucion (tamProblema);
			s.randomizar(generadorRandom);
			//System.out.println(s.toString());
			poblacion.add(new Individuo (s, 0));
		}
		
		System.out.println("Poblacion generada");
	}
	
	/**
	 * Le asigna a cada individuo de la población una probabilidad de que sea seleccionado para ser cruzado mediante el método de la selección proporcional.
	 */
	
	/**
	 * Se calculan máximo, mínimo y total de los costes para utilizarlos. También se le asigna a cada individuo la probabilidad de ser seleccionado para reproducirse.
	 */
	private void evaluar(){
		totalCoste = 0;
		maxCoste = 0;
		minCoste = (int) Double.POSITIVE_INFINITY;
		
		for (Individuo i : poblacion){
			int c = coste(i.getSolucion());
			totalCoste += c;
			if (c > maxCoste)
				maxCoste = c;
			if (c < minCoste)
				minCoste = c;
		}
		
		//Se le asigna a cada individuo la probabilidad de reproducirse, utilizando el máximo y el mínimo para invertir la probabilidad.
		//Nota: las probabilidades nunca suman 1, pero siempre suman entre 0.99 y 1.01
		for (Individuo i : poblacion){
			double prob = (double)((maxCoste + minCoste) - coste(i.getSolucion())) / totalCoste;		
			i.setProbabilidadSeleccion(prob);
		}
	}
	
	/**
	 * Selecciona un individuo de la población al azar teniendo en cuenta la probabilidad de selección.
	 * @return el índice del padre dentro del array de la población
	 */
	private int seleccionar(){		
		double random = generadorRandom.nextDouble();		
		int indicePadre;
		
		for (indicePadre = 0; indicePadre < tamPoblacion && random > 0; indicePadre++)
			random -= poblacion.get(indicePadre).getProbabilidadReproducirse();
		
		return indicePadre - 1;
	}
	
	/**
	 * Cruza dos individuos utilizando el método PMX
	 * @param p1
	 * @param p2
	 * @return
	 */
	private Individuo[] cruzar (Individuo p1, Individuo p2){
		System.out.println("Cruzando los padres: ");
		System.out.println(p1.toString());
		System.out.println(p2.toString());
		System.out.println("");
		
		Individuo [] hijos = new Individuo [2];
		//Mapeos de posiciones
		Map <Integer, Integer> map1 = new HashMap <Integer, Integer> (tamProblema);
		Map <Integer, Integer> map2 = new HashMap <Integer, Integer> (tamProblema);
		
		//Copio la solución de un padre en el hijo "opuesto"
		Solucion s1 = new Solucion(p1.getSolucion());
		Solucion s2 = new Solucion(p2.getSolucion());
		
		//Elegir un segmento aleatorio de un padre y copiarlo en el hijo, registrando el mapeo
		int inicioSegmentoAleatorio = generadorRandom.nextInt(tamProblema);
		int tamSegmentoAleatorio = generadorRandom.nextInt(tamProblema);
		int finSegmentoAleatorio = inicioSegmentoAleatorio + tamSegmentoAleatorio;
		
		if (finSegmentoAleatorio > tamProblema)
			finSegmentoAleatorio = tamProblema;
		
		for (int i = inicioSegmentoAleatorio; i < finSegmentoAleatorio; i++){
			s1.set(i, p2.getSolucion().get(i));			
			s2.set(i, p1.getSolucion().get(i));		
			map1.put(s1.get(i), s2.get(i));
		}
		
		System.out.println(map1.toString());
		
		
		System.out.println("Hijos:");
		System.out.println(s1.toString());
		System.out.println(s2.toString());
		
		return hijos;
	}
	
	/**
	 * Dada una solución devuelve su coste para el problema
	 * @param s la solución
	 * @return el coste de la solución para este problema
	 */
	public int coste (Solucion s){
		int coste = 0;
		
		for (int i = 0; i < tamProblema; i++){
			for (int j = i; j < tamProblema; j++){				
				coste += flujos[i][j] * distancias[s.get(i)][s.get(j)];
			}
		}
		
		return coste * 2;
	}
	
	public void resolver (){
		parse();
		init ();		
		evaluar();
		
		//TODO cambiar a while not (condicion de terminacion)
		while (iteraciones < maxIteraciones){
			iteraciones ++;
			//Se realizan tamPoblacion cruces
			for (int i = 0; i < tamPoblacion; i++){
				//Se seleccionan 2 individuos
				int p1 = seleccionar();
				int p2 = seleccionar();

				
				//Si son distintos y salta la probabilidad se cruzan
				if (generadorRandom.nextDouble() < probabilidadCruce && p1 != p2){
					cruzar(poblacion.get(p1), poblacion.get(p2));
				}
			}
		}
	}
	
	public void printFlujos (){
		System.out.println("Matriz de flujos:");
		for (int i = 0; i < tamProblema; i++){
			for (int j = 0; j < tamProblema; j++){
				System.out.print(flujos[i][j] + " ");
			}
			System.out.println("");
		}
	}
	
	public void printDistancias (){
		System.out.println("Matriz de distancias:");
		for (int i = 0; i < tamProblema; i++){
			for (int j = 0; j < tamProblema; j++){
				System.out.print(distancias[i][j] + " ");
			}
			System.out.println("");
		}
	}

	/**
	 * @return the tamProblema
	 */
	public int getTamProblema() {
		return tamProblema;
	}

	/**
	 * @param tamProblema the tamProblema to set
	 */
	public void setTamProblema(int tamProblema) {
		this.tamProblema = tamProblema;
	}

	/**
	 * @return the flujos
	 */
	public int[][] getFlujos() {
		return flujos;
	}

	/**
	 * @param flujos the flujos to set
	 */
	public void setFlujos(int[][] flujos) {
		this.flujos = flujos;
	}

	/**
	 * @return the distancias
	 */
	public int[][] getDistancias() {
		return distancias;
	}

	/**
	 * @param distancias the distancias to set
	 */
	public void setDistancias(int[][] distancias) {
		this.distancias = distancias;
	}
}
