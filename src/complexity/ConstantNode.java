package complexity;

public class ConstantNode extends FormulaNode{
	
  //Common constants, reuse them.
  public static final ConstantNode ONE = new ConstantNode(1), ZERO = new ConstantNode(0), MINUS_ONE = new ConstantNode(-1);
  public static final ConstantNode E = new ConstantNode(Math.E);
  public static final ConstantNode TWO = new ConstantNode(2);
  
  
  public double value;
  public ConstantNode(double value){
	  this.value = value;
  }
  public double evaluate(VarSet v){
    return value;
  }
  //Calculate the bigO of the tree.  Assumes that simplify has been called.
  public FormulaNode bigO(){
	  if(value == 0) return ZERO;
	  else if (value > 0) return ONE;
	  else if (value < 0) return MINUS_ONE;
	  else{
		System.err.println("A serious error has occured handling a constant."); //NaN or related.
		return null;
	  }
  }
  public String asStringRecurse(){
	  long lval = (long)value;
	  double frac = value - lval;
	  
	  if(frac < .001){
		  return ("" + (lval)).replace("-", "~");
	  }
	  return ("" + (value)).replace("-", "~");
  }
  
  public String asLatexStringRecurse(){
	  long lval = (long)value;
	  double frac = value - lval;
	  
	  if(frac < .001){
		  return "" + (lval);
	  }
	  return "" + value;
  }
  
  public long formulaHash(){
	  return Double.doubleToLongBits(value);
  }
  
  public boolean formulaEquals(FormulaNode f){
	  return (f instanceof ConstantNode) && ((ConstantNode)f).value == value;
  }
  
  public boolean isConstant(){
	  return true;
  }
}