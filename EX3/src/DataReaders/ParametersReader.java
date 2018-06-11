package DataReaders;

import java.util.List;

public class ParametersReader {

	// Retrieve the QueryFilePath from the parameters file
	public String getQueryFilePath(List<String> parameters) 
	{		
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

	// Retrieve the DocsFilePath from the parameters file
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

	// Retrieve the OutputFilePath from the parameters file
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

	// Retrieve the algo type from the parameters file
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