package complexity;

import java.util.Random;

import algorithm.Algorithm;
import algorithm.UnorderedArray;

public class ComplexityTest {
	public static void main(String[] args){

		runEqualityTests();
		runSimplifierTests();
		runBigOTests();
		
	}
	
	public static void runEqualityTests(){
		String[] formulae = "n | n ^ 2 | n * (2 log n) + 1 - 1 | (2 * (2 log (n ^ (m + l)))) + ((m * l) ^ 2)".split("\\|");
		String fStr = formulae[0] + "|" + formulae[0];
		for(int i = 1; i < formulae.length; i++){
			fStr += "|" + formulae[i] + "|" + formulae[i];
		}
		
		FormulaNode[] test = FormulaParser.parseFormulae(fStr, "\\|");

		System.out.println("Equality Tester");
		for(int i = 0; i < test.length; i += 2){
			if(test[i + 0].formulaEquals(test[i + 1])){
				System.out.println(test[i + 0].asString() + " = " + test[i + 1].asString());
			}
			else{
				System.err.println(test[i + 0].asString() + " != " + test[i + 1].asString());
			}
		}
	}
	
	public static void runSimplifierTests(){

		System.out.println("\n\nSIMPLIFIER TESTS\n");
		FormulaNode[] test = FormulaParser.parseFormulae(
				"n|n   |   n + (1 - 1)|n   |   n + (1 - 1 + 1 - 1 + 1 - 1)|n   |   n + 1 - 1|n   |   (n * m) / m | n   |" +
				"n * n|n^2   |   n * (n * (n / n))|n^2   |   n * n * n|n^3   |   (n * n) * (n * n) | n^4   |" +
				"(n ^ 4) / (n ^ 2) | (n ^ 2)   |   (n ^ 4) / (n ^ 3) | n   |   (n ^ 2) / (n ^ 3) | (n ^ ~1)   |" +
				"(2 * n * (2 log (n ^ 2))) - (2 * n * (2 log (n ^ 2)))|0   |   (2 * n * m) / (2 * n * m)|1   |" +
				"(n + m) - (m + n) | 0   |   ((n + m) * (m + n)) / ((m + n) * (n + m)) | 1   |   ((n + m) ^ 1) * (1 / (n + m)) | 1   |" + 
				"0 * (0 - 1)|0   |   (1 / 3) + (1 / 3) + (1 / 3)|1   |   (0 / 0) | (0 / 0)   |" +
				"n * ((1 / n) + 1)|1 + n   |   n * ((1 / n) + (1 / m)) * m | n + m   |   ((n * m) * ((1 / n) + (1 / m))) / (n + m) | 1   |" +
				"np ^ (np log mp)|mp   |   (np log (np ^ mp))|mp   |" +
				"(npi choose 0) | 1   |   (npi choose 1) | npi   |   (npi choose 2) | (npi ^ 2 - npi) / 2   |   (npi choose npi) | 1   " +
				"", "\\|");

//		System.out.println("KEY:");
//		for(int i = 0; i < test.length; i+= 2){
//			System.out.println(test[i].asString() + " -> " + test[i + 1].asString());
//		}
		
		int totalSuccess = 0;
		int valueSuccess = 0;
		for(int i = 0; i < test.length; i+= 2){
			FormulaNode f = test[i];
			FormulaNode s = new Formula(f).simplify();
			
			if(testValueEqual(new String[]{"n", "m"}, f, s)) valueSuccess++;
			
			FormulaNode key = test[i + 1];
			if(key.formulaEquals(s)){
				System.out.println("Simplification success: " + f.asString() + " -> " + s.asString() + " = " + key.asString());
				totalSuccess++;
			}
			else{
				System.out.println("Simplification failure: " + f.asString() + " -> " + s.asString() + " != " + key.asString());
			}
		}
		System.out.println("Total success: " + totalSuccess + " / " + test.length / 2);
		System.out.println("Value success: " + valueSuccess + " / " + test.length / 2);
	}
	
