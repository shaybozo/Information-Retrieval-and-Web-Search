package DataReaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import Dto.AnalyzerQuery;
import Dto.ParseResult;
import Main.Constants;
import Parsers.AnalyzerStringUtils;

public class QueriesReader {

	// Load the queries from queries file and prepare them for execution using lucene
	public List<AnalyzerQuery> readQueries(String queriesFile, StandardAnalyzer analyzer, Boolean isImprovedAlgo, HashSet<String> stopWords) 
			throws IOException, ParseException 
	{
		// Retrieve the queries from file 
		Path queriesFilePath = Paths.get(queriesFile);
	    
		List<String> queries = Files.readAllLines(queriesFilePath);
		
		String doc = AnalyzerStringUtils.Concat(queries, "\n");
		List<ParseResult> parsedQueries = AnalyzerStringUtils.parseText(doc, "*FIND");
		
		List<AnalyzerQuery> analyzedQueries = new ArrayList<AnalyzerQuery>();
		
		for(ParseResult parsedQuery : parsedQueries)
		{
			AnalyzerQuery analyzerQuery = new AnalyzerQuery();
			
			analyzerQuery.QueryId = getQueryNumberFromHeader(parsedQuery.Header);
			
			if (isImprovedAlgo) {
				analyzerQuery.Query = buildLuceneQuery(parsedQuery.Body, analyzer);
			} else {
				// Tokenize without stop words removal
				analyzerQuery.Query = buildTokenizedQuery(parsedQuery.Body, analyzer, stopWords);
			}
			
			
			
			analyzedQueries.add(analyzerQuery);
		}
		
		return analyzedQueries;
	}
	
	private Query buildTokenizedQuery(String body, StandardAnalyzer analyzer, HashSet<String> stopWords) throws ParseException  
	{
		String bodyLowercase= body.toLowerCase(); // We lower case.
		String bodyWithoutPanctuations = bodyLowercase.replaceAll("-|,|\\.|:|/|\\\\|\\`|;|:|!|@|#|$|%|^|&|[|]|\"|'", ""); // remove panctuations.
		List<String> textTokensList = Arrays.asList(bodyWithoutPanctuations.split(" "));
		
		// Remove stop words from query
		List<String> filteredTokensList = new ArrayList<String>();  
		
		for (String token: textTokensList) {
			if (!stopWords.contains(token)) {
				filteredTokensList.add(token);
			}
		}
		
		String queryTokens = AnalyzerStringUtils.Concat(filteredTokensList, " ");
		Query query = new QueryParser(Constants.FIELD_NAME_CONTENT, analyzer).parse(queryTokens);
		
		return query;
	}
	
	private Query buildLuceneQuery(String body, StandardAnalyzer analyzer) throws ParseException, IOException {

		List<String> queryTokensList = AnalyzerStringUtils.tokenizeString(analyzer, body);
		
		String queryTokens = AnalyzerStringUtils.Concat(queryTokensList, " ");
		
		Query query = new QueryParser(Constants.FIELD_NAME_CONTENT, analyzer).parse(queryTokens);
		
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