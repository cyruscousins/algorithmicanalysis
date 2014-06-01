package complexity;

import java.io.PrintStream;
import java.util.Random;

import algorithm.Algorithm;
import algorithm.UnorderedArray;

public class ComplexityTest {

	public static void main(String[] args){
		runAllTests(false, System.out);
//		runBigOTests(false, System.out);
//		runSimplifierTests(true, System.out);
//		runSimplifierTests(false, System.out);
	}
	public static void runAllTests(boolean verbose, PrintStream out){
		runEqualityTests(verbose, out);
		runValueTests(verbose, out);
		runStringTests(verbose, out);
		runSimplifierTests(verbose, out);
		runBigOTests(verbose, out);
		runBigOSubstitutionTests(verbose, out);
		
	}
	
	public static final String splitter = "\\s*\\|\\s*";
	
	public static void runEqualityTests(boolean verbose, PrintStream out){
		String[] formulae = "n | n ^ 2 | n * (2 log n) + 1 - 1 | (2 * (2 log (n ^ (m + l)))) + ((m * l) ^ 2)".split(splitter);
		String fStr = "";
		for(int i = 0; i < formulae.length; i++){
			fStr += formulae[i] + "|" + formulae[i] + "|";
		}
		
		FormulaNode[] test = FormulaParser.parseFormulae(fStr, "\\|");

		out.println("Equality Tester");
		for(int i = 0; i < test.length; i += 2){
			if(test[i + 0].formulaWeakEquals(test[i + 1])){
				if(verbose) out.println((i / 2) + ": Success: " + test[i + 0].asStringRecurse() + " = " + test[i + 1].asStringRecurse());
			}
			else{
				out.println((i / 2) + ": Error: " + test[i + 0].asStringRecurse() + " != " + test[i + 1].asStringRecurse());
			}
		}
	}
	
	public static void runValueTests(boolean verbose, PrintStream out){
		FormulaNode[] tests = FormulaParser.parseFormulae(
				"4! | sum i from 0 to 5 of i | sum i from 0 to 4 of (i ^ i)", splitter);
		//= new FormulaNode[]{FormulaParser.parseFormula("4!"), new Summation(ConstantNode.ZERO, new ConstantNode(5), FormulaParser.parseFormula("i"), "i"), new Summation(ConstantNode.ZERO, new ConstantNode(4), FormulaParser.parseFormula("i ^ 2"), "i"), new Summation(ConstantNode.ZERO, new ConstantNode(4), FormulaParser.parseFormula("i ^ i"), "i")};
		double[] correctValues = new double[]{24, 15, 289};
		
		VarSet empty = new VarSet();
		for(int i = 0; i < tests.length; i++){
			double val = tests[i].evaluate(empty);
			if(epsilonCompare(val, correctValues[i])){
				if(verbose) out.println(i + ": Value test " + i + " passed: " + tests[i].asString() + " -> " + correctValues[i]);
			}
			else{
				out.println(i + ": Value test " + i + " failed: " + tests[i].asString() + " -> " + val + " != " + correctValues[i]);
			}
		}
	}
	
