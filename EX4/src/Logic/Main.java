package Logic;

import Main.Analyzer;

public class Main {

	public static void main(String[] args) {
		
		  // http://sujitpal.blogspot.com/2011/10/computing-document-similarity-using.html
		  Analyzer analyzer = new Analyzer();
		  
		  String parametersFileName = args[0];
		  
		  analyzer.run(parametersFileName);
		  
		  System.out.println("Finished");

	}

}
