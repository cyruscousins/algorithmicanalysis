package complexity;

//A formula is a handle on a FormulaNode.
//Formulae are "mutable", although FormulaNodes are immutable and persistent.
public class Formula {
	public FormulaNode formula;
	
	public Formula(FormulaNode formula){
		this.formula = formula;
	}

	  public FormulaNode simplify(){
		  long lastHash = formula.formulaHash(); //TODO this function is broken.  Test and debug hashing.
		  FormulaNode lastFormula = formula;
		  while(true){
//			  System.out.println(lastFormula.asString() + " -> " + lastHash);
			  
			  FormulaNode nextFormula = lastFormula.simplify();
			  long nextHash = nextFormula.formulaHash();
			  if(lastHash == nextHash && lastFormula.formulaEquals(nextFormula)) break;
			  lastFormula = nextFormula;
			  lastHash = nextHash;
			  
		  }
		  this.formula = lastFormula;
		  return formula;
	  }

	  public FormulaNode takeBigO(){
		  long lastHash = formula.formulaHash();
		  FormulaNode lastFormula = formula;
		  while(true){
//			  System.out.println(lastFormula.asString());
			  FormulaNode nextFormula = lastFormula.simplify().bigO();
			  long nextHash = nextFormula.formulaHash();
			  if(lastHash == nextHash && lastFormula.formulaEquals(nextFormula)) break;
			  lastFormula = nextFormula;
			  lastHash = nextHash;
		  }
		  this.formula = lastFormula;
		  return formula;
	  }
}
