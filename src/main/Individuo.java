package main;

import util.Debug;

public class Individuo implements Comparable<Individuo>{
	private Solucion solucion;
	private Solucion solucionOptimizada;
	private double probabilidadReproducirse;
	private Problema problema;
	private boolean optimizada;

	public Individuo(Solucion solucion, double probabilidadReproducirse, Problema problema) {
		this.solucion = solucion;
		this.probabilidadReproducirse = probabilidadReproducirse;
		this.problema = problema;
		optimizada = false;
	}

	/**
	 * @return the solucion
	 */
	public Solucion getSolucion() {
		return solucion;
	}

	/**
	 * @param solucion the solucion to set
	 */
	public void setSolucion(Solucion solucion) {
		this.solucion = solucion;
	}

	/**
	 * @return the solucionOptimizada
	 */
	public Solucion getSolucionOptimizada() {
		if (!optimizada){
			//Se utiliza el algoritmo greedy basado en 2-opt, pero en cuanto encuentra algo mejor o se ha intentado mejorar 10 veces se sale ya que para n=256 tarda demasiado
			solucionOptimizada = solucion;
			Solucion permutada;
			int iteraciones = 0;
			
			boolean mejorada = false;
			int costeSolucionOptimizada = problema.coste(solucionOptimizada);
			
			int tamProblema = problema.getTamProblema();
			do{
				Debug.p("Intentando optimizar a " + toStringPeque() + " por " + iteraciones + " vez");
				iteraciones ++;
				for (int i = 0; i < tamProblema; i++){
					for (int j = i + 1; j < tamProblema; j++){
						
						permutada = new Solucion (solucionOptimizada);
						int aux = permutada.get(j);
						permutada.set(j, permutada.get(i));
						permutada.set(i, aux);
						
						if (problema.coste(permutada) < costeSolucionOptimizada){
							solucionOptimizada = permutada;
							costeSolucionOptimizada = problema.coste(solucionOptimizada);
							mejorada = true;							
						}
					}
				}	
			} while (!mejorada && iteraciones < 10);		
			
			optimizada = true;
		}
		else
			Debug.p("Como estaba optimizada no he tenido que volver a optimizar");
		
		return solucionOptimizada;
	}

	/**
	 * @param solucionOptimizada the solucionOptimizada to set
	 */
	public void setSolucionOptimizada(Solucion solucionOptimizada) {
		this.solucionOptimizada = solucionOptimizada;
	}

	/**
	 * @return the probabilidadReproducirse
	 */
	public double getProbabilidadReproducirse() {
		return probabilidadReproducirse;
	}

	/**
	 * @param prob the probabilidadReproducirse to set
	 */
	public void setProbabilidadSeleccion(double prob) {
		this.probabilidadReproducirse = prob;
	}
	
	public String toString (){
		String s = "";
		
		if (optimizada){
			s +=solucionOptimizada.toString();
			s += ", Coste  (Optimizada) = " + problema.coste(solucionOptimizada);
		}
		else{
			s += solucion.toString();
			s += ", Coste = " + problema.coste(solucion);			
		}
		
		return s;
	}
	
	public String toStringPeque(){
		return ("Coste = " + problema.coste(solucion));
	}

	@Override
	public int compareTo(Individuo o) {			
		return problema.coste(solucion) - problema.coste(o.solucion);
	}	
}
