package complexity;

public class BinaryOperatorNode extends FormulaNode{
  public static final String[] opStrings = new String[]{"+", "-", "*", "/", "^", "log", "choose"};
  
  public static final int NOP = -1; //A value of NOOP represents an invalid operator.
  public static final int ADD = 0, SUBTRACT = 1, MULTIPLY = 2, DIVIDE = 3, EXPONENTIATE = 4, LOGARITHM = 5, CHOOSE = 6;
  
  int operationType;
  FormulaNode l, r;
  
  public BinaryOperatorNode(int type, FormulaNode nl, FormulaNode nr){
	  this.operationType = type;
	  this.l = nl;
	  this.r = nr;
  }
  
  public BinaryOperatorNode swap(){
	  return new BinaryOperatorNode(operationType, r, l);
  }
  
  //TODO this is quite lazy.
  private static double dChoose(double n, double k){
	  return UnaryOperatorNode.dfactorial(n) / (UnaryOperatorNode.dfactorial(k) * UnaryOperatorNode.dfactorial(n - k));
  }
  
  public double evaluate(VarSet v){
    double v0 = l.evaluate(v);
    double v1 = r.evaluate(v);
    switch (operationType){
      case ADD:
        return v0 + v1;
      case SUBTRACT:
        return v0 - v1;
      case MULTIPLY:
        return v0 * v1;
      case DIVIDE:
        return v0 / v1;
      case EXPONENTIATE:
        return Math.pow(v0, v1);
      case LOGARITHM:
        return Math.log(v1) / Math.log(v0);  //log base v0 of v1.
      case CHOOSE:
    	return dChoose(v0, v1);
      default:
    	return -1;
    }
  }
  
  //////////////////
  //SIMPLIFICATION//
  //////////////////
  
  //TODO CHOOSE!
  
