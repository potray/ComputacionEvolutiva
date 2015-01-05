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
	 * Asigna un valor a una posici�n
	 * @param pos la posici�n
	 * @param valor el valor
	 */
	public void set (int pos, int valor){
		if (pos >= tam || valor >= tam)
			System.out.println("Error: posici�n o valor mayor que tama�o");
		else
			permutacion.set(pos, valor);
	}
	
	/**
	 * Devuelve el valor de una posici�n
	 * @param pos la posici�n
	 * @return el valor de la posici�n
	 */
	public int get (int pos){
		return permutacion.get(pos);
	}
	
	/**
	 * Autogenera una soluci�n de forma aleatoria
	 * @param semilla la semilla para el generador de n�meros pseudoaleatorios
	 */
	public void randomizar (Random generador){		
		
		for (int i = 0; i < tam; i++){
			int candidato = generador.nextInt(tam);				
			
			while (permutacion.contains(candidato))
				candidato = generador.nextInt(tam);
			
			permutacion.set(i, candidato);
		}
	}
	
	/**
	 * Devuelve una subsecci�n de la soluci�n
	 * @param inicio inicio de la subsecci�n
	 * @param fin fin de la subsecci�n
	 * @return la subsecci�n desde inicio hasta fin
	 * @throws IndexOutOfBoundsException
	 */
	public ArrayList <Integer> subSeccion (int inicio, int fin) throws IndexOutOfBoundsException{
		if (inicio > fin || inicio < 0 || fin >= tam)
			throw new IndexOutOfBoundsException("Error: subsecci�n no v�lida. Inicio = " + inicio + ", fin = " + fin);
		else
			return new ArrayList <Integer> (permutacion.subList(inicio, fin));
	}	
	
	/**
	 * Introduce una subsecci�n dentro de la soluci�n
	 * @param inicio posici�n inicial donde se va a introducir la subsecci�n
	 * @param subseccion subsecci�n a insertar
	 * @throws IndexOutOfBoundsException
	 */
	public void introducirSubSeccion (int inicio, ArrayList <Integer> subseccion) throws IndexOutOfBoundsException{
		if (inicio + subseccion.size() > tam || inicio < 0)
			throw new IndexOutOfBoundsException("Error: subsecci�n no v�lida. Inicio = " + inicio + ", tama�o subsecci�n = " + subseccion.size());
		else{
			for (int i = inicio; i < inicio + subseccion.size(); i++){
				set(i, subseccion.get(i - inicio));
			}
		}
	}	
	
	
	/**
	 * Para una soluci�n a una cadena de caracteres
	 */
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
