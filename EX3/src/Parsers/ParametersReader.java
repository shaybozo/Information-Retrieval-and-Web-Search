package Parsers;

import java.util.List;

public class ParametersReader {

	public String getQueryFilePath(List<String> parameters) {
		
		String result = null;
		
		for(String s : parameters)
		{
			if(s.contains("queryFile"))
			{
				result = s.substring(s.indexOf("=") + 1);
				break;
			}
		}
		
		return result;
	}

	public String getDocsFilePath(List<String> parameters) {
		
		String result = null;
		
		for(String s : parameters)
		{
			if(s.contains("docsFile"))
			{
				result = s.substring(s.indexOf("=") + 1);
				break;
			}
		}
		
		return result;
	}

	public String getOutputFilePath(List<String> parameters) {
		
		String result = null;
		
		for(String s : parameters)
		{
			if(s.contains("outputFile"))
			{
				result = s.substring(s.indexOf("=") + 1);
				break;
			}
		}
		
		return result;
	}

	public String getRetrievalAlgorithm(List<String> parameters) {
		
		String result = null;
		
		for(String s : parameters)
		{
			if(s.contains("retrievalAlgorithm"))
			{
				result = s.substring(s.indexOf("=") + 1);
				break;
			}
		}
		
		return result;
	}
}