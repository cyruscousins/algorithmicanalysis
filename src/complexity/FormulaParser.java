
package complexity;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Pattern;

public class FormulaParser {
  
  static boolean validate(String s){
	  //Ensure parens match
	  
	  int parenBalance = 0;
	  for(int i = 0; i < s.length(); i++){
		  if(s.charAt(i) == '(') parenBalance++;
		  else if(s.charAt(i) == ')') parenBalance--;
		  if(parenBalance < 0){
			  //A ')' was found unmatched to an '('.
			  //Issue some sort of error message.
			  return false;
		  }
	  }
	  
	  if(parenBalance != 0){
		  //An '(' was found unmatched to a ')'.
		  //Issue some sort of error message.
		  return false;
	  }
	  
	  //Check other stuff.
	  
	  return true;
  }
  
  private static int parseOperator(String s){
	  switch(s){
		  case "+":
			  return BinOpNode.ADD;
		  case "-":
			  return BinOpNode.SUBTRACT;
		  case "*":
			  return BinOpNode.MULTIPLY;
		  case "/":
			  return BinOpNode.DIVIDE;
		  case "log":
			  return BinOpNode.LOGARITHM;
		  case "^":
			  return BinOpNode.EXPONENTIATE;
		  default:
		      return BinOpNode.NOP;
	  }
  }
  
  private static FormulaNode apply(int op, FormulaNode f0, FormulaNode f1){
	  if((f0 == null) ^ (op == BinOpNode.NOP)){
		  //Invalid, must have an op and a node, or neither.
		  System.out.println("Apply error: " + op + " " + ((f0 == null) ? f0 : f0.asString()) + " " + ((f1 == null) ? f1 : f1.asString()));
		  return null;
	  }
	  else if(f0 == null && op == BinOpNode.NOP){
		  //First node, no op has been selected and f0 doesn't exist.
		  return f1;
	  }
	  else return new BinOpNode(op, f0, f1);
  }
  
  private static Pattern numPattern = Pattern.compile("~?(?:[0-9]+.?[0-9]*)|(?:.[0-9]+)"); //handle negative numbers (weakly).
  private static Pattern varPattern = Pattern.compile("[a-zA-Z][0-9a-zA-Z~_]*");

  private static Pattern operatorPattern = Pattern.compile("([\\(\\)+\\-*/!^]|(?:log)|(?:ln)|(?:choose))");
  
  private static void substituteBinaryOperators(List<Object> s, String[] operators, int[] opTypes){
	  for(int i = 0; i < s.size(); i++){
		  for(int j = 0; j < operators.length; j++){
			  if(s.get(i).equals(operators[j])){
				  s.set(i, new BinOpNode(opTypes[j], (FormulaNode)s.get(i - 1), (FormulaNode)s.get(i + 1)));
				  s.remove(i + 1);
				  s.remove(i - 1);
				  i--;
				  break;
			  }
		  }
	  }
  }
  
  private static void substituteInfixBinaryOperators(List<Object> s, int[] opTypes){
	  for(int i = 0; i < s.size(); i++){
		  for(int j = 0; j < opTypes.length; j++){
			  //System.out.println("i: " + i + ", j: " + j);
			  if(s.get(i).equals(BinOpNode.opStrings[opTypes[j]])){
				  s.set(i, new BinOpNode(opTypes[j], (FormulaNode)s.get(i - 1), (FormulaNode)s.get(i + 1)));
				  s.remove(i + 1);
				  s.remove(i - 1);
				  i--;
				  break;
			  }
		  }
	  }
  }
  
  //Pull * and + into groups.
  private static FormulaNode expandOperatorTrees(FormulaNode f){
	  if(f instanceof UnaryOperator){
		  UnaryOperator u = (UnaryOperator)f;
		  return new UnaryOperator(u.operationType, expandOperatorTrees(u.argument));
	  }
	  else if(f instanceof BinOpNode){
		  BinOpNode b = (BinOpNode)f;
		  int opType = b.operationType;
		  
		  if(!(opType == BinOpNode.ADD || opType == BinOpNode.MULTIPLY)){
			  return new BinOpNode(opType, expandOperatorTrees(b.l), expandOperatorTrees(b.r));
		  }
		  
		  List<FormulaNode> nodes = new ArrayList<FormulaNode>();
		  
		  nodes.add(b.l);
		  nodes.add(b.r);
		  
		  for(int i = 0; i < nodes.size(); i++){
			  if(nodes.get(i) instanceof BinOpNode){
				  BinOpNode thisNode = (BinOpNode)nodes.get(i);
				  if(thisNode.operationType == opType){
					  //Expand
					  nodes.set(i, thisNode.l);
					  nodes.add(i + 1, thisNode.r);
					  i--; //And process this index again.
				  }
				  else if (opType == BinOpNode.ADD && thisNode.operationType == BinOpNode.SUBTRACT){
					  //TODO this.
				  }
			  }
		  }
		  
		  if(nodes.size() == 2){
			  return new BinOpNode(opType, expandOperatorTrees(b.l), expandOperatorTrees(b.r));
		  }
		  else{
			  FormulaNode[] nodesArr = new FormulaNode[nodes.size()];
			  for(int i = 0; i < nodes.size(); i++){
				  nodesArr[i] = expandOperatorTrees(nodes.get(i));
			  }
			  
			  int ocOp = -1;
			  if (opType == BinOpNode.ADD){
				  ocOp = OpCollectionNode.ADD;
			  }
			  else if (opType == BinOpNode.MULTIPLY){
				  ocOp = OpCollectionNode.MULTIPLY;
			  }

			  OpCollectionNode o = new OpCollectionNode(nodesArr, nodesArr.length, ocOp);
			  return o;
		  }
		  
	  }
	  else return f;
  }

