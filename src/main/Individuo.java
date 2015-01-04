package main;

public class Individuo {
	private Solucion solucion;
	private double probabilidadReproducirse;

	public Individuo(Solucion solucion, double probabilidadReproducirse) {
		this.solucion = solucion;
		this.probabilidadReproducirse = probabilidadReproducirse;
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
		s += ", Prob selección = " + probabilidadReproducirse;
		
		return s;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
