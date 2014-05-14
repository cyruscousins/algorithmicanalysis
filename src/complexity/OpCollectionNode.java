package complexity;

import java.util.ArrayList;
import java.util.List;

public class OpCollectionNode extends FormulaNode{
	public static final int ADD = 0, MULTIPLY = 1;
	private static final int[] toBinOpMap = new int[]{BinaryOperatorNode.ADD, BinaryOperatorNode.MULTIPLY};
	int operator;
	
	String[] opStrings = new String[]{"+", "*"};
	String[] latexOpStrings = new String[]{"+", "\\cdot"};
	
	int len = 0;
	public FormulaNode[] data;
	
	int invLen = 0;
	public FormulaNode[] invData;

	public OpCollectionNode(FormulaNode[] data, int len, int operator){
		this.data = data;
		this.len = len;
		this.operator = operator;
	}

	public OpCollectionNode(FormulaNode[] data, int len, FormulaNode[] invData, int invLen, int operator){
		this.data = data;
		this.len = len;
		
		this.invData = invData;
		this.invLen = invLen;
		
		this.operator = operator;
	}
	
	//Combine 2 OpCollectionNodes.  Assumes they have the same operator.
	public OpCollectionNode combine(OpCollectionNode l){
		//Make a new array and copy into it.
		int newLen = len + l.len;
		FormulaNode[] newArray = new FormulaNode[newLen];

		System.arraycopy(data, 0, newArray, 0, len);
		System.arraycopy(l.data, 0, newArray, len, l.len);
		
		OpCollectionNode newNode = new OpCollectionNode(newArray, newLen, operator);
		return newNode;
	}
	
	public OpCollectionNode pushFirst(FormulaNode f){
		//Make a new array and copy into it.
		int newLen = len + 1;
		FormulaNode[] newArray = new FormulaNode[newLen];

		System.arraycopy(data, 0, newArray, 1, len);
		
		newArray[0] = f;
		
		OpCollectionNode newNode = new OpCollectionNode(newArray, newLen, operator);
		return newNode;
	}
	
	public OpCollectionNode pushLast(FormulaNode f){
		//Make a new array and copy into it.
		int newLen = len + 1;
		FormulaNode[] newArray = new FormulaNode[newLen];

		System.arraycopy(data, 0, newArray, 0, len);
		
		newArray[len] = f;
		
		OpCollectionNode newNode = new OpCollectionNode(newArray, newLen, operator);
		return newNode;
	}
	
	public double evaluate(VarSet v){
		double val;
		switch(operator){
			case ADD:
				val = 0;
				for(int i = 0; i < data.length; i++){
					val += data[i].evaluate(v);
				}
				break;
			case MULTIPLY:
				val = 1;
				for(int i = 0; i < data.length; i++){
					val *= data[i].evaluate(v);
				}
				break;
			default:
				val = Double.NaN;
				break;
		}
		return val;
	}
	
	private static void removeSwapBack(List l, int i){
		if(i == l.size() - 1){
			l.remove(l.size() - 1);
		}
		else{
			l.set(i, l.remove(l.size() - 1));
		}
	}
	
	public FormulaNode simplify(){
		
		ArrayList<FormulaNode> nodes = new ArrayList<FormulaNode>();
		for(int i = 0; i < len; i++){
			nodes.add(data[i].simplify());
			//TODO slurp in children!
			
			//sluuuuurp
		}
		
		for(int i = 0; i < nodes.size(); i++){
			for(int j = i + 1; j < nodes.size(); j++){
				FormulaNode attempt = new BinaryOperatorNode(toBinOpMap[operator], nodes.get(i), nodes.get(j));
				FormulaNode simp = attempt.simplify();
				if(!simp.formulaEquals(attempt)){
					//Simplification was successful, this means that the terms were combined.
					nodes.set(i, simp);
					removeSwapBack(nodes, j);
					
					//A change has occurred, must restart (now with a smaller number of nodes).
					i = -1;
					break;
				}
			}
		}
		
		if(nodes.size() == 1){
			return nodes.get(0);
		}
		else if(nodes.size() == 2){
			return new BinaryOperatorNode(toBinOpMap[operator], nodes.get(0), nodes.get(1));
		}
		else{
			return new OpCollectionNode(nodes.toArray(new FormulaNode[nodes.size()]), nodes.size(), operator);
		}
	}
	
