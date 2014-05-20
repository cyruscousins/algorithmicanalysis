package complexity;

import java.util.ArrayList;
import java.util.List;

public class OpCollectionNode extends FormulaNode{
	public static final int ADD = 0, MULTIPLY = 1;
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
	
	//Same as binaryoperatornode, for convenience.  TODO, make arg order more consistent with above.
	public OpCollectionNode(int operator, FormulaNode a, FormulaNode b){
		data = new FormulaNode[]{a, b};
		len = 2;
		this.operator = operator;
	}
	
	//Convenience constructor.
	
	public OpCollectionNode(List<FormulaNode> l, int operator){
		this(l.toArray(new FormulaNode[l.size()]), l.size(), operator);
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
	
	public FormulaNode trimConstants(){

		ArrayList<FormulaNode> nodes = new ArrayList<FormulaNode>();
		
		//Stick everything except constants into a list and spit it back out.
		for(int i = 0; i < len; i++){
			
			if(!(data[i] instanceof ConstantNode)){
				nodes.add(data[i]);
			}
		}
		
		if(nodes.size() == len){
			return this;
		}
		
		return new OpCollectionNode(nodes.toArray(new FormulaNode[nodes.size()]), nodes.size(), operator);
		
		
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
	
	//////////////////
	//SIMPLIFICATION//
	//////////////////
	
	//Returns a node representing the given pair simplified, or null if no simplification is possible.
	FormulaNode simplifyAdditionPair(FormulaNode l, FormulaNode r){

		if(l.formulaEquals(r)){
			return new OpCollectionNode(MULTIPLY, ConstantNode.TWO, l).takeSimplified();
		}
		
		if(ConstantNode.ZERO.equals(l)){
			return r;
		}
		else if (ConstantNode.ZERO.equals(r)){
			return l;
		}
		
		
		
		
		return null;
	}
	
	FormulaNode simplifyMultiplicationPair(FormulaNode l, FormulaNode r){
		if(l.formulaEquals(r)){
	  		return new BinaryOperatorNode(BinaryOperatorNode.EXPONENTIATE, l, ConstantNode.TWO).takeSimplified();
		}
		
		if(ConstantNode.ZERO.equals(l) || ConstantNode.ZERO.equals(r)){
			return ConstantNode.ZERO;
		}
		
		if(ConstantNode.ONE.equals(l)){
			return r;
		}
		else if(ConstantNode.ONE.equals(r)){
			return l;
		}
		
		//Attempt an exponent multiplication simplification.
		{
		  BinaryOperatorNode exponential = null;
		  FormulaNode other = null; //unnecessary initialization...
		  
		  //If the right is an exponent.
		  if(r instanceof BinaryOperatorNode && ((BinaryOperatorNode)r).operationType == BinaryOperatorNode.EXPONENTIATE){
			  //If the left is also an exponent
			  if(l instanceof BinaryOperatorNode && ((BinaryOperatorNode)l).operationType == BinaryOperatorNode.EXPONENTIATE){
				  //If they have the same base
				  if(((BinaryOperatorNode)l).l.formulaEquals(((BinaryOperatorNode)r).l)){
					  return new BinaryOperatorNode(BinaryOperatorNode.EXPONENTIATE, ((BinaryOperatorNode)l).l, new OpCollectionNode(ADD, ((BinaryOperatorNode)l).r, ((BinaryOperatorNode)r).r)).takeSimplified();
				  }
			  }
			  else{
				  exponential = (BinaryOperatorNode)r;
				  other = l;
			  }
		  }
		  else if(l instanceof BinaryOperatorNode && ((BinaryOperatorNode)l).operationType == BinaryOperatorNode.EXPONENTIATE){
			  exponential = (BinaryOperatorNode)l;
			  other = r;
		  }
		  
		  if(exponential != null){
			  if(other.formulaEquals(exponential.l)){
				  return new BinaryOperatorNode(BinaryOperatorNode.EXPONENTIATE, exponential.l, new OpCollectionNode(ADD, ConstantNode.ONE, exponential.r)).takeSimplified();
			  }
		  }
		}
		
		return null;
	}
	
	public FormulaNode takeSimplified(){
		
		ArrayList<FormulaNode> nodes = new ArrayList<FormulaNode>();
		
		//Need to convert the representation to a mutable list.
		//In this step, any opcollection nodes are flattened if possible.
		for(int i = 0; i < len; i++){
			
			//Simplify each child
			FormulaNode s = data[i].takeSimplified();
			
			//Flatten the tree if possible.
			if(s instanceof OpCollectionNode && ((OpCollectionNode)s).operator == operator){
				for(int j = 0; j < ((OpCollectionNode)s).len; j++){
					nodes.add(((OpCollectionNode)s).data[j]);
				}
			}
			//Or add directly.
			else{
				nodes.add(data[i].takeSimplified());
			}
		}
		
		for(int i = 0; i < nodes.size(); i++){
			for(int j = i + 1; j < nodes.size(); j++){
				FormulaNode attempt;
				
				if(operator == ADD){
					attempt = simplifyAdditionPair(nodes.get(i), nodes.get(j));
				}
				else if(operator == MULTIPLY){
					attempt = simplifyMultiplicationPair(nodes.get(i), nodes.get(j));
				}
				else{
					System.err.println("Invalid OCN");
					continue;
				}
				
				if(attempt != null){
					//Simplification was successful, this means that the terms were combined.
					nodes.set(i, attempt);
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
		else{
			return new OpCollectionNode(nodes.toArray(new FormulaNode[nodes.size()]), nodes.size(), operator);
		}
	}
	
	////////
	//BIGO//
	////////
	
	//Returns a node representing bigO(l + r), or null if it is the same as l + r is possible.
	FormulaNode bigOAdditionPair(FormulaNode l, FormulaNode r){
	      if(l instanceof ConstantNode && ((ConstantNode)l).value > 0){
	    	  return r;
	      }
	      else if(r instanceof ConstantNode && ((ConstantNode)r).value > 0){
	        	return l;
	      }
		
	      if(BinaryOperatorNode.xInBigOofY(l, r)){
	    	  return r;
	      }
	      else if(BinaryOperatorNode.xInBigOofY(r, l)){
	    	  return l;
	      }
	      
		return null;
	}
	
	FormulaNode bigOMultiplicationPair(FormulaNode l, FormulaNode r){
	      if(l instanceof ConstantNode && ((ConstantNode)l).value > 0){
	    	  return r;
	      }
	      else if(r instanceof ConstantNode && ((ConstantNode)r).value > 0){
	        	return l;
	      }
		
		return null;
	}
	
	public FormulaNode bigO(){
		
		FormulaNode simp = takeSimplified();

		if(!(simp instanceof OpCollectionNode)){
			return simp.takeBigO();
		}
		
		OpCollectionNode oSimp = (OpCollectionNode)simp;
		
		//Condense into mutable list.  Remove any constants in this phase.
		ArrayList<FormulaNode> nodes = new ArrayList<FormulaNode>();
		for(int i = 0; i < oSimp.len; i++){
			FormulaNode newData = oSimp.data[i].takeBigO();
			if(!(newData instanceof ConstantNode && (operator == ADD || ((ConstantNode)newData).value > 0))){
				nodes.add(oSimp.data[i].takeBigO());
			}
		}
		//Everything was a constant.  Scrap it.
		if(nodes.size() == 0){
			return ConstantNode.ONE;
		}
		
		for(int i = 0; i < nodes.size(); i++){
			for(int j = i + 1; j < nodes.size(); j++){
				FormulaNode attempt;
				
				if(operator == ADD){
					attempt = bigOAdditionPair(nodes.get(i), nodes.get(j));
				}
				else if(operator == MULTIPLY){
					attempt = bigOMultiplicationPair(nodes.get(i), nodes.get(j));
				}
				else{
					System.err.println("Invalid OCN");
					continue;
				}
				
				if(attempt != null){
					//Simplification was successful, this means that the terms were combined.
					nodes.set(i, attempt);
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
		else{
			OpCollectionNode nn = new OpCollectionNode(nodes.toArray(new FormulaNode[nodes.size()]), nodes.size(), operator);
			//TODO handle this more cleanly; this is quite inefficient.
			if(nn.formulaEquals(this)){
				return this;
			}
			else{
				return nn;
			}
		}
	}
	
  public FormulaNode bigOVarSub(String s, FormulaNode b){
	  
	  if(true) return this;
	  
	  //TODO this is a heavy function, test it.
	  
	  
	  FormulaNode f = bigO();
	  
	  if(f instanceof OpCollectionNode){
		  OpCollectionNode o = (OpCollectionNode)f;
		  List<FormulaNode> nodes = new ArrayList<FormulaNode>(o.len);
		  List<FormulaNode> nodesSubbed = new ArrayList<FormulaNode>(o.len);
		  for(int i = 0; i < o.data.length; i++){
			  nodes.add(o.data[i].bigOVarSub(s, b));
			  nodesSubbed.add(nodes.get(i).substitute(s, b));
		  }
		  
		  if(operator == ADD){
			  for(int i = 0; i < nodes.size(); i++){
				  for(int j = i + 1; j < nodes.size(); j++){
					  if(BinaryOperatorNode.xInBigOofY(nodesSubbed.get(i), nodes.get(j))){
						  nodes.remove(i);
						  nodesSubbed.remove(i);
						  i--;
						  break;
					  }
					  else if(BinaryOperatorNode.xInBigOofY(nodesSubbed.get(j), nodes.get(i))){
						  nodes.remove(j);
						  nodesSubbed.remove(j);
						  break;
					  }
					  
				  }
			  }
			  
			  if(nodes.size() == 1){
				  return nodes.get(0);
			  }
			  
		  }
		  
		  OpCollectionNode ret = new OpCollectionNode(nodes, operator);
		  if(ret.formulaEquals(this)){
			  return this;
		  }
		  else{
			  return ret;
		  }
	  }
	  else{
		  return f.bigOVarSub(s, b);
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
		
		FormulaNode[] data = new FormulaNode[]{
			ConstantNode.ONE, new VariableNode("x"), ConstantNode.MINUS_ONE, new VariableNode("x")
		};
		OpCollectionNode o = new OpCollectionNode(data, data.length, ADD);
		System.out.println(o.asStringRecurse());
		System.out.println(o.takeSimplified().asStringRecurse());
		

		o = new OpCollectionNode(data, data.length, MULTIPLY);
		System.out.println(o.asStringRecurse());
		System.out.println(o.takeSimplified().asStringRecurse());
		
	}
}
