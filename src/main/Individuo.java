package main;

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
			//Se utiliza el algoritmo greedy basado en 2-opt
			solucionOptimizada = solucion;
			Solucion mejor;
			Solucion permutada;
			
			int tamProblema = problema.getTamProblema();
			do{
				mejor = solucionOptimizada;
				
				for (int i = 0; i < tamProblema; i++){
					for (int j = i + 1; j < tamProblema; j++){
						permutada = new Solucion (mejor);
						int aux = permutada.get(j);
						permutada.set(j, permutada.get(i));
						permutada.set(i, aux);
					}
				}
			} while (solucionOptimizada != mejor);		
			
			optimizada = true;
		}
		
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
		
		s += solucion.toString();
		s += ", Coste = " + problema.coste(solucion);
		
		return s;
	}

	@Override
	public int compareTo(Individuo o) {			
		return problema.coste(solucion) - problema.coste(o.solucion);
	}	
}