  //TODO distributative law!
  public FormulaNode simplify(){

	    FormulaNode nl = l.simplify();
	    FormulaNode nr = r.simplify();
	    
	  //Simplify variable compositions.
	  if(nl.formulaEquals(nr)){
		  //Same variable composition.
		  switch(operationType){
		  	case ADD:
			  return new BinaryOperatorNode(MULTIPLY, new ConstantNode(2), nl);
		  	case SUBTRACT:
		  		return new ConstantNode(0);
		  	case MULTIPLY:
		  		return new BinaryOperatorNode(EXPONENTIATE, nl, new ConstantNode(2));
		  	case DIVIDE:
		  		if(!nr.formulaEquals(ConstantNode.ZERO)){
			  		return new ConstantNode(1);
		  		}
		  }
	  }
	  
	  //Simplify constant compositions.
	  if(nl instanceof ConstantNode && nr instanceof ConstantNode){
		  if(((ConstantNode)nr).value != 0) return new ConstantNode(new BinaryOperatorNode(operationType, nl, nr).evaluate(null));
	  }
	  
	  //Operation with constant properties.
	  //Must check right first to handle (ie ignore for now) divide by 0.
	  if(nr instanceof ConstantNode){
			if(((ConstantNode)nr).value == 0){
				if(operationType == ADD || operationType == SUBTRACT){
					return nl;
				}
				else
					if(operationType == MULTIPLY){
					return ConstantNode.ZERO;
				}
				else if(operationType == DIVIDE){
					// Divide by 0.  Must explicitly ignore it here.
					return new BinaryOperatorNode(operationType, nl, nr);
				}
				else if(operationType == EXPONENTIATE){
					return ConstantNode.ONE;
				}
			}
			else if(((ConstantNode)nr).value == 1){
				if(operationType == MULTIPLY || operationType == DIVIDE || operationType == EXPONENTIATE){
					return nl;
				}
			}
	  }
	  
	  if(nl instanceof ConstantNode){
		if(((ConstantNode)nl).value == 0){
			if(operationType == ADD){
				return nr;
			}
			else if(operationType == SUBTRACT){
				return new BinaryOperatorNode(MULTIPLY, ConstantNode.MINUS_ONE, nr);
			}
			else if(operationType == MULTIPLY){
				return ConstantNode.ZERO;
			}
			else if(operationType == DIVIDE){
				if(nr instanceof ConstantNode && ((ConstantNode)nr).value == 0){
					// 0/0.  Don't handle it here.
					return this;
				}
				else return ConstantNode.MINUS_ONE;
			}
			else if(operationType == EXPONENTIATE){
				return ConstantNode.ZERO;
			}
		}
		else if(((ConstantNode)nl).value == 1){
			if(operationType == MULTIPLY){
				return nr;
			}
			else if(operationType == EXPONENTIATE){
				return ConstantNode.ONE;
			}
		}
	  }
	  
	  //Subtractive inverse
	  if(operationType == SUBTRACT && nl.formulaEquals(nr)){
		  return ConstantNode.ZERO;
	  }
	  //Divisive inverse
	  else if(operationType == DIVIDE && nl.formulaEquals(nr)){
		  return ConstantNode.ONE;
	  }
	  
	  //Simplify exponent multiplication and division.
	  if(operationType == MULTIPLY || operationType == DIVIDE){
		  int joinOp;
		  if(operationType == MULTIPLY){
			  joinOp = ADD;
		  }
		  else{
			  joinOp = SUBTRACT;
		  }
		  BinaryOperatorNode exponential = null;
		  FormulaNode other = null; //unnecessary initialization...
		  if(nr instanceof BinaryOperatorNode && ((BinaryOperatorNode)nr).operationType == EXPONENTIATE){
			  //TODO exponent * exponent...
			  if(nl instanceof BinaryOperatorNode && ((BinaryOperatorNode)nl).operationType == EXPONENTIATE){
				  if(((BinaryOperatorNode)nl).l.formulaEquals(((BinaryOperatorNode)nr).l)){
					  return new BinaryOperatorNode(EXPONENTIATE, ((BinaryOperatorNode)nl).l, new BinaryOperatorNode(joinOp, ((BinaryOperatorNode)nl).r, ((BinaryOperatorNode)nr).r).simplify());
				  }
			  }
			  
			  exponential = (BinaryOperatorNode)nr;
			  other = nl;
		  }
		  else if(nl instanceof BinaryOperatorNode && ((BinaryOperatorNode)nl).operationType == EXPONENTIATE){
			  exponential = (BinaryOperatorNode)nl;
			  other = nr;
		  }
		  
		  if(exponential != null){
			  if(other.formulaEquals(exponential.l)){
				  return new BinaryOperatorNode(EXPONENTIATE, exponential.l, new BinaryOperatorNode(joinOp, ConstantNode.ONE, exponential.r).simplify());
			  }
		  }
	  }
	  

	  //TODO it might be a good idea to convert all division to exponentiation.
	  
	  // convert 1 / x to x^-1
	  if(operationType == DIVIDE && nl.formulaEquals(ConstantNode.ONE)){
		  return new BinaryOperatorNode(EXPONENTIATE, nr, ConstantNode.MINUS_ONE);
	  }
	  
	  //Simplify exponents
	  if(operationType == EXPONENTIATE){
		  
		  //For this form
		  // a ^ log a b
		  if(nr instanceof BinaryOperatorNode && ((BinaryOperatorNode)nr).operationType == LOGARITHM){
			  FormulaNode expBase = nl;
			  FormulaNode logBase = ((BinaryOperatorNode)nr).l;
			  
			  if(expBase.formulaEquals(logBase)){
				  return ((BinaryOperatorNode)nr).r;
			  }

			  //TODO Is there a way to simplify with different bases?
		  }
		  
		  //Simplify (a ^ b) ^ c to a ^ (b * c)
		  if(nl instanceof BinaryOperatorNode && ((BinaryOperatorNode)nl).operationType == EXPONENTIATE){
			  return new BinaryOperatorNode(EXPONENTIATE, ((BinaryOperatorNode)nl).l, new BinaryOperatorNode(MULTIPLY, ((BinaryOperatorNode)nl).r, nr)).simplify();
		  }
	  }

	  //Simplify logarithms
	  else if (operationType == LOGARITHM){
		  if(nr instanceof BinaryOperatorNode && ((BinaryOperatorNode)nr).operationType == EXPONENTIATE){
			  FormulaNode expBase = ((BinaryOperatorNode)nr).l;
			  FormulaNode logBase = nl;
			  
			  if(expBase.formulaEquals(logBase)){
				  return ((BinaryOperatorNode)nr).r;
			  }
			  
			  FormulaNode expExponent = ((BinaryOperatorNode)nr).r;

			  return new BinaryOperatorNode(MULTIPLY, expExponent, new BinaryOperatorNode(LOGARITHM, logBase, expBase));
		  }
	  }
	  
	  else if (operationType == CHOOSE){
		  
		  //TODO what is 0 choose 0?
		  if(nr.formulaEquals(nl)){
			  return ConstantNode.ONE;
		  }
		  
		  //
		  if(nr instanceof ConstantNode){
			  if(((ConstantNode)nr).value == 0){
				  return ConstantNode.ONE;
			  }
			  if(((ConstantNode)nr).value == 1){
				  return nl;
			  }
			  
			  //Can't make simplifications without knowing the value of n.
			  
//			  int k = (int)((ConstantNode)nr).value;
//			  for(int i = 1; i < k; i++){
//				  
//			  }
//			  FormulaNode
		  }
	  }

	  //No known simplifications.  Standardize the order for commutative operations.
	  
	  //TODO order standardization.  
	  //Form a tree of the form
	  //a + bx + cx^2 + dx^3 ...
	  
//	  //TODO this doesn't work
//	  else if (operationType == ADD || operationType == MULTIPLY){
//		  //Constants move left
//		  if(nr instanceof ConstantNode){ 
//			  return new BinOpNode(operationType, nr, nl); //Swap two leaves
//		  }
//		  else if(nr instanceof BinOpNode && ((BinOpNode)nr).operationType == operationType){ //Must be the same type of operation.
//			  //Tree rotation.
//		  }
//		  //Shift greater exponents with the same base right.
//		  else if(nl instanceof BinOpNode && ((BinOpNode)nl).operationType == EXPONENTIATE && nr instanceof BinOpNode && ((BinOpNode)nr).operationType == EXPONENTIATE && ((BinOpNode)nl).l.formulaStrongEquals(((BinOpNode)nr).l)){
//			  return new Bin
//		  }
//	  }
//	  
	  
	  //Augment or form groups
	  
	  if(operationType == ADD || operationType == MULTIPLY){
		  int collectionType = OpCollectionNode.MULTIPLY;
		  if(operationType == ADD){
			  collectionType = OpCollectionNode.ADD;
		  }
		  else{
			  collectionType = OpCollectionNode.MULTIPLY;
		  }
		  
		  if(nl instanceof OpCollectionNode && ((OpCollectionNode) nl).operator == collectionType){
			  OpCollectionNode nlo = (OpCollectionNode) nl;
			  if(nr instanceof OpCollectionNode){
				  return nlo.combine((OpCollectionNode) nr).simplify();
			  }
			  else return nlo.pushLast(nr).simplify();
		  }
		  else if(nr instanceof OpCollectionNode && (((OpCollectionNode)nr).operator == collectionType)){
			  return ((OpCollectionNode)nr).pushFirst(nl).simplify();
		  }
		  
	  }
	  
      return new BinaryOperatorNode(operationType, nl, nr);
      
  }
  
