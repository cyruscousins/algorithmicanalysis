package complexity;

public class OpCollectionNode extends FormulaNode{
	public static final int ADD = 0, MULTIPLY = 1;
	int operator;
	
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
			case MULTIPLY:
				val = 1;
				for(int i = 0; i < data.length; i++){
					val *= data[i].eval(v);
				}
			default:
				val = Double.NaN;
		}
		return val;
	}
	
	public FormulaNode simplify(){
		
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
	}
	
	public FormulaNode bigO(){
		//FormulaNode newNode = new FormulaNode()
		FormulaNode f = simplify();
		if(f instanceof OpCollectionNode){
			OpCollectionNode node = (OpCollectionNode)f;
			//Filter
			boolean[] good = new boolean[len];
			for(int i = 0; i < len; i++){
				//if
			}
		}
		else return f;
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
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public boolean formulaEquals(FormulaNode f) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String asString() {
		// TODO Auto-generated method stub
		return null;
	}
}
