package Parsers;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import Dto.ParseResult;

public class AnalyzerStringUtils 
{
	// Parse the text to header and body parts
	public static List<ParseResult> parseText(String text, String headerPrefix)
	{
		List<ParseResult> result = new ArrayList<ParseResult>();
		int fromIndex = 0;
		int headerStartIndex = text.indexOf(headerPrefix, fromIndex);
		int headerEndIndex;
		int bodyStartIndex;
		int bodyEndIndex;
		
		while (headerStartIndex != -1) 
		{	
			headerEndIndex = text.indexOf("\n", headerStartIndex);
			
			ParseResult parseResult = new ParseResult();
			parseResult.Header = text.substring(headerStartIndex, headerEndIndex);
					
			// for next iteration
			headerStartIndex = text.indexOf(headerPrefix, headerEndIndex);
			
			bodyStartIndex = headerEndIndex + 1;
			
			if(headerStartIndex != -1)
			{
				bodyEndIndex = headerStartIndex - 1;
			}
			else
			{
				bodyEndIndex = text.length() - 1;
			}
			
			parseResult.Body = text.substring(bodyStartIndex, bodyEndIndex);
			
			result.add(parseResult);
		}
		
		return result;			
	}
	
	// Concat a given string list into a single string
	public static String Concat(List<String> queries, String delimiter)
	{
		StringBuilder builder = new StringBuilder();
		for(String s : queries) {
		    builder.append(s);
		    builder.append(delimiter);
		}
		String str = builder.toString();
		
		return str;
	}
	
	// Tokenize the given string using the lucene Analyzer
	public static List<String> tokenizeString(Analyzer analyzer, String text) throws IOException {
	    List<String> tokens = new ArrayList<String>();
	    
	    TokenStream stream  = null; 
	    		
	    try {
	      stream  = analyzer.tokenStream(null, new StringReader(text));
	      stream.reset();
	      while (stream.incrementToken()) {
	    	  tokens.add(stream.getAttribute(CharTermAttribute.class).toString());
	      }
	    } catch (IOException e) {
	
	      // not thrown b/c we're using a string reader...
	      throw new RuntimeException(e);
	    }
	    finally{
	    	if (stream != null) {
	    		stream.close();	
	    	}
	    	
	    }
	    
	    return tokens;
	}
}