  //I'm don't really see a good way to do this in an object oriented manner, so static it is.
  static boolean xInBigOofY(FormulaNode x, FormulaNode y){
	  double xExponent = 1;
	  if(x instanceof BinaryOperatorNode && ((BinaryOperatorNode)x).operationType == EXPONENTIATE && ((BinaryOperatorNode)x).r instanceof ConstantNode) { //Could be a while and a *, for multiplicative rule, but simplifier should take care of that.
	      xExponent = ((ConstantNode)(((BinaryOperatorNode)x).r)).value;
		  x = ((BinaryOperatorNode)x).l;
	  }
	  return xInBigOofY(x, xExponent, y);
  }
  private static boolean xInBigOofY(FormulaNode x, double xExponent, FormulaNode y){
	  if(y.formulaEquals(x)){
		  if(xExponent <= 1) return true;
	  }
	  else if(y instanceof BinaryOperatorNode){
		  BinaryOperatorNode by = (BinaryOperatorNode)y;
		  if(by.operationType == EXPONENTIATE){
			  if(x.formulaEquals(by.l)){
				  if(by.r instanceof ConstantNode && ((ConstantNode)by.r).value >= xExponent){ //TODO doesn't handle something like x ^ x.  Need something stronger here.
					  return true;
				  }
			  }
			  else{
				  //Not the same base.  However, x may be contained in the base.
				  
				  //For a situation like x in bigO( a ^ b ), we must return x ^ -b in bigO(a)
				  //Here we handle b as a constant separately.
				  if(by.r instanceof ConstantNode){
					  return xInBigOofY(x, xExponent - ((ConstantNode)by.r).value, by.l);
				  }
				  
				  //TODO we are testing this with ((n * m) + (n ^ m)) -> ((n * m) + (n ^ m)) != (n ^ m).  I believe the error is in SIMPLIFY, in that (n * m) / m is not properly simplified to n.
				  else return xInBigOofY(new BinaryOperatorNode(EXPONENTIATE, x, new BinaryOperatorNode(MULTIPLY, ConstantNode.MINUS_ONE, by.r)), xExponent, by.l); //This is the dodgiest line of code here; must test.
			  }
			  
		  }
		  else if(by.operationType == ADD || by.operationType == MULTIPLY){
			  return xInBigOofY(x, xExponent, by.l) || xInBigOofY(x, xExponent, by.r); //either side can do it.  Here we assume that we don't have a stupid query like "is x in (x^2 - x^2)" or "is x in (x ^ 2 / x)".  I'm not sure if we really have this guarantee.
			  //TODO we absolutely need to guarantee that the item in question works like the above dictates it must.  Otherwise this is a critical flaw in that it produces an inaccurate, rather than nonnormative, answer.
		  }
	  }
	  else{
		  
	  }
	  return false;
  }
  
