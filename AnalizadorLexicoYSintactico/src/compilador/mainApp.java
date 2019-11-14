package compilador;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javafx.scene.layout.Pane; 

public class mainApp extends JFrame {
	JProgressBar current;
	JTextArea out;
	JButton find;

	Thread runner;
	int num = 0;
	JTextArea mensajes = new JTextArea(10,20);
	String frases[] = {"Cargando lolis","RC","SpecialK","cgtmPancho","Pepigod","Balbuena"};

	
	public mainApp() {
		super("Cargando compilador");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel pane = new JPanel();
		pane.setLayout(new FlowLayout());
		
		mensajes.setLineWrap(true);
		mensajes.setEditable(false);
		
		mensajes.setText(frases[0]);
		
		current = new JProgressBar(0, 2000);
		current.setValue(0);
		current.setStringPainted(true);
		pane.add(current);
		pane.add(mensajes);
		setContentPane(pane);
	}

public void iterate() {
	int x = 0;
	
	while (num < 2000) {
				
		
		current.setValue(num);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) { }
		num += 95;
		
		if(x==frases.length-1) {
			x=0;			
		}else {
			mensajes.setText(frases[x]);
			x++;
		}
		

	}
}

public static void main(String[] arguments) {
	mainApp frame = new mainApp();
	frame.pack();
	frame.setSize(250,100);
	frame.setLocation(500, 250);
	frame.setVisible(true);
	frame.iterate();
    frame.setVisible(false);
	new AppCompilador();
}

}