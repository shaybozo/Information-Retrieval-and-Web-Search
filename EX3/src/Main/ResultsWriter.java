package Main;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;

import DataReaders.BenchmarkReader;
import DataReaders.DocsReader;
import DataReaders.ParametersReader;
import DataReaders.QueriesReader;
import Dto.QueryResult;
import Dto.QueryTruthResult;
import Parsers.QueriesRunner;

public class ResultsWriter 
{
	private BenchmarkReader m_BenchmarkReader = null;
	
	public ResultsWriter()
	{
		m_BenchmarkReader = new BenchmarkReader();
	}
	
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
	
	public void CalculatePrecisionAndRecall(List<QueryResult> calculatedQueriesResults) throws IOException 
	{
		List<QueryTruthResult> queriesTruthResults = m_BenchmarkReader.readQueriesTruthResults();
	}
	
	private String parseQueriesResults(List<QueryResult> queriesResults)
	{
		StringBuilder sb = new StringBuilder();
		
		for(QueryResult queryResult : queriesResults)
		{
			sb.append(queryResult.QueryId);
			
			if (queryResult.HittedDocs != null && queryResult.HittedDocs.length > 0)
			{
				sb.append(" ");
				for (int hittedDoc : queryResult.HittedDocs)
				{
					sb.append(hittedDoc + 1);
					sb.append(" ");
				}
			}
			
			sb.append("\n");
			sb.append("\n");
		}
		
		String result = sb.toString();
		
		return result;
	}
}