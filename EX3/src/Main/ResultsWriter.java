package Main;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	private static DecimalFormat df2 = new DecimalFormat(".##");
	
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
		
		File resultsFile = new File(Constants.REPORT_FILE_PATH);
		resultsFile.createNewFile(); // if file already exists will do nothing 
		FileOutputStream oFile = new FileOutputStream(resultsFile, false);
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Queries report:");
		sb.append("\n");
		
		for(QueryTruthResult queryTruthResult : queriesTruthResults)
		{
			QueryResult queryResult = getQueryResult(calculatedQueriesResults, queryTruthResult.QueryId);
			double totalCount = queryResult.HittedDocs.length;
			double intersection = getIntersection(queryResult.HittedDocs, queryTruthResult.HittedDocs);
			double turuHitsCount = queryTruthResult.HittedDocs.length;
			
			double precision = totalCount == 0 ? 0 : intersection / totalCount;
			double recall = turuHitsCount == 0 ? 0 : intersection / turuHitsCount;
			
			sb.append("Query number " + queryTruthResult.QueryId + ": ");
			sb.append("Precision: " + intersection + " / "  + totalCount + " = " + df2.format(precision));
			sb.append("; ");
			sb.append("Recall: " + intersection + " / "  + turuHitsCount + " = " + df2.format(recall));
			sb.append("\n");
		}
		
	    byte[] strToBytes = sb.toString().getBytes();
	    
	    oFile.write(strToBytes);
	    
	    oFile.close();
	}
	
	private QueryResult getQueryResult(List<QueryResult> calculatedQueriesResults, int queryId) 
	{
		QueryResult queryResult = null;
		
		for(QueryResult calculatedQuerysResult : calculatedQueriesResults)
		{
			if (calculatedQuerysResult.QueryId == queryId)
			{
				queryResult = calculatedQuerysResult;
			}
		}
		
		return queryResult;
	}

	private int getIntersection(int[] a, int[] b)
	{
		Integer[] aa = Arrays.stream( a ).boxed().toArray( Integer[]::new );
		Integer[] bb = Arrays.stream( b ).boxed().toArray( Integer[]::new );
		Set<Integer> s1 = new HashSet<Integer>(Arrays.asList(aa));
		Set<Integer> s2 = new HashSet<Integer>(Arrays.asList(bb));
		s1.retainAll(s2);

		int result = s1.size();
		
		return result;
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
					sb.append(hittedDoc);
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