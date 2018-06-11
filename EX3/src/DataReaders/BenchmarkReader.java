package DataReaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.queryparser.classic.ParseException;
import Dto.QueryTruthResult;

public class BenchmarkReader 
{
	public final static String QUERIES_TRUTH_RESULTS_FILE_PATH = "data/truth.txt";
	
	public List<QueryTruthResult> readQueriesTruthResults() throws IOException
	{
		// Retrieve the truth results from file 
		Path queriesFilePath = Paths.get(QUERIES_TRUTH_RESULTS_FILE_PATH);
	    
		List<String> queriesTruthResultsString = Files.readAllLines(queriesFilePath);

		List<QueryTruthResult> queriesTruthResults = new ArrayList<QueryTruthResult>();
		
		for(String queryResult : queriesTruthResultsString)
		{
			QueryTruthResult queryTruthResult = new QueryTruthResult();
			
			queryTruthResult.QueryId = getQueryNumberFromHeader(queryResult);
			queryTruthResult.HittedDocs = getHittedDocs(queryResult);
			
			queriesTruthResults.add(queryTruthResult);
		}
		
		return queriesTruthResults;
	}

	private int getQueryNumberFromHeader(String queryResult) 
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	private int[] getHittedDocs(String queryResult) 
	{
		// TODO Auto-generated method stub
		return new int[] {1, 2, 3, 4, 5};
	}
}