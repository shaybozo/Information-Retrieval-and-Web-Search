package DataReaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import Dto.QueryTruthResult;
import Main.Constants;

public class BenchmarkReader 
{
	public List<QueryTruthResult> readQueriesTruthResults() throws IOException
	{
		// Retrieve the truth results from file 
		Path queriesFilePath = Paths.get(Constants.QUERIES_TRUTH_RESULTS_FILE_PATH);
	    
		List<String> queriesTruthResultsString = Files.readAllLines(queriesFilePath);

		List<QueryTruthResult> queriesTruthResults = new ArrayList<QueryTruthResult>();
		
		for(String queryResult : queriesTruthResultsString)
		{
			if(queryResult != null && !queryResult.isEmpty() && !queryResult.trim().isEmpty())
			{
				QueryTruthResult queryTruthResult = getQueryTruthResult(queryResult);
				
				queriesTruthResults.add(queryTruthResult);	
			}
		}
		
		return queriesTruthResults;
	}
		
	private QueryTruthResult getQueryTruthResult(String s)
	{
		QueryTruthResult queryTruthResult = new QueryTruthResult();
		
		List<Integer> hittedDocsList = new ArrayList<Integer>();
		
		int i = 0;
		
		while(i < s.length()) 
		{
			while (i < s.length() && !Character.isDigit(s.charAt(i))) 
			{
				i++;
			}
			
			int j = i;
			
			while (j < s.length() && Character.isDigit(s.charAt(j))) 
			{
				j++;
			}
			
			hittedDocsList.add(Integer.parseInt(s.substring(i, j)));
			
			i = j + 1;
		}
		
		Iterator<Integer> iter = hittedDocsList.iterator();

		queryTruthResult.QueryId = iter.next();
		queryTruthResult.HittedDocs = new int[hittedDocsList.size() - 1];
		
		int j = 0;
		
		while(iter.hasNext())
		{
			queryTruthResult.HittedDocs[j] = iter.next();
			j++;
		}
		
		return queryTruthResult;
	}
}