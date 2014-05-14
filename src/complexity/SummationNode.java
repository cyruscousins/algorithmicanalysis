package complexity;

public class SummationNode extends FormulaNode{
	FormulaNode lower;
	FormulaNode upper;
	
	FormulaNode inner;
	String varName;
	
	public SummationNode(FormulaNode lower, FormulaNode upper, FormulaNode inner, String varName) {
		this.lower = lower;
		this.upper = upper;
		this.inner = inner;
		this.varName = varName;
	}

	public double evaluate(VarSet v) {
		int s = (int)lower.evaluate(v);
		int h = (int)upper.evaluate(v);
		
		double oldVarValue = Double.NaN;
		
		if(v.has(varName)){
			oldVarValue = v.get(varName);
		}
		
		double value = 0;
		
		for(int i = s; i <= h; i++){
			v.put(varName, i);
			value += inner.evaluate(v);
		}
		
		if(Double.isNaN(oldVarValue)){
			if(v.has(varName)) v.remove(varName);
		}
		else{
			v.put(varName, oldVarValue);
		}
		
		return value;
	}
	
	FormulaNode ntimesnplusoneovertwo(FormulaNode f){
		return new BinaryOperatorNode(BinaryOperatorNode.DIVIDE, new BinaryOperatorNode(BinaryOperatorNode.MULTIPLY, f, new BinaryOperatorNode(BinaryOperatorNode.ADD, f, ConstantNode.ONE)), ConstantNode.TWO);
	}
	
	FormulaNode summationrange(FormulaNode bottom, FormulaNode top){
		return new BinaryOperatorNode(BinaryOperatorNode.SUBTRACT, top, new BinaryOperatorNode(BinaryOperatorNode.SUBTRACT, bottom, ConstantNode.ONE));
	}
	
	public FormulaNode simplify(){

		FormulaNode ls = lower.simplify();
		FormulaNode us = upper.simplify();
		FormulaNode is = inner.simplify();
		
		if(inner instanceof BinaryOperatorNode && ((BinaryOperatorNode)is).operationType == BinaryOperatorNode.MULTIPLY){
			BinaryOperatorNode bi = (BinaryOperatorNode) is;
			
			FormulaNode constant = null;
			FormulaNode other = null;

			//TODO collect variables, recurse, ...
			
			if(bi.l instanceof ConstantNode){
				constant = bi.l;
				other = bi.r;
			}
			else if(bi.r instanceof ConstantNode){
				constant = bi.r;
				other = bi.l;
			}
			return new BinaryOperatorNode(BinaryOperatorNode.MULTIPLY, constant, new SummationNode(ls, us, other, varName)).simplify();
		}
		//sum i from a to b of i
		else if(is instanceof VariableNode && ((VariableNode)is).varName.equals(varName)){
			return new BinaryOperatorNode(BinaryOperatorNode.SUBTRACT, ntimesnplusoneovertwo(us), ntimesnplusoneovertwo(new BinaryOperatorNode(BinaryOperatorNode.SUBTRACT, ls, ConstantNode.ONE))).simplify();
		}
		
		return new SummationNode(ls, us, is, varName);
	}
	
	public FormulaNode bigO(){
		FormulaNode simp = simplify();
		
		if(simp instanceof SummationNode){
			SummationNode s = (SummationNode) simp;
			
//			FormulaNode count = summationrange(s.lower, s.upper);
//			
//			BinaryOperatorNode b = new BinaryOperatorNode(BinaryOperatorNode.MULTIPLY, count, new BinaryOperatorNode(BinaryOperatorNode.ADD, s.inner.substitute(varName, lower), s.inner.substitute(varName, upper)));
////			System.out.println("\n" + count.asString());
////			System.out.println(b.asString());
////			System.out.println(b.bigO().asString());
//			return new BinaryOperatorNode(BinaryOperatorNode.MULTIPLY, count, new BinaryOperatorNode(BinaryOperatorNode.ADD, s.inner.substitute(varName, lower), s.inner.substitute(varName, upper))).bigO();
			
			
			//TODO the subtraction stuff would be good for something like i = n to n + 5, but it causes problems.  Need to improve simplifier first.
			return new BinaryOperatorNode(BinaryOperatorNode.MULTIPLY, s.upper, new BinaryOperatorNode(BinaryOperatorNode.ADD, s.inner.substitute(varName, lower), s.inner.substitute(varName, upper))).bigO();
		}
		
		return simp;
	}
	
	public FormulaNode substitute(String s, FormulaNode f){
		
		//TODO reuse this if no change.
		return new SummationNode(lower.substitute(s, f), upper.substitute(s, f), inner.substitute(s, f), varName);
	}
	@Override
	public long formulaHash() {
		return circShiftL(lower.formulaHash(), 3) ^ circShiftL(upper.formulaHash(), 5) ^ circShiftL(inner.formulaHash(), 7);
	}
	@Override
	public boolean formulaEquals(FormulaNode f) {
		if(f instanceof SummationNode){
			SummationNode s = (SummationNode)f;
			return varName.equals(s.varName) && lower.formulaEquals(s.lower) && upper.formulaEquals(s.upper) && inner.formulaEquals(s.inner);
		}
		return false;
	}
	@Override
	public String asStringRecurse() {
		
		return "(" + "sum " + varName + " from " + lower.asStringRecurse() + " to " + upper.asStringRecurse() + " of " + inner.asStringRecurse() + ")";
		
	}
	
	public String asLatexStringRecurse() {
		return "\\sum_{" + trimParens(lower.asLatexString()) + "}^{" + trimParens(upper.asLatexString()) + "}\\big(" + trimParens(inner.asLatexString()) + "\\big)";
	}
}
