package algorithm;

import java.util.HashMap;

import complexity.FormulaNode;

public class Function {
	
	String name;
	HashMap<String, FormulaNode> costs;
	
	public Function(String name, HashMap<String, FormulaNode> costs) {
		this.name = name;
		this.costs = costs;
	}
	
}