	public static void runStringTests(boolean verbose, PrintStream out){

		out.println("\n\nSTRING TESTS\n");
		FormulaNode[] test = FormulaParser.parseFormulae("n|n   |   n + (1 - 1)|n   |   n + (1 - 1 + 1 - 1 + 1 - 1)|n   |   n + 1 - 1|n   |   (n * m) / m | n   |" +
				"n * n | n^2   |   n * (n * (n / n)) | n^2   |   n * n * n | n^3   |   (n * n) * (n * n) | n^4   |" +
				"(n ^ 4) / (n ^ 2) | (n ^ 2)   |   (n ^ 4) / (n ^ 3) | n   |   (n ^ 2) / (n ^ 3) | (n ^ ~1)   |" +
				"(2 * n * (2 log (n ^ 2))) - (2 * n * (2 log (n ^ 2)))|0   |   (2 * n * m) / (2 * n * m)|1   |" +
				"(n + m) - (m + n) | 0   |   ((n + m) * (m + n)) / ((m + n) * (n + m)) | 1   |   ((n + m) ^ 1) * (1 / (n + m)) | 1   |" +
				"0 * (0 - 1)|0   |   (1 / 3) + (1 / 3) + (1 / 3)|1   |   (0 / 0) | (0 / 0)   |" +
				"n * ((1 / n) + 1)|1 + n   |   n * ((1 / n) + (1 / m)) * m | n + m   |   ((n * m) * ((1 / n) + (1 / m))) / (n + m) | 1   |" +
				"np ^ (np log mp)|mp   |   (np log (np ^ mp))|mp   |" +
				"(npi choose 0) | 1   |   (npi choose 1) | npi   |   (npi choose npi) | 1   |" +
				"ceil(2.5) | 3   |   floor(4 / 5) | 0   |   ceil(n) | ceil(n)   |" + //ceil and floor
				"ceil(ceil(n)) | ceil(n)   |   ceil(floor(n)) | floor(n)   |   floor(ceil(n)) | ceil(n)   |   floor(floor(n)) | floor(n)   |   floor(ceil(npi!)) | npi!   |" + //ceil and floor
				"sum i from 1 to 3 of (5 * i ^ 2) | 5 * sum i from 1 to 3 of (i ^ 2)  |   sum i from 1 to 3 of i | 6   |   sum i from 2 to 5 of (i * 2) | 28   |" + //Summations
				"|", splitter);
		
		int success = 0;
		for(int i = 0; i < test.length; i++){
			FormulaNode res;
			try{
				res = FormulaParser.parseFormula(test[i].asString());
			}
			catch(Exception e){
				out.println(i + ": Error parsing \"" + test[i].asString() + "\".");
				e.printStackTrace();
				continue;
			}
			if(!test[i].formulaWeakEquals(res)){
				out.println(i + ": STRING FAILURE: \"" + test[i].asString() + "\" -> \"" + res.asString() + "\"");
			}
			else{
				if(verbose) out.println(i + ": STRING SUCCESS: \"" + test[i].asString() + "\" -> \"" + res.asString() + "\"");
				success++;
			}
		}
		out.println("Success: " + success + " / " + test.length);
	}
	
	public static void runSimplifierTests(boolean verbose, PrintStream out){

		out.println("\n\nSIMPLIFIER TESTS\n");
		FormulaNode[] test = FormulaParser.parseFormulae(
				"n | n   |   n + (1 - 1)|n   |   n + (1 - 1 + 1 - 1 + 1 - 1)|n   |   n + 1 - 1|n   |   (n * m) / m | n   |" +
				"n * n | n^2   |   n * (n * (n / n)) | n^2   |   n * n * n | n^3   |   (n * n) * (n * n) | n^4   |" +
				"(n ^ 4) / (n ^ 2) | (n ^ 2)   |   (n ^ 4) / (n ^ 3) | n   |   (n ^ 2) / (n ^ 3) | (n ^ ~1)   |" +
				"(2 * n * (2 log (n ^ 2))) - (2 * n * (2 log (n ^ 2)))|0   |   (2 * n * m) / (2 * n * m)|1   |" +
				"(n + m) - (m + n) | 0   |   ((n + m) * (m + n)) / ((m + n) * (n + m)) | 1   |   ((n + m) ^ 1) * (1 / (n + m)) | 1   |" +
				"0 * (0 - 1)|0   |   (1 / 3) + (1 / 3) + (1 / 3)|1   |   (0 / 0) | (0 / 0)   |" +
				"n * ((1 / n) + 1) | 1 + n   |   n * ((1 / n) + (1 / m)) * m | n + m   |   ((n * m) * ((1 / n) + (1 / m))) / (n + m) | 1   |" +
				"np ^ (np log mp) | mp   |   (np log (np ^ mp))|mp   |" +
				"(npi choose 0) | 1   |   (npi choose 1) | npi   |   (npi choose npi) | 1   |" +
				"ceil(2.5) | 3   |   floor(4 / 5) | 0   |   ceil(n) | ceil(n)   |" + //ceil and floor
				"ceil(ceil(n)) | ceil(n)   |   ceil(floor(n)) | floor(n)   |   floor(ceil(n)) | ceil(n)   |   floor(floor(n)) | floor(n)   |   floor(ceil(npi!)) | npi!   |" + //ceil and floor
				"sum i from 1 to 3 of (5 * i ^ 2) | 5 * sum i from 1 to 3 of (i ^ 2)  |   sum i from 1 to 3 of i | 6   |   sum i from 2 to 5 of (i * 2) | 28   |" + //Summations
				"(n + m) - (n + m + 1) | ~1   |   (n * m) / (n * m * 2) | (1 / 2)   |   (n + m) - (2 * n) | (m - n)   |" + //OpCollectionNode
				"(1 / n ^ 2) * (n + 1) * (n - 1) + 1 / (n ^ 2) | 1   |   (n + m - n + 1 - m) | 1   |   (n + 1 - n + n + ~1 - m) * n * m / ((n - m) * n * n) | (m / n)   |   (n * m / m * 1 * (1 / n)) - 1 | 0   |" + //Complex
				"|", splitter);

//		out.println("KEY:");
//		for(int i = 0; i < test.length; i+= 2){
//			out.println(test[i].asString() + " -> " + test[i + 1].asString());
//		}
		
		int totalSuccess = 0;
		int valueSuccess = 0;
		for(int i = 0; i < test.length; i+= 2){
			FormulaNode f = test[i];
			FormulaNode s = f.takeSimplified();

			//Test simplification completion
			FormulaNode s2 = s.takeSimplified();
			if(!s2.formulaWeakEquals(s)){
				out.println((i / 2) + ": simplifier incompletion failure: " + f.asString() + " -> " + s.asString() + " -> " + s2.asString());
				continue;
			}
			
			if(testValueEqual(new String[]{"n", "m"}, f, s, out)) valueSuccess++;
			
			FormulaNode key = test[i + 1];
			FormulaNode simpKey = key.takeSimplified();
			if(!(key.formulaWeakEquals(simpKey))){
				out.println((i / 2) + ": Simplification key test error: \"" + key.asString() + " -> " + simpKey.asString() + "\" is not fully simplified.");
				continue;
			}
			else if(!(key.equals(simpKey))){
				//TODO go back to this!
				if(verbose) out.println((i / 2) + ": Simplification key test warning: \"" + key.asString() + " is fully simplified, but is given new memory.");
				
			}
			
			
			if(key.formulaWeakEquals(s)){
				if(verbose) out.println((i / 2) + ": Simplification success: " + f.asString() + " -> " + s.asString() + " = " + key.asString());
				totalSuccess++;
			}
			else{
				out.println((i / 2) + ": Simplification failure: " + f.asString() + " -> " + s.asString() + " != " + key.asString());
			}
		}
		out.println("Total success: " + totalSuccess + " / " + test.length / 2);
		out.println("Value success: " + valueSuccess + " / " + test.length / 2);
	}
	
