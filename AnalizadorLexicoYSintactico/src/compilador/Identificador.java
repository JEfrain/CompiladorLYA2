package compilador;


public class Identificador {

	@Override
	public String toString() {
		return "Identificador [nombre=" + nombre + ", valor=" + valor + ", tipo=" + tipo + ", alcance=" + Alcance + ", posicion=" + Posicion + "]";
	}
	String nombre;
	String valor;
	String tipo;
	String Alcance;
	int Posicion;
	
	public Identificador(String nombre, String valor, String tipo, String Alcance, int Posicion) {
		super();
		this.nombre = nombre;
		this.valor = valor;
		this.tipo = tipo;
		this.Alcance = Alcance;
		this.Posicion= Posicion;
		
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}
	
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	public String getAlcance() {
		return Alcance;
	}
	public void setAlcance(String alcance) {
		Alcance = alcance;
	}
	
	public int getPosicion() {
		return Posicion;
	}
	public void setPosicion(int posicion) {
		Posicion = posicion;
	}

	
	
}