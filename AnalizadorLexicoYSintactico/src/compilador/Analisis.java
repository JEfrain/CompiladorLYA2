package compilador;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;



public class Analisis
{
	int renglon=1;
	ArrayList<String> impresion; //para la salida
	ArrayList<Identificador> identi = new ArrayList<Identificador>();
	ListaDoble<Token> tokens;
	ArrayList<String> auxiliar= new ArrayList<String>();
	final Token vacio=new Token("", 9,0);
	boolean bandera=true;
	private ArrayList<String> dataCodigo;
	String operation = "";
	

	
	ArrayList<arbol> arbol = new ArrayList<arbol>();
	ArrayList<String> expresion = new ArrayList<String>();
	
	public ArrayList<Identificador> getIdenti() {
		return identi;
	}
	public ArrayList<arbol> getIdenti2(){
		return arbol;
	}
	
	public ArrayList<String> getTabla3() {
		return dataCodigo ;
	}

	public Analisis(String ruta) {//Recibe el nombre del archivo de texto
		analisaCodigo(ruta);
		if(bandera) {
			impresion.add("No hay errores lexicos");
			analisisSintactio(tokens.getInicio());
			AnalisisSemantico(tokens.getInicio());
			VarNoDeclarado(tokens.getInicio());
			GenerarCodigoIntermedio(getIdenti2());

		}
		if(impresion.get(impresion.size()-1).equals("No hay errores lexicos"))
			impresion.add("No hay errores sintacticos");
			
	}
	
	
	
