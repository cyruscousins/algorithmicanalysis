package algorithm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbstractDataType {
	
	String name;
	
	String[] functions;
	String[] analysisTypes;
	String[] variables;
	
	//Additional data.
	List<Algorithm> algorithms;
	
	public AbstractDataType(String name, String[] functions, String[] analysisTypes, String[] variables) {
		this.name = name;
		this.functions = functions;
		this.analysisTypes = analysisTypes;
		this.variables = variables;
		
		algorithms = new ArrayList<Algorithm>();
	}
	
	
	
	//FILE PARSER:
	
	//Abstract file type:
/*
ADT_FILE_0.1
VAR = {a, b, c, ..., n}
ANALYSIS = {WORST, AMORTIZED, EXPECTED}
FUNCTIONS = {a, b, c}
*/

/*
FUNCTIONS:
ANALISIS name: (ARGUMENTS) -> RESULT
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
	
	public static AbstractDataType loadAbstractDatatype(FileReader f) throws IOException{
		BufferedReader r = new BufferedReader(f);
		
		//Validate header
		String l = r.readLine().trim();
		if(!l.equals("ADT_FILE_0.1")){
			System.err.println("Failure to read ADT file: header mismatch");
			return null;
		}
		
		String name = null;
		
		String[] functions = null;
		String[] analysisTypes = null;
		String[] vars = null;
		
		ArrayList<Function> fList = new ArrayList<Function>();
		
		while(true){
			
			l = r.readLine();
			if(l == null) break;
			l = l.trim();
			
			if(l.length() == 0 || l.charAt(0) == '#') continue;
			else if(l.startsWith("NAME")){
				name = matchQuote(l);
			}
			else if(l.startsWith("VAR")){
				vars = tokenizeSet(l);
			}
			else if(l.startsWith("ANALYSIS")){
				analysisTypes = tokenizeSet(l);
			}
			else if(l.startsWith("FUNCTIONS")){
				functions = tokenizeSet(l);
			}
			else{
				System.err.println("AbstractDataType analyzer can't analyze line \"" + l + "\"");
			}
		}
		
		if(name == null || functions == null || analysisTypes == null || vars == null){
			System.err.println("Incomplete definition for \"" + name + "\".");
			System.err.println(name + ", " + functions + ", " + analysisTypes + ", " + vars);
			return null;
		}
		return new AbstractDataType(name, functions, analysisTypes, vars);
	}
}
