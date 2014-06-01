package complexity;

public abstract class FormulaNode {
	
  //EVALUATION
  public abstract double evaluate(VarSet v);
//  public abstract FormulaNode evaluateExact(VarSet v){
//	  sub
//  }
  ////////////////////////
  //HASHING AND EQUALITY//
  ////////////////////////

  //For hashing
  static long circShiftL(long l, int shift){
	  shift %= 64;
	  return (l << shift) | (l >>> (64 - shift));
  }

  //Weak equality represents equivalence, but not necessarily written the same way.

  //Default to using strong concepts, these are overridden when necessary.
  public boolean formulaWeakEquals(FormulaNode f){
	  return formulaStrongEquals(f);
  }
  public long formulaWeakHash(){ //In some sense, the weak hash has more collisions than the strong because there are fewer equivalence classes for weak equality.
	  return formulaStrongHash();
  }
  
  //Strong equality represents exact equality.
  public abstract long formulaStrongHash();
  public abstract boolean formulaStrongEquals(FormulaNode f);

  //EX a: (a + b), (a + b), weakly and strongly equal.
  //EX b: (a + b), (b + a), weakly but not strongly equal.
  //EX c: (a + b), (a + c), neither weakly nor strongly equal.
  

  //Simplification and bigO
  
  //These are managed with booleans, flagging whether the operations have already been completed.
  //They are also exposed through takeSimplified() and takeBigO(), which control these flags.
  
  public boolean isSimplified, isBigO;
	
  public final FormulaNode takeSimplified(){
	  if(isSimplified) return this;
	  FormulaNode f = simplify();
	  f.isSimplified = true;
	  return f;
  }
  
  public final FormulaNode takeBigO(){
	  if(isBigO) return this;
	  FormulaNode f = takeSimplified().bigO();
	  f.isBigO = true;
	  return f;
  }
  
  //This function shall only be called on a simplified expression.
  FormulaNode bigO(){
	  return this;
  }
  FormulaNode simplify(){
	  return this;
  }
  
  //Take bigO, where it is known that x is in bigO(y).
  FormulaNode bigOVarSub(String x, FormulaNode y){
	  return this;
  }
  
  public String toString(){
	  return super.toString() + ": " + asStringRecurse();
  }
  
//  public FormulaNode substitute(VarSet v){
//	  v.
//	  for(int i = 0; i < v.)
//  }
  
  public FormulaNode substitute(String s, FormulaNode f){
	  return this;
  }

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
		  cur = cur.bigOVarSub(littles[i], new VariableNode(bigs[i])).bigO();
	  }
	  return cur;
  }
  
  
  
  public final boolean isConstant(){
	FormulaNode f = takeSimplified();
	return f.isConstantRecurse();
  }
  
  public abstract boolean isConstantRecurse();
  
  //TODO:  CollectVariables, CollectOperators, ...
}
