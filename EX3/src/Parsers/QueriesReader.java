package Parsers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import Dto.AnalyzerQuery;
import Dto.ParseResult;

public class QueriesReader {

	public List<AnalyzerQuery> readQueries(String queriesFile, StandardAnalyzer analyzer) throws IOException 
	{
		// Retrieve the parameters from file 
		Path queriesFilePath = Paths.get(queriesFile);
	    
		List<String> queries = Files.readAllLines(queriesFilePath);
		
		String doc = Concat(queries);
		
		AnalyzerStringUtils stringUtils = new AnalyzerStringUtils();
		
		List<ParseResult> parsedQueries = stringUtils.parseText(doc, "*Find");
		
		List<AnalyzerQuery> result = new ArrayList<AnalyzerQuery>();
		
		for(ParseResult parsedQuery : parsedQueries)
		{
			AnalyzerQuery analyzerQuery = new AnalyzerQuery();
			
			analyzerQuery.QueryId = getQueryNumberFromHeader(parsedQuery.Header);
			analyzerQuery.Query = BuildLuceneQuery(parsedQuery.Body, analyzer);
			
			result.add(analyzerQuery);
		}
		
		return result;
	}
	
	private Query BuildLuceneQuery(String body, StandardAnalyzer analyzer) {

		//Query q = new QueryParser("title", analyzer).parse(querystr);
		return null;
	}

	private String Concat(List<String> queries)
	{
		StringBuilder builder = new StringBuilder();
		for(String s : queries) {
		    builder.append(s);
		}
		String str = builder.toString();
		
		return str;
	}
	
	private int getQueryNumberFromHeader(String header)
	{
		int result = 0;
		Pattern p = Pattern.compile("-?\\d+");
		Matcher m = p.matcher(header);
		if (m.find()) {
			result = Integer.parseInt(m.group());
		}
		return result;
	}
}