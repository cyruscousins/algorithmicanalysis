package practical;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import algorithm.Algorithm;
import algorithm.Environment;

import color.ColorDatabase;

import complexity.FormulaNode;
import complexity.FormulaParser;

import graphics.GraphRenderer;
import graphics.RenderInfo;
import graphics.stroke.HandStroke;

public class GUI extends JPanel implements ActionListener, ChangeListener{
	
	public static final int INPUTS = 8;

	TextField[] inputs;
	TextField[] expanded;
	TextField[] bigOs;
	JButton[] shows;
	JButton[] clears;
	
	FormulaNode[] formulae;
	
	GraphCanvas graph;
	JPanel texts;
	JPanel buttons;
	
	JPanel substitutions;

	JTextArea subArea;
	TextField subAlgorithmName;
	TextField subAnalysisName;

	JTextArea console;
	
	JSlider xSlider, ySlider;
	
	//TODO latex mode?
	
	Color[] colors;
	
	Environment algEnv;
	

	private static final String[] demoButtonStrings = new String[]{"Partial Sort", "Dijkstra's"};
	
	public GUI(){
		
		//ENVIRONMENT AND OTHER:
		algEnv = new Environment();
		algEnv.loadADTDirectory("res/adt/");
		algEnv.loadAlgorithmDirectory("res/alg/");
		
		
		//LOAD XKCD COLOR DB
		ColorDatabase d = new ColorDatabase();
		
		colors = d.getRandomSelection(INPUTS);


		//LOAD FONT
		File ff = null;
		Font fraw = null;;
		Font f2 = null;
		try{
			ff = new File("res/font/Gregscript.ttf");
			fraw = Font.createFont(Font.TRUETYPE_FONT, ff);
			f2 = fraw.deriveFont(Font.PLAIN, 32f);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		Font f3 = new Font("Sans-Serif", Font.PLAIN, 20);
		
		RenderInfo ri;
		
		if(f2 != null){
			ri = new RenderInfo(f2, new HandStroke(2f, 2.5f), Color.BLACK);
		}
		else{
			ri = new RenderInfo(f3, new HandStroke(2f, 2.5f), Color.BLACK);
		}
		
		//DATA

		formulae = new FormulaNode[INPUTS];
		
		//BASIC SIZING
		
		Dimension size = new Dimension(100, 800);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);
		
		//setLayout(new GridLayout(2, 1));
		setLayout(new FlowLayout());
		
		graph = new GraphCanvas(this, ri, formulae, colors);
		graph.setMinimumSize(new Dimension(1000, 500));
		graph.setPreferredSize(new Dimension(1000, 500));
		graph.setMaximumSize(new Dimension(2000, 1000));
		
		//TEXT:
		
		texts = new JPanel();
//		texts.setLayout(new GridLayout(INPUTS + 1, 5));
		texts.setLayout(new GridLayout(INPUTS + 1, 4));

		inputs = new TextField[INPUTS];
		expanded = new TextField[INPUTS];
		bigOs = new TextField[INPUTS];
		
		shows = new JButton[INPUTS];
		clears = new JButton[INPUTS];

		String[] labels = new String[]{"Formulae", "Expanded", "BigO", "Clear Graph"};
//		String[] labels = new String[]{"Formulae", "Expanded", "BigO", "Graph", "Clear"};
		for(int i = 0; i < labels.length; i++){
			texts.add(new JLabel(labels[i]));
		}
		
		for(int i = 0; i < INPUTS; i++){
			inputs[i] = new TextField();
			//inputs[i].setFont(inputs[i].getFont().deriveFont(10));
			expanded[i] = new TextField();
			expanded[i].setEditable(false);
			
			bigOs[i] = new TextField();
			bigOs[i].setEditable(false);
			
			shows[i] = new JButton("Show Formula " + (i + 1));
			shows[i].setBackground(colors[i]);
			clears[i] = new JButton("Clear Formula " + (i + 1));
			clears[i].setBackground(colors[i]);
//			clears[i].setBackground(Color.WHITE);

			texts.add(inputs[i]);
			texts.add(expanded[i]);
			texts.add(bigOs[i]);
//			texts.add(shows[i]);
			texts.add(clears[i]);
			
			shows[i].addActionListener(this);
			clears[i].addActionListener(this);
		}

		//SUBSTITUTIONS:
		
		substitutions = new JPanel();
		substitutions.setLayout(new BoxLayout(substitutions, BoxLayout.Y_AXIS));
		
		substitutions.add(new JLabel("Substitutions Panel:"));
		subArea = new JTextArea(40, 20);
//		subArea.setMinimumSize(new Dimension(200, 500));
		substitutions.add(new JScrollPane(subArea));
		
		substitutions.add(new JLabel("Algorithm Name"));
		
		subAlgorithmName = new TextField("", 15);
		substitutions.add(subAlgorithmName);
		
		substitutions.add(new JLabel("Analysis Mode"));
		subAnalysisName = new TextField("amortized");
		substitutions.add(subAnalysisName);
		
		
		JButton subButton = new JButton("Load Algorithm");
		subButton.addActionListener(this);
		
		substitutions.add(subButton);
		
		//Sliders
		xSlider = new JSlider(1, 100, 1);
		xSlider.addChangeListener(this);
		ySlider = new JSlider(1, 100, 1);
		ySlider.addChangeListener(this);
		
		substitutions.add(new JLabel("X graph range:"));
		substitutions.add(xSlider);
		substitutions.add(new JLabel("Y graph range:"));
		substitutions.add(ySlider);
		
		console = new JTextArea(10, 20);
		
		console.setEditable(false);
		console.setBackground(new Color(0xffddee));
		
		substitutions.add(new JLabel("Error Console:"));
		substitutions.add(new JScrollPane(console));
		
		JPanel demoPanel = new JPanel();
		demoPanel.setLayout(new BoxLayout(demoPanel, BoxLayout.X_AXIS));
		
		substitutions.add(new JLabel("Demos:"));
		for(int i = 0; i < demoButtonStrings.length; i++){
			JButton jb = new JButton(demoButtonStrings[i]);
			jb.addActionListener(this);
			demoPanel.add(jb);
//			substitutions.add(jb);
		}
		
		substitutions.add(demoPanel);
		
		JButton showButton = new JButton("Show Formulae");
		showButton.addActionListener(this);
		substitutions.add(showButton);
		
		
		
		//OVERALL LAYOUT:
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.add(graph);
		leftPanel.add(texts);
		
		add(leftPanel);
		add(substitutions);
		
	}

