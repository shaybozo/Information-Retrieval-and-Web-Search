import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import Dto.QueryResult;

public class ResultsWriter 
{

	public void writeQueriesResultsToFile(List<QueryResult> queriesResults, String outputFile) throws IOException {
		File resultsFile = new File(outputFile);
		resultsFile.createNewFile(); // if file already exists will do nothing 
		FileOutputStream oFile = new FileOutputStream(resultsFile, false);
		
	    byte[] strToBytes = "fdfd".getBytes();
	    oFile.write(strToBytes);
	    
	    oFile.close();
	}

	private String parseQueriesResults(List<QueryResult> queriesResults)
	{
		String result = null;
		StringBuilder sb = new StringBuilder();
		
		for(QueryResult queryResult : queriesResults)
		{
			sb.append(queryResult.QueryId);
			
			if (queryResult.HittedDocs != null && queryResult.HittedDocs.length > 0)
			{
				sb.append(" ");
				for (int hittedDoc : queryResult.HittedDocs)
				{
					sb.append(hittedDoc);
					sb.append(" ");
				}
			}
			
			sb.append("\n");
		}
		
		return result;
	}
}