	public static void runBigOTests(){
		System.out.println("\n\nBIGO TESTS:\n");
		FormulaNode[] test = FormulaParser.parseFormulae(
			"n|n   |   n + (n * n)|n^2   |   2 * n|n   |   n ^ 2 - n|n ^ 2   |" +
			"n * 3 * n / 4 * n|n^3   |   (5 + n + 4 / 3) / n + 7|1   |   (n * 4 * m / 3 + 7) / ((n + 8) * m)|1   |" +
			"(n ^ 2) + (n ^ 3) | (n ^ 3)   |   n + (n + m) | (n + m)   |   (n ^ 2) + ((n ^ 2) + m) | (n ^ 2) + m   |   (n ^ 2) + ((n + m) ^ 2) | (n + m) ^ 2   |" +
			"(n ^ 1.5) + (n * m) | (n ^ 1.5) + (n * m)   |   (n ^ 2) * ((n ^ 2) / (n + m)) | (n ^ 2) * ((n ^ 2) / (n + m))   |" +
			"(n * n) + (n * ((2 log n) ^ 4)) | (n ^ 2)   |   (n ^ 3) + (n ^ 2 * (1 + (n ^ .5) + (2 log n) ^ 2)) | (n ^ 3)   |" +
			"n + (n ^ m) | (n ^ m)   |   (n + m) + (n ^ m) | (n ^ m)   |" + //Is this last one true?
			"log_2 (n!) | n * log_2 n | (n choose 2) | n ^ 2" + //Log factorial and choose.
			"", "\\|");

		String[] args = new String[]{"n", "m"};
		
		int valueSuccess = 0;
		int totalSuccess = 0;
		for(int i = 0; i < test.length; i+= 2){
			FormulaNode f = test[i];
			FormulaNode s = new Formula(f).takeBigO();
			
			//Test approximate accuracy
			if(testBigOApprox(args, f, s)){
				valueSuccess++;
			}
			else{
				System.err.println("Serious error: Value failure for " + f.asLatexString() + " -> " + s.asLatexString());
			}
			
			//Test accuracy
			FormulaNode key = test[i + 1];
			if(key.formulaEquals(s)){
				System.out.println("BigO success: " + f.asString() + " -> " + s.asString() + " = " + key.asString());
				totalSuccess++;
			}
			else{
				System.out.println("BigO failure: " + f.asString() + " -> " + s.asString() + " != " + key.asString());
			}
		}
		
		for(int i = 0; i < test.length; i++){
			//TODO Does this only holds for polynomials?  I think we need to ensure that all args are polynomial.
			FormulaNode f = new Formula(test[i]).simplify();
			FormulaNode fn = new Formula(new BinOpNode(BinOpNode.DIVIDE, f, new VariableNode("n"))).simplify();
			if(BinOpNode.xInBigOofY(f, fn)){
				System.out.println("BigO Error: " + f.asString() + " -> " + fn.asString() + " is innaccurate.");
			}
		}
		System.out.println("Total success: " + totalSuccess + " / " + test.length / 2);
		System.out.println("Value success: " + valueSuccess + " / " + test.length / 2);
	}
	
	static boolean testBigOApprox(String[] args, FormulaNode f0, FormulaNode f1){
		return testBigOApprox(args, f0, f1, 100000, 100);
	}
	
	static boolean testBigOApprox(String[] args, FormulaNode f0, FormulaNode f1, double x, double c){
		
		VarSet v = new VarSet();
		
		for(int i = 0; i < args.length; i++){
			v.put(args[i], x);
		}
		
		double d0 = f0.eval(v);
		double d1 = f1.eval(v) * c;
		
		if(d0 == Double.POSITIVE_INFINITY || d1 == Double.POSITIVE_INFINITY){
			return testBigOApprox(args, f0, f1, x / 10, c);
		}
		
		if(d0 >= d1){
			System.err.println("Failure: " + f0.asString() + " -> " + d0 + " > " + f1.asString() + " -> " + d1);
		}
		
		return d0 < d1;
	}
	
	private static boolean epsilonCompare(double d0, double d1){
		if(Double.isNaN(d0) && Double.isNaN(d1)) return true;
		else return Math.abs(d0 - d1) <= .00001;
	}
	
	static boolean testValueEqual(String[] args, FormulaNode f, FormulaNode s){
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
			
			double d0 = f.eval(v);
			double d1 = s.eval(v);
			
			if(!epsilonCompare(d0, d1)){
				failed = true;
				System.err.println("Simplification Error: " + f.asString() + " -> " + s.asString() + " is invalid for " + v.asString() + ".  Original: " + d0 + ", Simplified: " + d1);
			}
		}
		
		if(!failed){
			//System.out.println("Simplification " + f.asString() + " -> " + s.asString() + " accurate.");
		}
		return !failed;
	}
	
}
