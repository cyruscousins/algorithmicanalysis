package algorithm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import complexity.Formula;
import complexity.FormulaNode;
import complexity.FormulaParser;
import complexity.VariableMapping;
import complexity.VariableNode;

public class Algorithm {
	//An algorithm is represented as a collection of functions.

	public String name;
	public AbstractDataType type;
	public HashMap<String, Function> functions;
	
	public Algorithm(AbstractDataType type, String name, HashMap<String, Function> functions) {
		this.type = type;
		this.name = name;
		this.functions = functions;
	}
	
	public String[] summarizeAlgorithm(){
		ArrayList<String> summary = new ArrayList<String>();
		
//		summary.add("\\subsection{" + name + " $\\in$ " + type.name + "}");
		summary.add("\\subsection{" + name.replaceAll("_", "\\\\_") + ", a " + type.name.replaceAll("_", "\\\\_") + "}");
		for(String analysisType: type.analysisTypes){
			summary.add("\\subsubsection{" + analysisType + " analysis}");
			for(String s: functions.keySet()){
				String fCost = functions.get(s).costs.get(analysisType).asLatexString();
				String fCostBigO = functions.get(s).costs.get(analysisType).takeBigO().asLatexString();
				if(fCost.equals(fCostBigO)){
					summary.add("\\texttt{" + s.replaceAll("_", "\\\\_") + "}" + " $ \\in \\bigO\\big(" + fCost + "\\big)$");

				} else{
					summary.add("\\texttt{" + s.replaceAll("_", "\\\\_") + "}" + " $ \\in \\bigO\\big(" + fCost + "\\big) \\in \\bigO \\big(" + fCostBigO + "\\big)$");
				}
			}
		}
		return summary.toArray(new String[summary.size()]);
	}
	
	public String[] summarizeAlgorithmSimple(){
		ArrayList<String> summary = new ArrayList<String>();
		
		summary.add(("\\textbf{" + name.replaceAll("_", "\\\\_") + ", a " + type.name.replaceAll("_", "\\\\_") + "}\n").replaceAll("log\\_", "log_"));
//		summary.add(("\\subsection{}{" + name.replaceAll("_", "\\\\_") + ", a " + type.name.replaceAll("_", "\\\\_") + "}\n").replaceAll("log\\_", "log_"));
//		summary.add("\\subsection{" + name + " $\\in$ " + type.name + "}");
//		summary.add(("\n\n" + name.replaceAll("_", "\\\\_") + ", a " + type.name.replaceAll("_", "\\\\_") + "\n").replaceAll("log\\_", "log_"));
		for(String analysisType: type.analysisTypes){
			summary.add("\\paragraph{" + analysisType + " analysis}");
			for(String s: functions.keySet()){
				String fCost = functions.get(s).costs.get(analysisType).asLatexString();
				String fCostBigO = functions.get(s).costs.get(analysisType).takeBigO().asLatexString();
				if(fCost.equals(fCostBigO)){
					summary.add("\\texttt{" + s.replaceAll("_", "\\\\_") + "}" + " $ \\in \\bigO\\big(" + fCost + "\\big)$");

				} else{
					summary.add("\\texttt{" + s.replaceAll("_", "\\\\_") + "}" + " $ \\in \\bigO\\big(" + fCost + "\\big) \\in \\bigO \\big(" + fCostBigO + "\\big)$");
				}
			}
		}
		return summary.toArray(new String[summary.size()]);
	}
	
	public Algorithm substituteIn(String infoString, String v0, FormulaNode toSub){
		String newName = name + infoString;
		AbstractDataType newType = type;
		HashMap<String, Function> newFunctions = new HashMap<String, Function>();

		for(Function f : functions.values()){
			HashMap<String, FormulaNode> newCosts = new HashMap<String, FormulaNode>();
			
			for(String analysisType: type.analysisTypes){
				FormulaNode newNode = f.costs.get(analysisType).substitute(v0, toSub);
				
				newCosts.put(analysisType, newNode);
			}
			Function newFunction = new Function(f.name, newCosts);
			newFunctions.put(f.name, newFunction);
		}
		
		return new Algorithm(newType, newName, newFunctions);
	}

