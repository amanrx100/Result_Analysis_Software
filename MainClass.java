package projectfinalpackage;

import PDFToExcelConversion.MarksExtractor;

public class MainClass {
	public static void main(String args[]) {
		//enter whether you want to work on certificates or tr
    	WelcomeScreen.invoke();


	String input=FilePathSelection.filePathSelection("Enter Certificates (or 1 for certificates )or Tr or (2 for Tr) ");
	input=input.toLowerCase();
	if(input.equals("certificates")||input.equals("1") ){
		MarksExtractor.marksExtractInExcel();
	}
	else{
		TrToExcelEntry.trToExcelEntry();
	}
	}

}