  public FormulaNode bigO(){
	  
	  FormulaNode simp = simplify();
	  
	  if(simp instanceof BinaryOperatorNode){
		BinaryOperatorNode nn = (BinaryOperatorNode)simp;
		FormulaNode nl = nn.l;
		FormulaNode nr = nn.r;
	  
	    if (nn.operationType == ADD || nn.operationType == MULTIPLY || nn.operationType == SUBTRACT){
	      //One or fewer nodes could be constants, here we throw them out.
	      if(nl instanceof ConstantNode && ((ConstantNode)nl).value > 0){
	    	  if(nn.operationType == SUBTRACT){
	    		  return new BinaryOperatorNode(MULTIPLY, ConstantNode.MINUS_ONE, nr.bigO()).bigO();
	    	  }
	        return nr.bigO();
	      }
	      else if(nr instanceof ConstantNode && ((ConstantNode)nr).value > 0){
	        	return nl.bigO();
	      }
	      
	    }
	    
	    else if (nn.operationType == DIVIDE){
	      if(nr instanceof ConstantNode && ((ConstantNode)nr).value > 0){
	    	  return nl.bigO();
	      }
	      else if(nl instanceof ConstantNode){
	    	  return new BinaryOperatorNode(EXPONENTIATE, nr.bigO(), ConstantNode.MINUS_ONE).bigO();
	      }
	      
	      //TOTAL HACK:
	      else if(nl instanceof BinaryOperatorNode && ((BinaryOperatorNode)nl).l.formulaEquals(nr)){
	    	  return ((BinaryOperatorNode)nl).r;
	      }
	      else if(nl instanceof BinaryOperatorNode && ((BinaryOperatorNode)nl).r.formulaEquals(nr)){
	    	  return ((BinaryOperatorNode)nl).l;
	      }
	      
	      
	    }
	    
	    //Check if one op is bigo of the other.
	    if(nn.operationType == ADD){
		      if(xInBigOofY(nl, nr)){
		    	  return nr;
		      }
		      if(xInBigOofY(nr, nl)){
		    	  return nl;
		      }
	    }
	    
	    //For substitution, we must have one way containment but NOT the other.  TODO This needs to be made more formal.
	    if(nn.operationType == SUBTRACT){
	    	if(xInBigOofY(nr, nl) && !xInBigOofY(nl, nr)){
	    		return nl;
	    	}
	    	
	    	//TODO this is not inaccurate, but sometimes these are 0s?
		      if(xInBigOofY(nl, nr)){
		    	  return nr;
		      }
		      if(xInBigOofY(nr, nl)){
		    	  return nl;
		      }
	    	
	    }
	    
	    //TODO handle these properly.
		if(nn.operationType == EXPONENTIATE){
			if(nl instanceof ConstantNode && nr instanceof BinaryOperatorNode){
				//This is a bit of a hack to get rid of problems like 2 ^ (1 + n)
				BinaryOperatorNode nrb = (BinaryOperatorNode)nr;
				if(nrb.operationType == ADD || nrb.operationType == MULTIPLY){
					if(nrb.l instanceof ConstantNode){
						nr = nrb.r;
					}
					if(nrb.r instanceof ConstantNode){
						nr = nrb.l;
					}
				}
			}
			if(nr instanceof UnaryOperatorNode && (((UnaryOperatorNode)nr).operationType == UnaryOperatorNode.CEIL || ((UnaryOperatorNode)nr).operationType == UnaryOperatorNode.FLOOR)){
				return new BinaryOperatorNode(EXPONENTIATE, nl, ((UnaryOperatorNode)nr).argument);
			}
			else return nn;
		} else if(operationType == LOGARITHM){
			FormulaNode nll;
			
			if(nl instanceof ConstantNode){
				nll = ConstantNode.E;
			}
			else{
				nll = nl;
			}
			
			if(nr instanceof UnaryOperatorNode && ((UnaryOperatorNode)nr).operationType == UnaryOperatorNode.FACTORIAL){

				FormulaNode factoriand = ((UnaryOperatorNode)nr).argument.simplify();
				return new BinaryOperatorNode(MULTIPLY, factoriand.bigO(), new BinaryOperatorNode(LOGARITHM, nll, factoriand));
			}
			
			return new BinaryOperatorNode(LOGARITHM, nll, nr.takeBigO());
		}
		else if(operationType == CHOOSE){
			
			if(nr instanceof ConstantNode){
				return new BinaryOperatorNode(EXPONENTIATE, nl, nr).bigO();
			}
			else{
				return nn;
			}
		}
		
		//Handle addition to an exponent.   This gets rid of some lower order terms, but isn't really an effective solution.
		if(nn.operationType == ADD || nn.operationType == SUBTRACT){
			
			boolean negate = false;
			
			BinaryOperatorNode exponent = null;
			FormulaNode other = null;
			
			if(nn.l instanceof BinaryOperatorNode && ((BinaryOperatorNode)nn.l).operationType == EXPONENTIATE){
				exponent = (BinaryOperatorNode)nn.l;
				other = nn.r;
			}
			else if(nn.r instanceof BinaryOperatorNode && ((BinaryOperatorNode)nn.r).operationType == EXPONENTIATE){
				exponent = (BinaryOperatorNode)nn.r;
				other = nn.l;
				if(nn.operationType == SUBTRACT) negate = true;
			}
			
			if(exponent != null && exponent.l.formulaEquals(other)){
				if(negate){
					return new BinaryOperatorNode(MULTIPLY, ConstantNode.MINUS_ONE, exponent);
				}
				else{
					return exponent;
				}
			}
			
		}
		//TODO it might be a good idea to divide and determine if the result is greater than one as n -> \infty.

		//TODO alternatively, write a searchAdditive function that searches an additive tree for X or X^n for some n >= 1.
		return new BinaryOperatorNode(nn.operationType, nl.bigO(), nr.bigO());
	  }
	  else{
		  return simp.bigO();
	  }
  }

