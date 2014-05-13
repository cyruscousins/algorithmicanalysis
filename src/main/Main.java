package main;

import graphics.*;
import graphics.stroke.HandStroke;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;

import complexity.*;

import color.ColorDatabase;

import algorithm.BinaryHeap;
import algorithm.UnorderedArray;

public class Main {

	public static void main(String[] args){
		
		int width = 1000;
		int height = 800;
		Frame f = new Frame();
		
		f.setSize(width, height);
		f.setVisible(true);

		try{
			Thread.sleep(100);
		}
		catch(Exception e){ }
		
		Graphics2D g = (Graphics2D) f.getGraphics();
		
		//LOAD FONT
		File ff = null;
		Font fraw = null;;
		Font f2 = null;
		try{
			ff = new File("res/font/Gregscript.ttf");
			fraw = Font.createFont(Font.TRUETYPE_FONT, ff);
			f2 = fraw.deriveFont(Font.PLAIN, 32f);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		Font f3 = new Font("Sans-Serif", Font.PLAIN, 20);
		
		RenderInfo ri = new RenderInfo(f2, new HandStroke(2f, 2.5f), Color.BLACK);
		ri.apply(g);
		
		//LOAD XKCD COLOR
		ColorDatabase d = new ColorDatabase();
		
		String[] colStrs = "cloudy blue|dark pastel green|dust|electric lime|fresh green|grey teal|macaroni and cheese|pinkish tan|spruce|strong blue|toxic green|windows blue".split("\\|");
		ArrayList<String> colNames = new ArrayList(colStrs.length);
		
		for(int i = 0; i < colStrs.length; i++){
			colNames.add(colStrs[i]);
		}
		
		ColorKey c = new ColorKey(ri, colNames, d, 800, 200);
		

	    // Set drawing attributes and starting position
	    g.setColor(Color.black);
		
		//"Representation is the Essence of Programming"-- Bernard I. Ng
		
		Renderer r = new Renderer(width, height);
		r.g = g;
		
//		r.put("Title", new RenderableText("This is a test", 100, 100));
		r.put("Hex", RenderableShape.regularPolygon(400, 400, 6, 100));
		r.put("Circle", RenderableShape.regularPolygon(500, 500, 100, 100));
		
		r.put("Colors", c);
		
		FormulaNode form = new BinOpNode(BinOpNode.ADD, new VariableNode("n"), new BinOpNode(BinOpNode.LOGARITHM, new ConstantNode(2), new VariableNode("m")));
		
		r.put("Formula", new FormulaRenderer(form, 100, 50));
		
		RenderableTextQueue out = new RenderableTextQueue(ri, 800, 50, 20);
		
		out.push("\"Representation is the Essence of Programming\"-- Bernard I. Ng");
		out.push("Welcome.");
		
		r.put("OUTPUT", out);
		
		//Grapher:
		FormulaNode[] fn = FormulaParser.parseFormulae("n | n * (2 log n) | n ^ 2 | n ^ 3", "\\|");
//		GraphRenderer gr = new GraphRenderer(ri, 600, 100, 400, 400, 100, 10000, 10, 1000, fn, "n"); //TODO fix graphing module!
//		r.put("Graphs", gr);
		
//		r.render((Graphics2D)f.getGraphics(), 0, 0, 800, 600);
		
//		
//		BinaryHeap h = new BinaryHeap(5);
//		
//		for(int i = 0; i < 100; i++){
//			h.put(i, r);
//			try{
//				Thread.sleep(500);
//			}
//			catch(Exception e){ }
//		}

		int[] data = new int[]{1, 8, 5, 9, 6, 2, 3, 4, 12, 11, 16, 7, 15, 10, 13, 14};
		UnorderedArray a = new UnorderedArray(out, ri, 8);
		
		for(int i = 0; i < 16; i++){
			a.put(data[i], r);
			try{
				Thread.sleep(500);
			}
			catch(Exception e){ }
		}
		
		try{
			Thread.sleep(10000);
		}
		catch(Exception e){ }
	}
}
