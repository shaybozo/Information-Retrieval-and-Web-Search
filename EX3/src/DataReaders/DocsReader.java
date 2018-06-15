package DataReaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

import Dto.ParseResult;
import Main.Constants;
import Parsers.AnalyzerStringUtils;
import Dto.AnalyzerDocument;

public class DocsReader {

	HashMap<String, Integer> wordsMap = new HashMap<>();
	
	// Load all documents from the documents file into the IndexWriter and index them
	// Returns an hashset of stop words (or null in case of improved algorithm)
	public HashSet<String> LoadAndIndexDocs(IndexWriter indexWriter, String docsFile, 
								 StandardAnalyzer analyzer, Boolean isImprovedAlgo) throws IOException, ParseException 
	{
		int errorCount = 0;
		
		Path  docsFilePath = Paths.get(docsFile);
		List<String> docsLines = Files.readAllLines(docsFilePath);

		// Parse documents
		String docsfileText = AnalyzerStringUtils.Concat(docsLines, "\n");
		List<ParseResult> parsedResults = AnalyzerStringUtils.parseText(docsfileText, "*TEXT");
		List<AnalyzerDocument> parsedDocuments = new ArrayList<AnalyzerDocument>();
		for(ParseResult parsedResult : parsedResults)
		{
			AnalyzerDocument analyzerDocument = new AnalyzerDocument();
			
			String[] headerTokens = parsedResult.Header.split(" ");
			if (headerTokens.length != 5) {
				errorCount++;
	    	}
			
			analyzerDocument.DocId = Integer.parseInt(headerTokens[1]);

			// Originally, we parsed and indexed the fields from the headers of each doc. 
			// Later we realized there is not use of this content, so we removed these fields from the index, and avoid parsing it.
			/*
	    	try { 
	    		DateFormat format = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);
	    		analyzerDocument.Date = format.parse(headerTokens[2]);
	    	} catch(Exception ex ) { errorCount++;};
	    	
	    	try { 
	    		analyzerDocument.PageNumber = Integer.parseInt(headerTokens[4]);
	    	} catch(Exception ex ) { errorCount++;};
	    	*/
			
			if (isImprovedAlgo) {
				analyzerDocument.Text = BuildLuceneText(parsedResult.Body, analyzer);
			} else {
				// Tokenize without stop words removal
				analyzerDocument.Text = buildTokenizedText(parsedResult.Body);
			}
			
			parsedDocuments.add(analyzerDocument);
		}
		
		// Find the 20 stop words
		HashSet<String> stopWords = null;
		if (!isImprovedAlgo) {
			stopWords = getStopWords(); 
		}
	    
     	// Store Documents to index
	    for (AnalyzerDocument parsedDoc : parsedDocuments) {
		    Document doc = new Document();
		    
		    if (!isImprovedAlgo) {
		    	// Remove Stop words
		    	parsedDoc.Text = removeStopWords(parsedDoc.Text, stopWords);
		    }

		    doc.add(new TextField(Constants.FIELD_NAME_CONTENT, parsedDoc.Text, Field.Store.YES));
		    doc.add(new TextField("isbn", Integer.toString(parsedDoc.DocId), Field.Store.YES));  
		    
		    indexWriter.addDocument(doc);
	    }
	    
	    return stopWords;
	}
	
	// lowercase, remove panctuations
	// build a dictionary which counts the words (we'll remove stop words later)
	private String buildTokenizedText(String body)  
	{
		String bodyLowercase= body.toLowerCase(); // We lower case.
		String bodyWithoutPanctuations = bodyLowercase.replaceAll("-|,|\\.|:|/|\\\\|\\`|;|:|!|@|#|$|%|^|&|[|]|\"|'", ""); // remove panctuations.
		List<String> textTokensList = Arrays.asList(bodyWithoutPanctuations.split(" "));
		
		// Add to map (we'll remove stop words later)
		for (String token : textTokensList) {
	        if (wordsMap.containsKey(token)) {
	        	wordsMap.put(token, wordsMap.get(token) + 1); // increment count by 1
	        } else {
	        	wordsMap.put(token, 0); // add token to dictionary
	        }
		}
		
		String textTokens = AnalyzerStringUtils.Concat(textTokensList, " ");
		
		return textTokens;
	}
	
	// Return the top 20 words across all docs.
	private HashSet<String> getStopWords() {
	    
		HashSet<String> topWords = new HashSet<String>(); 
		ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>(wordsMap.entrySet());
	    Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {

	        @Override
	        public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
	            return a.getValue().compareTo(b.getValue());
	        }
	    });

	    for(int i = 0; i < Constants.STOP_WORDS_COUNT; i++){
	    	topWords.add(entries.get(entries.size() - i - 1).getKey());
	    }
	    
	    return topWords;
	}
	
	// Remove stop words from doc 
	private String removeStopWords(String docText, HashSet<String> stopWords) {
		List<String> textTokensList = Arrays.asList(docText.split(" "));
		List<String> filteredTokensList = new ArrayList<String>();  
		
		for (String token: textTokensList) {
			if (!stopWords.contains(token)) {
				filteredTokensList.add(token);
			}
		}
		
		return AnalyzerStringUtils.Concat(filteredTokensList, " ");
	}
	
	// improved approach, use lucene tokenization
	private String BuildLuceneText(String body, StandardAnalyzer analyzer) throws ParseException, IOException 
	{
		List<String> textTokensList = AnalyzerStringUtils.tokenizeString(analyzer, body);
		
		String textTokens = AnalyzerStringUtils.Concat(textTokensList, " ");
		
		return textTokens;
	}
}