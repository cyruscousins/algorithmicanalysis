package complexity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class VarSet {
	HashMap<String, Double> vars;
	
	public VarSet(){
		vars = new HashMap<>();
	}
	
	public void put(String s, double d){
		vars.put(s, d);
	}
	public double get(String s){
		return vars.get(s);
	}
	
	public String asString(){
		String r = "{";
		
		Set<String> stringSet = vars.keySet();
		
		String[] strings = stringSet.toArray(new String[stringSet.size()]);
		Arrays.sort(strings);
		
		r += strings[0] + " -> " + vars.get(strings[0]);
		
		for(int i = 1; i < strings.length; i++){
			r += ", " + strings[i] + " -> " + vars.get(strings[i]);
		}
		
		r += "}";
		return r;
	}
}