  public static FormulaNode parseFormula(List<Object> s){
	  //Recursively handle parens

	  int parenDepth = 0;
	  int start = -1; //Initialization unneccessary, this variable will be set before use on valid input, and this function is not called without validation.
	  for(int i = 0; i < s.size(); i++){
		  if(s.get(i).equals("(")){
			  if(parenDepth == 0){
				  start = i;
			  }
			  parenDepth++;
		  }
		  else if(s.get(i).equals(")")){
			  parenDepth--;
			  if(parenDepth == 0){
				  int nlen = i - start - 1;
				  if(nlen > 0){
					  ArrayList<Object> sub = new ArrayList<>();
					  
					  s.remove(start);
					  i--;
					  
					  for(int j = 0; j < nlen; j++){
						  sub.add(s.remove(start));
						  i--;
					  }
					  
					  FormulaNode f1 = parseFormula(sub);
					  s.set(i, f1);
				  }
			  }
		  }
	  }
	  
//	  for(int i = 0; i < s.size(); i++){
//		  System.out.println(s.get(i));
//	  }
	  
	  //Handle right associative operations (!)
	  
	  for(int i = 0; i < s.size(); i++){
		  if(s.get(i).equals("!")){
			  s.set(i - 1, new UnaryOperator(UnaryOperator.FACTORIAL, (FormulaNode)s.get(i - 1)));
			  s.remove(i);
			  i--;
		  }
	  }
	  
	  substituteInfixBinaryOperators(s, new int[]{BinOpNode.EXPONENTIATE, BinOpNode.LOGARITHM, BinOpNode.CHOOSE});
	  
	  //Natural Logarithm: Has its own syntax.
	  
	  for(int i = 0; i < s.size(); i++){
		  if(s.get(i).equals("ln")){
			  s.set(i, new BinOpNode(BinOpNode.LOGARITHM, ConstantNode.E, (FormulaNode)s.remove(i + 1)));
			  i--;
		  }
	  }
	  
	  substituteInfixBinaryOperators(s, new int[]{BinOpNode.MULTIPLY, BinOpNode.DIVIDE});
	  substituteInfixBinaryOperators(s, new int[]{BinOpNode.ADD, BinOpNode.SUBTRACT});
	  
//	  System.out.println("RESULT:");
//	  for(int i = 0; i < s.size(); i++){
//		  System.out.println(s.get(i));
//	  }
	  
	  if(s.size() != 1){
		  System.err.println("Parse Error: Size " + s.size());
		  for(int i = 0; i < s.size(); i++){
			  System.err.println(s.get(i));
		  }
		  return null;
	  }
	  else{
		  if(!(s.get(0) instanceof FormulaNode)){
			  System.err.println("Parse Error: Invalid Result");
			  System.err.println(s.get(0));
			  return null;
		  }
	  }
	  return (FormulaNode) s.get(0);
  }