	public void analisaCodigo(String ruta) {
		String linea="", token="";
		StringTokenizer tokenizer;
		try{
	          FileReader file = new FileReader(ruta);
	          BufferedReader archivoEntrada = new BufferedReader(file);
	          linea = archivoEntrada.readLine();
	          impresion=new ArrayList<String>();
	          tokens = new ListaDoble<Token>();
	          while (linea != null){
	        	    linea = separaDelimitadores(linea);
	                tokenizer = new StringTokenizer(linea);
	                while(tokenizer.hasMoreTokens()) {
	                	token = tokenizer.nextToken();
	                	analisisLexico(token);
	                }
	                linea=archivoEntrada.readLine();
	                renglon++;
	          }
	          archivoEntrada.close();
		}catch(IOException e) {
			JOptionPane.showMessageDialog(null,"No se encontro el archivo favor de checar la ruta","Alerta",JOptionPane.ERROR_MESSAGE);
		}
	}
	// La neta le falta hacer mantenimiento pero funciona
	public Token analisisSintactio(NodoDoble<Token> nodo) {
		Token  to;
		if(nodo!=null) // si no llego al ultimo de la lista
		{
			to =  nodo.dato;
			
			switch (to.getTipo()) // un switch para validar la estructura
			{
			case Token.MODIFICADOR:
				int sig=nodo.siguiente.dato.getTipo();
				// aqui se valida que sea 'public int' o 'public class' 
				if(sig!=Token.TIPO_DATO && sig!=Token.CLASE)// si lo que sigue 
					impresion.add("Error sintactico en la linea "+to.getLinea()+" se esparaba un tipo de dato");
				break;
			case Token.IDENTIFICADOR:
				// lo que puede seguir despues de un idetificador
				if(!(Arrays.asList("{","=",";").contains(nodo.siguiente.dato.getValor()))) 
					impresion.add("Error sintactico en la linea "+to.getLinea()+" se esparaba un simbolo");
				else
					if(nodo.anterior.dato.getValor().equals("class")) // se encontro la declaracion de la clase
					{
						
						identi.add( new Identificador(to.getValor(), " ", "class","Local",nodo.dato.getLinea()));
					}
				break;
			// Estos dos entran en el mismo caso
			case Token.TIPO_DATO:
			case Token.CLASE:
				// si lo anterior fue modificador
				if (nodo.anterior!=null) 
					if(nodo.anterior.dato.getTipo()==Token.MODIFICADOR) {
						if(nodo.siguiente.dato.getTipo()!=Token.IDENTIFICADOR) 
							impresion.add("Error sintactico en la linea "+to.getLinea()+" se esparaba un identificador");
					}else
						impresion.add("Error sintactico en la linea "+to.getLinea()+" se esperaba un modificador");
				break;
			case Token.SIMBOLO:
				// Verificar que el mismo numero de parentesis y llaves que abren sean lo mismo que los que cierran
			
				
				if(to.getValor().equals(";")){
					
					if (nodo.anterior.anterior.dato.getTipo()==Token.TIPO_DATO && nodo.anterior.dato.getTipo()==Token.IDENTIFICADOR){
						identi.add(new Identificador(nodo.anterior.dato.getValor(),"",nodo.anterior.anterior.dato.getValor(),"Global",to.getLinea()));

					}
					
					else if (nodo.anterior.anterior.anterior.anterior.dato.getTipo()==Token.TIPO_DATO 
							&& nodo.anterior.anterior.anterior.dato.getTipo()==Token.IDENTIFICADOR 
							&& nodo.anterior.anterior.dato.getTipo()==Token.SIMBOLO
							&&nodo.anterior.dato.getTipo()==Token.CONSTANTE){

						int x =0,auxRenglon=0;
						boolean bandera=false;
						for (int i = 0; i < identi.size(); i++) {
							if (identi.get(i).getNombre().equals(nodo.anterior.anterior.anterior.dato.getValor()) ){
								x++;
								auxRenglon=i;
							}

						}
						if(nodo.anterior.anterior.anterior.anterior.dato.getTipo()==Token.TIPO_DATO && x>0 && nodo.anterior.anterior.anterior.dato.getTipo()==Token.IDENTIFICADOR){
							impresion.add("Error semantico en linea "+to.getLinea()+ " la variable "+nodo.anterior.anterior.anterior.dato.getValor()+" ya habia sido declarada en la linea "+identi.get(auxRenglon).Posicion);
							bandera=true;
						}

						if(!bandera)
							identi.add(new Identificador(nodo.anterior.anterior.anterior.dato.getValor(),nodo.anterior.dato.getValor(),nodo.anterior.anterior.anterior.anterior.dato.getValor(),"Global",to.getLinea()));

					}	
				
				//
				else if ( (nodo.anterior.anterior.anterior.anterior.dato.getTipo()==Token.CONSTANTE 
				&& nodo.anterior.anterior.anterior.dato.getTipo()==Token.OPERADOR_ARITMETICO 
				&& nodo.anterior.anterior.dato.getTipo()==Token.CONSTANTE
				&& nodo.anterior.dato.getValor().contains(")"))  
				|| 
				(nodo.anterior.anterior.anterior.anterior.dato.getTipo()==Token.CONSTANTE
				&& nodo.anterior.anterior.anterior.dato.getTipo()==Token.SIMBOLO 
				&& nodo.anterior.anterior.dato.getTipo()==Token.OPERADOR_ARITMETICO
				&& nodo.anterior.dato.getTipo()==Token.CONSTANTE) 
				|| 
				( nodo.anterior.anterior.anterior.dato.getTipo()==Token.CONSTANTE 
				&& nodo.anterior.anterior.dato.getTipo()==Token.OPERADOR_ARITMETICO
				&& nodo.anterior.dato.getTipo()==Token.CONSTANTE)
				|| 
				( nodo.anterior.anterior.anterior.dato.getTipo()==Token.IDENTIFICADOR 
				&& nodo.anterior.anterior.dato.getTipo()==Token.OPERADOR_ARITMETICO
				&& nodo.anterior.dato.getTipo()==Token.IDENTIFICADOR)
				|| 
				( nodo.anterior.anterior.anterior.dato.getTipo()==Token.IDENTIFICADOR
				&& nodo.anterior.anterior.dato.getTipo()==Token.OPERADOR_ARITMETICO
				&& nodo.anterior.dato.getTipo()==Token.CONSTANTE)
				|| 
				( nodo.anterior.anterior.anterior.dato.getTipo()==Token.CONSTANTE 
				&& nodo.anterior.anterior.dato.getTipo()==Token.OPERADOR_ARITMETICO
				&& nodo.anterior.dato.getTipo()==Token.IDENTIFICADOR)){
			
			
			
			NodoDoble<Token> nodoaux = nodo;
			NodoDoble<Token> nodoaux2 = nodo;
			NodoDoble<Token> nodoaux3 = nodo;
			while(nodoaux!=null){
				String aux2 = nodoaux.anterior.dato.getValor();
				//System.out.println(aux2);
				if(aux2.contains("="))
					break;
				
				nodoaux = nodoaux.anterior;
			}
	
			
			while(nodoaux!=null){
				String aux2 = nodoaux.dato.getValor();
				//System.out.println(aux2);
				if(aux2.contains(";"))
					break;
				
				expresion.add(aux2);
				nodoaux = nodoaux.siguiente;
			}
			

			
			for (int i = 0; i < expresion.size(); i++) {
				for (int j = 0; j < identi.size(); j++) {
					
					if(identi.get(j).getNombre().equals(expresion.get(i))){
		
						expresion.set(i, identi.get(j).getValor());
				}
					
				}
				
			}
			
			ArrayList<String> expresion2 = new ArrayList<String>(expresion);

					for (int i = 0; i < expresion.size(); i++) {
						
						if(expresion.get(i).contains("("))
						expresion.set(i, "ParAbierto");
						
						else if(expresion.get(i).contains(")"))
							expresion.set(i, "ParCerrado");
						
						else if(expresion.get(i).contains("/"))
							expresion.set(i, "Div");
						
						else if(expresion.get(i).contains("*"))
							expresion.set(i, "Multi");
						
						else if(expresion.get(i).contains("+"))
							expresion.set(i, "Suma");
						
						else if(expresion.get(i).equals("-"))
							expresion.set(i, "Resta");
						
					}
					int Resultadofinal=0;
					int contador =1;
					
					
					
					
					for (int i = 0; i < expresion.size(); i++) {
						
						try{
							if(Integer.parseInt(expresion.get(i)) <0){
								expresion2.set(i,"T"+contador);
		
								arbol.add(new arbol("-",expresion.get(i).substring(1)," " ,expresion2.get(i)));
								contador++;

							}	
						}catch (Exception e){
							e.getMessage();
						}
						
						
							
							if(expresion.get(i).contains("ParAbierto") ){
								
								if (expresion.get(i).contains("ParAbierto")){
									
									int aux5 = i;
									int aux6 = 0 ;
									boolean banderaParentesis = false;
									
									for (int j = 0; j < expresion.size(); j++) {
										if(expresion.get(j).contains("ParCerrado")){
										aux6 = j;
										break;
										}
									}
									
									
									while(!banderaParentesis){
										
									//(3 * 6 - 2)	
										
										for (int j = aux5; j < aux6; j++) {

											if(expresion.get(j).contains("Div")){
												Resultadofinal =  dividir(expresion.get(j-1), expresion.get(j+1));
												expresion2.set(j,"T"+contador);
												arbol.add(new arbol("/",expresion2.get(j-1),expresion2.get(j+1),expresion2.get(j)));
												
												expresion2.remove(j+1);
												expresion2.remove(j-1);
												
												expresion.set(j,Resultadofinal+"" );
												expresion.remove(j+1);
												expresion.remove(j-1);
												
												aux6 = aux6 - 2;
												contador++;
												
											}
											 if (expresion.get(j).contains("Multi")){
												Resultadofinal =  multiplicar(expresion.get(j-1), expresion.get(j+1));
												
												
												expresion2.set(j,"T"+contador);
												arbol.add(new arbol("*",expresion2.get(j-1),expresion2.get(j+1),expresion2.get(j)));
												expresion2.remove(j+1);
												expresion2.remove(j-1);
												
												expresion.set(j,Resultadofinal+"" );
												
												expresion.remove(j+1);
												expresion.remove(j-1);
												aux6 = aux6 - 2;
											
												
												contador++;
											}
										}
										
										 if (expresion.get(i+2).contains("Suma")){
											Resultadofinal =  Sumar(expresion.get(i+1), expresion.get(i+3));
											expresion2.set(i+2,"T"+contador);
											arbol.add(new arbol("+",expresion2.get(i+1),expresion2.get(i+3),expresion2.get(i+2)));
											expresion2.remove(i+3);
											expresion2.remove(i+1);
											
											expresion.set(i+1,Resultadofinal+"" );
											expresion.remove(i+2);
											expresion.remove(i+2);
											contador++;
										}
										 if (expresion.get(i+2).contains("Resta")){
											Resultadofinal =  Restar(expresion.get(i+1), expresion.get(i+3));
											expresion2.set(i+2,"T"+contador);
											arbol.add(new arbol("-",expresion2.get(i+1),expresion2.get(i+3),expresion2.get(i+2)));
											expresion2.remove(i+3);
											expresion2.remove(i+1);
											
											expresion.set(i+1,Resultadofinal+"" );
											expresion.remove(i+2);
											expresion.remove(i+2);
						
											contador++;
										}
										


										if(expresion.get(i+2).contains("ParCerrado"))	{
											expresion.remove(i+2);
											expresion.remove(i);
											expresion2.remove(i+2);
											expresion2.remove(i);
											banderaParentesis = true;
										}
										

									
										
										
									}
								
								}
							}
					}
					
						
						for (int i = 0; i < expresion.size(); i++) {

						if(expresion.get(i).contains("Multi") || expresion.get(i).contains("Div")){
							
							
							if (expresion.get(i).contains("Multi")){
								Resultadofinal =  multiplicar(expresion.get(i-1), expresion.get(i+1));
								
								expresion2.set(i,"T"+contador);

								
								System.out.print("MULT "+expresion.get(i-1)+", "+expresion.get(i+1));
								
								
								
								
								arbol.add(new arbol("*",expresion2.get(i-1),expresion2.get(i+1),expresion2.get(i)));
								System.out.println("MULT "+expresion2.get(i-1)+ ", "+expresion2.get(i));
								
								expresion2.remove(i+1);
								expresion2.remove(i-1);
								
								expresion.set(i-1,Resultadofinal+"" );
								expresion.remove(i);
								expresion.remove(i);
								
								
								i--;
								contador++;
							}
							else if (expresion.get(i).contains("Div")){
								Resultadofinal =  dividir(expresion.get(i-1), expresion.get(i+1));
								
								expresion2.set(i,"T"+contador);
							

								arbol.add(new arbol("/",expresion2.get(i-1),expresion2.get(i+1),expresion2.get(i)));
								expresion2.remove(i+1);
								expresion2.remove(i-1);
								
								expresion.set(i-1,Resultadofinal+"" );
								expresion.remove(i);
								expresion.remove(i);
								
								
								i--;
								contador++;
							}
						
						}
						
						
					}

					
					for (int i = 0; i < expresion.size(); i++) {
						
						if(expresion.get(i).contains("Suma") || expresion.get(i).contains("Resta")){
							
							if (expresion.get(i).contains("Suma")){

								Resultadofinal =  Sumar(expresion.get(i-1), expresion.get(i+1));
							
								expresion2.set(i,"T"+contador);
								
								
								arbol.add(new arbol("+",expresion2.get(i-1),expresion2.get(i+1),expresion2.get(i)));
								expresion2.remove(i+1);
								expresion2.remove(i-1);
								
								expresion.set(i-1,Resultadofinal+"" );
								expresion.remove(i);
								expresion.remove(i);
								i--;
								contador++;
							}
							
							else if (expresion.get(i).contains("Resta")){
								if(expresion.get(i).contains("Resta")){
									Resultadofinal =  Restar(expresion.get(i-1), expresion.get(i+1));
						
									expresion2.set(i,"T"+contador);
									
									
									arbol.add(new arbol("-",expresion2.get(i-1),expresion2.get(i+1),expresion2.get(i)));
									expresion2.remove(i+1);
									expresion2.remove(i-1);
									
									expresion.set(i-1,Resultadofinal+"" );
									expresion.remove(i);
									expresion.remove(i);
									i--;
									contador++;
								}
								
							}
							
						}

							
						
					}
		



					int Tipo, nombre;
					String auxTipo ="", auxNombre = "";
					while(nodoaux2!=null){
						Tipo = nodoaux2.anterior.dato.getTipo();
						//System.out.println(Tipo);
						if(Tipo==2 ){
							auxTipo = nodoaux2.anterior.dato.getValor();
							auxNombre = nodoaux2.dato.getValor();
							break;

						}
						
						nodoaux2 = nodoaux2.anterior;
					}
				
				arbol.add(new arbol("=",expresion2.get(0)," ",auxNombre));
				identi.add(new Identificador (auxNombre,Resultadofinal+"",auxTipo,"Global",to.getLinea()));
				
				expresion.remove(0);
				expresion2.remove(0);
		
	}
							
				else if(to.getValor().equals("}")) 
				{
					if(cuenta("{")!=cuenta("}"))
						impresion.add("Error sintactico en la linea "+to.getLinea()+ " falta un {");
				}else if(to.getValor().equals("{")) {
					if(cuenta("{")!=cuenta("}"))
						impresion.add("Error sintactico en la linea "+to.getLinea()+ " falta un }");
				}
				else if(to.getValor().equals("(")) {
					if(cuenta("(")!=cuenta(")"))
						impresion.add("Error sintactico en la linea "+to.getLinea()+ " falta un )");
					else
					{
						if(!(nodo.anterior.dato.getValor().equals("if")&&nodo.siguiente.dato.getTipo()==Token.CONSTANTE)) {
							impresion.add("Error sintactico en la linea "+to.getLinea()+ " se esperaba un valor");
						}
					}
				}else if(to.getValor().equals(")")) {
					if(cuenta("(")!=cuenta(")"))
						impresion.add("Error sintactico en la linea "+to.getLinea()+ " falta un (");
				}
				else if(to.getValor().equals(";")) {//Si el token tiene una ; 
					if(nodo.anterior.anterior.anterior.anterior.dato.getTipo()==Token.TIPO_DATO && nodo.anterior.anterior.anterior.dato.getTipo()==Token.IDENTIFICADOR && nodo.anterior.anterior.dato.getTipo()==Token.OPERADOR_ARITMETICO && nodo.anterior.dato.getTipo()==Token.CONSTANTE) {
					//se inserta a la tabla de simbolos todo la expresion
						identi.add(new Identificador(nodo.anterior.anterior.anterior.dato.getValor(),
								nodo.anterior.dato.getValor(),nodo.anterior.anterior.anterior.anterior.dato.getValor(),
								"Local",nodo.dato.getLinea()));	
					}	
				}
				
				// verificar la asignacion
				else if(to.getValor().equals("=")){
					if(nodo.anterior.dato.getTipo()==Token.IDENTIFICADOR) {
						
							if(nodo.anterior.anterior.dato.getTipo()==Token.TIPO_DATO)
								identi.add(new Identificador(nodo.anterior.dato.getValor(),nodo.siguiente.dato.getValor(),nodo.anterior.anterior.dato.getValor(),"Local",nodo.dato.getLinea()));
							else
								impresion.add("Error sintactico en linea "+to.getLinea()+ " se esperaba un tipo de dato");

						}
					}else
						impresion.add("Error sintactico en linea "+to.getLinea()+ " se esperaba un identificador");
				
			
				}
				break;
			
			case Token.CONSTANTE:
				if(nodo.anterior.dato.getValor().equals("="))
					if(nodo.siguiente.dato.getTipo()!=Token.OPERADOR_ARITMETICO&&nodo.siguiente.dato.getTipo()!=Token.CONSTANTE&&!nodo.siguiente.dato.getValor().equals(";"))
						impresion.add("Error sintactico en linea "+to.getLinea()+ " asignacion no valida");
				break;
			case Token.PALABRA_RESERVADA:
				// verificar esructura de if
				if(to.getValor().equals("if"))
				{
					if(!nodo.siguiente.dato.getValor().equals("(")) {
						impresion.add("Error sintactico en linea "+to.getLinea()+ " se esperaba un (");
					}
				}
				else 
				{
					// si es un else, buscar en los anteriores y si no hay un if ocurrira un error
					
					NodoDoble<Token> aux = nodo.anterior;
					boolean bandera=false;
					while(aux!=null&&!bandera) {
						if(aux.dato.getValor().equals("if"))
							bandera=true;
						aux =aux.anterior;
				
					}
					if(!bandera)
						impresion.add("Error sintactico en linea "+to.getLinea()+ " else no valido");
				}
				
				break;
			case Token.OPERADOR_LOGICO:
				// verificar que sea  'numero' + 'operador' + 'numero' 
				if(nodo.anterior.dato.getTipo()!=Token.CONSTANTE && !nodo.anterior.dato.getValor().equals("(") && !nodo.anterior.dato.getValor().equals(")") ) {
					impresion.add("Error sintactico en linea "+to.getLinea()+ " se esperaba una constante");
					//System.out.println("333");
				}
				if(nodo.siguiente.dato.getTipo()!=Token.CONSTANTE  && !nodo.siguiente.dato.getValor().equals(")") && !nodo.siguiente.dato.getValor().equals("(")){
					impresion.add("Error sintactico en linea "+to.getLinea()+ " se esperaba una constante");
					//System.out.println("999");
				}
				break;
				
				//punto 5
			case Token.OPERADOR_ARITMETICO:
				if( nodo.anterior.dato.getTipo()!=Token.CONSTANTE && nodo.siguiente.dato.getTipo()!=Token.CONSTANTE && !nodo.anterior.dato.getValor().equals("(") && !nodo.siguiente.dato.getValor().equals(")")  ) {
					impresion.add("Error semantico en linea "+to.getLinea()+ " se esperaba una constante");
				//System.out.println("555");
				}
					String aux1="";
			String aux2="";
			aux1=TipoDato(nodo.anterior.dato.getValor());
			aux2=TipoDato(nodo.siguiente.dato.getValor());
			if(!aux1.equals(aux2)) {
				impresion.add("Error semantico en linea "+to.getLinea()+ " Datos incompatibles");
			}
				
				break;
			}
			analisisSintactio(nodo.siguiente);
			return to;
		}
		return vacio;// para no regresar null y evitar null pointer
	}

