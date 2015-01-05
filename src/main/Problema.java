package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import util.Debug;

public class Problema {
	public static final int ESTANDAR = 0;
	public static final int BALDWINIANA = 1;
	public static final int LAMARCKIANA = 2;	
	
	private String path;
	private Scanner s;	
	
	private Random generadorRandom;
	private long semilla;
	
	private int tamProblema;
	private int tamPoblacion;
	
	private int [][] flujos;
	private int [][] distancias;
	
	private ArrayList <Individuo> poblacion;
	
	private int iteraciones;
	private int maxIteraciones;	
	private int maxIteracionesSinMejora;
	private int iteracionesSinMejorar;
	private double probabilidadCruce;
	private double probabilidadMutacion;
	private int elitismo;
	
	private long totalCoste;
	private int minCoste;
	private int maxCoste;
	private int costeAnterior;
	
	public Problema (String path, int tamPoblacion, long semilla, int maxIteraciones, int maxIteracionesSinMejora, double probabilidadCruce, double probabilidadMutacion, int elitismo){
		this.path = path;
		this.tamPoblacion = tamPoblacion;
		this.semilla = semilla;
		this.maxIteraciones = maxIteraciones;
		this.maxIteracionesSinMejora = maxIteracionesSinMejora;
		this.probabilidadCruce = probabilidadCruce;
		this.probabilidadMutacion = probabilidadMutacion;
		this.elitismo = elitismo;
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
		iteraciones = 0;
		iteracionesSinMejorar = 0;
		costeAnterior = 0;
		poblacion = new ArrayList <Individuo> (tamPoblacion);
		generadorRandom = new Random (semilla);
		System.out.println("Generando población");
		
		for (int i = 0; i < tamPoblacion; i++){
			Solucion s = new Solucion (tamProblema);
			s.randomizar(generadorRandom);
			//System.out.println(s.toString());
			poblacion.add(new Individuo (s, 0, this));
		}
		
		System.out.println("Poblacion generada");
	}
	
	/**
	 * Se calculan máximo, mínimo y total de los costes para utilizarlos. También se le asigna a cada individuo la probabilidad de ser seleccionado para reproducirse.
	 */
	private void evaluar(int modo){
		totalCoste = 0;
		maxCoste = 0;
		minCoste = (int) Double.POSITIVE_INFINITY;
		Individuo mejor = new Individuo (new Solucion(tamProblema), 0, this);
		
		Debug.p("Mirando mejor y peor coste");
		for (Individuo i : poblacion){
			int c;
			if (modo == ESTANDAR)
				c = coste(i.getSolucion());
			else{
				Debug.p("Usando como coste la solución optimizada");
				c = coste(i.getSolucionOptimizada());				
			}
			if (c > maxCoste)
				maxCoste = c;
			if (c < minCoste){
				mejor = i;
				minCoste = c;
			}
			totalCoste += c;
		}
		
		if (minCoste == costeAnterior)
			iteracionesSinMejorar ++;
		else
			iteracionesSinMejorar = 0;
		costeAnterior = minCoste;
		
		System.out.println("En la iteración " + iteraciones + " el mejor individuo es " + mejor.toString());
				
		//Se le asigna a cada individuo la probabilidad de ser seleccionado, utilizando el máximo y el mínimo para invertir la probabilidad.
		//Nota: las probabilidades nunca suman 1, pero siempre suman entre 0.99 y 1.01
		for (Individuo i : poblacion){
			double prob;
			if (modo == ESTANDAR)
				prob = (double)((maxCoste + minCoste) - coste(i.getSolucion())) / totalCoste;	
			else
				prob = (double)((maxCoste + minCoste) - coste(i.getSolucionOptimizada())) / totalCoste;		
			i.setProbabilidadSeleccion(prob);	
		}
	}
	
	/**
	 * Selecciona un individuo de la población al azar teniendo en cuenta la probabilidad de selección.
	 * @return el índice del padre dentro del array de la población
	 */
	private int seleccionar(){		
		double random = generadorRandom.nextDouble();		
		int indiceSeleccionado;
		
		for (indiceSeleccionado = 0; indiceSeleccionado < tamPoblacion && random > 0; indiceSeleccionado++)
			random -= poblacion.get(indiceSeleccionado).getProbabilidadReproducirse();
		
		return indiceSeleccionado - 1;
	}
	
