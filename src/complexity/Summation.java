package complexity;

public class Summation extends FormulaNode{
	FormulaNode lower;
	FormulaNode upper;
	
	FormulaNode inner;
	String varName;
	
	

	public Summation(FormulaNode lower, FormulaNode upper, FormulaNode inner, String varName) {
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
		
		if(oldVarValue == Double.NaN){
			v.remove(varName);
		}
		else{
			v.put(varName, oldVarValue);
		}
		
		return value;
	}
	
	@Override
	public long formulaHash() {
		return circShiftL(lower.formulaHash(), 3) ^ circShiftL(upper.formulaHash(), 5) ^ circShiftL(inner.formulaHash(), 7);
	}
	@Override
	public boolean formulaEquals(FormulaNode f) {
		if(f instanceof Summation){
			Summation s = (Summation)f;
			return varName.equals(s.varName) && lower.formulaEquals(s.lower) && upper.formulaEquals(s.upper) && inner.formulaEquals(s.inner);
		}
		return false;
	}
	@Override
	public String asStringRecurse() {
		return "SUM from " + lower.asStringRecurse() + " to " + upper.asStringRecurse() + " of " + inner.asStringRecurse();
	}
	
	public String asLatexString() {
		return "\\sum_{" + trimParens(lower.asLatexString()) + "}^{" + trimParens(upper.asLatexString()) + "}\\big(" + trimParens(inner.asLatexString()) + "\\big)";
	}
}
