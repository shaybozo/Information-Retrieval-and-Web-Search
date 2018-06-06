package Parsers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import Dto.ParseResult;
import Main.Constants;
import Dto.AnalyzerDocument;

public class DocsReader {

	public void LoadAndIndexDocs(IndexWriter indexWriter, String docsFile, Boolean isImprovedAlgo, StandardAnalyzer analyzer) throws IOException, ParseException {
	
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
			
			analyzerDocument.DocId = Integer.parseInt(headerTokens[1]) + 10;

	    	try { //best effort. TODO: improve later
	    		DateFormat format = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);
	    		analyzerDocument.Date = format.parse(headerTokens[2]);
	    	} catch(Exception ex ) { errorCount++;};
	    	
	    	try { //best effort. TODO: improve later
	    		analyzerDocument.PageNumber = Integer.parseInt(headerTokens[4]);
	    	} catch(Exception ex ) { errorCount++;};
	    	
			analyzerDocument.Text = BuildLuceneText(parsedResult.Body, analyzer);
			
			parsedDocuments.add(analyzerDocument);
		}	
		
		// Store Documents

	    for (AnalyzerDocument parsedDoc : parsedDocuments) {
	    	//doc.Tokens = AnalyzerStringUtils.tokenizeString(analyzer, doc.Text);
		    Document doc = new Document();
		    doc.add(new TextField(Constants.FIELD_NAME_CONTENT, parsedDoc.Text, Field.Store.YES));

		    // use a string field for isbn because we don't want it tokenized
		    doc.add(new TextField("isbn", Integer.toString(parsedDoc.DocId), Field.Store.YES)); // TODO: check if needed 
		    // doc.add(new StringField("date", parsedDoc.Date.toString(), Field.Store.YES)); // TODO: needed? string field? store? 
		    // doc.add(new StringField("pageNumber", parsedDoc.PageNumber, Field.Store.YES)); // TODO: right?
		    
		    indexWriter.addDocument(doc);
	    }
	}
	
	private String BuildLuceneText(String body, StandardAnalyzer analyzer) throws ParseException, IOException 
	{
		List<String> textTokensList = AnalyzerStringUtils.tokenizeString(analyzer, body);
		
		String textTokens = AnalyzerStringUtils.Concat(textTokensList, " ");
		
		return textTokens;
	}
}