	public Algorithm substituteIn(Algorithm a, String[] functionsToSub, VariableMapping postSubstitution){
		
		String newName = name + " with " + a.name;
		AbstractDataType newType = type;
		HashMap<String, Function> newFunctions = new HashMap<String, Function>();
		
		
		for(Function f : functions.values()){
			HashMap<String, FormulaNode> newCosts = new HashMap<String, FormulaNode>();
			
			for(String analysisType: type.analysisTypes){
				FormulaNode newNode = f.costs.get(analysisType);

				//Perform function substitutions
				for(String functionToSub: functionsToSub){
//					System.out.println(functionToSub + ":");
//					System.out.println(newNode);
//					System.out.println(a.functions);
//					System.out.println(a.functions.get(functionToSub));
					newNode = newNode.substitute(functionToSub, a.functions.get(functionToSub).costs.get(analysisType));
				}
				
				if(postSubstitution != null) newNode = postSubstitution.apply(newNode); //Perform postsubstitutions (to convert between variable spaces)
				
				newCosts.put(analysisType, newNode);
			}
			Function newFunction = new Function(f.name, newCosts);
			newFunctions.put(f.name, newFunction);
		}
		
		return new Algorithm(newType, newName, newFunctions);
		
//		for(String analysisType: type.analysisTypes){
//			//TODO check that a has this analysis type as well.
//			
//			VariableMapping v = new VariableMapping();
//			//For everything I want to sub in
//			for(int i = 0; i < toSub.length; i++){
//				v.put(toSub[i], a.functions.get(toSub[i]).costs.get(analysisType));
//			}
//			
//			for(Function f: functions.values()){
//				functions.put(f.name, value);
//			}
//			a.type.variables;
//		}
	}
	
//	public Algorithm substituteIn(Algorithm helper, VariableMapping mapping){
//		
//	}
	

//	
//	//TODO need to do inputs.
//	//TODO might be best to improve the substitute function, substitute a SET of strings for a SET of formula nodes.
//	//TODO also apply a postsubstitution.  It's not enough to swap n for removeMin, we need to swap n for removeMin, and then swap e for n.
//	public FormulaNode substituteAlgorithm(String analysisType, FormulaNode formula, VariableMapping mapping){
//		FormulaNode result = formula;
//		for(int i = 0; i < functionNames.length; i++){
//			Function f = functions.get(functionNames[i]);
//			
//			FormulaNode form = f.costs.get(analysisType); //Get the formula
//			if(form == null){
//				System.err.println("Function " + functionNames[i] + " or analysis type " + analysisType + " not found for class " + this.getClass().getName() + ".");
//				continue;
//			}
//			
//			form = mapping.apply(form);  //Use the mapping to convert from the inner algorithms variables to whatever.  For example, a priority queue algorithm has variable n (size), and a Djikstra's has |v|, |e|.  mapping is used to map n to |e|.
//			
//			result = result.substitute(f.name, form);
//		}
//		
//		return result;
//	}
	
