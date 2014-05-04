package algorithm;

import java.util.HashMap;

public abstract class PriorityQueue extends Algorithm{
	
	protected static final String[] functionNames = new String[]{"insert", "removeMin", "isEmpty", "size", "decreaseKey"};
	public final String[] getFunctionNames(){
		return functionNames;
	}
	
	public abstract boolean isEmpty();
	public abstract int removeMax();
	public abstract void put();
	
	TextStream text;
	
	public PriorityQueue(TextStream text, HashMap<String, Function> functions){
		this.text = text;
		this.functions = functions;
	}
}
