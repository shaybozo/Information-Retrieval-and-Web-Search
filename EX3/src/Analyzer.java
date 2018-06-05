import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
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
	    IndexWriterConfig config = new IndexWriterConfig(analyzer);
	    IndexWriter indexWriter = new IndexWriter(index, config);
	    
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
		LoadAllDocs(indexWriter, docsFile, isImprovedAlgo, analyzer); // TODO:YRHUDA
		
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
	    IndexReader reader = DirectoryReader.open(index);
	    IndexSearcher searcher = new IndexSearcher(reader);
	    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, new ScoreDoc(10, 10));
	    
	    for(AnalyzerQuery query : queries)
	    {
	    	searcher.search(query.Query, collector);
		    ScoreDoc[] hits = collector.topDocs().scoreDocs;
		    
		    QueryResult queryResult = new QueryResult();
		    queryResult.QueryId = query.QueryId;
		    queryResult.HittedDocs = getHittedDocs(hits);
	    }
	    
	    reader.close();
	    
	    return result;
	}
	
	private int[] getHittedDocs(ScoreDoc[] hits)
	{
		int[] result = new int[hits.length];
		
	    for(int i=0;i<hits.length;++i) 
	    {
	    	result[i] = hits[i].doc;
        }
	    
		return result;
	}
	
	private void WriteQueriesResultsToFile(List<QueryResult> queriesResults, String outputFile) throws IOException {
		m_ResultsWriter.writeQueriesResultsToFile(queriesResults, outputFile);
	}
}