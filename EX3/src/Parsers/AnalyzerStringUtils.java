package Parsers;
import java.util.ArrayList;
import java.util.List;
import Dto.ParseResult;

public class AnalyzerStringUtils 
{
	public List<ParseResult> parseText(String text, String headerPrefix)
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
			headerStartIndex = text.indexOf(headerPrefix, fromIndex);
			
			bodyStartIndex = headerEndIndex + 1;
			
			if(headerStartIndex != -1)
			{
				bodyEndIndex = headerStartIndex - 1;
			}
			else
			{
				bodyEndIndex = text.length() - 1;
			}
			
			parseResult.Header = text.substring(bodyStartIndex, bodyEndIndex);
			
			result.add(parseResult);
		}
		
		return result;			
	}
}