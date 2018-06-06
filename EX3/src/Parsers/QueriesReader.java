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
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import Dto.AnalyzerQuery;
import Dto.ParseResult;

public class QueriesReader {

	public List<AnalyzerQuery> readQueries(String queriesFile, StandardAnalyzer analyzer) throws IOException, ParseException 
	{
		// Retrieve the parameters from file 
		Path queriesFilePath = Paths.get(queriesFile);
	    
		List<String> queries = Files.readAllLines(queriesFilePath);
		
	
		String doc = AnalyzerStringUtils.Concat(queries, "\n");
		List<ParseResult> parsedQueries = AnalyzerStringUtils.parseText(doc, "*FIND");
		
		List<AnalyzerQuery> analyzedQueries = new ArrayList<AnalyzerQuery>();
		
		for(ParseResult parsedQuery : parsedQueries)
		{
			AnalyzerQuery analyzerQuery = new AnalyzerQuery();
			
			analyzerQuery.QueryId = getQueryNumberFromHeader(parsedQuery.Header);
			analyzerQuery.Query = BuildLuceneQuery(parsedQuery.Body, analyzer);
			
			analyzedQueries.add(analyzerQuery);
		}
		
		return analyzedQueries;
	}
	
	private Query BuildLuceneQuery(String body, StandardAnalyzer analyzer) throws ParseException, IOException {

		List<String> queryTokensList = AnalyzerStringUtils.tokenizeString(analyzer, body);
		
		String queryTokens = AnalyzerStringUtils.Concat(queryTokensList, " ");
		
		Query query = new QueryParser(Consts.FIELD_NAME_CONTENT, analyzer).parse(queryTokens);
		
		return query;
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