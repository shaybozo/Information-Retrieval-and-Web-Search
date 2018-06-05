import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
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
	
	public void run(String parametersFileName) throws IOException {
		
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
		String retrievalAlgorithm = m_ParametersReader.getRetrievalAlgorithm(parameters); // TODO: use it
		    
		// Load Queries from file and prepare them for execution
		List<AnalyzerQuery> queries = LoadQueries(queryFile, analyzer);
		
		// Load all documents from file into the IndexWriter and index them
		LoadAllDocs(indexWriter, docsFile);
		
		// Run queries
		List<QueryResult> queriesResults = ExecuteQueries(indexWriter, queries);
		
		WriteQueriesResultsToFile(queriesResults, outputFile);	
	}

	private List<AnalyzerQuery> LoadQueries(String queryFile, StandardAnalyzer analyzer) 
	{
		List<AnalyzerQuery> queries = m_QueriesReader.readQueries(queryFile, analyzer);
		
		return queries;
	}
	
	private void LoadAllDocs(IndexWriter indexWriter, String docsFile) {
		// TODO Auto-generated method stub
		
	}

	private List<QueryResult> ExecuteQueries(IndexWriter indexWriter, List<AnalyzerQuery> queries) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void WriteQueriesResultsToFile(List<QueryResult> queriesResults, String outputFile) throws IOException {
		m_ResultsWriter.writeQueriesResultsToFile(queriesResults, outputFile);
	}
}