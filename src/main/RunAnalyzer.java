package main;

import algorithm.Algorithm;
import algorithm.Environment;
import complexity.FormulaParser;
import complexity.VariableMapping;
import complexity.VariableNode;
import document.LatexStream;

public class RunAnalyzer {
	Environment environment;
	
	LatexStream latex;
	
	public RunAnalyzer(){
		environment = new Environment();
	}
	
	public void run(){
		
		//Load Priority Queues
		
//		String[] algorithmNames = new String[]{"unordered_array"};	
		String[] algorithmFiles = new String[]{"unordered_array", "ordered_array", "binary_heap", "fibonacci_heap", "brodal_queue"};
		String[] algorithmNames = new String[]{"unordered array", "ordered array", "binary heap", "fibonacci heap", "brodal queue"};
//		String[] algorithmNames = new String[]{"Unordered Array", "Ordered Array", "Binary Heap", "Splay Tree", "Fibonacci Heap"};
		
		Algorithm[] priorityQueues = new Algorithm[algorithmNames.length];
		
		environment.loadADTDirectory("res/adt/");
		environment.loadAlgorithmDirectory("res/alg/");
		for(int i = 0; i < algorithmFiles.length; i++){
			priorityQueues[i] = environment.getAlgorithm(algorithmNames[i]);
		}

		Algorithm dijkstras = environment.getAlgorithm("dijkstras_algorithm");
		Algorithm partialSort = environment.getAlgorithm("priority_queue_partial_sort");
		
		
		LatexStream stream = new LatexStream(System.out);

		//Basic Test Document:
		
		stream.printTitledHeader(14, true, "Basic Priority Queue Properties", "An Overview of Priority Queues", "Cyrus Cousins", "Spring 2014");
		
		for(int i = 0; i < priorityQueues.length; i++){
			stream.printAlgorithmInfo(priorityQueues[i]);
		}
		
		stream.closeDocument();
		
		
		if(true) return;
//		return;
		
		//Print

		//Latex Header
		
		stream.println(
				"\\documentclass[14pt]{extarticle}\n" + 
				"\\usepackage{amssymb, amsmath}\n" + 
				"\\usepackage{fullpage}\n" + 
				"\\usepackage{verbatim}\n" + 
				"\\usepackage{indentfirst}\n" + 
				"\\usepackage{multicol}\n" +
				"\\usepackage[landscape,margin=.5in]{geometry}\n" +
				"\\usepackage[utf8]{inputenc}" +
				
				
				"\\title{Analysis of Dijkstra's Algorithm and Partial Sorting with various Priority Queue types.}\n" +
				"\\author{Cyrus Cousins, with additional credit to Cyrus' Computer}\n" + 
				"\\date{17 April 2014}\n" +
				
				"\\newcommand{\\bigO}{\\mathcal{O}}\n" +
				
				"\\begin{document}\n" + 
				"\\maketitle\n" + 
				"\\tableofcontents\n" +
				"\\pagebreak\n"
				
				);
		
		stream.println("\\input{introduction.tex}");

		stream.println("\\section{Priority Queue Properties}");
		
		stream.println("\\begin{multicols}{2}");
		
		for(int i = 0; i < priorityQueues.length; i++){
			String[] s = priorityQueues[i].summarizeAlgorithm();
			stream.printStrings(s);
		}
		
		stream.println("\\end{multicols}");
		
		//Dijkstra's analysis
		
		stream.println("\\pagebreak");
		stream.println("\\section{Analysis of Dijkstra's algorithm with various priority queue structures.}");
		
//		stream.println("\\subsection{Summary of Dijkstra's Algorithm}");
		
		stream.printStrings(dijkstras.summarizeAlgorithm());
		
		VariableMapping postSubstitution = new VariableMapping();
		postSubstitution.put("n", new VariableNode("e"));
		
		for(int i = 0; i < priorityQueues.length; i++){
			Algorithm newAlg = dijkstras.substituteIn(priorityQueues[i], "insert, empty, remove_min, decrease_key".split(", "), postSubstitution);

			stream.printStrings(newAlg.summarizeAlgorithm());
		
		}
		
		//Dijkstra's with constraints on graph size

		stream.println("\\pagebreak");
		stream.println("\\section{Dijkstras on Constrained Graphs}");

		stream.println("Suppose we know something about the graphs we will be working with.  Perhaps we know that the graph will be particularly dense, and that $|E| \\in \\Theta\\big(|V|^2\\big)$.  Alternatively, the graph may be sparse, and $|E| \\in \\Theta\\big(|V|\\big)$.");
		stream.println("Maybe the graph was created by an exotic spanner, and we have a stranger bound, such as $|E| \\in \\bigO\\big(n \\log n\\big)$ or $|E| \\in \\bigO\\big(n \\sqrt n\\big)$.");
		
		stream.println("We can analyze the performance of Dijkstra's under these constraints, and we may find that the playing field has changed: perhaps we don't need Fibonacci Heaps or Brodal Queues to get an optimal lower bound\\footnote{This is extremely valuable in practice, as both of these algorithms are rarely used due to their extreme complexity and very high associated constants.}.");
//		
		String[] formulae = "v | v * log_2 v | (v ^ (3 / 2)) | v ^ 2".split("\\|");
		for(String formula : formulae){

			stream.println("\\subsection[Analysis of Dijkstra's with constraints.]{Analysis of Dijkstra's with $e =" + FormulaParser.parseFormula(formula).asLatexStringRecurse() + "$.}");
//			stream.println("\\par\n\\bigskip\n\n\\textbf{Analysis of Dijkstra's with $e =" + FormulaParser.parseFormula(formula).asLatexString() + "$.}");
//			stream.println("\\par\n\\bigskip\n\n\\textbf{Analysis of Dijkstra's with $e =" + formula + "$.}");
			for(int i = 0; i < priorityQueues.length; i++){
				Algorithm newAlg = dijkstras.substituteIn(priorityQueues[i], "insert, empty, remove_min, decrease_key".split(", "), postSubstitution);
				
				newAlg = newAlg.substituteIn(", $e \\in \\bigO\\big(" + formula + "\\big)$", "e", FormulaParser.parseFormula(formula));
				stream.println("\\par\\bigskip");
				stream.printStrings(newAlg.summarizeAlgorithmSimple());
			
			}
		}
		
		
		stream.println("\\pagebreak");
		stream.println("\\section{Analysis of Partial Sort}");
		stream.println("\\subsection{Introduction to Partial Sorting}");
		stream.print("Partial sorting refers to the idea of obtaining the first $b$ values of a set of $a$ in sorted order.");
		stream.print("Partial sorting is conceptually somewhat similar to median finding and $k$th order statistics (\\texttt{kth\\_order} $\\leq_m$ \\texttt{partial\\_sort}), but because the lower half of the list is sorted, the problem is fundamentally harder.");

		stream.print("As an example, considering the following array:\n $$A = \\begin{bmatrix} 5 & 6 & 1 & 9 & 8 & 13 & 0 & 42 \\end{bmatrix}$$\n");
		stream.print("We can quickly see that \\texttt{partial\\_sort(A, 4)} = $\\begin{bmatrix} 0 & 1 & 5 & 6 \\end{bmatrix}$, but it is a nontrivial problem, because the problem of partial sorting becomes more difficult as both the size of the original list and the number of elements to be sorted grow.");
		
		stream.println("Popular naïve techniques for partial sort include sorting the list and extracting the first $m$ items, in $\\Theta(n \\log n + m)$ time, and extracting the minimum $m$ times, in $\\Theta(n \\cdot m)$ time.  The latter is probably the technique you used to sort the above list manually.  ");
		stream.print("Under a comparison model, we can easily show this lower bound of $\\Omega(a + b \\log b)$ via a reduction from the sorting $\\Omega(b \\log b)$ lower bound, as a list of $b$ items can be ''partial sorted'' using parameters $a', b'$ both equal to $b$.  The $\\Omega\\big(a\\big)$ term of the lower bound can be derived through an adversarial argument: clearly all the data must be examined in an arbitrary partial sorting.  Fibonacci heaps and Brodal queues also meet this bound, and a quick look at the formulae shows that any data structure with linear time construction and $\\log n$ min removal cost will as well.");
		
		stream.println("Let's see how a data structure construction with repeated extraction algorithm works:");
		
		stream.printStrings(partialSort.summarizeAlgorithm());
		
		

		VariableMapping ntoa = new VariableMapping();
		ntoa.put("n", new VariableNode("a"));

		VariableMapping ntob = new VariableMapping();
		ntob.put("n", new VariableNode("b"));
		
		for(int i = 0; i < priorityQueues.length; i++){
			Algorithm newAlg = partialSort.substituteIn(priorityQueues[i], "remove_min".split(", "), ntoa);
			
			newAlg = newAlg.substituteIn(priorityQueues[i], "construct".split(", "), ntoa);

			stream.printStrings(newAlg.summarizeAlgorithm());
		}
		
		stream.println("\\subsection{Partial Sort conclusions}");
		stream.println("Here we see that the sorted array performs on par with the naïve sorting based technique (this should come as no surprise, as it is essentially the same algorithm),  and we also see that a binary heap meets the lower bound of $\\bigO\\big(n \\log n\\big)$.  This technique, which is equivalent to a heapsort that terminates early, is a popular technique, but more advanced techniques exist that match the lower bound in complexity.");
		
		
		stream.println("\\pagebreak\n\\null \\hfill \\textbf{Thanks for Listening!} \\hfill \\null");
		stream.println("\\end{document}");
	}
	
	//PROGRAM ENTRY
	public static void main(String[] args){
		new RunAnalyzer().run();
	}
}