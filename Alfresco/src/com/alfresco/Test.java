package com.alfresco;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Test extends DocumentBaseImpl {

	
	public static void main(String[] args)  {

		DocumentBase myDoc = new DocumentBaseImpl();

		String fileName ="C:\\Users\\Sibi\\Downloads\\Resume-SIBI AKASH M (1).docx";
		
		
		System.out.println("Saving file: " + fileName);
		String alfId;
		try {
			alfId = myDoc.createDocumentAlfresco(fileName);
			System.out.println("id is: " + alfId);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("Getting file:");
		try {
		String Doc_Url=	myDoc.getDocument("3bbab669-15a0-4835-93ec-5cea5de69197");
		System.out.println(Doc_Url);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
