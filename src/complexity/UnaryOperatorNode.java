package complexity;

public class UnaryOperatorNode extends FormulaNode {
	public static final int NOP = -1;
	
	public static final int FACTORIAL = 0, FLOOR = 1, CEIL = 2, SINE = 3, COSINE = 4;
	
	public static final String[] opStrings = new String[]{"!", "floor", "ceil", "sin", "cos"};
	private static final boolean[] isBoolValued = new boolean[]{true, true, true, false, false};
	
	int operationType;
	FormulaNode argument;
	
	public UnaryOperatorNode(int operationType, FormulaNode argument) {
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
	  public double evaluate(VarSet v){
		  double argVal = argument.evaluate(v);
		  switch(operationType){
			  case FACTORIAL:
				  return dfactorial(argVal);
			  case FLOOR:
				  return Math.ceil(argVal);
			  case CEIL:
				  return Math.ceil(argVal);
			  case SINE:
				  return Math.sin(argVal);
			  case COSINE:
				  return Math.cos(argVal);
		  }
		  System.err.println("Invalid Unary Operator");
		  return -1;
	  }
	  public FormulaNode simplify(){
		  FormulaNode argSimp = argument.simplify();
		  if(argSimp instanceof ConstantNode){
			  return new ConstantNode(new UnaryOperatorNode(operationType, argSimp).evaluate(null));
		  }
		  else if(argSimp instanceof UnaryOperatorNode 
				&& (operationType == CEIL || operationType == FLOOR) //floor/ceil of an integer is just the integer.
				&& argSimp instanceof UnaryOperatorNode 
				&& isBoolValued[((UnaryOperatorNode)argSimp).operationType]){
			  return argSimp;
		  }
		  return new UnaryOperatorNode(operationType, argument.simplify());
	  }
	  
	  public FormulaNode bigO(){
		  
		  FormulaNode simp = simplify();
		  if(simp instanceof UnaryOperatorNode){
			  UnaryOperatorNode nn = (UnaryOperatorNode)simp;
			  if(nn.operationType == FACTORIAL){
				  return new BinaryOperatorNode(BinaryOperatorNode.EXPONENTIATE, nn.argument, nn.argument);
			  }
			  else if(nn.operationType == CEIL || nn.operationType == FLOOR){
				  return nn.argument;
			  }
		  }
		return simp;
	  }
	  
	  public String asStringRecurse(){
		  switch(operationType){
			  case FACTORIAL:
				  return "(" + argument.asStringRecurse() + "!" + ")"; //TODO use gamma of notation?
			  
			  case FLOOR:
			  case CEIL:
			  case SINE:
			  case COSINE:
				  return "(" + opStrings[operationType] + "(" + argument.asStringRecurse() + ")" + ")";
			  
			  default:
				  System.err.println("Invalid Unary Operator: asString.");
				  return null;
		  }
	  }
	  
	  public String asLatexStringRecurse(){
		  switch(operationType){
		  	case FACTORIAL:
		  		return argument.asLatexStringRecurse() + "!";
		  	case FLOOR:
		  		return "\\floor{" + trimParens(argument.asLatexStringRecurse()) + "}";
		  	case CEIL:
		  		return "\\ceil{" + trimParens(argument.asLatexStringRecurse()) + "}";
		  	case SINE:
		  		return "\\sin(" + trimParens(argument.asLatexStringRecurse()) + ")";
		  	case COSINE:
		  		return "\\cos(" + trimParens(argument.asLatexStringRecurse()) + ")";
		  	default:
				  System.err.println("Invalid Unary Operator: asString.");
				  return null;
		  }
	  }
	  
	  public FormulaNode substitute(String s, FormulaNode f){
		  return new UnaryOperatorNode(operationType, argument.substitute(s, f));
	  }

	  public long formulaHash(){
		  return operationType * 27 ^ circShiftL(argument.formulaHash(), 7);
	  }
	  public boolean formulaEquals(FormulaNode f){
		  return (f instanceof UnaryOperatorNode && ((UnaryOperatorNode)f).operationType == operationType && ((UnaryOperatorNode)f).argument.formulaEquals(argument));
	  }
	
}
