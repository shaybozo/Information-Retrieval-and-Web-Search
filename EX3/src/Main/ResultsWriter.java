package Main;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import Dto.ClassPerformance;
import Dto.ClassType;
import Dto.QueryResult;

public class ResultsWriter 
{
	private static DecimalFormat df2 = new DecimalFormat(".##");
	HashMap<Integer, ClassPerformance> _classesPerformance;
	double _successfulPredictionRate = 0.0;
	
	// Write the queries results to the output file
	public void writeQueriesResultsToFile(List<QueryResult> queriesResults, List<ClassType> classesTypes, String outputFile, int kValue) throws IOException 
	{
		_classesPerformance = initializeClassesPerformanceMap(classesTypes);
		File resultsFile = new File(outputFile);
		resultsFile.createNewFile(); // if file already exists will do nothing 
		FileOutputStream oFile = new FileOutputStream(resultsFile, false);
		
		String resultsContent = parseQueriesResults(queriesResults);
		
	    byte[] strToBytes = resultsContent.getBytes();
	    
	    oFile.write(strToBytes);
	    oFile.close();
	    
		// write summary:
	    writeSummary(kValue);
	}
	
	private HashMap<Integer, ClassPerformance> initializeClassesPerformanceMap(List<ClassType> classesTypes)
	{
		HashMap<Integer, ClassPerformance> classesPerformance = new HashMap<Integer, ClassPerformance>();
		for (ClassType classType: classesTypes) 
		{
			classesPerformance.put(classType.classID, new ClassPerformance());
		}
		
		return classesPerformance;
	}
	
	private String parseQueriesResults(List<QueryResult> queriesResults)
	{
		int successfulPredictionDocs = 0;
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Results:\n");
		sb.append("-----------------------------------------");
		
		for(QueryResult queryResult : queriesResults)
		{
			updateClassPerformance(queryResult);
			sb.append("Test doc ");
			sb.append(queryResult.QueryId);
			sb.append(": \n");

			sb.append("Actual class type: ");
			sb.append(queryResult.ActualClassType);
			sb.append("\n");
			
			sb.append("Calculated Class Type:");
			sb.append(queryResult.CalculatedClassType);
			sb.append("\n");
			
			sb.append("-----------------------------------------");
			sb.append("\n");
			
			if (queryResult.IsGoodClassTypePrediction) {
				successfulPredictionDocs++;
			}
		}
		
		_successfulPredictionRate = successfulPredictionDocs * 1.0 / queriesResults.size();
		
		sb.append("Total prediction results:");
		sb.append(" from ");
		sb.append(queriesResults.size());
		sb.append(" test docs we predicted successfully ");
		sb.append(successfulPredictionDocs);
		sb.append("\n");
		sb.append("The successful prediction rate is: ");
		sb.append(df2.format(_successfulPredictionRate));
		sb.append("\n");
		sb.append("The MacroAverage is: ");
		sb.append(df2.format(computeMacroAveraging()));
		sb.append("\n");
		sb.append("The MicroAverage is: ");
		sb.append(df2.format(computeMicroAveraging()));
		sb.append("\n");
		sb.append("-----------------------------------------");
		sb.append("\n");
		String result = sb.toString();
		
		return result;
	}
	
	private void updateClassPerformance(QueryResult queryResult)
	{
		for (HashMap.Entry<Integer, ClassPerformance> classPerformance : _classesPerformance.entrySet()) 
		{
			if (classPerformance.getKey() == queryResult.ActualClassType) // in the class
			{
				if (queryResult.ActualClassType == queryResult.CalculatedClassType) // predicted to be in the class
				{
					classPerformance.getValue().TP += 1;
				} 
				else // predicted to not be in class
				{
					classPerformance.getValue().FN += 1;
				}
			}
			else // not in the class
			{ 
				if (queryResult.CalculatedClassType == classPerformance.getKey()) // predicted to be in the class
				{
					classPerformance.getValue().FP += 1;
					
				} 
			}
		}
	}
	
	private double computeMacroAveraging()
	{
		double totalF1 = 0.0;
		
		for (HashMap.Entry<Integer, ClassPerformance> classPerformance : _classesPerformance.entrySet()) 
		{
			ClassPerformance perf = classPerformance.getValue();
			double P = perf.TP * 1.0 / (perf.TP + perf.FP);
			double R = perf.TP * 1.0 / (perf.TP + perf.FN);
			double classF1 =  2.0 * P * R / (P + R);
			totalF1 += classF1;
		}
		
		double averageF1 = totalF1 * 1.0 /_classesPerformance.entrySet().size();
		return averageF1;
	}
	
	private double computeMicroAveraging()
	{
		double totalTP = 0.0, totalFP = 0.0, totalFN = 0.0;
		
		for (HashMap.Entry<Integer, ClassPerformance> classPerformance : _classesPerformance.entrySet()) 
		{
			ClassPerformance perf = classPerformance.getValue();
			totalTP += perf.TP;
			totalFP += perf.FP;
			totalFN += perf.FN;
		}
		
		double P = totalTP * 1.0 / (totalTP + totalFP);
		double R = totalTP * 1.0 / (totalTP + totalFN);
		return  2.0 * P * R / (P + R); // F1
	}
	
	// function used to store summary results when various k's were tested.
	private void writeSummary(int kValue) throws IOException
	{
		Writer output;
		output = new BufferedWriter(new FileWriter("out/summary2.txt", true));
		output.append(System.lineSeparator() + 
						"Current K:" + kValue + 
						"\tCurrent ratio: " + _successfulPredictionRate + 
						"\tMacroAveraging: " + computeMacroAveraging() +
						"\tMicroAveraging: " + computeMicroAveraging() +
						"\tTime: " + (new Date()).toString());
		output.close();
	}
}