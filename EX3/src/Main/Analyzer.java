package Main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import DataReaders.ClassesTypesReader;
import DataReaders.DocsReader;
import DataReaders.ParametersReader;
import Dto.AnalyzerQuery;
import Dto.ClassPerformance;
import Dto.ClassType;
import Dto.DocumentData;
import Dto.ProjectParametrs;
import Dto.QueryResult;
import Parsers.QueriesRunner;

// This class is the orchestrator of all the tasks that should be done
// a. load the data
// b. index the data
// c. run all queries
// d. write the results to output file
public class Analyzer {

	private DocsReader m_DocsReader = null;
	private ParametersReader m_ParametersReader = null;
	private QueriesRunner m_QueriesRunner = null;
	private ResultsWriter m_ResultsWriter = null;
	private ClassesTypesReader m_ClassesTypesReader = null;
	
	public Analyzer()
	{
		m_DocsReader = new DocsReader();
		m_ParametersReader = new ParametersReader();
		m_QueriesRunner = new QueriesRunner();
		m_ResultsWriter = new ResultsWriter();
		m_ClassesTypesReader = new ClassesTypesReader();
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
		ProjectParametrs projectParametrs = m_ParametersReader.readParametersFromFile(parametersFileName);
		
		// Retrieve the class types from file
		List<ClassType> classesTypes = m_ClassesTypesReader.readClassesTypesFromFile("data/classes.txt");
		
		// Load all documents from the documents file into the IndexWriter and index them
		List<DocumentData> trainDocuments = LoadAllDocsAndIndexThem(indexWriter, projectParametrs, analyzer);

		// Load the queries from queries file and prepare them for execution using lucene
		List<AnalyzerQuery> testDocsAsQueries = BuildTestDocsAsQueries(projectParametrs.testFile, analyzer);

		// Run queries using lucene
		List<QueryResult> queriesResults = ExecuteQueries(index, trainDocuments, testDocsAsQueries, projectParametrs.K_parameterValue);
		
		// Write all queries results to the output file
		WriteQueriesResultsToFile(queriesResults, classesTypes, projectParametrs.outputFile, projectParametrs.K_parameterValue);

		// following fuction is meant for testing various K values.
		// testVariousK(index, trainDocuments, testDocsAsQueries, classesTypes, projectParametrs.outputFile);
	}

	// Public methods - end
	
	// Private methods - start
	
    // Returns the stop words (or null in case of improved algorithm)
	private List<DocumentData> LoadAllDocsAndIndexThem(IndexWriter indexWriter, ProjectParametrs projectParametrs, 
				StandardAnalyzer analyzer) throws IOException, ParseException
	{
		List<DocumentData> documents = m_DocsReader.LoadAndIndexDocs(indexWriter, projectParametrs, analyzer);
		indexWriter.close();
		
		return documents;
	}
	
	// Load the queries from queries file and prepare them for execution using lucene
	private List<AnalyzerQuery> BuildTestDocsAsQueries(String testFile, StandardAnalyzer analyzer) 
				throws IOException, org.apache.lucene.queryparser.classic.ParseException, ParseException 
	{
		List<AnalyzerQuery> queries = m_DocsReader.BuildTestDocsAsQueries(testFile, analyzer);
		
		return queries;
	}

	// Run the queries and building the results 
	private List<QueryResult> ExecuteQueries(Directory index, List<DocumentData> trainDocuments,
											 List<AnalyzerQuery> queries, int k) throws IOException
	{ 
		List<QueryResult> result = m_QueriesRunner.ExecuteQueries(index, trainDocuments, queries, k);
	    
	    return result;
	}
	
	// Write all queries results to the output file
	private void WriteQueriesResultsToFile(List<QueryResult> queriesResults, List<ClassType> classesTypes, String outputFileName, int kValue) throws IOException 
	{
		m_ResultsWriter.writeQueriesResultsToFile(queriesResults, classesTypes, outputFileName, kValue);
	}
	
	private void testVariousK(Directory index, List<DocumentData> trainDocuments, List<AnalyzerQuery> testDocsAsQueries, List<ClassType> classesTypes, String outputFile) throws IOException 
	{
		int[] kValues = {10,20,30,40,50,60,70,80,90,100,110,120,130,140,150,160,170,180, 190, 200};
		
		for (int kValue : kValues) {
			// Run queries using lucene
			List<QueryResult> queriesResults = ExecuteQueries(index, trainDocuments, testDocsAsQueries, kValue);
			
			// Write all queries results to the output file
			WriteQueriesResultsToFile(queriesResults, classesTypes, outputFile + "_" + "k" + kValue, kValue);
		}
	}
	// Private methods - end
}