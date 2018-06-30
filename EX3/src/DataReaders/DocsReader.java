package DataReaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import Dto.ProjectParametrs;
import Main.Constants;
import Parsers.AnalyzerStringUtils;
import Dto.AnalyzerQuery;
import Dto.DocumentData;

public class DocsReader {
	
	// Load all documents from the documents file into the IndexWriter and index them
	public List<DocumentData> LoadAndIndexDocs(IndexWriter indexWriter, ProjectParametrs projectParametrs, 
								 StandardAnalyzer analyzer) throws IOException, ParseException 
	{
		Path docsFilePath = Paths.get(projectParametrs.trainFile);
		List<String> docsLines = Files.readAllLines(docsFilePath);
		String cvsSplitBy = ",";
		List<DocumentData> result = new ArrayList<DocumentData>();
		
		for(String doc : docsLines)
		{
			String[] parts = doc.split(cvsSplitBy);
			DocumentData document = new DocumentData();
			
			document.documentID = Integer.parseInt(parts[0]);
			document.classID = Integer.parseInt(parts[1]);
			document.text = parts[2] + " " + parts[3];
			
			result.add(document);
			
		    Document luceneDoc = new Document();
		    luceneDoc.add(new TextField(Constants.FIELD_NAME_CONTENT, document.text, Field.Store.YES));
		    luceneDoc.add(new TextField("isbn", Integer.toString(document.documentID), Field.Store.YES));  
		    indexWriter.addDocument(luceneDoc);
		}
	    
	    return result;
	}
	
	// Load all documents from the documents file into the IndexWriter and index them
	public List<AnalyzerQuery> BuildTestDocsAsQueries(String testFile, StandardAnalyzer analyzer) 
				throws IOException, ParseException, org.apache.lucene.queryparser.classic.ParseException 
	{
		Path docsFilePath = Paths.get(testFile);
		List<String> docsLines = Files.readAllLines(docsFilePath);
		String cvsSplitBy = ",";
		List<AnalyzerQuery> analyzerQueries = new ArrayList<AnalyzerQuery>();
		
		for(String doc : docsLines)
		{
			String[] parts = doc.split(cvsSplitBy);
			AnalyzerQuery analyzerQuery = new AnalyzerQuery();
			
			analyzerQuery.QueryId = Integer.parseInt(parts[0]);
			analyzerQuery.ClassId = Integer.parseInt(parts[1]);
			String text = parts[2] + " " + parts[3];
			
			analyzerQuery.Query = buildLuceneQuery(text, analyzer);
			
			if (analyzerQuery.Query != null) {
				analyzerQueries.add(analyzerQuery);	
			} else {
				System.out.println("Query failure " + analyzerQuery.QueryId);
			}
		}
	    
	    return analyzerQueries;
	}
	
	private Query buildLuceneQuery(String body, StandardAnalyzer analyzer) 
			throws ParseException, IOException, org.apache.lucene.queryparser.classic.ParseException {

		List<String> queryTokensList = AnalyzerStringUtils.tokenizeString(analyzer, body);
		
		String queryTokens = AnalyzerStringUtils.Concat(queryTokensList, " ");
		
		Query query = null;
		
		try {
			QueryParser queryParser = new QueryParser(Constants.FIELD_NAME_CONTENT, analyzer);
			
			query = queryParser.parse(queryTokens);
		}
		catch(Exception e) {
			
		}
		
		return query;
	}
}