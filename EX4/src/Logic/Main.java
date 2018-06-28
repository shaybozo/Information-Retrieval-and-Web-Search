package Logic;

import Main.Analyzer;

public class Main {

	public static void main(String[] args) {
		
		  Analyzer analyzer = new Analyzer();
		  
		  String parametersFileName = args[0];
		  
		  analyzer.run(parametersFileName);
		  
		  System.out.println("Finished");

	}

}
