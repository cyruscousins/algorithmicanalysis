package algorithm;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Environment {
	HashMap<String, Algorithm> algorithms;
	HashMap<String, AbstractDataType> dataTypes;
	
	public Environment(){
		dataTypes = new HashMap<String, AbstractDataType>();
		algorithms = new HashMap<String, Algorithm>();
	}
	
	public AbstractDataType getADT(String name){
		
		AbstractDataType a = dataTypes.get(name);
		
		if(dataTypes.containsKey(name)){
			return dataTypes.get(name);
		}
		else{
			try{
				FileReader f = new FileReader("res/adt/" + name);
				a = AbstractDataType.loadAbstractDatatype(f);
				f.close();
				
				if(a == null){
					return null;
				}
				
				dataTypes.put(name, a);
				
				return a;
			}
			catch(Exception e){
				return null;
			}
		}
	}
	
	public Algorithm getAlgorithm(String name){
		
		Algorithm a = algorithms.get(name);
		
		if(a != null){
			return algorithms.get(name);
		}
		else{
			try{
				FileReader f = new FileReader("res/alg/" + name);
				a = Algorithm.loadAlgorithm(this, f);
				f.close();

				if(a == null){
					System.err.println("Failure to find algorithm \"" + name + "\"");
					return null;
				}
				
				algorithms.put(name, a);
				return a;
			}
			catch(IOException e){
				System.err.println("IO exception reading algorithm \"" + name + "\"");
				e.printStackTrace();
				return null;
			}
		}
	}
	
}
