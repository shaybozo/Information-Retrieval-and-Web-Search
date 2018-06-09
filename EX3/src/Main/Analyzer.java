package Main;
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
import Parsers.QueriesRunner;

// This class is the orchestrator of all the tasks that should be done
// a. load the data
// b. index the data
// c. run all queries
// d. write the results to output file
public class Analyzer {

	private DocsReader m_DocsReader = null;
	private ParametersReader m_ParametersReader = null;
	private QueriesReader m_QueriesReader = null;
	private QueriesRunner m_QueriesRunner = null;
	private ResultsWriter m_ResultsWriter = null;
		
	public Analyzer()
	{
		m_DocsReader = new DocsReader();
		m_ParametersReader = new ParametersReader();
		m_QueriesReader = new QueriesReader();
		m_QueriesRunner = new QueriesRunner();
		m_ResultsWriter = new ResultsWriter();
	}
	
	// Public methods - start
	
	public void run(String parametersFileName) throws IOException, 
					ParseException, org.apache.lucene.queryparser.classic.ParseException
	{	
		// Initialize the lucene analyzer
		StandardAnalyzer analyzer = new StandardAnalyzer();
	    Directory index = new RAMDirectory();
	    IndexWriterConfig indexWritercConfig = new IndexWriterConfig(analyzer);
	    IndexWriter indexWriter = new IndexWriter(index, indexWritercConfig);
	    
	    // Retrieve the parameters from the parameters file 
		Path parametersFilePath = Paths.get(parametersFileName);
	    List<String> parameters = Files.readAllLines(parametersFilePath);
	    
		String queryFile = m_ParametersReader.getQueryFilePath(parameters);
		String docsFile = m_ParametersReader.getDocsFilePath(parameters);
		String outputFile = m_ParametersReader.getOutputFilePath(parameters);
		String retrievalAlgorithm = m_ParametersReader.getRetrievalAlgorithm(parameters);
		Boolean isImprovedAlgo = retrievalAlgorithm.toLowerCase() == "improved";  
		
		// Load the queries from queries file and prepare them for execution using lucene
		List<AnalyzerQuery> queries = LoadQueries(queryFile, analyzer);
				
		// Load all documents from the documents file into the IndexWriter and index them
		LoadAllDocsAndIndexThem(indexWriter, docsFile, isImprovedAlgo, analyzer);
		
		// Run queries using lucene
		List<QueryResult> queriesResults = ExecuteQueries(index, queries);
		
		// Write all queries results to the output file
		WriteQueriesResultsToFile(queriesResults, outputFile);
	}

	// Public methods - end
	
	// Private methods - start
	
	// Load the queries from queries file and prepare them for execution using lucene
	private List<AnalyzerQuery> LoadQueries(String queryFile, StandardAnalyzer analyzer) 
				throws IOException, org.apache.lucene.queryparser.classic.ParseException 
	{
		List<AnalyzerQuery> queries = m_QueriesReader.readQueries(queryFile, analyzer);
		
		return queries;
	}
	
	private void LoadAllDocsAndIndexThem(IndexWriter indexWriter, String docsFile, Boolean isImprovedAlgo, 
							  			 StandardAnalyzer analyzer) throws IOException, ParseException
	{
		m_DocsReader.LoadAndIndexDocs(indexWriter, docsFile, isImprovedAlgo, analyzer);
		indexWriter.close();
	}

	// Run the queries and building the results 
	private List<QueryResult> ExecuteQueries(Directory index, List<AnalyzerQuery> queries) throws IOException
	{ 
		List<QueryResult> result = m_QueriesRunner.ExecuteQueries(index, queries);
	    
	    return result;
	}
	
	// Write all queries results to the output file
	private void WriteQueriesResultsToFile(List<QueryResult> queriesResults, String outputFile) throws IOException 
	{
		m_ResultsWriter.writeQueriesResultsToFile(queriesResults, outputFile);
	}
	
	// Private methods - end
}