	public static void Main(String[] args){
		
		//TODO skiplists, trees, types of ordered array, fibonacci, and splay.
		
//		Algorithm unorderedArray;
//		Algorithm orderedArray;
//		Algorithm heap;

		//UNORDERED ARRAY
		{
			String[] functionNames = new String[]{"insert", "removeMin", "isEmpty", "size", "decreaseKey"};
			String[] variableNames = new String[]{"n"};
			
			FormulaNode[] worstCosts = FormulaParser.parseFormulae(    "n|n|1|1|n", "\\|");
			FormulaNode[] amortizedCosts = FormulaParser.parseFormulae("1|n|1|1|n", "\\|");
			FormulaNode[] expectedCosts = FormulaParser.parseFormulae( "1|n|1|1|n", "\\|");
			
			HashMap<String, Function> functions = new HashMap<>();
			
			for(int i = 0; i < functionNames.length; i++){
				HashMap<String, FormulaNode> complexity = new HashMap<String, FormulaNode>();
				complexity.put("worst case cost", worstCosts[i]);
				complexity.put("worst case bigO", worstCosts[i].takeBigO());
				
				//TODO use takeBigO().  Write reader for this datatype
	
				complexity.put("amortized cost", amortizedCosts[i]);
				complexity.put("amortized bigO", amortizedCosts[i].takeBigO());
				
				complexity.put("expected cost", expectedCosts[i]);
				complexity.put("expected bigO", expectedCosts[i].takeBigO());
				
				Function f = new Function(functionNames[i], complexity);
				
				functions.put(functionNames[i], f);
			}
			
			unorderedArray = new Algorithm(null, "Unordered Array", functions, functionNames, variableNames);
		}

		//ORDERED ARRAY
		{
			String[] functionNames = new String[]{"insert", "removeMin", "isEmpty", "size", "decreaseKey"};
			String[] variableNames = new String[]{"n"};
			
			//Ignoring shifting (circular)
			FormulaNode[] worstCosts = FormulaParser.parseFormulae(    "n|1|1|1|2 log n", "\\|");
			FormulaNode[] amortizedCosts = FormulaParser.parseFormulae("n|1|1|1|2 log n", "\\|");
			FormulaNode[] expectedCosts = FormulaParser.parseFormulae( "n|1|1|1|2 log n", "\\|");
			
			HashMap<String, Function> functions = new HashMap<>();
			
			for(int i = 0; i < functionNames.length; i++){
				HashMap<String, FormulaNode> complexity = new HashMap<String, FormulaNode>();
				complexity.put("worst case cost", worstCosts[i]);
				complexity.put("worst case bigO", worstCosts[i].takeBigO());
	
				complexity.put("amortized cost", amortizedCosts[i]);
				complexity.put("amortized bigO", amortizedCosts[i].takeBigO());
				
				complexity.put("expected cost", expectedCosts[i]);
				complexity.put("expected bigO", expectedCosts[i].takeBigO());
				
				Function f = new Function(functionNames[i], complexity);
				
				functions.put(functionNames[i], f);
			}
			
			orderedArray = new Algorithm(null, "Ordered Array", functions, functionNames, variableNames);
		}

		//TODO loader and filetype for this.
		
		//HEAP
		{
			String[] functionNames = new String[]{"insert", "removeMin", "isEmpty", "size", "decreaseKey"};
			String[] variableNames = new String[]{"n"};
			
			FormulaNode[] worstCosts = FormulaParser.parseFormulae("2 log n | 2 log n | 1 | 1 | 2 log n", "\\|"); //TODO log_2
			FormulaNode[] amortizedCosts = FormulaParser.parseFormulae("2 log n | 2 log n | 1 | 1 | 2 log n", "\\|");
			FormulaNode[] expectedCosts = FormulaParser.parseFormulae("2 log n | 2 log n | 1 | 1 | 2 log n", "\\|");
			
			HashMap<String, Function> functions = new HashMap<>();
			
			for(int i = 0; i < functionNames.length; i++){
				HashMap<String, FormulaNode> complexity = new HashMap<String, FormulaNode>();
				complexity.put("worst case cost", worstCosts[i]);
				complexity.put("worst case bigO", worstCosts[i].());
	
				complexity.put("amortized cost", amortizedCosts[i]);
				complexity.put("amortized bigO", amortizedCosts[i].());
				
				complexity.put("expected cost", expectedCosts[i]);
				complexity.put("expected bigO", expectedCosts[i].());
				
				Function f = new Function(functionNames[i], complexity);
				
				functions.put(functionNames[i], f);
			}
			
			heap = new Algorithm(null, "Binary Heap", functions, functionNames, variableNames);
		}
		
		VariableMapping m = new VariableMapping();
		m.put("n", new VariableNode("v"));
		
		//Djikstras should probably be an algorithm too...
		
		//Adapted from the notes of Professor Andrew Winslow http://www.cs.tufts.edu/comp/260/lectures/lecture-5-fibonacci-heaps.pdf
		FormulaNode cost = FormulaParser.parseFormula("v + ( v * insert ) + ( v * ( isEmpty + removeMin ) ) + (e * ( 1 + decreaseKey ) ) + v");

		System.out.println("Dijkstra's Cost: " + cost.asString());
		
		FormulaNode bigO = new Formula(cost).takeBigO();
		System.out.println("Dijkstra's BigO: " + bigO.asString());
		
//		Algorithm a = new UnorderedArray(null, null, 0);
		FormulaNode ua = unorderedArray.substituteAlgorithm("amortized bigO", cost, m);
		

//		System.out.println("Dijkstra's Cost with Unordered Array: " + ua.asString());
		FormulaNode uaSimp = new Formula(ua).simplify();
		
		System.out.println("Dijkstra's Cost with Unordered Array: " + uaSimp.asString());
		
		FormulaNode uaBigO = new Formula(ua).takeBigO();
		System.out.println("Dijkstra's BigO with Unordered Array: " + uaBigO.asString());

		FormulaNode orderedForm = orderedArray.substituteAlgorithm("amortized bigO", cost, m);
		System.out.println("Dijkstra's BigO with Ordered Array: " + (new Formula(orderedForm).takeBigO()).asString());

		FormulaNode heapForm = heap.substituteAlgorithm("amortized bigO", cost, m);
		System.out.println("Dijkstra's BigO with Heap: " + (new Formula(heapForm).takeBigO()).asString());
		
		
		
		//SORTING TEST
		FormulaNode sort = FormulaParser.parseFormula("(n * insert) + (n * removeMin) + (n * 1)");
		VariableMapping map = new VariableMapping();
		map.put("m", new VariableNode("m"));

		System.out.println("Sort with Unordered Array: " + new Formula(unorderedArray.substituteAlgorithm("amortized bigO", sort, map)).takeBigO().asString() );
		System.out.println("Sort with Ordered Array: " + new Formula(orderedArray.substituteAlgorithm("amortized bigO", sort, map)).takeBigO().asString() );
		System.out.println("Sort with Heap: " + new Formula(heap.substituteAlgorithm("amortized bigO", sort, map)).takeBigO().asString() );

//		//GRAPH TEST
//		Algorithm adjacencyMatrix;
//		Algorithm edgeList;
//		{
//			String[] functionNames = new String[]{"d", "removeMin", "isEmpty", "size", "decreaseKey"};
//			String[] variableNames = new String[]{"v", "d", "e"};
//			
//			FormulaNode[] worstCosts = FormulaParser.parseFormulae("2 log n | 2 log n | 1 | 1 | 2 log n", "\\|"); //TODO log_2
//			FormulaNode[] amortizedCosts = FormulaParser.parseFormulae("2 log n | 2 log n | 1 | 1 | 2 log n", "\\|");
//			FormulaNode[] expectedCosts = FormulaParser.parseFormulae("2 log n | 2 log n | 1 | 1 | 2 log n", "\\|");
//		}
	
	}
	
	
	//LOAD FROM FILE
	
/*
ALG_FILE_0.2
NAME = "ALGORITHM_NAME"
ADT = "ADT_NAME"

FUNNAME: (expression), (expression), ...
*/
	
