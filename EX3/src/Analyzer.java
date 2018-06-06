import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import Dto.AnalyzerQuery;
import Dto.QueryResult;
import Parsers.DocsReader;
import Parsers.ParametersReader;
import Parsers.QueriesReader;

public class Analyzer {

	private DocsReader m_DocsReader = null;
	private ParametersReader m_ParametersReader = null;
	private QueriesReader m_QueriesReader = null;
	private ResultsWriter m_ResultsWriter = null;
	
	public Analyzer()
	{
		m_DocsReader = new DocsReader();
		m_ParametersReader = new ParametersReader();
		m_QueriesReader = new QueriesReader();
		m_ResultsWriter = new ResultsWriter();
	}
	
	public void run(String parametersFileName) throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException {
		
		// Initialize the lucene analyzer
		StandardAnalyzer analyzer = new StandardAnalyzer();
	    Directory index = new RAMDirectory();
	    IndexWriterConfig indexWritercConfig = new IndexWriterConfig(analyzer);
	    IndexWriter indexWriter = new IndexWriter(index, indexWritercConfig);
	    
	    // Retrieve the parameters from file 
		Path parametersFilePath = Paths.get(parametersFileName);
	    List<String> parameters = Files.readAllLines(parametersFilePath);
	    
		String queryFile = m_ParametersReader.getQueryFilePath(parameters);
		String docsFile = m_ParametersReader.getDocsFilePath(parameters);
		String outputFile = m_ParametersReader.getOutputFilePath(parameters);
		String retrievalAlgorithm = m_ParametersReader.getRetrievalAlgorithm(parameters);
		Boolean isImprovedAlgo = retrievalAlgorithm.toLowerCase() == "improved";  
		
		// Load Queries from file and prepare them for execution
		List<AnalyzerQuery> queries = LoadQueries(queryFile, analyzer);
				
		// Load all documents from file into the IndexWriter and index them
		LoadAllDocs(indexWriter, docsFile, isImprovedAlgo, analyzer); // TODsO:YRHUDA
		
		// Run queries
		List<QueryResult> queriesResults = ExecuteQueries(index, queries);
		
		WriteQueriesResultsToFile(queriesResults, outputFile);
	}

	private List<AnalyzerQuery> LoadQueries(String queryFile, StandardAnalyzer analyzer) throws IOException, org.apache.lucene.queryparser.classic.ParseException 
	{
		List<AnalyzerQuery> queries = m_QueriesReader.readQueries(queryFile, analyzer);
		
		return queries;
	}
	
	private void LoadAllDocs(IndexWriter indexWriter, String docsFile, Boolean isImprovedAlgo, StandardAnalyzer analyzer) throws IOException, ParseException {
		m_DocsReader.LoadAndIndexDocs(indexWriter, docsFile, isImprovedAlgo, analyzer);
		indexWriter.close();
	}

	private List<QueryResult> ExecuteQueries(Directory index, List<AnalyzerQuery> queries) throws IOException {
	    
		List<QueryResult> result = new ArrayList<QueryResult>();
	    int hitsPerPage = 10;
	    
	    for(AnalyzerQuery query : queries)
	    {
    	   IndexReader reader = DirectoryReader.open(index);
		    IndexSearcher searcher = new IndexSearcher(reader);
		    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
	    	searcher.search(query.Query, collector);
		    ScoreDoc[] hits = collector.topDocs().scoreDocs;
		    
		    QueryResult queryResult = new QueryResult();
		    queryResult.QueryId = query.QueryId;
		    queryResult.HittedDocs = getHittedDocs(hits);
		    
		    // TODO:SHAY - remove test code
		    System.out.println("Query " + query.QueryId + " number of hits: " + hits.length);
		    System.out.println();
		    
		    result.add(queryResult);
		    
		    reader.close();
	    }
	    
	    return result;
	}
	
	private int[] getHittedDocs(ScoreDoc[] hits)
	{
		ArrayList<Integer> result = new ArrayList<Integer>();
		
	    for(int i = 0; i < hits.length; i++) 
	    {
	    	if (hits[i].score > 10)
	    	{
	    		result.add(hits[i].doc);
	    	}
        }
	    
		return convertIntegers(result);
	}
	
	public static int[] convertIntegers(List<Integer> integers)
	{
	    int[] ret = new int[integers.size()];
	    for (int i=0; i < ret.length; i++)
	    {
	        ret[i] = integers.get(i).intValue();
	    }
	    
	    Arrays.sort(ret);
	    
	    return ret;
	}
	
	private void WriteQueriesResultsToFile(List<QueryResult> queriesResults, String outputFile) throws IOException {
		m_ResultsWriter.writeQueriesResultsToFile(queriesResults, outputFile);
	}
}