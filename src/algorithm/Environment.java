package algorithm;

import java.io.File;
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
	
	//////
	//ADT:
	
	public AbstractDataType getADT(String name){
		return dataTypes.get(name);
	}

	public AbstractDataType loadADT(String fileName){

		try{
			FileReader f = new FileReader("res/adt/" + fileName);
			AbstractDataType a = AbstractDataType.loadAbstractDatatype(f);
			f.close();
			
			if(a == null){
				return null;
			}
			
			dataTypes.put(a.name, a);
			
			return a;
		}
		catch(Exception e){
			return null;
		}
	}

	public void loadADTDirectory(String dirName){
		if(!dirName.endsWith("/")){
			dirName += "/";
		}
		File dir = new File(dirName);
		if(!dir.isDirectory()){
			System.err.println("Must call loadADTDirectory on a directory: \"" + dirName + " invalid");
			return;
		}
		for(String f: dir.list()){
			loadADT(f);
		}
	}
	
	////////////
	//ALGORITHM:

	public Algorithm getAlgorithm(String name){
		return algorithms.get(name);
	}
	
	public Algorithm loadAlgorithm(String filename){
		Algorithm a;
		try{
			FileReader f = new FileReader("res/alg/" + filename);
			a = Algorithm.loadAlgorithm(this, f);
			f.close();

			if(a == null){
				System.err.println("Failure to load algorithm from \"" + filename + "\"");
				return null;
			}
			
			algorithms.put(a.name, a);
			return a;
		}
		catch(IOException e){
			System.err.println("IO exception reading algorithm \"" + filename + "\"");
			e.printStackTrace();
			return null;
		}
	}
	
	public void loadAlgorithmDirectory(String dirName){
		if(!dirName.endsWith("/")){
			dirName += "/";
		}
		File dir = new File(dirName);
		if(!dir.isDirectory()){
			System.err.println("Must call loadAlgorithmDirectory on a directory: \"" + dirName + " invalid");
			return;
		}
		for(String f: dir.list()){
			loadAlgorithm(f);
		}
	}
	
	public String[] getAlgorithmNames(){
		return algorithms.keySet().toArray(new String[algorithms.size()]);
	}
	
}
