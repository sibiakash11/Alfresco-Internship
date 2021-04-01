package com.alfresco;

import java.io.FileNotFoundException;
import java.io.IOException;



public interface DocumentBase {

	String getDocument(String obId) throws IOException;

	
	String createDocumentAlfresco(String pathname) throws FileNotFoundException, IOException;

}