  //Validate and lex.
  public static FormulaNode parseFormula(String s){
	  //TODO remove parens & recurse
	  
	  if(!validate(s)){
		  //Issue some sort of error message.
		  return null;
	  }
	  
	  // Preprocessing
	  
//	  System.out.println(s);
	  s = s.replaceAll(" *([\\(\\)+\\-*/!^]) *", " $1 "); //Preprocess to get operators spaced out.
//	  System.out.println(s);
	  s = s.replaceAll(" *log_((?:[0-9]+.?[0-9]*)|(?:.[0-9]+)) *", " $1 log ");
//	  System.out.println(s);
	  s = s.trim();
//	  System.out.println(s);
//	  System.out.println("");
	  
	  String[] split = s.split("[ \t\n]+");
	  
	  List<Object> l = new ArrayList<Object>();
	  for(int i = 0; i < split.length; i++){
		  if(numPattern.matcher(split[i]).matches()){

			  boolean negate = false;
			  if(split[i].charAt(0) == '~'){
				  negate = true;
				  split[i] = split[i].substring(1);
			  }
			  double val = Double.valueOf(split[i]);
			  if(negate) val *= -1;
			  l.add(new ConstantNode(val));
		  }
		  else if(operatorPattern.matcher(split[i]).matches()){
			  //This is an operator.  For now, leave it as a string.
			  l.add(split[i]);
		  }
		  else if(varPattern.matcher(split[i]).matches()){
			  l.add(new VariableNode(split[i]));
		  }
		  //TODO don't know what to do with these.
		  else{
			  System.err.println("WARNING: Questionable input: " + split[i]);
			  l.add(split[i]);
		  }
	  }
	  
	  //Read variables and constants.
	  return expandOperatorTrees(parseFormula(l));
  }
  
  
  //Parse and recurse.
  //TODO 'twould be best to not have right to left associativity.
  private static FormulaNode parseFormulaOld(String[] s){
//	  System.out.print("Processing tokens: ");
//	  for(int i = 0; i < s.length; i++){
//		  System.out.print("\"" + s[i] + "\", ");
//	  }
//	  System.out.println("");
	  
	  FormulaNode f0 = null;
	  int lastOp = BinOpNode.NOP;
	  
	  int parenDepth = 0;
	  int start = -1; //Initialization unneccessary, this variable will be set before use on valid input, and this function is not called without validation.
	  for(int i = 0; i < s.length; i++){
		  if(s[i].equals("(")){
			  if(parenDepth == 0){
				  start = i;
			  }
			  parenDepth++;
		  }
		  else if(s[i].equals(")")){
			  parenDepth--;
			  if(parenDepth == 0){
				  int nlen = i - start - 1;
				  if(nlen > 0){
					  String[] subArray = new String[nlen];
					  for(int j = 0; j < nlen; j++){
						  subArray[j] = s[start + 1 + j];
					  }
					  FormulaNode f1 = parseFormulaOld(subArray);
					  f0 = apply(lastOp, f0, f1);
				  }
			  }
		  }
		  else if(parenDepth == 0){
			  int binOp = parseOperator(s[i]);
			  if(binOp != BinOpNode.NOP){
				  if(lastOp != BinOpNode.NOP){
					  //This is bad
					  System.err.println("Multiple Operators Detected?");
				  }
				  else{
					  lastOp = binOp;
				  }
			  }
			  else if (numPattern.matcher(s[i]).matches()){
				  boolean negate = false;
				  if(s[i].charAt(0) == '~'){
					  negate = true;
					  s[i] = s[i].substring(1);
				  }
				  double val = Double.valueOf(s[i]);
				  if(negate){
					  val *= -1;
				  }
				  FormulaNode f1 = new ConstantNode(val);
				  f0 = apply(lastOp, f0, f1);
				  lastOp = BinOpNode.NOP;
			  }
			  else if (varPattern.matcher(s[i]).matches()){
				  FormulaNode f1 = new VariableNode(s[i]);
				  f0 = apply(lastOp, f0, f1);
				  lastOp = BinOpNode.NOP;
			  }
			  else{
				  System.err.println("String \"" + s[i] +"\" not recognized.");
			  }
		  }
	  }
//	  System.out.println("Returning " + (f0 == null ? f0 : f0.asString()));
	  return f0;
  }
  
  //Validate and lex.
  public static FormulaNode parseFormulaOld(String s){
	  //TODO remove parens & recurse
	  
	  if(!validate(s)){
		  //Issue some sort of error message.
		  return null;
	  }
	  
	  // Preprocessing
	  
	  //s = s.replaceAll("\\-((?:[0-9]+.?[0-9]*)|(?:.[0-9]+))", "~$1"); //Handle negative numbers.
	  s = s.replaceAll(" *([\\(\\)+\\-*/^]) *", " $1 "); //Preprocess to get operators spaced out.
	  s = s.trim();
	  
//	  System.out.println("Proccessing \"" + s + "\"");
	  
	  String[] split = s.split("[ \t\n]+");
	  
	  return parseFormulaOld(split);
  }
  
  public static FormulaNode[] parseFormulae(String s, String splitter){
	  String[] split = s.split(splitter);
//	  for(int i = 0; i < split.length; i++){
//		  System.out.println(split[i]);
//	  }
	  FormulaNode[] f = new FormulaNode[split.length];
	  for(int i = 0; i < split.length;i++){
		  f[i] = parseFormula(split[i]);
	  }
	  return f;
  }
  
  public static void main(String[] args){
	String[] tests = "n|n*n|n^n|(n+m)|n*(2 log n)|n * log_2 n|log_2 (n!)".split("\\|");
	for(int i = 0; i < tests.length; i++){
		//System.out.println(tests[i]);
		FormulaNode f = parseFormula(tests[i]);
		System.out.println(tests[i] + " = " + ((f == null) ? f : f.asString()));
	}
  }
  
}
