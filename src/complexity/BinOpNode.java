package complexity;

public class BinOpNode extends FormulaNode{
  public static final String[] opStrings = new String[]{"+", "-", "*", "/", "^", "log", "choose"};
  
  public static final int NOP = -1; //A value of NOOP represents an invalid operator.
  public static final int ADD = 0, SUBTRACT = 1, MULTIPLY = 2, DIVIDE = 3, EXPONENTIATE = 4, LOGARITHM = 5, CHOOSE = 6;
  
  int operationType;
  FormulaNode l, r;
  
  public BinOpNode(int type, FormulaNode nl, FormulaNode nr){
	  this.operationType = type;
	  this.l = nl;
	  this.r = nr;
  }
  
  public BinOpNode swap(){
	  return new BinOpNode(operationType, r, l);
  }
  
  //TODO this is quite lazy.
  private static double dChoose(double n, double k){
	  return UnaryOperator.dfactorial(n) / (UnaryOperator.dfactorial(k) * UnaryOperator.dfactorial(n - k));
  }
  
  public double eval(VarSet v){
    double v0 = l.eval(v);
    double v1 = r.eval(v);
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
			  return new BinOpNode(MULTIPLY, new ConstantNode(2), nl);
		  	case SUBTRACT:
		  		return new ConstantNode(0);
		  	case MULTIPLY:
		  		return new BinOpNode(EXPONENTIATE, nl, new ConstantNode(2));
		  	case DIVIDE:
		  		if(!nr.formulaEquals(ConstantNode.ZERO)){
			  		return new ConstantNode(1);
		  		}
		  }
	  }
	  
	  //Simplify constant compositions.
	  if(nl instanceof ConstantNode && nr instanceof ConstantNode){
		  if(((ConstantNode)nr).value != 0) return new ConstantNode(new BinOpNode(operationType, nl, nr).eval(null));
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
					return new BinOpNode(operationType, nl, nr);
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
				return new BinOpNode(MULTIPLY, ConstantNode.MINUS_ONE, nr);
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
		  BinOpNode exponential = null;
		  FormulaNode other = null; //unnecessary initialization...
		  if(nr instanceof BinOpNode && ((BinOpNode)nr).operationType == EXPONENTIATE){
			  //TODO exponent * exponent...
			  if(nl instanceof BinOpNode && ((BinOpNode)nl).operationType == EXPONENTIATE){
				  if(((BinOpNode)nl).l.formulaEquals(((BinOpNode)nr).l)){
					  return new BinOpNode(EXPONENTIATE, ((BinOpNode)nl).l, new BinOpNode(joinOp, ((BinOpNode)nl).r, ((BinOpNode)nr).r).simplify());
				  }
			  }
			  
			  exponential = (BinOpNode)nr;
			  other = nl;
		  }
		  else if(nl instanceof BinOpNode && ((BinOpNode)nl).operationType == EXPONENTIATE){
			  exponential = (BinOpNode)nl;
			  other = nr;
		  }
		  
		  if(exponential != null){
			  if(other.formulaEquals(exponential.l)){
				  return new BinOpNode(EXPONENTIATE, exponential.l, new BinOpNode(joinOp, ConstantNode.ONE, exponential.r).simplify());
			  }
		  }
	  }
	  

	  //TODO it might be a good idea to convert all division to exponentiation.
	  
	  // convert 1 / x to x^-1
	  if(operationType == DIVIDE && nl.formulaEquals(ConstantNode.ONE)){
		  return new BinOpNode(EXPONENTIATE, nr, ConstantNode.MINUS_ONE);
	  }
	  
	  //Simplify exponents
	  if(operationType == EXPONENTIATE){
		  
		  //For this form
		  // a ^ log a b
		  if(nr instanceof BinOpNode && ((BinOpNode)nr).operationType == LOGARITHM){
			  FormulaNode expBase = nl;
			  FormulaNode logBase = ((BinOpNode)nr).l;
			  
			  if(expBase.formulaEquals(logBase)){
				  return ((BinOpNode)nr).r;
			  }

			  //TODO Is there a way to simplify with different bases?
		  }
		  
		  //Simplify (a ^ b) ^ c to a ^ (b * c)
		  if(nl instanceof BinOpNode && ((BinOpNode)nl).operationType == EXPONENTIATE){
			  return new BinOpNode(EXPONENTIATE, ((BinOpNode)nl).l, new BinOpNode(MULTIPLY, ((BinOpNode)nl).r, nr)).simplify();
		  }
	  }

	  //Simplify logarithms
	  else if (operationType == LOGARITHM){
		  if(nr instanceof BinOpNode && ((BinOpNode)nr).operationType == EXPONENTIATE){
			  FormulaNode expBase = ((BinOpNode)nr).l;
			  FormulaNode logBase = nl;
			  
			  if(expBase.formulaEquals(logBase)){
				  return ((BinOpNode)nr).r;
			  }
			  
			  FormulaNode expExponent = ((BinOpNode)nr).r;

			  return new BinOpNode(MULTIPLY, expExponent, new BinOpNode(LOGARITHM, logBase, expBase));
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
	  
      return new BinOpNode(operationType, nl, nr);
      
  }
  
  //I'm don't really see a good way to do this in an object oriented manner, so static it is.
  static boolean xInBigOofY(FormulaNode x, FormulaNode y){
	  double xExponent = 1;
	  if(x instanceof BinOpNode && ((BinOpNode)x).operationType == EXPONENTIATE && ((BinOpNode)x).r instanceof ConstantNode) { //Could be a while and a *, for multiplicative rule, but simplifier should take care of that.
	      xExponent = ((ConstantNode)(((BinOpNode)x).r)).value;
		  x = ((BinOpNode)x).l;
	  }
	  return xInBigOofY(x, xExponent, y);
  }
  private static boolean xInBigOofY(FormulaNode x, double xExponent, FormulaNode y){
	  if(y.formulaEquals(x)){
		  if(xExponent <= 1) return true;
	  }
	  else if(y instanceof BinOpNode){
		  BinOpNode by = (BinOpNode)y;
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
				  else return xInBigOofY(new BinOpNode(EXPONENTIATE, x, new BinOpNode(MULTIPLY, ConstantNode.MINUS_ONE, by.r)), xExponent, by.l); //This is the dodgiest line of code here; must test.
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
	  
	  if(simp instanceof BinOpNode){
		BinOpNode nn = (BinOpNode)simp;
		FormulaNode nl = nn.l;
		FormulaNode nr = nn.r;
	  
	    if (nn.operationType == ADD || nn.operationType == MULTIPLY || nn.operationType == SUBTRACT){
	      //One or fewer nodes could be constants, here we throw them out.
	      if(nl instanceof ConstantNode && ((ConstantNode)nl).value > 0){
	    	  if(nn.operationType == SUBTRACT){
	    		  return new BinOpNode(MULTIPLY, ConstantNode.MINUS_ONE, nr.bigO()).bigO();
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
	    	  return new BinOpNode(EXPONENTIATE, nr.bigO(), ConstantNode.MINUS_ONE).bigO();
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
	    }
	    
	    //TODO handle these properly.
		if(nn.operationType == EXPONENTIATE){
			if(nr instanceof UnaryOperator && (((UnaryOperator)nr).operationType == UnaryOperator.CEIL || ((UnaryOperator)nr).operationType == UnaryOperator.FLOOR)){
				return new BinOpNode(EXPONENTIATE, nl, ((UnaryOperator)nr).argument);
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
			
			if(nr instanceof UnaryOperator && ((UnaryOperator)nr).operationType == UnaryOperator.FACTORIAL){

				FormulaNode factoriand = ((UnaryOperator)nr).argument.simplify();
				return new BinOpNode(MULTIPLY, factoriand.bigO(), new BinOpNode(LOGARITHM, nll, factoriand));
			}
			
			return new BinOpNode(LOGARITHM, nll, nr.takeBigO());
		}
		else if(operationType == CHOOSE){
			
			if(nr instanceof ConstantNode){
				return new BinOpNode(EXPONENTIATE, nl, nr).bigO();
			}
			else{
				return nn;
			}
		}
		
		//Handle addition to an exponent.   This gets rid of some lower order terms, but isn't really an effective solution.
		if(nn.operationType == ADD || nn.operationType == SUBTRACT){
			
			boolean negate = false;
			
			BinOpNode exponent = null;
			FormulaNode other = null;
			
			if(nn.l instanceof BinOpNode && ((BinOpNode)nn.l).operationType == EXPONENTIATE){
				exponent = (BinOpNode)nn.l;
				other = nn.r;
			}
			else if(nn.r instanceof BinOpNode && ((BinOpNode)nn.r).operationType == EXPONENTIATE){
				exponent = (BinOpNode)nn.r;
				other = nn.l;
				if(nn.operationType == SUBTRACT) negate = true;
			}
			
			if(exponent != null && exponent.l.formulaEquals(other)){
				if(negate){
					return new BinOpNode(MULTIPLY, ConstantNode.MINUS_ONE, exponent);
				}
				else{
					return exponent;
				}
			}
			
		}
		//TODO it might be a good idea to divide and determine if the result is greater than one as n -> \infty.

		//TODO alternatively, write a searchAdditive function that searches an additive tree for X or X^n for some n >= 1.
		return new BinOpNode(nn.operationType, nl.bigO(), nr.bigO());
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
		  if(xInBigOofY(lSub, rSub)){
			  return rSub;
		  }

		  if(xInBigOofY(rSub, lSub)){
			  return lSub;
		  }
	  }
	  else if(operationType == SUBTRACT){
		  if(xInBigOofY(rSub, lSub) && !xInBigOofY(lSub, rSub)){
			  return lSub;
		  }
	  }
	  
	  return new BinOpNode(operationType, sl, sr);
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
  
  public BinOpNode substitute(String s, FormulaNode f){
	  FormulaNode nl = l.substitute(s, f);
	  FormulaNode nr = r.substitute(s, f);
	  if(nl == l && nr == r){
		  return this; //No change
	  }
	  else return new BinOpNode(operationType, nl, nr);
  }

  long circShiftL(long l, int shift){
	  shift %= 64;
	  return (l << shift) | (l >>> (64 - shift));
  }
  
  public long formulaHash(){
	  return (operationType * 11) ^ circShiftL(l.formulaHash() ^ r.formulaHash(), 5);
  }
  
  public boolean formulaEquals(FormulaNode f){
	  return (f instanceof BinOpNode) && ((BinOpNode)f).operationType == operationType && 
			  ((l.formulaEquals(((BinOpNode)f).l) && r.formulaEquals(((BinOpNode)f).r)) ||
			   (l.formulaEquals(((BinOpNode)f).r) && r.formulaEquals(((BinOpNode)f).l)));
//	  return (f instanceof BinOpNode) && ((BinOpNode)f).operationType == operationType && l.formulaEquals(((BinOpNode)f).l) && r.formulaEquals(((BinOpNode)f).r);
  }
  
  //TODO rather than strongHash and strongEquals, need to have a standardization technique, possibly using hashes?
}