	public static void runBigOTests(boolean verbose, PrintStream out){
		out.println("\n\nBIGO TESTS:\n");
		FormulaNode[] test = FormulaParser.parseFormulae(
			"n|n   |   n + (n * n)|n^2   |   2 * n|n   |   n ^ 2 - n|n ^ 2   |" +
			"n * 3 * n / 4 * n|n^3   |   (5 + n + 4 / 3) / n + 7|1   |   (n * 4 * m / 3 + 7) / ((n + 8) * m)|1   |" +
			"n * log_2 n / n | ln n   |   n * m * log_2 n / n | m * ln n   |" + //Commutativity
			"2 ^ (2 * n) | 2 ^ (2 * n)   |   n ^ (1 + (2 * m) / 2 - 1) | n ^ m   |" + //Basic exponentiation
			"(n ^ 2) + (n ^ 3) | (n ^ 3)   |   n + (n + m) | (n + m)   |   (n ^ 2) + ((n ^ 2) + m) | (n ^ 2) + m   |   (n ^ 2) + ((n + m) ^ 2) | (n + m) ^ 2   |" +
			"(n ^ 1.5) + (n * m) | (n ^ 1.5) + (n * m)   |   (n ^ 2) * ((n ^ 2) / (n + m)) | (n ^ 4) / (n + m)   |" +
			"(n * n) + (n * ((2 log n) ^ 4)) | (n ^ 2)   |   (n ^ 3) + (n ^ 2 * (1 + (n ^ .5) + (2 log n) ^ 2)) | (n ^ 3)   |" + //Logarithms
			"n ^ 2 + (n * log_2 n) | n ^ 2   |   n + (log_2 n) ^ 4 | n   |   (n * n * m) + (n * (log_2(n) ^ 4) * log_2 ( log_2 m )) | n ^ 2 * m   |" + //More Logarithms
			"n + (n ^ m) | (n ^ m)   |   (n + m) + (n ^ m) | (n ^ m)   |" + //Multivariate bigO with exponentiation.
			"n + log_2 n | n   |   n * log_2 n - n | n * ln n  |" + //Even more logarithms
			"n! | n ^ n   |   log_2 (n!) | n * ln n   |   (n choose 2) | n ^ 2   |" + //factorials and choose.
			"ceil(log_2 n) | ln n   |   2 ^ (floor n)   |   2 ^ n   |   n ^ (ceil 2.5) | n ^ 3   |" + //Floor and ceil
			"n * (1 + (ceil(log_2 n) - 1)) | n * ln n   |   2 ^ (1 + ceil (log_2 n)) | n   |" +
			"sum i from 1 to n of (5 * i ^ 5) | n ^ 6   |   sum i from n to (n * 2) of (log_2 n) | n * ln n   |   sum i from n to (n + 5) of (log_2 n) | ln n   |" + //Summations
			"n + log_2 n | n   |   n ^ (3 / 2) + n * (log_2 n) ^ 2 | n ^ (3 / 2)   |   n * (log_2 (log_2 n)) | n * (ln (ln n))   |   log_3 ((log_2 n) ^ 2) | ln ((ln n))   |   (log_2 n) ^ 2 + (log_2 ((log_2 n) ^ 3)) | (ln n) ^ 2   |   n * m - (n + m) | n * m " + //Complex
			"|", splitter);
		
//		test = FormulaParser.parseFormulae(
//				"n + log_2 n | n   |   n + (log_2 n) ^ 2 | n   |   n + (log_2 (log_2 n)) | n", splitter);
//
//		test = FormulaParser.parseFormulae(
//				"n ^ 2 + (log_2 n) ^ 3 | n ^ 2", splitter);

//		test = FormulaParser.parseFormulae(
//				"n ^ 2 + (n * log_2 n) | n ^ 2   |   n + (log_2 n) ^ 4 | n   |   (n * n) + (n * (log_2(n) ^ 4)) | n ^ 2", splitter);
		
		
		String[] args = new String[]{"n", "m"};
		
		int valueSuccess = 0;
		int totalSuccess = 0;
		for(int i = 0; i < test.length; i += 2){
			FormulaNode f = test[i];
			FormulaNode s = f.bigO();
			
			//Test bigO completion
			FormulaNode s2 = s.bigO();
			if(!s2.formulaWeakEquals(s)){
				out.println((i / 2) + ": bigO incompletion failure: " + f.asString() + " -> " + s.asString() + " -> " + s2.asString());
				continue;
			}
			
			//Test approximate accuracy
			if(testBigOApprox(args, f, s, out)){
				valueSuccess++;
			}
			else{
				out.println((i / 2) + ": Serious error: Value failure for " + f.asString() + " -> " + s.asString());
			}
			
			//Test accuracy
			FormulaNode key = test[i + 1];
			
			if(!key.formulaWeakEquals(key.bigO())){
				out.println((i / 2) + ": BigO test error: key " + key.asString() + " -> " + key.bigO().asString() + " is not in normative form.");
			}
			
			if(key.formulaWeakEquals(s)){
				if(verbose) out.println((i / 2) + ": BigO success: " + f.asStringRecurse() + " -> " + s.asStringRecurse() + " = " + key.asStringRecurse());
				totalSuccess++;
			}
			else{
				out.println((i / 2) + ": BigO failure: " + f.asStringRecurse() + " -> " + s.asStringRecurse() + " != " + key.asStringRecurse());
			}
		}
		
		for(int i = 0; i < test.length; i++){
			//TODO Does this only holds for polynomials?  I think we need to ensure that all args are polynomial.
			FormulaNode f = test[i].takeSimplified();
			FormulaNode fn = new BinaryOperatorNode(BinaryOperatorNode.DIVIDE, f, new VariableNode("n")).takeSimplified();
			if(BinaryOperatorNode.xInBigOofY(f, fn)){
				out.println("BigO Error: " + f.asStringRecurse() + " -> " + fn.asStringRecurse() + " is innaccurate.");
			}
		}
		out.println("Total success: " + totalSuccess + " / " + test.length / 2);
		out.println("Value success: " + valueSuccess + " / " + test.length / 2);
	}
	
