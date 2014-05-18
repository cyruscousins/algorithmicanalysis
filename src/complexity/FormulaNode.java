package complexity;

public abstract class FormulaNode {
	
  public abstract double evaluate(VarSet v);
  
  public FormulaNode takeSimplified(){
	  return this;
  }
  FormulaNode takeBigO(){
	  return this;
  }
  
  //Take bigO, where it is known that x is in bigO(y).
  FormulaNode bigOVarSub(String x, FormulaNode y){
	  return this;
  }
  
  public String toString(){
	  return super.toString() + ": " + asStringRecurse();
  }
  
  public FormulaNode substitute(String s, FormulaNode f){
	  return this;
  }

  //For hashing
  static long circShiftL(long l, int shift){
	  shift %= 64;
	  return (l << shift) | (l >>> (64 - shift));
  }
  
  public abstract long formulaHash();
  public abstract boolean formulaEquals(FormulaNode f);

  public abstract String asStringRecurse();
  public String asLatexStringRecurse(){
	  return asStringRecurse();
  }
  
  public String asString(){
	  return trimParens(asStringRecurse());
  }
  
  public String asLatexString(){
	  return trimParens(asLatexStringRecurse());
  }

  static String trimParens(String s){
	  if(s.charAt(0) == '(' && s.charAt(s.length() - 1) == ')'){
		  return s.substring(1, s.length() - 1);
	  }
	  return s;
  }
  
  public FormulaNode takeBigO(String[] littles, String[] bigs){
	  FormulaNode cur = takeBigO();
	  if(littles == null){
		  return cur;
	  }
	  
	  for(int i = 0; i < littles.length; i++){
		  cur = cur.bigOVarSub(littles[i], new VariableNode(bigs[i])).takeBigO();
	  }
	  return cur;
  }
}
