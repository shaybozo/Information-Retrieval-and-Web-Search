package Parsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import Dto.AnalyzerQuery;
import Dto.QueryResult;
import Main.Constants;

public class QueriesRunner {

	// Run queries using lucene index and return the results ready to be written
	public List<QueryResult> ExecuteQueries(Directory index, List<AnalyzerQuery> queries) throws IOException
	{ 
		List<QueryResult> result = new ArrayList<QueryResult>();
	    
	    for(AnalyzerQuery query : queries)
	    {
    	   IndexReader reader = DirectoryReader.open(index);
		    IndexSearcher searcher = new IndexSearcher(reader);
		    TopScoreDocCollector collector = TopScoreDocCollector.create(Constants.HITS_PER_PAGE);
	    	searcher.search(query.Query, collector);
		    ScoreDoc[] hits = collector.topDocs().scoreDocs;
		    
		    QueryResult queryResult = new QueryResult();
		    queryResult.QueryId = query.QueryId;
		    queryResult.HittedDocs = getHittedDocsIds(hits);

		    result.add(queryResult);
		    
		    reader.close();
	    }
	    
	    return result;
	}
	
	private int[] getHittedDocsIds(ScoreDoc[] hits)
	{
		ArrayList<Integer> result = new ArrayList<Integer>();
		
	    for(int i = 0; i < hits.length; i++) 
	    {
	    	if (hits[i].score > Constants.SCORE_THRESHOLD)
	    	{
	    		result.add(hits[i].doc);
	    	}
        }
	    
		return convertAndSortIntegers(result);
	}
	
	private int[] convertAndSortIntegers(List<Integer> integers)
	{
	    int[] ret = new int[integers.size()];
	    
	    for (int i = 0; i < ret.length; i++)
	    {
	        ret[i] = integers.get(i).intValue();
	    }
	    
	    Arrays.sort(ret);
	    
	    return ret;
	}
}