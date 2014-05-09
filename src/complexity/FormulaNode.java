package complexity;

public abstract class FormulaNode {
	
  public abstract double eval(VarSet v);
  
  FormulaNode simplify(){
	  return this;
  }
  FormulaNode bigO(){
	  return this;
  }
  
  public String toString(){
	  return super.toString() + ": " + asString();
  }
  
  public FormulaNode substitute(String s, FormulaNode f){
	  return this;
  }
  
  public abstract long formulaHash();
  public abstract boolean formulaEquals(FormulaNode f);

  public abstract String asString();
  public String asLatexString(){
	  return asString();
  }
  
  //Final:
  
  public final FormulaNode takeSimplified(){
	  long lastHash = formulaHash(); //TODO this function is broken.  Test and debug hashing.
	  FormulaNode lastFormula = this;
	  while(true){
//		  System.out.println(lastFormula.asString() + " -> " + lastHash);
		  
		  FormulaNode nextFormula = lastFormula.simplify();
		  long nextHash = nextFormula.formulaHash();
		  if(lastHash == nextHash && lastFormula.formulaEquals(nextFormula)) break;
		  lastFormula = nextFormula;
		  lastHash = nextHash;
	  }
	  return lastFormula;
  }

  public FormulaNode takeBigO(){
	  long lastHash = formulaHash();
	  FormulaNode lastFormula = this;
	  while(true){
//		  System.out.println(lastFormula.asString());
		  FormulaNode nextFormula = lastFormula.simplify().bigO();
		  long nextHash = nextFormula.formulaHash();
		  if(lastHash == nextHash && lastFormula.formulaEquals(nextFormula)) break;
		  lastFormula = nextFormula;
		  lastHash = nextHash;
	  }
	  return lastFormula;
  }
}
