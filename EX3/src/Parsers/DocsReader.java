package Parsers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.index.IndexWriter;

public class DocsReader {

	public void LoadAndIndexDocs(IndexWriter indexWriter, String docsFile, Boolean isImprovedAlgo) throws IOException {
		Path  docsFilePath = Paths.get(docsFile);
	    byte[] encoded = Files.readAllBytes(docsFilePath);
	    String docs =  new String(encoded);
	    		
	}
}