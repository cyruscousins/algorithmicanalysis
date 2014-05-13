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
import javax.swing.JTextArea;

import algorithm.Algorithm;
import algorithm.Environment;

import color.ColorDatabase;

import complexity.FormulaNode;
import complexity.FormulaParser;

import graphics.GraphRenderer;
import graphics.RenderInfo;
import graphics.stroke.HandStroke;

public class GUI extends JPanel implements ActionListener{
	
	public static final int INPUTS = 4;

	TextField[] inputs;
	TextField[] expanded;
	TextField[] bigOs;
	JButton[] shows;
	JButton[] clears;
	
	FormulaNode[] formulae;
	FormulaNode[] formulaeSubbed;
	
	GraphCanvas graph;
	JPanel texts;
	JPanel buttons;
	
	JPanel substitutions;

	JTextArea subArea;
	TextField subAlgorithmName;
	TextField subAnalysisName;

	JTextArea console;
	
	//TODO console?
	//TODO latex mode?
	
	GraphRenderer gr;
	Color[] colors;
	
	Environment algEnv;
	
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
		formulaeSubbed = new FormulaNode[INPUTS];
		
		//BASIC SIZING
		
		Dimension size = new Dimension(100, 800);
		setMinimumSize(size);
		setPreferredSize(size);
		setMaximumSize(size);
		
		//setLayout(new GridLayout(2, 1));
		setLayout(new FlowLayout());
		
		graph = new GraphCanvas(this, ri, formulaeSubbed, colors);
		graph.setMinimumSize(new Dimension(1000, 500));
		graph.setPreferredSize(new Dimension(1000, 500));
		graph.setMaximumSize(new Dimension(2000, 1000));
		
		//TEXT:
		
		texts = new JPanel();
		texts.setLayout(new GridLayout(INPUTS + 1, 5));

		inputs = new TextField[INPUTS];
		expanded = new TextField[INPUTS];
		bigOs = new TextField[INPUTS];
		
		shows = new JButton[INPUTS];
		clears = new JButton[INPUTS];
		
		String[] labels = new String[]{"Formulae", "Expanded", "BigO", "Graph", "Clear"};
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
			clears[i].setBackground(Color.WHITE);

			texts.add(inputs[i]);
			texts.add(expanded[i]);
			texts.add(bigOs[i]);
			texts.add(shows[i]);
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
		
		console = new JTextArea(10, 20);
		
		substitutions.add(new JLabel("Error Console:"));
		substitutions.add(new JScrollPane(console));
		
		String[] extraButtons = new String[]{"View Partial Sort Demo"};
		for(int i = 0; i < extraButtons.length; i++){
			JButton jb = new JButton(extraButtons[i]);
			jb.addActionListener(this);
			substitutions.add(jb);
		}
		
		
		
		//OVERALL LAYOUT:
		
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
		leftPanel.add(graph);
		leftPanel.add(texts);
		
		add(leftPanel);
		add(substitutions);
		
	}
	
	public FormulaNode substitute(FormulaNode formula){
		List<String> subVars = new ArrayList<>();
		List<FormulaNode> vals = new ArrayList<>();
		
		String[] lines = subArea.getText().split("\n");
		for(int i = 0; i < lines.length; i++){
			String[] s = lines[i].split("->");
			if(s.length != 2){
				continue;
			}
			
			try{
				String v = s[0].trim();
				FormulaNode f = FormulaParser.parseFormula(s[1]);
				
				subVars.add(v);
				vals.add(f);
			}
			catch(Exception e){
				e.printStackTrace();
				console.setText("Error reading line " + i + " of the substitutions box.");
				
				System.exit(1);
			}
		}

//		console.setText("");
		for(int j = 0; j < subVars.size(); j++){
			formula = formula.substitute(subVars.get(j), vals.get(j));
//			console.setText(console.getText() + "Substituting \"" + subVars.get(j) + "\" for " + vals.get(j).asString() + "\n" + formula.asString());
		}
		return formula;
	}
	
	public void showFormula(int i, boolean repaint){
		FormulaNode f = FormulaParser.parseFormula(inputs[i].getText());
		if(f != null){
			System.out.println("Showing " + i);
			inputs[i].setBackground(Color.WHITE);
			
			FormulaNode sub = substitute(f);
			FormulaNode bigO = sub.takeBigO();

			formulae[i] = f;
			formulaeSubbed[i] = sub;
			
			expanded[i].setText(sub.asString());
			bigOs[i].setText(bigO.asString());
		}
		else{
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
		
		formulae[i] = formulaeSubbed[i] = null; //TODO formulae necessary?
		
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
			else if(name.equals("Substitute from Algorithm")){
				String algName = subAlgorithmName.getText();
				String analysisType = subAnalysisName.getText();
				
				loadAlgorithmToSubstitutions(algName, analysisType);
			}
			else if (name.equals("View Partial Sort Demo")){
				
				//Get back to default state.
				subArea.setText("");
				loadAlgorithmToSubstitutions("unordered array", "expected");
				loadAlgorithmToSubstitutions("ordered array", "expected");
				loadAlgorithmToSubstitutions("binary heap", "expected");
				
				String moreCosts = 
						"kthorderstatistic -> n * 20\n" +
						"sort -> a * ceil(log_2 a) - 2 ^ (ceil (log_2 a)) + 1\n\n" +
						"a -> n / 4\n";
				
				subArea.setText(subArea.getText() + moreCosts);
				
				
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
						"Note that here b is a \"fake variable\" in that it is implemented in terms of n: this is because the grapher only supports univariate functions.\n" +
						"For this reason, the bigO value is only in terms of b.\n" +
						"Try experimenting with the value of b and the costs of the various operations in the substitution panel.\n" +
						"This is an excellent example of an algorithm in which the naïve solution outperforms the asymptotically optimal solution for small values.\n" +
						"Please be patient.  This program is a bit slow.\n");
			}
		}
	}
	
}
