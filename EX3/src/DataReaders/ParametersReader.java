package DataReaders;

import java.util.List;

public class ParametersReader {

	// Retrieve the QueryFilePath from the parameters file
	public String getQueryFilePath(List<String> parameters) 
	{		
		String path = null;
		
		for(String s : parameters)
		{
			if(s.contains("queryFile"))
			{
				path = s.substring(s.indexOf("=") + 1);
				break;
			}
		}
		
		return path;
	}

	// Retrieve the DocsFilePath from the parameters file
	public String getDocsFilePath(List<String> parameters) {
		
		String path = null;
		
		for(String s : parameters)
		{
			if(s.contains("docsFile"))
			{
				path = s.substring(s.indexOf("=") + 1);
				break;
			}
		}
		
		return path;
	}

	// Retrieve the OutputFilePath from the parameters file
	public String getOutputFilePath(List<String> parameters) {
		
		String path = null;
		
		for(String s : parameters)
		{
			if(s.contains("outputFile"))
			{
				path = s.substring(s.indexOf("=") + 1);
				break;
			}
		}
		
		return path;
	}

	// Retrieve the algo type from the parameters file
	public String getRetrievalAlgorithm(List<String> parameters) {
		
		String path = null;
		
		for(String s : parameters)
		{
			if(s.contains("retrievalAlgorithm"))
			{
				path = s.substring(s.indexOf("=") + 1);
				break;
			}
		}
		
		return path;
	}
}