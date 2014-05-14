package algorithm;

import java.util.HashMap;

import complexity.FormulaNode;
import complexity.FormulaParser;

import graphics.*;

public class UnorderedArray extends PriorityQueue{
	public static HashMap<String, Function> PQMAP;
	static{
		
		PQMAP = new HashMap<String, Function>();
		
		String[] fnames = functionNames;
		//fnames = new String[]{"insert", "remove", "isEmpty", "size"};
		
		FormulaNode[] worstCosts = FormulaParser.parseFormulae("n|n|1|n|1|n", "\\|");
		FormulaNode[] amortizedCosts = FormulaParser.parseFormulae("1|n|1|1|1|n", "\\|");
		FormulaNode[] expectedCosts = FormulaParser.parseFormulae("1|n|1|1|1|n", "\\|");
		
		for(int i = 0; i < fnames.length; i++){
			HashMap<String, FormulaNode> complexity = new HashMap<String, FormulaNode>();
			complexity.put("worst case cost", worstCosts[i]);
//			complexity.put("worst case bigO", worstCosts[i].bigO());

			complexity.put("amortized cost", amortizedCosts[i]);
//			complexity.put("amortized bigO", amortizedCosts[i].bigO());
			
			complexity.put("expected cost", expectedCosts[i]);
//			complexity.put("expected bigO", expectedCosts[i].bigO());
			
			Function f = new Function(fnames[i], complexity);
			PQMAP.put(fnames[i], f);
		}
		
	}
	
	
	
	int[] data;
	int top;

	RenderInfo info;
	
//	public UnorderedArray(TextStream text, RenderInfo info, int startSize){
//		super(text, PQMAP);
//		this.info = info;
//		data = new int[startSize];
//	}
	
	public void put(int i, Renderer r){
		
		
		if(top >= data.length){
			text.push("Put " + i +" in position " + top + ".  Space not avaialble, must resize, O(n) cost, O(1) amortized.");
			
			int[] newData = new int[data.length * 2];
			//out << "double array size."
			for(int j = 0; j < top; j++){
				newData[j] = data[j];
				r.put("Unordered Array 2", new ArrayRenderer(info, newData, j + 1, 400, 500));

				r.redraw();

				try{
					Thread.sleep(100);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			r.remove("Unordered Array 2");

			data = newData;

			//Need to resize.
		}
		else{
			text.push("Put " + i +" in position " + top + ".  Space available, O(1).");
		}
		data[top] = i;
		top++;
		
		r.put("Unordered Array", new ArrayRenderer(info, data, top, 400, 400));

		r.redraw();
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int removeMax() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void put() {
		// TODO Auto-generated method stub
		
	}
}
