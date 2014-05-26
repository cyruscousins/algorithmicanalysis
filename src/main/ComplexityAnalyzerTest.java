package main;

import graphics.ColorKey;
import graphics.ComplexityVisualizer;
import graphics.FormulaRenderer;
import graphics.GraphRenderer;
import graphics.RenderInfo;
import graphics.RenderableShape;
import graphics.RenderableTextQueue;
import graphics.Renderer;
import graphics.stroke.HandStroke;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;

import algorithm.UnorderedArray;
import color.ColorDatabase;

import complexity.BinaryOperatorNode;
import complexity.ConstantNode;
import complexity.FormulaNode;
import complexity.FormulaParser;
import complexity.OpCollectionNode;
import complexity.VariableNode;

public class ComplexityAnalyzerTest {
	
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
		
		RenderInfo ri = new RenderInfo(f2, new HandStroke(2f, 5.5f), Color.BLACK);
//		ri = new RenderInfo(f3, new HandStroke(2f, 2.5f), Color.BLACK);
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
//		r.put("Hex", RenderableShape.regularPolygon(400, 400, 6, 100));
//		r.put("Circle", RenderableShape.regularPolygon(500, 500, 100, 100));
		
//		r.put("Colors", c);
		
		r.put("Hex", RenderableShape.regularPolygon(400, 400, 6, 100));
		r.put("Circle", RenderableShape.regularPolygon(500, 500, 100, 100));
		
		FormulaNode form = new OpCollectionNode(OpCollectionNode.ADD, new VariableNode("n"), new BinaryOperatorNode(BinaryOperatorNode.LOGARITHM, new ConstantNode(2), new VariableNode("m")));
		
		r.put("Formula", new FormulaRenderer(form, 100, 50));
		
		RenderableTextQueue out = new RenderableTextQueue(ri, 200, 50, 10);
		
		out.push("\"Representation is the Essence of Programming\"-- Bernard I. Ng");
		out.push("Welcome.");
		
		r.put("OUTPUT", out);
		
		//Grapher:
		FormulaNode[] fn = FormulaParser.parseFormulae("n | n * (2 log n) | n ^ 2 | n ^ 3", "\\|");
		Color[] colors = d.getRandomSelection(4);
		GraphRenderer gr = new GraphRenderer(ri, 700, 200, 400, 400, 20, 500, 2, 100, fn, colors, "n"); //TODO fix graphing module!
		r.put("Graphs", gr);
		
//		ComplexityVisualizer v = new ComplexityVisualizer(ri);
//		r.put("Complexities", v);
		
		

		r.render((Graphics2D)f.getGraphics(), 0, 0, width, height);
		
		
		try{
			Thread.sleep(1000 * 60 * 10);
		}
		catch(Exception e){ }
	}
}