  FormulaNode bigOVarSub(String x, FormulaNode y){
	  
//	  if(!(operationType == ADD || operationType == MULTIPLY || operationType == DIVIDE)) return this;
	  
	  FormulaNode sl = l.bigOVarSub(x, y);
	  FormulaNode sr = r.bigOVarSub(x, y);

	  FormulaNode lSub = sl.substitute(x, y).bigO();
	  FormulaNode rSub = sr.substitute(x, y).bigO();
	  
	  if(operationType == ADD){
		  if(xInBigOofY(lSub, sr)){
			  return sr;
		  }

		  if(xInBigOofY(rSub, sl)){
			  return sl;
		  }
		  
		  //TODO do both ever need to be substituted?
		  
		  /*
		  
		  if(xInBigOofY(lSub, rSub)){
			  return rSub;
		  }

		  if(xInBigOofY(rSub, lSub)){
			  return lSub;
		  }
		  
		   */
		  
		  
	  }
	  else if(operationType == SUBTRACT){
		  if(xInBigOofY(rSub, sl) && !xInBigOofY(sl, sr)){
			  return sl;
		  }
	  }
	  
//	  else if (operationType == DIVISION){
//		  if(xInBigOofY(rSub, sl) && !xInBigOofY(sl, sr))){
//			  return sl;
//		  }
//	  }
	  
	  return new BinaryOperatorNode(operationType, sl, sr);
  }
  
