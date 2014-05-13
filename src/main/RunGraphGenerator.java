package main;

import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import complexity.FormulaNode;

public class RunGraphGenerator {
	public static void makeGraph(int width, int height, int name, FormulaNode[] f, String[] names, int xMax, int yMax){
		BufferedImage i = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D g = (Graphics2D)i.getGraphics();
		
		g.clearRect(0, 0, width, height);
		
	}
	public static void main(String[] args){

		int width = 1200;
		int height = 800;
		Frame f = new Frame();
		
		f.setSize(width, height);
		f.setVisible(true);

		try{
			Thread.sleep(100);
		}
		catch(Exception e){ }
		
		Graphics2D g = (Graphics2D) f.getGraphics();
		
	}
}
