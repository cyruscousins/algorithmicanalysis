package document;

import java.io.OutputStream;
import java.io.PrintStream;

import algorithm.AbstractDataType;
import algorithm.Algorithm;

public class LatexStream {
	PrintStream out;
	public LatexStream(PrintStream o){
		this.out = o;
	}
	
	/////////////////////////
	//PRIMITIVE IO FUNCTIONS:
	
	public void print(String s){
		out.print(s);
//		out.print(s.replaceAll("_", "\\\\_").replaceAll("log\\\\_", "log_"));
	}
	public void println(String s){
		print(s + "\n\n");
	}
	public void printStrings(String[] s){
		for(int i = 0; i < s.length; i++){
			println(s[i]);
		}
	}
	
	//////////////////////////////
	//DOCUMENT CREATION FUNCTIONS:
	
	//Includes and definitions.
	private void printEnvironment(){
		println(

				"\\usepackage{amssymb, amsmath}\n" + 
				"\\usepackage{fullpage}\n" + 
				"\\usepackage{verbatim}\n" + 
				"\\usepackage{indentfirst}\n" + 
				"\\usepackage{multicol}\n" +
				"\\usepackage[landscape,margin=.5in]{geometry}\n" +
				"\\usepackage[utf8]{inputenc}" +
				
				"\\newcommand{\\bigO}{\\mathcal{O}}\n"
		);
	}
	
	public void printHeader(int fontSize){
		println("\\documentclass[" + fontSize + "14pt]{extarticle}\n");
		
		printEnvironment();
		
		println(
				
				"\\begin{document}\n"// +
//				"\\maketitle\n" + 
//				"\\tableofcontents\n" + 
//				"\\pagebreak\n"
		);
	}
	
	public void printTitledHeader(int fontSize, boolean toc, String title, String subtitle, String author, String date){
		println("\\documentclass[" + fontSize + "pt]{extarticle}\n");
		
		printEnvironment();
		
		if(title != null){
			println("\\title{" + title + "}");
		}
		if(subtitle != null){
			println("\\usepackage{titling}");
			println("\\newcommand{\\subtitle}[1]{\\posttitle{\\par\\end{center}\n\\begin{center}\\large#1\\end{center}\n\\vskip0.5em}}");
			println("\\subtitle{" + subtitle + "}");
		}
		if(author != null){
			println("\\author{" + author + "}");
		}
		if(date != null){
			println("\\date{" + date + "}");
		}
		
		
		/*
		
		 		"\\title{Analysis of Dijkstra's Algorithm and Partial Sorting with various Priority Queue types.}\n" + 
				"\\author{Cyrus Cousins, with additional credit to Cyrus' Computer}\n" + 
				"\\date{17 April 2014}\n" + 
		
		 */
		
		println(
				"\\begin{document}\n" + 
				"\\maketitle");
		
		
		if(toc){
			println(
				"\\tableofcontents\n" + 
				"\\listoffigures\n" +
				"\\pagebreak\n");
		}
	}
	
	public void closeDocument(){
		println("\\end{document}");
	}
	
	///////////////////////////////////////////
	//Print the info for an algorithm/datatype:
	public void printADTInfo(AbstractDataType adt){
		
		
	}
	
	public void printAlgorithmInfo(Algorithm algorithm){
		
		println("\\section{" + algorithm.name.replace("_", "\\_") + "}");
		
		if(algorithm.strings.containsKey("latexinfofile")){
			println("\\input{" + "res/tex/" + algorithm.strings.get("latexinfofile") + "}");
		}
		
		String[] algStrings = algorithm.summarizeAlgorithm(); //TODO refactor this function to take strings for \section and \subsection.
		printStrings(algStrings);
		
	}
}