  public String asStringRecurse(){
	  if(operationType == LOGARITHM && l instanceof ConstantNode){
		  if(l == ConstantNode.E){
			  return "log(" + r.asStringRecurse() + ")";
		  }
		  else{
			  return "log_" + l.asStringRecurse() + "(" + r.asStringRecurse() + ")";
		  }
	  }
    return "(" + l.asStringRecurse() + " " + opStrings[operationType] + " " + r.asStringRecurse() + ")";
  }
  
  public String asLatexStringRecurse(){
	  if(operationType == LOGARITHM){
		  if (l == ConstantNode.E){
			  return "\\log(" + trimParens(r.asLatexStringRecurse()) + ")";
			 }
		  else{
			  return "\\log_{" + trimParens(l.asLatexStringRecurse()) + "}(" + trimParens(r.asLatexStringRecurse()) + ")";
		  }
	  }
	  else if(operationType == DIVIDE){
		  return "\\frac{" + trimParens(l.asLatexStringRecurse()) + "}{" + trimParens(r.asLatexStringRecurse()) + "}";
	  }
	  else if(operationType == MULTIPLY){
		  return "(" + l.asLatexStringRecurse() + " \\cdot " + r.asLatexStringRecurse() + ")";
	  }
	  else if(operationType == EXPONENTIATE){
		  String base = l.asLatexStringRecurse();
		  String exponent = trimParens(r.asLatexStringRecurse());
		  return base + " ^ {" + exponent + "}";
//		  if(l instanceof BinOpNode && ((BinOpNode)l).operationType == DIVIDE){
//			  return base + " ^ " + exponent;
//		  }
//		  else{
//			  return trimParens(base) + " ^ {" + exponent + "}";
//		  }
	  }
	  else if (operationType == CHOOSE){
		  return "\\binom{" + trimParens(l.asLatexStringRecurse()) + "}{" + trimParens(r.asLatexStringRecurse()) + "}";
	  }
	  else return "(" + l.asLatexStringRecurse() + " " + opStrings[operationType] + " " + r.asLatexStringRecurse() + ")";
  }
  
  public BinaryOperatorNode substitute(String s, FormulaNode f){
	  FormulaNode nl = l.substitute(s, f);
	  FormulaNode nr = r.substitute(s, f);
	  if(nl == l && nr == r){
		  return this; //No change
	  }
	  else return new BinaryOperatorNode(operationType, nl, nr);
  }

  public long formulaHash(){
	  return (operationType * 11) ^ circShiftL(l.formulaHash() ^ r.formulaHash(), 5);
  }
  
  public boolean formulaEquals(FormulaNode f){
	  return (f instanceof BinaryOperatorNode) && ((BinaryOperatorNode)f).operationType == operationType && 
			  ((l.formulaEquals(((BinaryOperatorNode)f).l) && r.formulaEquals(((BinaryOperatorNode)f).r)) ||
			   (l.formulaEquals(((BinaryOperatorNode)f).r) && r.formulaEquals(((BinaryOperatorNode)f).l)));
//	  return (f instanceof BinOpNode) && ((BinOpNode)f).operationType == operationType && l.formulaEquals(((BinOpNode)f).l) && r.formulaEquals(((BinOpNode)f).r);
  }
  
  //TODO rather than strongHash and strongEquals, need to have a standardization technique, possibly using hashes?
}