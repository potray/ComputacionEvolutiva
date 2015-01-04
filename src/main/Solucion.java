package main;

import java.util.ArrayList;
import java.util.Random;

public class Solucion {
	private ArrayList <Integer> permutacion;
	private int tam;
	
	public Solucion (int tam){
		this.tam = tam;
		permutacion = new ArrayList <Integer> (tam);
		
		for (int i = 0; i < tam; i++)
			permutacion.add(-1);
	}
	
	public Solucion (Solucion s){
		this.tam = s.tam;
		permutacion = new ArrayList <Integer> (s.permutacion);
	}
		
	/**
	 * Asigna un valor a una posición
	 * @param pos la posición
	 * @param valor el valor
	 */
	public void set (int pos, int valor){
		if (pos >= tam || valor >= tam)
			System.out.println("Error: posición o valor mayor que tamaño");
		else
			permutacion.set(pos, valor);
	}
	
	/**
	 * Devuelve el valor de una posición
	 * @param pos la posición
	 * @return el valor de la posición
	 */
	public int get (int pos){
		return permutacion.get(pos);
	}
	
	/**
	 * Autogenera una solución de forma aleatoria
	 * @param semilla la semilla para el generador de números pseudoaleatorios
	 */
	public void randomizar (Random generador){		
		
		for (int i = 0; i < tam; i++){
			int candidato = generador.nextInt(tam);				
			
			while (permutacion.contains(candidato))
				candidato = generador.nextInt(tam);
			
			permutacion.set(i, candidato);
		}
	}
	
	
	public String toString (){
		String s = "{";
		
		for (int i = 0; i < permutacion.size(); i++){
			s += String.valueOf(permutacion.get(i));
			if (i != tam - 1)
				s += ", ";
		}
		
		s += "}";
		
		return s;
	}
}