	public FormulaNode bigO(){
		
		FormulaNode simp = simplify();

		if(!(simp instanceof OpCollectionNode)){
			return simp.bigO();
		}
		
		OpCollectionNode oSimp = (OpCollectionNode)simp;
		
		ArrayList<FormulaNode> nodes = new ArrayList<FormulaNode>();
		for(int i = 0; i < oSimp.len; i++){
			nodes.add(oSimp.data[i].bigO());
		}
		
		for(int i = 0; i < nodes.size(); i++){
			for(int j = i + 1; j < nodes.size(); j++){
				FormulaNode attempt = new BinaryOperatorNode(toBinOpMap[operator], nodes.get(i), nodes.get(j));
				FormulaNode bigo = attempt.bigO();
				if(!bigo.formulaEquals(attempt)){
					//Simplification was successful, this means that the terms were combined.
					nodes.set(i, bigo);
					removeSwapBack(nodes, j);
					
					//A change has occurred, must restart (now with a smaller number of nodes).
					i = -1;
					break;
				}
			}
		}
		
		if(nodes.size() == 1){
			return nodes.get(0);
		}
		else if(nodes.size() == 2){
			return new BinaryOperatorNode(toBinOpMap[operator], nodes.get(0), nodes.get(1));
		}
		else{
			return new OpCollectionNode(nodes.toArray(new FormulaNode[nodes.size()]), nodes.size(), operator);
		}
	}
	
  public FormulaNode substitute(String s, FormulaNode f){
	FormulaNode[] newData = new FormulaNode[len];
	for(int i = 0; i< len; i++){
		newData[i] = data[i].substitute(s, f);
	}
	
	return new OpCollectionNode(newData, len, operator);
  }

	@Override
	public long formulaHash() {
		long val = 0;
		for(int i = 0; i < len; i++){
			val ^= circShiftL(data[i].formulaHash(), i * 7);
		}
		return val;
	}
	
	@Override
	public boolean formulaEquals(FormulaNode f) {
		if(!(f instanceof OpCollectionNode)){
			return false;
		}
		OpCollectionNode o = (OpCollectionNode)f;
		if(o.len != len){
			return false;
		}
		
		boolean[] used = new boolean[len];
		
		//Look for every item.
		for(int i = 0; i < len; i++){
			boolean found = false;
			for(int j = 0; j < len; j++){
				if(used[j]) continue;
				if(data[i].formulaEquals(o.data[j])){
					used[j] = found = true;
					break;
				}
			}
			if(!found){
				return false;
			}
		}
		return true;
	}
	
	public String asStringRecurse() {
		String s = "(" + data[0].asStringRecurse();
		for(int i = 1; i < len; i++){
			s += " " + opStrings[operator] + " " + data[i].asStringRecurse();
		}
		s += ")";
		return s;
	}
	
	public String asLatexStringRecurse() {
		String s = "(" + data[0].asLatexStringRecurse();
		for(int i = 1; i < len; i++){
			s += " " + latexOpStrings[operator] + " " + data[i].asLatexStringRecurse();
		}
		s += ")";
		return s;
	}
	
	public static void main(String[] args){
//		FormulaNode[] data = new FormulaNode[]{
//				ConstantNode.ONE, new VariableNode("x"), ConstantNode.MINUS_ONE
//			};
		

		FormulaNode[] data = new FormulaNode[]{
			ConstantNode.ONE, new VariableNode("x"), ConstantNode.MINUS_ONE, new VariableNode("x")
		};
		OpCollectionNode o = new OpCollectionNode(data, data.length, ADD);
		System.out.println(o.asStringRecurse());
		System.out.println(o.simplify().asStringRecurse());
		

		o = new OpCollectionNode(data, data.length, MULTIPLY);
		System.out.println(o.asStringRecurse());
		System.out.println(o.simplify().asStringRecurse());
		
	}
}