	//Returns partially substituted, bigO partially substituted, and fully substituted functions.
	public FormulaNode[] substitute(FormulaNode formula){
		
		List<String> variables = new ArrayList<>();

		List<String> bigOLittles = new ArrayList<>();
		List<String> bigOBigs = new ArrayList<>();
		
		List<String> subVars = new ArrayList<>();
		List<FormulaNode> vals = new ArrayList<>();
		
		String[] lines = subArea.getText().split("\n");
		for(int i = 0; i < lines.length; i++){
			String l = lines[i];
			if(lines[i].isEmpty() || lines[i].charAt(0) == '#'){
				continue;
			}
			if(lines[i].startsWith("VAR ")){
				variables.add(l.substring("VAR ".length()).trim());
			}
			else if(lines[i].contains("BIGO")){
				String[] s = l.split("BIGO");
				if(s.length != 2){
					console.setText("Error reading line " + i + " of the substitutions box.");
					break;
				}
				
				try{
					bigOLittles.add(s[0]);
					bigOBigs.add(s[1]);
				}
				catch(Exception e){
					e.printStackTrace();
					console.setText("Error reading line " + i + " of the substitutions box.");
					break;
				}
			}
			else if(lines[i].contains("->")){
				String[] s = l.split("->");
				if(s.length != 2){
					console.setText("Error reading line " + i + " of the substitutions box.");
					break;
				}
				
				try{
					String v = s[0].trim();
					FormulaNode f = FormulaParser.parseFormula(s[1]);
	
					if(f == null){
						console.setText("Error reading line " + i + " of the substitutions box.");
						break;
					}
					
					subVars.add(v);
					vals.add(f);
				}
				catch(Exception e){
					e.printStackTrace();
					console.setText("Error reading line " + i + " of the substitutions box.");
					break;
				}
			}
		}

//		console.setText("");
		
		FormulaNode withVars = formula;
		for(int j = 0; j < subVars.size(); j++){
			formula = formula.substitute(subVars.get(j), vals.get(j));
			if(!variables.contains(subVars.get(j))){
				withVars = withVars.substitute(subVars.get(j), vals.get(j));
			}
//			console.setText(console.getText() + "Substituting \"" + subVars.get(j) + "\" for " + vals.get(j).asString() + "\n" + formula.asString());
		}
		
		FormulaNode bigO = withVars.takeBigO();
		bigO = bigO.takeBigO(bigOLittles.toArray(new String[bigOLittles.size()]), bigOBigs.toArray(new String[bigOBigs.size()]));
				
		return new FormulaNode[]{withVars, bigO, formula};
	}
	
