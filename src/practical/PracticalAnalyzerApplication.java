package practical;

import java.awt.Dimension;

import javax.swing.JFrame;

public class PracticalAnalyzerApplication extends JFrame{

	public PracticalAnalyzerApplication(){
		setVisible(true);
		setSize(new Dimension(1200, 1000));
		add(new GUI());
		repaint();
	}
	
	public static void main(String[] args){
		new PracticalAnalyzerApplication();
	}
}