	/**
	 * Cruza dos individuos utilizando el método PMX
	 * @param p1 primer individuo
	 * @param p2 segundo individuo
	 * @return los dos hijos resultantes de cruzar los padres
	 */
	private Individuo[] cruzar (Individuo p1, Individuo p2, int modo){
		
		Individuo [] hijos = new Individuo [2];
		Solucion s1, s2;
		//Mapeos de posiciones
		Map <Integer, Integer> map1 = new HashMap <Integer, Integer> (tamProblema * 2);
		Map <Integer, Integer> map2 = new HashMap <Integer, Integer> (tamProblema * 2);
		
		//Copio la solución de un padre en el hijo "opuesto"
		if (modo == LAMARCKIANA){
			s1 = new Solucion(p1.getSolucionOptimizada());
			s2 = new Solucion(p2.getSolucionOptimizada());				
		}else{
			s1 = new Solucion(p1.getSolucion());
			s2 = new Solucion(p2.getSolucion());			
		}
		
		//Elegir un segmento aleatorio de un padre y copiarlo en el hijo, registrando el mapeo
		int inicioSegmentoAleatorio = generadorRandom.nextInt(tamProblema);
		int tamSegmentoAleatorio = generadorRandom.nextInt(tamProblema);
		int finSegmentoAleatorio = inicioSegmentoAleatorio + tamSegmentoAleatorio;
		
		if (finSegmentoAleatorio > tamProblema)
			finSegmentoAleatorio = tamProblema ;
		
		//Dependiendo de si es lamarckiana o no cojo los de la solución optimizada o no
		
		if (modo == LAMARCKIANA){
			Debug.p("Cruzando de la forma lamarckiana");
			for (int i = inicioSegmentoAleatorio; i < finSegmentoAleatorio; i++){
				s1.set(i, p2.getSolucionOptimizada().get(i));			
				s2.set(i, p1.getSolucionOptimizada().get(i));		
				map1.put(s1.get(i), s2.get(i));
				map2.put(s2.get(i), s1.get(i));
			}
		}
		else
			for (int i = inicioSegmentoAleatorio; i < finSegmentoAleatorio; i++){
				s1.set(i, p2.getSolucion().get(i));			
				s2.set(i, p1.getSolucion().get(i));		
				map1.put(s1.get(i), s2.get(i));
				map2.put(s2.get(i), s1.get(i));
			}
		
		
		//Se convierte cada hijo en una permutación válida sin elementos repetidos
		checkUnmappedElements(s1, map1, inicioSegmentoAleatorio, finSegmentoAleatorio);
		checkUnmappedElements(s2, map2, inicioSegmentoAleatorio, finSegmentoAleatorio);
				
		hijos[0] = new Individuo(s1, 0, this);
		hijos[1] = new Individuo(s2, 0, this);
		
		return hijos;
	}
	
	/**
	 * Muta un individuo mediante el operador de Revuelto [scramble]
	 * @param i el individuo a mutar
	 */
	private void mutar (Individuo i){
		Debug.p("Mutando a " + i.toString());
		//Elegir un segmento para mutarlo
		int inicioSegmentoAleatorio = generadorRandom.nextInt(tamProblema);
		int tamSegmentoAleatorio = generadorRandom.nextInt(tamProblema - 2) + 2;
		int finSegmentoAleatorio = inicioSegmentoAleatorio + tamSegmentoAleatorio;
		
		if (finSegmentoAleatorio >= tamProblema)
			finSegmentoAleatorio = tamProblema - 1;
		
		//Sacar el segmento y mezclarlo
		ArrayList <Integer> segmento = i.getSolucion().subSeccion(inicioSegmentoAleatorio, finSegmentoAleatorio);			

		Collections.shuffle(segmento);		
		
		//Volver a introducir el segmetno
		i.getSolucion().introducirSubSeccion(inicioSegmentoAleatorio, segmento);
		
		Debug.p("Mutado");
	}
	