	public 	String TipoDato(String cadena) {
		if(Pattern.matches("[0-9]+ ", cadena)) {
			return "int";
		}
		if(Pattern.matches("[0-9]+.[0-9]",cadena)) {
			return "float";
		}
		if(cadena.equals("True")|| cadena.equals("False")) {
			return "boolean";
		}
		return "";
	}

	public void analisisLexico(String token) {
		int tipo=0;
		//Se usan listas con los tipos de token
		// Esto se asemeja a un in en base de datos 
		//Ejemplo select * from Clientes where Edad in (18,17,21,44)
		if(Arrays.asList("public","static","private").contains(token)) 
			tipo = Token.MODIFICADOR;
		else if(Arrays.asList("if","else").contains(token)) 
			tipo = Token.PALABRA_RESERVADA;
		else if(Arrays.asList("int","char","float","boolean").contains(token))
			tipo = Token.TIPO_DATO;
		else if(Arrays.asList("(",")","{","}","=",";").contains(token))
			tipo = Token.SIMBOLO;
		else if(Arrays.asList("<","<=",">",">=","==","!=").contains(token))
			tipo = Token.OPERADOR_LOGICO;
		else if(Arrays.asList("+","-","*","/").contains(token))
			tipo = Token.OPERADOR_ARITMETICO;
		else if(Arrays.asList("true","false").contains(token)||Pattern.matches("^\\d+$",token) ||Pattern.matches("-[0-9]+$",token)
				|| Pattern.matches("[0-9]+.[0-9]+",token)) //es para a�adir a la tabla de simbolos datos tipos float
			tipo = Token.CONSTANTE;
		else if(token.equals("class")) 
			tipo =Token.CLASE;
		
		else {
			//Cadenas validas
			Pattern pat = Pattern.compile("^[a-zA-Z]+$");//Expresiones Regulares
			Matcher mat = pat.matcher(token);
			if(mat.find()) 
				tipo = Token.IDENTIFICADOR;
			else {
				impresion.add("Error en la linea "+renglon+" token "+token);
				bandera = false;
				return;
			}
		}
		tokens.insertar(new Token(token,tipo,renglon));
		impresion.add(new Token(token,tipo,renglon).toString());
	}
	// por si alguien escribe todo pegado 
	public String separaDelimitadores(String linea){
		for (String string : Arrays.asList("(",")","{","}","=",";")) {
			if(string.equals("=")) {
				if(linea.indexOf(">=")>=0) {
					linea = linea.replace(">=", " >= ");
					break;
				}
				if(linea.indexOf("<=")>=0) {
					linea = linea.replace("<=", " <= ");
					break;
				}
				if(linea.indexOf("==")>=0)
				{
					linea = linea.replace("==", " == ");
					break;
				}
			}
			if(linea.contains(string)) 
				linea = linea.replace(string, " "+string+" ");
		}
		return linea;
	}
	public int cuenta (String token) {
		
		int conta=0;
		NodoDoble<Token> Aux=tokens.getInicio();
		while(Aux !=null){
			if(Aux.dato.getValor().equals(token))
				conta++;
			Aux=Aux.siguiente;
		}	
		return conta;
	}
	public ArrayList<String> getmistokens() {
		return impresion;
	}
	

