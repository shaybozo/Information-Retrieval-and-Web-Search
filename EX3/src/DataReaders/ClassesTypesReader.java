package DataReaders;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import Dto.ClassType;

public class ClassesTypesReader {
	// Retrieve the classes types from the file
	public List<ClassType> readClassesTypesFromFile(String classesFilePath) throws IOException 
	{	
		Path classesTypesPath = Paths.get(classesFilePath);
		List<String> lines = Files.readAllLines(classesTypesPath);
		List<ClassType> result = new ArrayList<ClassType>();
		
		for(String line : lines)
		{
			String[] parts = line.split(" ");
			ClassType classType = new ClassType();
			
			classType.classID = Integer.parseInt(parts[0]);
			classType.className = parts[1];
			
			result.add(classType);
		}
		
		return result;
	}
}