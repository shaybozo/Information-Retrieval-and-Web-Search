package Parsers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

import Dto.ParsedDocument;

public class DocsReader {

	public void LoadAndIndexDocs(IndexWriter indexWriter, String docsFile, Boolean isImprovedAlgo, StandardAnalyzer analyzer) throws IOException, ParseException {
		ArrayList<ParsedDocument> docs = new ArrayList<ParsedDocument>(); 
		
		int errorCount = 0;
		Path  docsFilePath = Paths.get(docsFile);
	    byte[] encoded = Files.readAllBytes(docsFilePath);
	    String docsString =  new String(encoded);
	    
	    String[] documentStrings = docsString.split("\\*TEXT ");
	    
	    for (String documentString : documentStrings) {
	    	String[] tokens = documentString.split("\\r|\\n| ");
	    	
	    	if (tokens.length < 4) {
	    		continue;
	    	}
	    	ParsedDocument doc = new ParsedDocument();
	    	
	    	doc.DocId = tokens[0];
	    	
	    	try { //best effort. TODO: improve later
	    		DateFormat format = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);
	    		doc.Date = format.parse(tokens[1]);
	    	} catch(Exception ex ) { errorCount++;}; 
	    	
	    	try { //best effort. TODO: improve later
	    		doc.PageNumber = Integer.parseInt(tokens[3]);
	    	} catch(Exception ex ) { errorCount++;};
	    	
	    	doc.Text = String.join(" ", Arrays.copyOfRange(tokens, 4, tokens.length - 1));
	    	
	    	docs.add(doc);
	    }
	    
	    for (ParsedDocument parsedDoc : docs) {
	    	//doc.Tokens = AnalyzerStringUtils.tokenizeString(analyzer, doc.Text);
		    Document doc = new Document();
		    doc.add(new TextField("content", parsedDoc.Text, Field.Store.YES));

		    // use a string field for isbn because we don't want it tokenized
		    doc.add(new StringField("isbn", parsedDoc.DocId, Field.Store.YES));
		    // doc.add(new StringField("date", parsedDoc.Date.toString(), Field.Store.YES)); // TODO: needed? string field? store? 
		    // doc.add(new StringField("pageNumber", parsedDoc.PageNumber, Field.Store.YES)); // TODO: right?
		    
		    indexWriter.addDocument(doc);
	    }
	}
}