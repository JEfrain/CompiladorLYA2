package compilador;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import javafx.scene.layout.Pane; 

public class mainApp extends JFrame {
	JProgressBar current;
	JTextArea out;
	JButton find;
	JTextArea mensaje;
	Thread runner;
	int num = 0;
	String frases[] = {"Cargando lolis","RC","SpecialK","PPCDTECALV"};

	
	public mainApp() {
		super("Cargando compilador");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel pane = new JPanel();
		pane.setLayout(new FlowLayout());
		current = new JProgressBar(0, 2000);
		current.setValue(0);
		current.setStringPainted(true);
		pane.add(current);
		setContentPane(pane);
	}

public void iterate() {
	while (num < 2000) {
		current.setValue(num);
		try {
			Thread.sleep(250);
		} catch (InterruptedException e) { }
		num += 95;
	}
}

public static void main(String[] arguments) {
	mainApp frame = new mainApp();
	frame.pack();
	frame.setSize(250,150);
	frame.setLocation(500, 250);
	frame.setVisible(true);
	frame.iterate();
    frame.setVisible(false);
	new AppCompilador();
}

}