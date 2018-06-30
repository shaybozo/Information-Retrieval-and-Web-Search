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
import Dto.DocumentData;
import Dto.QueryResult;

public class QueriesRunner {

	// Run queries using lucene index and return the results ready to be written
	public List<QueryResult> ExecuteQueries(Directory index, List<DocumentData> trainDocuments, 
											List<AnalyzerQuery> queries, int k) throws IOException
	{ 
		List<QueryResult> result = new ArrayList<QueryResult>();
	    
	    for(AnalyzerQuery query : queries)
	    {
    	    IndexReader reader = DirectoryReader.open(index);
		    IndexSearcher searcher = new IndexSearcher(reader);
		    TopScoreDocCollector collector = TopScoreDocCollector.create(k);
	    	searcher.search(query.Query, collector);
		    ScoreDoc[] hits = collector.topDocs().scoreDocs;
		    
		    QueryResult queryResult = new QueryResult();
		    queryResult.QueryId = query.QueryId;
		    queryResult.HittedDocs = getHittedDocsIds(hits);
		    queryResult.ActualClassType = query.ClassId;
		    queryResult.CalculatedClassType = calculateClassTypeFromHittedDocs(queryResult.HittedDocs);
		    queryResult.IsGoodClassTypePrediction = queryResult.ActualClassType == queryResult.CalculatedClassType;
		    
		    result.add(queryResult);
		    
		    reader.close();
	    }
	    
	    return result;
	}
	
	public int calculateClassTypeFromHittedDocs(int[] a)
	{
	  int count = 1, tempCount;
	  int popular = a[0];
	  int temp = 0;
	  for (int i = 0; i < (a.length - 1); i++)
	  {
	    temp = a[i];
	    tempCount = 0;
	    for (int j = 1; j < a.length; j++)
	    {
	      if (temp == a[j])
	        tempCount++;
	    }
	    if (tempCount > count)
	    {
	      popular = temp;
	      count = tempCount;
	    }
	  }
	  return popular;
	}
	
	private int[] getHittedDocsIds(ScoreDoc[] hits)
	{
		ArrayList<Integer> result = new ArrayList<Integer>();
		
	    for(int i = 0; i < hits.length; i++) 
	    {
	    	// return only docs, with score higher than a predefined threshold.	
	    	result.add(hits[i].doc + 1);// Because the doc is added in zero based way
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