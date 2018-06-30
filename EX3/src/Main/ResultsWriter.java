package Main;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import Dto.QueryResult;

public class ResultsWriter 
{
	private static DecimalFormat df2 = new DecimalFormat(".##");
	
	// Write the queries results to the output file
	public void writeQueriesResultsToFile(List<QueryResult> queriesResults, String outputFile) throws IOException 
	{
		File resultsFile = new File(outputFile);
		resultsFile.createNewFile(); // if file already exists will do nothing 
		FileOutputStream oFile = new FileOutputStream(resultsFile, false);
		
		String resultsContent = parseQueriesResults(queriesResults);
		
	    byte[] strToBytes = resultsContent.getBytes();
	    
	    oFile.write(strToBytes);
	    
	    oFile.close();
	}
	
	private String parseQueriesResults(List<QueryResult> queriesResults)
	{
		int successfulPredictionDocs = 0;
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Results:\n");
		sb.append("-----------------------------------------");
		
		for(QueryResult queryResult : queriesResults)
		{
			sb.append("Test doc ");
			sb.append(queryResult.QueryId);
			sb.append(": \n");

			sb.append("Actual class type: ");
			sb.append(queryResult.ActualClassType);
			sb.append("\n");
			
			sb.append("Calculated Class Type:");
			sb.append(queryResult.CalculatedClassType);
			sb.append("\n");
			
			sb.append("-----------------------------------------");
			sb.append("\n");
			
			if (queryResult.IsGoodClassTypePrediction) {
				successfulPredictionDocs++;
			}
		}
		
		double successfulPredictionRate = successfulPredictionDocs / queriesResults.size();
		
		sb.append("Total prediction results:");
		sb.append(" from ");
		sb.append(queriesResults.size());
		sb.append(" test docs we predicted successfully ");
		sb.append(successfulPredictionDocs);
		sb.append("\n");
		sb.append("The successful prediction rate is: ");
		sb.append(df2.format(successfulPredictionRate));
		sb.append("\n");
		sb.append("-----------------------------------------");
		sb.append("\n");
		String result = sb.toString();
		
		return result;
	}
}