package Parsers;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.IndexWriter;

import Dto.Document;

public class DocsReader {

	public void LoadAndIndexDocs(IndexWriter indexWriter, String docsFile, Boolean isImprovedAlgo) throws IOException, ParseException {
		ArrayList<Document> docs = new ArrayList<Document>(); 
		
		Path  docsFilePath = Paths.get(docsFile);
	    byte[] encoded = Files.readAllBytes(docsFilePath);
	    String docsString =  new String(encoded);
	    
	    String[] documentStrings = docsString.split("\\*TEXT ");
	    
	    for (String documentString : documentStrings) {
	    	String[] tokens = documentString.split("\\r|\\n| ");
	    	
	    	if (tokens.length < 4) {
	    		continue;
	    	}
	    	Document doc = new Document();
	    	
	    	doc.DocId = tokens[0];
	    	DateFormat format = new SimpleDateFormat("dd/MM/yy", Locale.ENGLISH);
	    	doc.Date = format.parse(tokens[1]);

	    	doc.PageNumber = Integer.parseInt(tokens[3]);
    	
	    	doc.Text = String.join(" ", Arrays.copyOfRange(tokens, 4, tokens.length - 1));
	    	
	    	docs.add(doc);
	    }
	    
	    for (Document doc : docs) {
	    	doc.Tokens = 
	    }
	}
	
	  public static List<String> tokenizeString(Analyzer analyzer, String text) {
	    List<String> tokens = new ArrayList<String>();
	    
	    try {
	      TokenStream stream  = analyzer.tokenStream(null, new StringReader(text));
	      stream.reset();
	      while (stream.incrementToken()) {
	    	  tokens.add(stream.getAttribute(CharTermAttribute.class).toString());
	      }
	    } catch (IOException e) {

	      // not thrown b/c we're using a string reader...
	      throw new RuntimeException(e);
	    }
	    return tokens;
	  }
}