	//Match a quoted string
	public static Pattern stringPattern = Pattern.compile("\"([a-zA-Z_]+)\"");
	public static String matchQuote(String s){
		Matcher matcher = stringPattern.matcher(s);
		matcher.find();
		return matcher.group(1);
	}

	//Return the strings in a set.
	public static Pattern setPattern = Pattern.compile("\\{((?:[a-zA-Z_]+, *)*(?:[a-zA-Z_]+))\\}");
	public static String[] tokenizeSet(String s){
		Matcher matcher = setPattern.matcher(s);
		matcher.find();
		String setStr = matcher.group(1);
		return setStr.split(", *");
	}
	public static Algorithm loadAlgorithm(Environment e, FileReader f) throws IOException{
		BufferedReader r = new BufferedReader(f);
		
		//Validate header
		String l = r.readLine().trim();
		if(!l.equals("ALG_FILE_0.2")){
			System.err.println("Failure to read ALG file: header mismatch");
			return null;
		}
		
		
		String name = null;
		AbstractDataType adt = null;
		
		HashMap<String, Function> functions = new HashMap<String, Function>();
		
		while(true){
			l = r.readLine();
			if(l == null) break;
			l = l.trim();
			
			if(l.length() == 0 || l.charAt(0) == '#') continue;
			else if(l.startsWith("NAME")){
				String s = matchQuote(l);
				name = s;
			}
			else if(l.startsWith("ADT")){
				String s = matchQuote(l);
				if(adt != null){
					System.err.println("Can't define multiple ADT in a file.");
					return null;
				}
				else{
					adt = e.getADT(s);
					if(adt == null){
						System.err.println("Can't find ADT \"" + s + "\"");
						return null; //Couldn't find ADT
					}
				}
			}
			else{
				String[] split = l.split(":");
				
				String funName = split[0];
				
				String[] expStrings = split[1].split(",");
//				String[] info = split[0].split(" ");
				
				if(expStrings.length != adt.analysisTypes.length){
					System.err.println("Insufficient analysis types.");
				}
				
				HashMap<String, FormulaNode> fmap = new HashMap<String, FormulaNode>();
				
				for(int i = 0; i < expStrings.length; i++){
					FormulaNode fnode = FormulaParser.parseFormula(expStrings[i]);
					fmap.put(adt.analysisTypes[i], fnode);
				}
				
				functions.put(funName, new Function(funName, fmap));
			}
//			else{
//				System.err.println("AbstractDataType analyzer can't analyze line \"" + l + "\"");
//			}
		}
		
		Algorithm newAlg = new Algorithm(adt, name, functions);
		
		adt.algorithms.add(newAlg);
		
		return newAlg;
	}
}
