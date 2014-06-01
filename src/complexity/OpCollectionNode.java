package complexity;

import java.util.ArrayList;
import java.util.List;

public class OpCollectionNode extends FormulaNode{
	public static final int ADD = 0, MULTIPLY = 1;
	int operator;
	
	String[] opStrings = new String[]{"+", "*"};
	String[] latexOpStrings = new String[]{"+", "\\cdot"};
	
	public static final ConstantNode[] IDENTITY = new ConstantNode[]{ConstantNode.ZERO, ConstantNode.ONE};
	public static final int[] INVERSE = new int[]{BinaryOperatorNode.SUBTRACT, BinaryOperatorNode.DIVIDE};
	
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
	
	public List<FormulaNode> asList(){
		List<FormulaNode> l = new ArrayList<>(len);
		for(int i = 0; i < len; i++){
			l.add(data[i]);
		}
		return l;
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
	
	
	//Helper function for remove.
	//Returns success if the element was removed from the list.
	private boolean removeFromList(List<FormulaNode> list, FormulaNode elem){
		
		FormulaNode eexp = ConstantNode.ONE;
		if(elem instanceof BinaryOperatorNode && ((BinaryOperatorNode) elem).operationType == BinaryOperatorNode.EXPONENTIATE && ((BinaryOperatorNode)elem).r.isConstant()){
			eexp = ((BinaryOperatorNode)elem).r;
			elem = ((BinaryOperatorNode)elem).l;
		}
		
		for(int i = 0; i < list.size(); i++){
			FormulaNode l = list.get(i);
			FormulaNode lexp = ConstantNode.ONE;
			if(l instanceof BinaryOperatorNode && ((BinaryOperatorNode) l).operationType == BinaryOperatorNode.EXPONENTIATE && ((BinaryOperatorNode)l).r.isConstant()){
				lexp = ((BinaryOperatorNode)l).r;
				l =    ((BinaryOperatorNode)l).l;
			}
			
			if(l.formulaWeakEquals(elem)){
				if(lexp.evaluate(null) >= eexp.evaluate(null)){
					if(lexp.formulaWeakEquals(eexp)){
						list.remove(i);
						return true;
					}
					else{
						list.set(i, new BinaryOperatorNode(BinaryOperatorNode.EXPONENTIATE, l, new BinaryOperatorNode(BinaryOperatorNode.SUBTRACT, lexp, eexp).takeSimplified()).takeSimplified());
						return true;
					}
				}
			}
			
			//TODO could short circuit.
		}
		return false;
	}
	
	//Returns an FormulaNode of this minus one instance of f, if f is not contained in this OpCollectionNode, null is instead returned.
	public FormulaNode remove(FormulaNode f){
		if(f instanceof OpCollectionNode){
			OpCollectionNode other = (OpCollectionNode)f;
			if(other.operator == operator){
				//System.out.println("SIMPLIFY REMOVE: " + asString() + " " +  BinaryOperatorNode.opStrings[INVERSE[operator]] + " " + other.asString());
				//Trying to remove a set of items
				
				List<FormulaNode> left = new ArrayList<FormulaNode>();
				List<FormulaNode> right = new ArrayList<FormulaNode>();
				
				for(int i = 0; i < len; i++){
					left.add(data[i]);
				}
				
				for(int i = 0; i < other.len; i++){
					right.add(other.data[i]);
				}
				
				for(int i = 0; i < left.size(); i++){
					for(int j = 0; j < right.size(); j++){
						
						BinaryOperatorNode bn = new BinaryOperatorNode(INVERSE[operator], left.get(i), right.get(j));
						FormulaNode bns = bn.takeSimplified();
						
						//If things cancel to the identity, throw it out.
						if(bns.formulaWeakEquals(IDENTITY[operator])){
							left.remove(i);
							right.remove(j);
							i--;
							break;
						}
						if(!bns.formulaWeakEquals(bn)){
							left.set(i, bns);
							right.remove(j);
							i--;
							j = -1;
							break;
						}
					}
				}
				
				//Everything from right was removed:
				if(right.size() == 0){
					if(left.size() == 0){
						return IDENTITY[operator];
					}
					return new OpCollectionNode(left, operator).takeSimplified();
				}
				else if (left.size() == 0){
					return new BinaryOperatorNode(INVERSE[operator], IDENTITY[operator], new OpCollectionNode(right, operator)).takeSimplified();
				}
				//Nothing from right was removed:
				else if(right.size() == other.len){
					return null;
				}
				
				return new BinaryOperatorNode(INVERSE[operator], new OpCollectionNode(left, operator).takeSimplified(), new OpCollectionNode(right, operator).takeSimplified()).takeSimplified();
				
				//38 26 11
			}
		}
		
		List<FormulaNode> l = asList();
		if(removeFromList(l, f)){
//			System.out.println("REMOVE " + f.asString() + " FROM " + asString() + " -> " + new OpCollectionNode(l, operator).asString());
			return new OpCollectionNode(l, operator).takeSimplified();
		}
//		System.out.println("CAN'T REMOVE " + f.asString() + " FROM " + asString());
		
		return null;
		
		//Simple remove:
		
//		int index = -1;
//		for(int i = 0; i < len; i++){
//			if(data[i].formulaEquals(f)){
//				index = i;
//			}
//		}
//		if(index == -1){
//			return null;
//		}
//		
//		int newLen = len - 1;
//		
//		if(newLen == 1){
//			if(index == 0){
//				return data[1];
//			}
//			else{
//				return data[0];
//			}
//		}
//		
//		FormulaNode[] newData = new FormulaNode[newLen];
//		
//		System.arraycopy(data, 0, newData, 0, index);
//		System.arraycopy(data, index + 1, newData, index, newLen - index);
//		
//		return new OpCollectionNode(newData, newLen, operator);
		
	}
	
	public FormulaNode remove(int index){
		FormulaNode[] newData = new FormulaNode[len - 1];
		
		System.arraycopy(data, 0, newData, 0, index);
		System.arraycopy(data, index + 1, newData, index, len - 1 - index);
		
		return new OpCollectionNode(newData, len - 1, operator);
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

		if(l.formulaWeakEquals(r)){
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
		
		if(l.formulaWeakEquals(r)){
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
				  if(((BinaryOperatorNode)l).l.formulaWeakEquals(((BinaryOperatorNode)r).l)){
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
			  if(other.formulaWeakEquals(exponential.l)){
				  return new BinaryOperatorNode(BinaryOperatorNode.EXPONENTIATE, exponential.l, new OpCollectionNode(ADD, ConstantNode.ONE, exponential.r)).takeSimplified();
			  }
		  }
		}
		
		return null;
	}
	
	//Need to handle inverse operators and tree reduction.  
	public FormulaNode simplify(){
		
//		if(operator == ADD) return this; 
		
//		System.out.println("SIMPLIFYING: " + asString());
		
		ArrayList<FormulaNode> nodes = new ArrayList<>();
		ConstantNode constant = IDENTITY[operator];
		ArrayList<FormulaNode> inverseNodes = new ArrayList<FormulaNode>();
		
		//Need to convert the representation to a mutable list.
		//In this step, any opcollection nodes are flattened if possible.
		//Constants are also pulled to the front.
		//Inverses are also pulled out and grouped together.

		for(int i = 0; i < len; i++){
//			nodesStore.add(data[i].takeSimplified())
			
			//Simplify each child
			FormulaNode s = data[i].takeSimplified();
			
			int top = nodes.size();
			
			nodes.add(s);
			
			//Here constants and inverses 
			while(top < nodes.size()){
				s = nodes.get(top);
				if(s instanceof ConstantNode){
					constant = new ConstantNode(new OpCollectionNode(operator, constant, s).evaluate(null));
					nodes.remove(top);
				}
				else if(s instanceof BinaryOperatorNode && (INVERSE[operator] == ((BinaryOperatorNode)s).operationType)){
					nodes.set(top, ((BinaryOperatorNode)s).l);
					inverseNodes.add(((BinaryOperatorNode)s).r);
				}
				else if(s instanceof OpCollectionNode && ((OpCollectionNode)s).operator == operator){
					nodes.remove(top);
					for(int j = 0; j < ((OpCollectionNode)s).len; j++){
						FormulaNode tn = ((OpCollectionNode)s).data[j];
						nodes.add(top + j, tn);
					}
				}
				else{
					top++;
				}
			}
		}
		
		if(!constant.formulaWeakEquals(IDENTITY[operator])){
			nodes.add(0, constant);
		}
		
		//Attempt to intersimplify between nodes.
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

		//Apply distributative rule.
		
		//TODO I don't know how this handles a case where distribution needs to occur multiple times.
		if(operator == MULTIPLY){
			for(int i = 0; i < nodes.size(); i++){
				if(nodes.get(i) instanceof OpCollectionNode && ((OpCollectionNode)nodes.get(i)).operator == ADD){
//					System.out.println("DISTRIBUTING " + this.asString());
					OpCollectionNode sum = (OpCollectionNode)nodes.remove(i);
					List<FormulaNode> plusNodes = new ArrayList<>();
					for(int j = 0; j < sum.len; j++){
						nodes.add(sum.data[j]);
						plusNodes.add(new OpCollectionNode(nodes, MULTIPLY).takeSimplified());
						nodes.remove(nodes.size() - 1);
					}
					
//					FormulaNode o = new OpCollectionNode(plusNodes, ADD).takeSimplified();
//					System.out.println(asString() + " -DISTRIBUTION> " + o.asString());
					return new OpCollectionNode(plusNodes, ADD).takeSimplified();
				}
			}
		}
		
		FormulaNode l;

		if(nodes.size() == 0){
			l = IDENTITY[operator];
		}
		else if(nodes.size() == 1){
			l = nodes.get(0);
		}
		else{
			l = new OpCollectionNode(nodes.toArray(new FormulaNode[nodes.size()]), nodes.size(), operator);
		}
		
		
//		System.out.println(asString() + " -> " + l.asString() + " / " + inverseNodes);
		
		if(inverseNodes.size() > 0){
			if(inverseNodes.size() == 1){
				return new BinaryOperatorNode(INVERSE[operator], l, inverseNodes.get(0)).takeSimplified();
			}
			else{
				return new BinaryOperatorNode(INVERSE[operator], l, new OpCollectionNode(inverseNodes, operator)).takeSimplified();
			}
		}
		
		else return l;
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

//    	  System.out.println("Evaluating bigO of " + l + " + " + r);
		
	      if(BinaryOperatorNode.xInBigOofY(l, r)){
//	    	  System.out.println(l + " in bigO of " + r);
	    	  return r;
	      }
	      else if(BinaryOperatorNode.xInBigOofY(r, l)){
//	    	  System.out.println(r + " in bigO of " + l);
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
	
	FormulaNode bigO(){
		
		//Condense into mutable list.  Remove any constants in this phase.
		ArrayList<FormulaNode> nodes = new ArrayList<FormulaNode>();
		for(int i = 0; i < len; i++){
			FormulaNode newData = data[i].takeBigO();
			if(!(newData instanceof ConstantNode && (operator == ADD || ((ConstantNode)newData).value > 0))){
				nodes.add(data[i].takeBigO());
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
					
					//A change has occurred (this element might have changed), so restart from this element.
					i--;
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
			if(nn.formulaWeakEquals(this)){
				return this;
			}
			else{
				return nn.takeBigO();
			}
		}
	}
	
	public FormulaNode bigOVarSub(String s, FormulaNode b){
	  
//	  if(true) return this;
	  
	  //TODO this is a heavy function, test it.
	  
	  
	  FormulaNode f = takeBigO();
	  
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
		  if(ret.formulaWeakEquals(this)){
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
	public long formulaWeakHash() {
		long val = 0;
		for(int i = 0; i < len; i++){
			val ^= data[i].formulaWeakHash();
		}
		return val;
	}
	
	@Override
	public long formulaStrongHash(){
		long val = 0;
		for(int i = 0; i < len; i++){
			val ^= circShiftL(data[i].formulaWeakHash(), i * 7);
		}
		return val;
	}
	
	@Override
	public boolean formulaWeakEquals(FormulaNode f) {
		
		if(!(f instanceof OpCollectionNode)){
			return false;
		}
		OpCollectionNode o = (OpCollectionNode)f;
		if(o.len != len || o.operator != operator){
			return false;
		}
		
		boolean[] used = new boolean[len];
		
		//Look for every item.
		for(int i = 0; i < len; i++){
			boolean found = false;
			for(int j = 0; j < len; j++){
				if(used[j]) continue;
				if(data[i].formulaWeakEquals(o.data[j])){
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
	
	public boolean formulaStrongEquals(FormulaNode f){

		if(!(f instanceof OpCollectionNode)){
			return false;
		}
		OpCollectionNode o = (OpCollectionNode)f;
		if(o.len != len || o.operator != operator){
			return false;
		}
		
		for(int i = 0; i < len; i++){
			if(data[i].formulaStrongEquals(o.data[i])) return false;
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
	
	public boolean isConstantRecurse(){
		FormulaNode s = takeSimplified();
		if(s instanceof OpCollectionNode){
			for(int i = 0; i < ((OpCollectionNode)s).len; i++){
				if(!((OpCollectionNode)s).data[i].isConstant()){
					return false;
				}
			}
			return true;
		}
		return s.isConstant();
	}
}
