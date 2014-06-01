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
				  return Math.floor(argVal);
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
		  FormulaNode argSimp = argument.takeSimplified();
		  if(argSimp instanceof ConstantNode){
			  return new ConstantNode(new UnaryOperatorNode(operationType, argSimp).evaluate(null));
		  }
		  else if(argSimp instanceof UnaryOperatorNode 
				&& (operationType == CEIL || operationType == FLOOR) //floor/ceil of an integer is just the integer.
				&& argSimp instanceof UnaryOperatorNode 
				&& isBoolValued[((UnaryOperatorNode)argSimp).operationType]){
			  return argSimp;
		  }
		  return new UnaryOperatorNode(operationType, argument.takeSimplified());
	  }
	  
	  FormulaNode bigO(){
		  
		  FormulaNode simp = takeSimplified();
		  if(simp instanceof UnaryOperatorNode){
			  UnaryOperatorNode nn = (UnaryOperatorNode)simp;
			  if(nn.operationType == FACTORIAL){
				  return new BinaryOperatorNode(BinaryOperatorNode.EXPONENTIATE, nn.argument, nn.argument).takeBigO();
			  }
			  else if(nn.operationType == CEIL || nn.operationType == FLOOR){
				  return nn.argument.takeBigO();
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

	  public long formulaWeakHash(){
		  return operationType * 27 ^ circShiftL(argument.formulaWeakHash(), 7);
	  }	  
	  
	  public long formulaStrongHash(){
		  return operationType * 27 ^ circShiftL(argument.formulaStrongHash(), 7);
	  }

	  public boolean formulaWeakEquals(FormulaNode f){
		  return (f instanceof UnaryOperatorNode && ((UnaryOperatorNode)f).operationType == operationType && ((UnaryOperatorNode)f).argument.formulaWeakEquals(argument));
	  }
	  
	  public boolean formulaStrongEquals(FormulaNode f){
		  return (f instanceof UnaryOperatorNode && ((UnaryOperatorNode)f).operationType == operationType && ((UnaryOperatorNode)f).argument.formulaStrongEquals(argument));
	  }
	  
	  public boolean isConstantRecurse(){
		  return argument.isConstant();
	  }
	
}