	//Punto 2
	public Token AnalisisSemantico(NodoDoble<Token> nodo) {
		Token  tokensig, to;
		String cadena = null;
	if(nodo!=null) {
		to=nodo.dato;

		for (int i = 0; i < identi.size(); i++) {//recorrer toda la tabla de simbolos
			if(identi.get(i).getTipo().contains("int")) {
			cadena=identi.get(i).getValor();
				if(!isNumeric(cadena)) {
					impresion.add("Se ingreso un dato no entero, no es compatible. "+"Linea: "+identi.get(i).getPosicion());
				}
			}
			else if(identi.get(i).getTipo().contains("float")) {
				cadena=identi.get(i).getValor();
				if(!isFloat(cadena)) {
					impresion.add("Se ingreso un dato no float, no es compatible. "+"Linea: "+identi.get(i).getPosicion());
	
			}
				
			}else if(identi.get(i).getTipo().contains("boolean")) {
				cadena=identi.get(i).getValor();
				if(!isBoolean(cadena)) {
					impresion.add("Se ingreso un dato no booleano, no es compatible. "+"Linea: "+identi.get(i).getPosicion());
				}
					
			}
		}

		//Punto 4.- Repetidos
		for(int j=0; j<identi.size();j++) {
			int contador=0;
			boolean banderin=false;
			String bandera="";
			bandera=identi.get(j).getNombre();
			for (int i = 0; i < auxiliar.size(); i++) {
				if(auxiliar.get(i).contains(identi.get(j).getNombre())) {
					banderin=true;
				}
			}


			for(int x=0; x<identi.size();x++) {
				if(identi.get(x).getNombre().contains(bandera) && !banderin) {	
					contador++;
					if(contador>1) {
						impresion.add("Variable repetida en la linea "+ identi.get(x).getPosicion());
						auxiliar.add(identi.get(x).getNombre());
					}
				}
			}
			banderin=false;
		}
	
		for (int i = 0; i < identi.size(); i++) {
			
			
		}
		
	}
	return vacio;
	}
	