	public void showFormula(int i, boolean repaint){
		if(inputs[i].getText().isEmpty()){
			clearFormula(i);
		}
		FormulaNode f = FormulaParser.parseFormula(inputs[i].getText());
		if(f != null){
//			System.out.println("Showing " + i);
			inputs[i].setBackground(Color.WHITE);
			
			FormulaNode[] results = substitute(f); //Fully substituted, with variables.

			formulae[i] = results[2];
			
			expanded[i].setText(results[0].asString() + ", " + results[2].asString());
			bigOs[i].setText(results[1].asString());

			inputs[i].setBackground(Color.WHITE);
		}
		else{
			
			console.setText("Error parsing formula \"" + inputs[i].getText() + "\".");
			inputs[i].setBackground(Color.RED);
			expanded[i].setText("");
			bigOs[i].setText("");
			
		}

		inputs[i].repaint();
		expanded[i].repaint();
		bigOs[i].repaint();
		
		if(repaint) graph.repaint();
	}

	public void showFormula(int i){
		showFormula(i, true);
	}
	
	public void clearFormula(int i){
		
		inputs[i].setBackground(Color.WHITE);
		inputs[i].setText("");
		expanded[i].setText("");;
		bigOs[i].setText("");

		inputs[i].repaint();
		expanded[i].repaint();
		bigOs[i].repaint();
		
		formulae[i] = null;
		
	}
	
	public void loadAlgorithmToSubstitutions(String algName, String analysisType){

		Algorithm a = algEnv.getAlgorithm(algName);
		if(a == null){
			String text = "Algorithm \"" + algName + "\" not found.  Please select from:\n";
			String[] names = algEnv.getAlgorithmNames();
			for(int i = 0; i < names.length; i++){
				text += names[i] + "\n";
			}
			console.setText(text);
		}
		
		if(!a.type.hasAnalysisType(analysisType)){
			String text = "Algorithm \"" + algName + "\" does not have analysis type \"" + analysisType + "\".  Please select from:\n";
			String[] names = a.type.analysisTypes;
			for(int i = 0; i < names.length; i++){
				text += names[i] + "\n";
			}
			console.setText(text);
		}
		
		String subText = "";
		
		String[] fnames = a.type.functions;
		for(int i = 0; i < fnames.length; i++){
			subText += algName.replaceAll(" ", "_") + "_" + analysisType.replaceAll(" ",  "_") + "_" + fnames[i] + " -> " + a.functions.get(fnames[i]).costs.get(analysisType).asString() + "\n";
		}
		subArea.setText(subArea.getText() + subText);
	}
	
	public void clearSub(){
		subArea.setText("");
	}
	
	public void addSubstitionText(String text){
		subArea.setText(subArea.getText() + text);
	}
	