	private static String[] littles = new String[]{"a", "c"}, bigs = new String[]{"b", "d"};
	public static void runBigOSubstitutionTests(boolean verbose, PrintStream out){
		out.println("\n\nBIGO SUBSTITUTION TESTS:\n");
		FormulaNode[] test = FormulaParser.parseFormulae(
				"a | a   |   b | b   |   a + d | a + d   |   b - a | b   |" + //Trivial
				"a * b | a * b   |   a + b | b   |   2 ^ (a + b) | 2 ^ b   |   a ^ b | a ^ b   |" + //Basic
				"a ^ (b + a) | a ^ b   |   a ^ (b - a) | a ^ b   |   a ^ (a - b) | a ^ (a - b)   |   a * b + b ^ 2 | b ^ 2   |   a * b + b | a * b   |   (a * b ^ 2) + (a * b) | a * b ^ 2   |" + //Complicated
				"b + a * log_2(b) | b + a * ln b   |   (a + b) + (a * log_2(b)) | b + a * ln b   |" +
				"(a * c) + (b * d) | b * d   |   (a * c) + (b * d ^ 2) | b * d ^ 2   |"	+	//Multivariate
				"a * (a + b - 1) | (a * b)   |   b * (a + b - 1) | b ^ 2   |" +
				"|", splitter);
		
		int totalSuccess = 0;
		
		for(int i = 0; i < test.length; i += 2){
			FormulaNode f = test[i];
			FormulaNode s = f.takeBigO(littles, bigs);
			FormulaNode key = test[i + 1];
			
			if(!key.formulaWeakEquals(key.takeBigO(littles, bigs))){
				out.println("BIGO SUBSTITUTION TEST ERROR: key " + key.asString() + " is not in normative form.");
			}
			
			if(s.formulaWeakEquals(key)){
				if(verbose) out.println((i / 2) + ": BigO substitution success: " + f.asStringRecurse() + " -> " + s.asStringRecurse() + " = " + key.asStringRecurse());
				totalSuccess++;
			}
			else{
				out.println((i / 2) + ": BigO substitution failure: " + f.asStringRecurse() + " -> " + s.asStringRecurse() + " != " + key.asStringRecurse());
			}
		}
		
		out.println("Total success: " + totalSuccess + " / " + test.length / 2);
	}
	
