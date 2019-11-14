package compilador;

import java.util.*;


public class arbol {

	String operador;
	String argumento1;
	String argumento2;
	String resultado;

	
	public arbol(String operador, String argumento1, String argumento2, String resultado) {
		super();
		this.operador = operador;
		this.argumento1 = argumento1;
		this.argumento2 = argumento2;
		this.resultado=resultado;
		
	}

	public String getoperador() {
		return operador;
	}
	public void setoperador(String operador) {
		this.operador = operador;
	}
	public String getArgumento1() {
		return argumento1;
	}
	public void setArgumento1(String argumento1) {
		this.argumento1 = argumento1;
	}
	
	public String getResultado() {
		return resultado;
	}
	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
	
	public String getArgumento2() {
		return argumento2;
	}
	public void setArgumento2(String argumento2) {
		this.argumento2 = argumento2;
	}	

}