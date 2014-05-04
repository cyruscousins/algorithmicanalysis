package complexity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Maps strings to formulas.  Used for algorithm / data structure operations.
public class VariableMapping {
	HashMap<String, FormulaNode> mapping;
	List<String> varNames;
	
	public VariableMapping(){
		varNames = new ArrayList<String>();
		mapping = new HashMap<String, FormulaNode>();
	}
	
	//Creates a new variable mapping that contains all the mapping in a, each with b applied.
	public static VariableMapping compose(VariableMapping a, VariableMapping b){
		VariableMapping comp = new VariableMapping();
		
		for(int i = 0; i < a.varNames.size(); i++){
			String s = a.varNames.get(i);
			FormulaNode f = a.mapping.get(s);
			comp.put(s, b.apply(f));
		}
		
		return comp;
	}
	
	public void put(String s, FormulaNode f){
		if(!mapping.containsKey(s)){
			varNames.add(s);
		}
		mapping.put(s, f);
	}
	
	public FormulaNode apply(FormulaNode f){
		for(int i = 0; i < varNames.size(); i++){
			f = f.substitute(varNames.get(i), mapping.get(varNames.get(i)));
		}
		return f;
	}
}