	static boolean testBigOApprox(String[] args, FormulaNode f0, FormulaNode f1, PrintStream out){
		return testBigOApprox(args, f0, f1, 100000, 100, out);
	}
	
	static boolean testBigOApprox(String[] args, FormulaNode f0, FormulaNode f1, double x, double c, PrintStream out){
		
		VarSet v = new VarSet();
		
		for(int i = 0; i < args.length; i++){
			v.put(args[i], x);
		}
		
		double d0 = f0.evaluate(v);
		double d1 = f1.evaluate(v) * c;
		
		if(d0 == Double.POSITIVE_INFINITY || d1 == Double.POSITIVE_INFINITY){
			return testBigOApprox(args, f0, f1, x / 10, c, out);
		}
		
		if(d0 >= d1){
			out.println("Failure: " + f0.asStringRecurse() + " -> " + d0 + " > " + f1.asStringRecurse() + " -> " + d1);
		}
		
		return d0 < d1;
	}
	
	private static boolean epsilonCompare(double d0, double d1){
		if(Double.isNaN(d0) && Double.isNaN(d1)) return true;
		else return Math.abs(d0 - d1) <= .00001;
	}
	
	static boolean testValueEqual(String[] args, FormulaNode f, FormulaNode s, PrintStream out){
		VarSet v = new VarSet();
		
		Random r = new Random();
		int trials = 16;

		boolean failed = false;
		for(int i = 0; i < trials; i++){
			for(int j = 0; j < args.length; j++){
				double val = r.nextGaussian();
				v.put(args[j], val);
				
				if(val < 0) val *= -1;
				if(val == 0) val = 1;
				v.put(args[j] + "p", val);
				v.put(args[j] + "pi", (int)val + 1);
			}
			
			double d0 = f.evaluate(v);
			double d1 = s.evaluate(v);
			
			if(!epsilonCompare(d0, d1)){
				failed = true;
				out.println("Simplification Error: " + f.asStringRecurse() + " -> " + s.asStringRecurse() + " is invalid for " + v.asString() + ".  Original: " + d0 + ", Simplified: " + d1);
			}
		}
		
		if(!failed){
			//out.println("Simplification " + f.asString() + " -> " + s.asString() + " accurate.");
		}
		return !failed;
	}
	
}
