package DataReaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import Dto.ProjectParametrs;

public class ParametersReader {

	// Retrieve the QueryFilePath from the parameters file
	public ProjectParametrs readParametersFromFile(String parametersFilePathString) throws IOException 
	{	
		ProjectParametrs projectParametrs = new ProjectParametrs();		
		Path parametersFilePath = Paths.get(parametersFilePathString);		
		List<String> parameters = Files.readAllLines(parametersFilePath);
		 
		projectParametrs.trainFile = getParameter(parameters, "trainFile");
		projectParametrs.testFile = getParameter(parameters, "testFile");
		projectParametrs.outputFile = getParameter(parameters, "outputFile");
		projectParametrs.K_parameterValue = Integer.parseInt(getParameter(parameters, "k"));
		
		return projectParametrs;
	}

	// Retrieve the DocsFilePath from the parameters file
	public String getParameter(List<String> parameters, String parameterName) {
		
		String path = null;
		
		for(String s : parameters)
		{
			if(s.contains(parameterName))
			{
				path = s.substring(s.indexOf("=") + 1);
				break;
			}
		}
		
		return path;
	}
}