	/**
	 * Mata individuos basándose en la selección basada en el ranking, de forma que nunca se elimina el primero.
	 */
	private void matar (int modo){
		//Primero se ordenan
		Collections.sort(poblacion);		
				
		double random;
		int indiceSeleccionado;	
		
		while (poblacion.size() > tamPoblacion){
			random = generadorRandom.nextDouble();
			for (indiceSeleccionado = elitismo; indiceSeleccionado < poblacion.size() && random > 0; indiceSeleccionado++){
				if (modo == ESTANDAR)
					random -= (double)coste(poblacion.get(indiceSeleccionado).getSolucion()) / totalCoste;
				else
					random -= (double)coste(poblacion.get(indiceSeleccionado).getSolucionOptimizada()) / totalCoste;					
			}
		
			//Control por si las moscas
			if (indiceSeleccionado - elitismo == 0)
				indiceSeleccionado = elitismo + 1;
			
			
			Debug.p("Matando al individuo número " + indiceSeleccionado + " = " + poblacion.get(indiceSeleccionado - 1).toStringPeque());

			poblacion.remove(indiceSeleccionado - 1);
		}
		
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
	
    /**
     * Sacado de https://github.com/dwdyer/watchmaker/blob/master/framework/src/java/main/org/uncommons/watchmaker/framework/operators/ListOrderCrossover.java
     * 
     * Checks elements that are outside of the partially mapped section to
     * see if there are any duplicate items in the list.  If there are, they
     * are mapped appropriately.
     */
    private void checkUnmappedElements(Solucion s, Map<Integer, Integer> mapping, int mappingStart, int mappingEnd){
        for (int i = 0; i < tamProblema; i++)
        {
            if (!isInsideMappedRegion(i, mappingStart, mappingEnd))
            {
                int mapped = s.get(i);
                while (mapping.containsKey(mapped))
                {
                    mapped = mapping.get(mapped);
                }
                s.set(i, mapped);
            }
        }
    }
    
    /**
     * Sacado de https://github.com/dwdyer/watchmaker/blob/master/framework/src/java/main/org/uncommons/watchmaker/framework/operators/ListOrderCrossover.java
     * 
     * Checks whether a given list position is within the partially mapped
     * region used for cross-over.
     * @param position The list position to check.
     * @param startPoint The starting index (inclusive) of the mapped region.
     * @param endPoint The end index (exclusive) of the mapped region.
     * @return True if the specified position is in the mapped region, false
     * otherwise.
     */
    private boolean isInsideMappedRegion(int position, int startPoint, int endPoint){
        boolean enclosed = (position < endPoint && position >= startPoint);
        boolean wrapAround = (startPoint > endPoint && (position >= startPoint || position < endPoint)); 
        return enclosed || wrapAround;
    }
	
	public void resolver (int modo){
		parse();
		init ();		
		evaluar(modo);
		
		while (iteraciones < maxIteraciones && iteracionesSinMejorar < maxIteracionesSinMejora){
			iteraciones ++;
			
			//Se realizan tamPoblacion cruces
			Debug.p("Cruzando");
			for (int i = 0; i < tamPoblacion; i++){
				Debug.p("Seleccionando a 2 individuos");
				//Se seleccionan 2 individuos
				int p1 = seleccionar();
				int p2 = seleccionar();
				Debug.p("Seleccionados");
				
				//Si son distintos y salta la probabilidad se cruzan
				if (generadorRandom.nextDouble() < probabilidadCruce && p1 != p2){
					Individuo [] hijos = cruzar(poblacion.get(p1), poblacion.get(p2), modo);
					//Cada hijo se muta con probabilidad p
					if (generadorRandom.nextDouble() < probabilidadMutacion){
						mutar(hijos[0]);
					}					
					if (generadorRandom.nextDouble() < probabilidadMutacion){
						mutar(hijos[1]);
					}
					poblacion.add(hijos[0]);
					poblacion.add(hijos[1]);					
				}
				Debug.p("En el bucle de los cruces i = " + i);
			}
			
			//A continuación se eliminan individuos hasta que el quedan tamPoblacion individuos
			Debug.p("Matando");
			matar(modo);	
			
			
			//Por último se evalúa
			Debug.p("Evaluando");
			evaluar(modo);
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

	/**
	 * @return the minCoste
	 */
	public int getMinCoste() {
		return minCoste;
	}

	/**
	 * @param minCoste the minCoste to set
	 */
	public void setMinCoste(int minCoste) {
		this.minCoste = minCoste;
	}

	/**
	 * @return the iteraciones
	 */
	public int getIteraciones() {
		return iteraciones;
	}

	/**
	 * @param iteraciones the iteraciones to set
	 */
	public void setIteraciones(int iteraciones) {
		this.iteraciones = iteraciones;
	}
}
