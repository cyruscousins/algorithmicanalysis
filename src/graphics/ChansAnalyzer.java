package graphics;

import complexity.*;

public class ChansAnalyzer {
	
	//One guess runs in "n * (1 + (h / m)) * (2 log m)" time.
//	FormulaNode algorithm = FormulaParser.parseFormula("n * (1 + (H / m)) * (2 log m)");
//	FormulaNode algorithm = FormulaParser.parseFormula("n * (2 ^ (2 log ((2 log t) + 1)))");
	
	//Attempt to solve
	FormulaNode guessAlgorithm = FormulaParser.parseFormula("n * (1 + (h / m)) * (2 log m)");
	
	FormulaNode[] sequences = FormulaParser.parseFormulae("2 ^ t , 2 ^ (2 ^ t)", ",");
	FormulaNode[] inverses = FormulaParser.parseFormulae("2 log t, 2 log (2 log t)", ",");
	
//	System.out.printl
	
	public static void main(String[] args){
		new ChansAnalyzer().run();
	}
	
	public void run(){
		
		FormulaNode generalIterationCost = FormulaParser.parseFormula("n * (2 log H)");

		System.out.println("Guess algorithm takes O(" + new Formula(generalIterationCost).takeBigO().asString() + ")");
		
//		FormulaNode f = FormulaParser.parseFormula("n * (2 ^ (inv + 1)");
		for(int i = 0; i < sequences.length; i++){
			System.out.println("Using " + sequences[i].asString() + ", inverse " + inverses[i].asString());
			
			FormulaNode iterationCost = generalIterationCost.substitute("H", sequences[i]);
			
			System.out.println("One guess iteration runs in " + iterationCost.asString() + " = " + new Formula(iterationCost).takeBigO().asString());
			
			
			iterationCost = new Formula(iterationCost).takeBigO();
			
			//TODO mention sum
			
//			FormulaNode cost = inverses[i].substitute("t", )
//			System.out.println
			FormulaNode cost = iterationCost.substitute("t", new BinOpNode(BinOpNode.ADD, inverses[i].substitute("t", new VariableNode("m")), ConstantNode.ONE));
			
			System.out.println("Total cost is " + cost.asString() + " = " + new Formula(cost).takeBigO().asString());
			System.out.println();
		}
	}
	
}