	//Metodos para verificar que una cadena sea entera
	public static boolean isNumeric(String cadena) {
		try {
			Integer.parseInt(cadena);
			return true;
		}catch(NumberFormatException n) {
			return false;
		}
	}
	
	public static boolean isFloat(String cadena) {
		try {
			Float.parseFloat(cadena);
			return true;
		}catch(NumberFormatException n) {
			return false;
		}
	}
	
	public static boolean isBoolean(String cadena) {
		if(cadena.contains("True")|| cadena.contains("False")) {
			return true;
		}else {
			return false;
		}
	}
	
//Punto 3 no declarada	
	public Token VarNoDeclarado(NodoDoble<Token> nodo) {
		Token to;
		if(nodo!=null) {
			to=nodo.dato;
			
			if(to.getTipo()==Token.IDENTIFICADOR) {
				String auxiliar=to.getValor();
				boolean bandera=false;
				
				for(int i=0;i<identi.size();i++) {
					if(identi.get(i).getNombre().contains(auxiliar)) {
						bandera=true;
					}
				}
				if(!bandera) {
					impresion.add("Error semantico en la linea " + 
				to.getLinea()+ " esta usado en la variable " + auxiliar + " No esta declarado.");
				}
			}
			VarNoDeclarado(nodo.siguiente);
			return to;
		}
		return vacio;
	}
	
