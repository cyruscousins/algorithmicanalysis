package graphics;

import java.awt.Graphics2D;

import algorithm.Algorithm;

import complexity.Formula;
import complexity.FormulaNode;
import complexity.FormulaParser;
import complexity.VariableMapping;
import complexity.VariableNode;

public class ComplexityVisualizer implements Renderable{

	RenderInfo info;
	
	String algorithmName = "Djikstra's";
	String analysisType = "amortized bigO";
	
	FormulaNode algorithm;
	
	Algorithm[] algorithms;
	
	int x = 20;
	int y = 150;
	
	public ComplexityVisualizer(RenderInfo info){
		this.info = info;
		
		algorithm = FormulaParser.parseFormula("v + ( v * insert ) + ( v * ( isEmpty + removeMin ) ) + (e * ( 1 + decreaseKey ) ) + v");
	
		algorithms = new Algorithm[]{Algorithm.unorderedArray, Algorithm.orderedArray, Algorithm.heap};
	}
	
	public void render(Graphics2D g, int x0, int y0){
		
		x0 += x;
		y0 += y;

		//TODO deal with mapping.
		VariableMapping m = new VariableMapping();
		m.put("n", new VariableNode("v"));
		
		
		  info.apply(g);
		  
		  int textHeight = 20;
		  
		  int yRun = y0;
		  
		  g.drawString(analysisType + " analysis of algorithm " + algorithmName, x0, yRun);
		  yRun += textHeight;
		  
		  g.drawString(algorithm.asString() + " = bigO(" + new Formula(algorithm).takeBigO().asString() + ")", x0, yRun);
		  yRun += textHeight;
		  
		  for(int i = 0; i < algorithms.length; i++){
			  yRun += textHeight / 2;
			  
			  g.drawString("With " + algorithms[i].name, x0, yRun);
			  yRun += textHeight;
			  
			  g.drawString(new Formula(algorithms[i].substituteAlgorithm("amortized bigO", algorithm, m)).takeBigO().asString(), x0, yRun);
			  yRun += textHeight;
		  }
		  
		  
		  //Chan's Convex Hull.
		  //Kirkpatrick.
	}
}
