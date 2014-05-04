package complexity;

public class UnaryOperator extends FormulaNode {
	public static final int NOP = -1;
	
	public static final int FACTORIAL = 1, SINE = 2, COSINE = 3;
	
	int operationType;
	FormulaNode argument;
	
	public UnaryOperator(int operationType, FormulaNode argument) {
		this.operationType = operationType;
		this.argument = argument;
	}
	
	static double dfactorial(double d){
		double result = 1;
		while(d > 1){
			result *= d;
			d -= 1;
		}
		return result;
	}
	  public double eval(VarSet v){
		  double argEval = argument.eval(v);
		  switch(operationType){
			  case FACTORIAL:
				  return dfactorial(argEval);
			  case SINE:
				  return Math.sin(argEval);
			  case COSINE:
				  return Math.cos(argEval);
		  }
		  System.err.println("Invalid Unary Operator");
		  return -1;
	  }
	  public FormulaNode simplify(){
		  return new UnaryOperator(operationType, argument.simplify());
	  }
	  public FormulaNode bigO(){
		  
		  FormulaNode simp = simplify();
		  if(simp instanceof UnaryOperator){
			  UnaryOperator nn = (UnaryOperator)simp;
			  
		  }
		return simplify();
	  }
	  public String asString(){
		  switch(operationType){
			  case FACTORIAL:
				  return argument.asString() + "!"; //TODO use gamma of notation?
			  case SINE:
				  return "sin " + argument.asString();
			  case COSINE:
				  return "cos " + argument.asString();
		  }
		  System.err.println("Invalid Unary Operator: asString.");
		  return null;
		  
	  }
	  
	  public FormulaNode substitute(String s, FormulaNode f){
		  return new UnaryOperator(operationType, argument.substitute(s, f));
	  }

	  static long circShiftL(long l, int shift){
		  return (l << shift) | (l >>> (64 - shift));
	  }
	  public long formulaHash(){
		  return operationType * 27 ^ circShiftL(argument.formulaHash(), 7);
	  }
	  public boolean formulaEquals(FormulaNode f){
		  return (f instanceof UnaryOperator && ((UnaryOperator)f).operationType == operationType && ((UnaryOperator)f).argument.formulaEquals(argument));
	  }
	
}
