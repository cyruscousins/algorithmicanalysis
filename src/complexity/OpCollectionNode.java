package complexity;

import java.util.ArrayList;
import java.util.List;

public class OpCollectionNode extends FormulaNode{
	public static final int ADD = 0, MULTIPLY = 1;
	private static final int[] toBinOpMap = new int[]{BinOpNode.ADD, BinOpNode.MULTIPLY};
	int operator;
	
	String[] opStrings = new String[]{"+", "*"};
	String[] latexOpStrings = new String[]{"+", "\\cdot"};
	
	int len = 0;
	public FormulaNode[] data;
	
	public OpCollectionNode(FormulaNode[] data, int len, int operator){
		this.data = data;
		this.len = len;
		this.operator = operator;
	}
	
	public double eval(VarSet v){
		double val;
		switch(operator){
			case ADD:
				val = 0;
				for(int i = 0; i < data.length; i++){
					val += data[i].eval(v);
				}
				break;
			case MULTIPLY:
				val = 1;
				for(int i = 0; i < data.length; i++){
					val *= data[i].eval(v);
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
		}
		
		for(int i = 0; i < nodes.size(); i++){
			for(int j = i + 1; j < nodes.size(); j++){
				FormulaNode attempt = new BinOpNode(toBinOpMap[operator], nodes.get(i), nodes.get(j));
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
			return new BinOpNode(toBinOpMap[operator], nodes.get(0), nodes.get(1));
		}
		else{
			return new OpCollectionNode(nodes.toArray(new FormulaNode[nodes.size()]), nodes.size(), operator);
		}
		
		/*
		
		FormulaNode[] newData = new FormulaNode[len];
		for(int i = 0; i < len; i++){
			newData[i] = data[i].simplify();
		}
		boolean[] used = new boolean[len];
		int count = 0;
		
		for(int i = 0; i < len; i++){
			if(used[i]) continue;
			FormulaNode result = data[i];
			for(int j = i + 1; j < len; j++){
				if(used[j]) continue;
				if(result instanceof ConstantNode && data[j] instanceof ConstantNode){
					used[j] = true;
					result = new ConstantNode(result.)
				}
			}
			if()
		}
		
		*/
	}
	
	public FormulaNode bigO(){
		//FormulaNode newNode = new FormulaNode()
		FormulaNode f = simplify();
		if(f instanceof OpCollectionNode){
			OpCollectionNode node = (OpCollectionNode)f;
			
			//TODO return checking if things are bigO of other things.
			
			return node;
			
			/*
			//Filter
			boolean[] good = new boolean[len];
			for(int i = 0; i < len; i++){
				//if
			}
			
			*/
		}
		else return f.bigO();
	}
	
  public FormulaNode substitute(String s, FormulaNode f){
	FormulaNode[] newData = new FormulaNode[len];
	for(int i = 0; i< len; i++){
		newData[i] = data[i].substitute(s, f);
	}
	
	return new OpCollectionNode(newData, len, operator);
  }

  long circShiftL(long l, int shift){
	  shift %= 64;
	  return (l << shift) | (l >>> (64 - shift));
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
	
	public String asString() {
		String s = "(" + data[0].asString();
		for(int i = 1; i < len; i++){
			s += " " + opStrings[operator] + " " + data[i].asString();
		}
		s += ")";
		return s;
	}
	
	public String asLatexString() {
		String s = "(" + data[0].asLatexString();
		for(int i = 1; i < len; i++){
			s += " " + latexOpStrings[operator] + " " + data[i].asLatexString();
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
		System.out.println(o.asString());
		System.out.println(o.simplify().asString());
		

		o = new OpCollectionNode(data, data.length, MULTIPLY);
		System.out.println(o.asString());
		System.out.println(o.simplify().asString());
		
	}
}
