package Main;
import java.io.IOException;

public class Main {

  public static void main(String[] args) throws IOException, java.text.ParseException, 
							org.apache.lucene.queryparser.classic.ParseException 
  {
	  Analyzer analyzer = new Analyzer();
		  
	  String parametersFileName = args[0];
		  
	  analyzer.run(parametersFileName);
		  
	  System.out.println("Finished");
  }
}