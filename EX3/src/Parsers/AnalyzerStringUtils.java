package Parsers;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class AnalyzerStringUtils 
{
	// Concatenate a given string list into a single string
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
	public static List<String> tokenizeString(Analyzer analyzer, String text) throws IOException 
	{
		List<String> tokens = new ArrayList<String>();
	    
	    TokenStream stream  = null; 
	    		
	    try {
	      stream  = analyzer.tokenStream(null, new StringReader(text));
	      stream.reset();
	      while (stream.incrementToken()) {
	    	  tokens.add(stream.getAttribute(CharTermAttribute.class).toString());
	      }
	    } catch (IOException e) 
	    {	
	      // not thrown b/c we're using a string reader...
	      throw new RuntimeException(e);
	    }
	    finally{
	    	if (stream != null) 
	    	{
	    		stream.close();	
	    	}
	    }
	    
	    return tokens;
	}
}