	//
	public int Sumar (String uno, String dos){
		int suma =0;
		suma = suma+Integer.parseInt(uno)+Integer.parseInt(dos);
		return suma;
	}
	
	public int Restar (String uno, String dos){
		int Resta =0;
		Resta = Resta+Integer.parseInt(uno)-Integer.parseInt(dos);
		return Resta;
	}
	
	public int multiplicar (String uno, String dos){
		int multi =0;
		multi = multi+Integer.parseInt(uno)*Integer.parseInt(dos);
		return multi;
	}
	//	
	public int dividir (String uno, String dos){
		int div =0;
		div = div+ (int)( Integer.parseInt(uno)/Integer.parseInt(dos));
		return div;
	}
	

	public void GenerarCodigoIntermedio(ArrayList<arbol> tabla) {
		System.out.println("CODIGO INTERMEDIO");
		dataCodigo = new ArrayList<String>();
		dataCodigo.add("                    .MODEL                   small");
		dataCodigo.add("                    .DATA ");
		//DECLARAR VARIABLES
		for (arbol item : getIdenti2()) {
			dataCodigo.add(item.resultado+"               DW                  0");
		}
		dataCodigo.add("                    .CODE");
		dataCodigo.add("MAIN            PROC                     FAR");
		dataCodigo.add("                    .STARTUP");
		//dataCodigo.add("                    ;OPERACION = ''");
		// OPERACIONES
		for (int i=0; i < getIdenti2().size(); i++) {
			arbol id2 = getIdenti2().get(i);								
			System.out.println("Item: "+"[ "+id2.operador+", "+id2.argumento1+", "+id2.argumento2+", "+id2.resultado+" ]");
			if(id2.operador.equals("+")) {
				operation = "ADD";
				dataCodigo.add("                    ;SUMA");
			}else if (id2.operador.equals("-")) {
				operation = "SUB";
				dataCodigo.add("                    ;RESTA");
			}else if (id2.operador.equals("*")) {
				operation = "MUL";
				dataCodigo.add("                    ;MULTIPLICACION");
			}else if (id2.operador.equals("/")) {
				operation = "DIV";
				dataCodigo.add("                    ;DIVISION");
			}else if (id2.operador.equals("=")) {
				dataCodigo.add("                    ;ASIGNACION");
			}
			if(!id2.operador.equals("=") && ( operation.equals("MUL") || operation.equals("DIV") )) {
				dataCodigo.add("                    MOV	 AX,"+id2.argumento1);
				dataCodigo.add("                    MOV	 BX,"+id2.argumento2);
				dataCodigo.add("                    "+operation+" BX");
				dataCodigo.add("                    MOV	 "+id2.resultado+", AX");
			}
			else if(!id2.operador.equals("=") && ( operation.equals("ADD") || operation.equals("SUB") )) {
				dataCodigo.add("                    MOV	 AX,"+id2.argumento1);
				dataCodigo.add("                    MOV	 BX,"+id2.argumento2);
				dataCodigo.add("                    "+operation+" AX, BX");
				dataCodigo.add("                    MOV	 "+id2.resultado+", AX");
			}
			else {
				dataCodigo.add("                    MOV	 AX,"+id2.argumento1);
				dataCodigo.add("                    MOV "+id2.resultado+", AX");
			}
		}
		dataCodigo.add("MAIN            ENDP");
		// IMPRESION
		for (String item : dataCodigo) {
			System.out.println(item);
		}
		
	}

}