	public void actionPerformed(ActionEvent arg0) {
		Object source = arg0.getSource();
		if(source instanceof JButton){
			String name = ((JButton)source).getText();
			if(name.startsWith("Show Formula ")){
				showFormula(Integer.valueOf(name.substring("Show Formula ".length())) - 1);
			}
			else if(name.startsWith("Clear Formula ")){
				clearFormula(Integer.valueOf(name.substring("Clear Formula ".length())) - 1);
			}
			else if(name.equals("Load Algorithm")){
				String algName = subAlgorithmName.getText();
				String analysisType = subAnalysisName.getText();
				
				loadAlgorithmToSubstitutions(algName, analysisType);
			}
			else if (name.equals("Show Formulae")){
				for(int i = 0; i < INPUTS; i++){
					showFormula(i);
				}
			}
			//Partial Sort
			else if (name.equals(demoButtonStrings[0])){
				
				//Get back to default state.
				clearSub();
				
				addSubstitionText(
						"#This is a comment.\n" +
						"#Declare 'a' a variable so it shows up in bigO analysis.\n" +
						"VAR a\n" +
						"#Tell the program that a is in bigO of n, so analysis can be in normative form.\n" +
						"a BIGO n\n\n +");
				
				addSubstitionText("#Unordered array functions:\n\n");
				loadAlgorithmToSubstitutions("unordered array", "expected");
				
				addSubstitionText("\n#Ordered array functions:\n\n");
				loadAlgorithmToSubstitutions("ordered array", "expected");
				addSubstitionText("\n#Binary heap functions:\n\n");
				loadAlgorithmToSubstitutions("binary heap", "expected");

				addSubstitionText("\n#Kth order statistic and mergesort functions:\n\n");
				
				addSubstitionText(
						"kthorderstatistic -> n * 10\n" +
						"sort -> a * ceil(log_2 a) - 2 ^ (ceil (log_2 a)) + 1\n\n" +
						"#Define a.  Because the graph is univariate, everything must be in terms of constants and 'n'.\n" +
						"#Try adjusting the value of 'a' to explore the performance of these algorithms.\n" +
						"a -> n / 4\n");
				
				
				//Do formulae
				String[] types = new String[]{"unordered_array", "ordered_array", "binary_heap"};
				String analysisType = "expected";
				
				for(int i = 0; i < types.length; i++){
					String prefix = types[i] + "_" + analysisType;
					inputs[i].setText(prefix + "_construct + a * " + prefix + "_remove_min"); //TODO this is where sum goes!
				}
				
				inputs[types.length].setText("kthorderstatistic + sort");
				for(int i = types.length + 1; i < INPUTS; i++){
					inputs[i].setText("");
				}
				
				
				for(int i = 0; i < INPUTS; i++){
					showFormula(i, false);
				}
				
				graph.repaint();
				
				console.setText("In this demo, 3 naïve partial sorts are compared to an optimal kth order statistic + sort algorithm.\n" +
						"The operation being performed is a partial sort of the first a of n items.\n" +
						"Try experimenting with the value of a by modifying the line \"a -> n / 4\"." +
						"Also try adjusting the costs of the various operations in the substitution panel.\n" +
						"Try \"kthorderstatistic -> n * 2\" to see a more competitive 4th algorithm.\n" +
						"This is an excellent example of an algorithm in which the naïve solution outperforms the asymptotically optimal solution for small values.\n" +
						"Please be patient.  This program is a bit slow.\n");
			}
			else if (name.equals(demoButtonStrings[1])){

				subArea.setText("");

				subArea.setText(subArea.getText() +
						"#Declare e, the number of edges in the graph being analyzed, and v, the number of vertices, as variable.\n" +
						"VAR e\n" +
						"VAR v\n" +
						"#Tell the system that v is in bigO(e), to allow normative form analysis.\n" +
						"v BIGO e\n\n");
				
				addSubstitionText("#Unordered array functions:\n\n");
				loadAlgorithmToSubstitutions("unordered array", "expected");
				addSubstitionText("\n#Ordered array functions:\n\n");
				loadAlgorithmToSubstitutions("ordered array", "expected");
				addSubstitionText("\n#Binary heap functions:\n\n");
				loadAlgorithmToSubstitutions("binary heap", "expected");

				addSubstitionText(
						"\n#Declare e to be that of a dense graph (1/4 of all possible edges selected)\n" +
						"#Substitutions are performed in the order they appear, so make sure this line appears before \"v -> n\"\n" +
						"e -> (v choose 2) / 4\n" +
						"#Map n to v, to get v to show up in the bigO analysis (because v was declared a variable.\n#It will be immediately mapped back for the actual graph.\n" +
						"n -> v\n" +
						"#Map v to n, because n is the variable that is graphed.\n" +
						"v -> n");

				//Do formulae
				String[] types = new String[]{"unordered_array", "ordered_array", "binary_heap"};
				String analysisType = "expected";
				
				for(int i = 0; i < types.length; i++){
					String prefix = types[i] + "_" + analysisType;
					inputs[i].setText(prefix + "_construct + e * " + prefix + "_decrease_key"); //TODO this is where sum goes!
				}
				
				for(int i = types.length; i < INPUTS; i++){
					inputs[i].setText("");
				}
				
				
				for(int i = 0; i < INPUTS; i++){
					showFormula(i, false);
				}
				
				graph.repaint();
				
				console.setText(
						"In this demo, we see 3 priority queues go head to head on Dijkstra's algorithm, with a dense graph.  \n" +
						"Modify the \"e -> (v choose 2) / 4\" line to change the sparsity of the graph.\n" +
						"Try loading the fibonacci heap by entering \"fibonacci heap\" and \"expected\" into the Algorithm Name and Analysis Mode text fields, and clicking Load Algorithm.\n" +
						"");
			}
		}
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {
		Object o = arg0.getSource();
		if(o instanceof JSlider){
//			JSlider s = (JSlider) o;
//			
//			if(s == xSlider)
			
			//Either way, update the graph.
			
			graph.xMax = (int)(1000 * Math.pow(1.055, xSlider.getValue()));
			graph.yMax = (int)(10 * Math.pow(1.15, ySlider.getValue()));
			
			graph.repaint();
		}
	}
	
}
