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
	
	public HashMap<String, String> strings;

	public Algorithm(AbstractDataType type, String name, HashMap<String, Function> functions) {
		this.type = type;
		this.name = name;
		this.functions = functions;
		
		this.strings = new HashMap<String, String>();
	}
	public Algorithm(AbstractDataType type, String name, HashMap<String, Function> functions, HashMap<String, String> strings) {
		this.type = type;
		this.name = name;
		this.functions = functions;
		this.strings = strings;
	}
	
	public String[] summarizeAlgorithm(){
		ArrayList<String> summary = new ArrayList<String>();
		
		String formalName = name.replaceAll("_", "\\\\_") + ", a " + type.name.replaceAll("_", "\\\\_");
		
		if(strings.containsKey("objecttypename")){
			formalName += " " + strings.get("objecttypename").replaceAll("_", "\\\\_");
		}
		
//		summary.add("\\subsection{" + name + " $\\in$ " + type.name + "}");
		summary.add("\\subsection{Complexity Analysis of " + formalName + "}");
		for(String analysisType: type.analysisTypes){
			summary.add("\\subsubsection{" + analysisType + " analysis}");
			for(String s: functions.keySet()){
				String fCost = functions.get(s).costs.get(analysisType).asLatexString();
				String fCostBigO = functions.get(s).costs.get(analysisType).takeBigO().asLatexString();
				String nameStr = s.replaceAll("_", "\\\\_");
				
				if(fCost.equals(fCostBigO)){
					summary.add("\\texttt{" + nameStr + "}" + " $ \\in \\bigO\\big(" + fCost + "\\big)$");
				} else{
					
					if(strings.containsKey("exactanalysis") && strings.get("exactanalysis").equals("true")){
						summary.add("\\texttt{" + nameStr + "}" + " \\linebreak[2]$ = \\big(" + fCost + "\\big) $ \\linebreak[3]$ \\in \\bigO \\big(" + fCostBigO + "\\big)$");
					}
					else{
						summary.add("\\texttt{" + nameStr + "}" + " \\linebreak[2]$ \\in \\bigO\\big(" + fCost + "\\big) $ \\linebreak[3]$ = \\bigO \\big(" + fCostBigO + "\\big)$");
					}
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
					summary.add("\\texttt{" + s.replaceAll("_", "\\\\_") + "}" + " $ \\in \\bigO\\big(" + fCost + "\\big) = \\bigO \\big(" + fCostBigO + "\\big)$");
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
	
	
	//LOAD FROM FILE
	
/*
ALG_FILE_0.2
NAME = "ALGORITHM_NAME"
ADT = "ADT_NAME"

FUNNAME: (expression), (expression), ...
*/
	
	public static Pattern stringPattern = Pattern.compile("\"((?:[a-zA-Z_\\. ]|(?:\\\\\"))*)\"");
	public static String matchQuote(String s){
		Matcher matcher = stringPattern.matcher(s);
		matcher.find();
		return matcher.group(1);
	}
	public static String[] matchQuotes(String s){
		ArrayList<String> l = new ArrayList<>();
		Matcher matcher = stringPattern.matcher(s);
		while(matcher.find()){
			l.add(matcher.group(1));
		}
		return l.toArray(new String[l.size()]);
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
		if(!l.equals("ALG_FILE_0.3")){
			System.err.println("Failure to read ALG file: header mismatch");
			return null;
		}
		
		
		String name = null;
		AbstractDataType adt = null;
		
		HashMap<String, Function> functions = new HashMap<>();
		
		HashMap<String, String> strings = new HashMap<>();
		
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
			else if(l.startsWith("STRING")){
				String[] s = matchQuotes(l);
				if(s.length != 2){
					System.err.println("Error: line \"" + l + "\" must contain exactly 2 string arguments.  Found " + s.length + ".");
					return null;
				}
				strings.put(s[0], s[1]);
			}
			else{
				String[] split = l.split(":");
				
				String funName = split[0];
				
				String[] expStrings = split[1].split(",");
//				String[] info = split[0].split(" ");
				
				if(expStrings.length != adt.analysisTypes.length){
					System.err.println("Insufficient analysis types.");
					System.err.println("\tExpected:");
					for(int i = 0; i < adt.analysisTypes.length; i++){
						System.err.println("\t" + adt.analysisTypes[i]);
					}
					System.err.println("\tFound:");
					for(int i = 0; i < expStrings.length; i++){
						System.err.println("\t" + expStrings[i]);
					}
					
					return null;
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
		
		Algorithm newAlg = new Algorithm(adt, name, functions, strings);
		
		adt.algorithms.add(newAlg);
		
		return newAlg;
	}
}
