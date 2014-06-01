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
		return new BinaryOperatorNode(BinaryOperatorNode.DIVIDE, new OpCollectionNode(OpCollectionNode.MULTIPLY, f, new OpCollectionNode(OpCollectionNode.ADD, f, ConstantNode.ONE)), ConstantNode.TWO);
	}

	FormulaNode summationrange(FormulaNode bottom, FormulaNode top){
		return new BinaryOperatorNode(BinaryOperatorNode.SUBTRACT, top, new BinaryOperatorNode(BinaryOperatorNode.SUBTRACT, bottom, ConstantNode.ONE));
	}
	FormulaNode summationrangebigO(FormulaNode bottom, FormulaNode top){
//		return new BinaryOperatorNode(BinaryOperatorNode.SUBTRACT, top, new BinaryOperatorNode(BinaryOperatorNode.SUBTRACT, bottom, ConstantNode.ONE));
		return new BinaryOperatorNode(BinaryOperatorNode.SUBTRACT, top, bottom);
	}
	
	public FormulaNode simplify(){

		FormulaNode ls = lower.takeSimplified();
		FormulaNode us = upper.takeSimplified();
		FormulaNode is = inner.takeSimplified();
		
		if(is instanceof OpCollectionNode && ((OpCollectionNode)is).operator == OpCollectionNode.MULTIPLY){
			
			OpCollectionNode ois = (OpCollectionNode)is;
			
			if(ois.data[0] instanceof ConstantNode){
				return new OpCollectionNode(OpCollectionNode.MULTIPLY, ois.data[0], new SummationNode(ls, us, ois.trimConstants(), varName)).takeSimplified();
			}
			
			//TODO: Pull out constant.
			
//			OpCollectionNode bi = (OpCollectionNode) is;
//			
//			FormulaNode constant = null;
//			FormulaNode other = null;
//
//			//TODO collect variables, recurse, ...
//			
//			if(bi.l instanceof ConstantNode){
//				constant = bi.l;
//				other = bi.r;
//			}
//			else if(bi.r instanceof ConstantNode){
//				constant = bi.r;
//				other = bi.l;
//			}
//			return new OpCollectionNode(OpCollectionNode.MULTIPLY, constant, new SummationNode(ls, us, other, varName)).takeSimplified();
		}
		//sum i from a to b of i
		else if(is instanceof VariableNode && ((VariableNode)is).varName.equals(varName)){
			return new BinaryOperatorNode(BinaryOperatorNode.SUBTRACT, ntimesnplusoneovertwo(us), ntimesnplusoneovertwo(new BinaryOperatorNode(BinaryOperatorNode.SUBTRACT, ls, ConstantNode.ONE))).takeSimplified();
		}
		
		return new SummationNode(ls, us, is, varName);
	}
	
	FormulaNode bigO(){
		
			SummationNode s = (SummationNode) this;
			
//			FormulaNode count = summationrange(s.lower, s.upper).takeSimplified();
			FormulaNode count = summationrangebigO(s.lower, s.upper).takeBigO();
//			
			FormulaNode b = new OpCollectionNode(OpCollectionNode.MULTIPLY, count, new OpCollectionNode(OpCollectionNode.ADD, s.inner.substitute(varName, lower), s.inner.substitute(varName, upper)).takeBigO()).takeBigO();
//			System.out.println("\n" + count.asString() + " \\in " + count.takeBigO().asString());
//			System.out.println(b.asString());
//			System.out.println(b.bigO().asString());
//			System.out.println(asString() + " IN " + b.asString());
			return b;
//			return new OpCollectionNode(OpCollectionNode.MULTIPLY, count, new BinaryOperatorNode(OpCollectionNode.ADD, s.inner.substitute(varName, lower), s.inner.substitute(varName, upper)).takeBigO()).takeBigO();
			
			
			
			
			
			//TODO the subtraction stuff would be good for something like i = n to n + 5, but it causes problems.  Need to improve simplifier first.
//			System.out.println(asString() + " -> " + new OpCollectionNode(OpCollectionNode.MULTIPLY, s.upper.bigO(), new OpCollectionNode(OpCollectionNode.ADD, s.inner.substitute(varName, lower), s.inner.substitute(varName, upper))).asString()); 
//			return new OpCollectionNode(OpCollectionNode.MULTIPLY, s.upper.bigO(), new OpCollectionNode(OpCollectionNode.ADD, s.inner.substitute(varName, lower), s.inner.substitute(varName, upper)).bigO()).bigO();
	}
	
	public FormulaNode substitute(String s, FormulaNode f){
		
		//TODO reuse this if no change.
		return new SummationNode(lower.substitute(s, f), upper.substitute(s, f), inner.substitute(s, f), varName);
	}
	
	@Override
	public long formulaWeakHash() {
		return circShiftL(lower.formulaWeakHash(), 3) ^ circShiftL(upper.formulaWeakHash(), 5) ^ circShiftL(inner.formulaWeakHash(), 7);
	}

	@Override
	public long formulaStrongHash(){
		return circShiftL(lower.formulaStrongHash(), 3) ^ circShiftL(upper.formulaStrongHash(), 5) ^ circShiftL(inner.formulaStrongHash(), 7);
	}
	
	@Override
	public boolean formulaWeakEquals(FormulaNode f) {
		if(f instanceof SummationNode){
			SummationNode s = (SummationNode)f;
			return varName.equals(s.varName) && lower.formulaWeakEquals(s.lower) && upper.formulaWeakEquals(s.upper) && inner.formulaWeakEquals(s.inner);
		}
		return false;
	}
	
	@Override
	public boolean formulaStrongEquals(FormulaNode f) {
		if(f instanceof SummationNode){
			SummationNode s = (SummationNode)f;
			return varName.equals(s.varName) && lower.formulaStrongEquals(s.lower) && upper.formulaStrongEquals(s.upper) && inner.formulaStrongEquals(s.inner);
		}
		return false;
	}
	
	@Override
	public String asStringRecurse() {
		return "(" + "sum " + varName + " from " + lower.asStringRecurse() + " to " + upper.asStringRecurse() + " of " + inner.asStringRecurse() + ")";
	}
	
	@Override
	public String asLatexStringRecurse() {
		return "\\sum_{" + varName + " =  " + trimParens(lower.asLatexString()) + "}^{" + trimParens(upper.asLatexString()) + "}\\big(" + trimParens(inner.asLatexString()) + "\\big)";
	}
	
	//TODO pull out all the simplifier stuff into another helper.
	public boolean isConstantRecurse(){
		return lower.isConstant() && upper.isConstant() && inner.isConstant();
	}
}
