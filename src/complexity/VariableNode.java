package complexity;

public class VariableNode extends FormulaNode{
  String varName;
  public VariableNode(String s){
	  this.varName = s;
  }
  public double evaluate(VarSet v){
    return v.get(varName);
  }
  public String asStringRecurse(){
    return varName;
  }
  
  public String asLatexStringRecurse(){
	  if(varName.length() > 1){
		  return "\\text{\\texttt{" + varName.replaceAll("_", "\\\\_") + "}}";
	  }
	  else return varName;
  }
  
  public FormulaNode substitute(String s, FormulaNode f){
	if(varName.equals(s)){
		return f;
	}
	else return this;
  }

  public long formulaStrongHash(){
	  return varName.hashCode();
  }

  public boolean formulaStrongEquals(FormulaNode f){
	  return (f instanceof VariableNode) && ((VariableNode)f).varName.equals(varName);
  }
  
  public boolean isConstantRecurse(){
	  return false;
  }

}
