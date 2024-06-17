package projectfinalpackage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class TrToExcelEntry {

    public static void trToExcelEntry() {


    	//i have to generate window to select what he want



    	    File selectedFile=PDFSelection.selectPdf();
    	    String pdfFilePath=selectedFile.getAbsolutePath();
    	    System.out.println(pdfFilePath);

    	//for path calling filePathSelectionMethod
    if(pdfFilePath!=null) {

        String excelFilePath =FilePathSelection.filePathSelection(" enter the path of excel file that  will be created");  // Path to save Excel file
        String wordFilePath=FilePathSelection.filePathSelection("enter the path of word file in which result analysis will be created");
        try {
            // Extract text from the PDF file
            String pdfText = extractTextFromPDF(pdfFilePath);

            // Initialize Excel workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Student Data");

            // Create the first row for column headers
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Roll Number");
            headerRow.createCell(1).setCellValue("Student Name");

            // Initialize a map to hold subject types and their respective grades
            Map<String, List<String>> subjectGradesMap = new HashMap<>();
            ArrayList<String> headerList = new ArrayList<>();

            // Student name pattern
            Pattern namePattern = Pattern.compile("(\\d{4}[A-Za-z]+\\d{6})\\s+([\\p{L}\\s]+)\\s*");
            Matcher nameMatcher = namePattern.matcher(pdfText);

            int rowNum = 1; // Start from row 1 for data

            // Iterate through roll numbers, student names, and grades
            while (nameMatcher.find()) {
                String rollNumber = nameMatcher.group(1);
                String studentName = nameMatcher.group(2);
                if (studentName.endsWith("S")) {
                    studentName = studentName.substring(0, studentName.length() - 1);}
                // Create a row for each student
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(rollNumber);
                row.createCell(1).setCellValue(studentName);

                // Extract subject types and grades
                extractSubjectGrades(pdfText, row, subjectGradesMap, headerList);
            }

            // Create column headers for subject types
            int columnIdx = 2; // Starting column index after Roll Number and Student Name
            for (String header : headerList) {
                headerRow.createCell(columnIdx++).setCellValue(header);
            }

            // Write grades under respective subject type columns
            for (int i = 0; i < rowNum - 1; i++) { // Iterate over rows
                Row row = sheet.getRow(i + 1);
                for (int j = 0; j < headerList.size(); j++) { // Iterate over columns
                    String subjectType = headerList.get(j);
                    List<String> grades = subjectGradesMap.get(subjectType);
                    if (grades != null && i < grades.size()) {
                        row.createCell(j + 2).setCellValue(grades.get(i));
                    } else {
                        row.createCell(j + 2).setCellValue(""); // Fill empty if no grade found
                    }
                }
            }

            // Extract SGPA and CGPA
            String sgpaRegex = "SGPA[\\s\\S]*?(\\d+(?:\\.\\d+)?)";
            String cgpaRegex = "CGPA[\\s\\S]*?(\\d+(?:\\.\\d+)?)";
//            String result="\\b(PASS|FAIL)\\b";
            String result= "\\b(PASS\\s*with\\s*grace|PASS|FAIL)\\b";

            extractAndWriteSGPACGPA(pdfText, sgpaRegex, cgpaRegex,result, sheet, rowNum);

            // Write the workbook content to a file
            try (FileOutputStream outputStream = new FileOutputStream(excelFilePath)) {
                workbook.write(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
//            System.err.println("Error occurred while reading the PDF file: " + e.getMessage());
        }
        //calling ExcelDATaREsultanalysis so that analysis of top5 students will be done in word file
        ExcelDataResultAnalysis.excelDataResultAnalysis(excelFilePath,wordFilePath);
        try {
            PassPercentSubject.passPercentSubject(excelFilePath,wordFilePath);

    	}catch(Exception e)
    	{
    	e.printStackTrace();
    	}
        //calling method for the pass Percent graph
        try {
			AverageMarksPerSubject.averageMarksPerSubject(excelFilePath, wordFilePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

 }
    }

    // Method to extract subject types and grades from text
    private static void extractSubjectGrades(String pdfText, Row row, Map<String, List<String>> subjectGradesMap, ArrayList<String> headerList) {

    //may not support multiple *
//    	String regex = "((?:\\b[A-Z0-9]+-[A-Z0-9]+(?:\\([A-Z]\\))?\\s*\\[[A-Z]\\])|(?:[A-Za-z0-9]+(?:-[A-Za-z0-9]+)*(?:\\(A\\))?\\s*\\[[A-Z]\\]))\\s*(?:\\d+(?:\\.\\d*)?|-|ABS|abs)?(?:\\s*-?\\d+(?:\\.\\d*)?)?(?:\\r?\\n[\\d\\s-]+)*\\s*(?:[\\d\\s-]+)?\\s*([A-Za-z0-9#+]+\\*?)?";

   	String regex = "((?:\\b[A-Z0-9]+-[A-Z0-9]+(?:\\([A-Z]\\))?\\s*\\[[A-Z]\\])|(?:[A-Za-z0-9]+(?:-[A-Za-z0-9]+)*(?:\\(A\\))?\\s*\\[[A-Z]\\]))\\s*(?:\\b\\d*\\.?\\d+|#|-|ABS|abs\\b)?(?:\\s*-?\\d*\\.?\\d+)?(?:\\r?\\n[\\d\\s.#-]+)*\\s*(?:[\\d#\\s.-]+)?\\s*([A-Za-z0-9#+]*\\**\\**)";

    	Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE); // Adding CASE_INSENSITIVE flag
        Matcher matcher = pattern.matcher(pdfText);
        while (matcher.find()) {
            String subjectType = matcher.group(1); // Group 2 captures the subject type  1
            String grade = matcher.group(2);       // Group 7 captures the grade  3
            System.out.println("Subject Type: " + subjectType);
            System.out.println("Grade: " + grade);

            // Add subject type and grade to the map
            subjectGradesMap.computeIfAbsent(subjectType, k -> new ArrayList<>()).add(grade);

            // Add subject type to the header list if it's not already present
            if (!headerList.contains(subjectType)) {
                headerList.add(subjectType);
            }
        }
    }

    // Method to extract and write SGPA and CGPA
    private static void extractAndWriteSGPACGPA(String pdfText, String sgpaRegex, String cgpaRegex,String resultRegex, Sheet sheet, int rowNum) {
        Pattern sgpaPattern = Pattern.compile(sgpaRegex);
        Matcher sgpaMatcher = sgpaPattern.matcher(pdfText);

        Pattern cgpaPattern = Pattern.compile(cgpaRegex);
        Matcher cgpaMatcher = cgpaPattern.matcher(pdfText);

        Pattern resultPattern=Pattern.compile(resultRegex,Pattern.CASE_INSENSITIVE);
        Matcher resultMatcher=resultPattern.matcher(pdfText);
        // Create column headers for SGPA and CGPA
        Row headerRow = sheet.getRow(0);
        int sgpaCgpaColumnIdx = headerRow.getLastCellNum(); // Get the index for SGPA and CGPA column headers
        headerRow.createCell(sgpaCgpaColumnIdx).setCellValue("SGPA");
        headerRow.createCell(sgpaCgpaColumnIdx + 1).setCellValue("CGPA");
        headerRow.createCell(sgpaCgpaColumnIdx + 2).setCellValue("Result");
        int i = 1; // Start from the second row (as student data starts from the second row)

        // Extract and write SGPA
        while (sgpaMatcher.find()) {
            String sgpa = sgpaMatcher.group(1);

            // Write SGPA value under respective column
            Row row = sheet.getRow(i);
            if (row == null) {
                row = sheet.createRow(i);
            }
            row.createCell(sgpaCgpaColumnIdx).setCellValue(sgpa);

            i++; // Move to the next row
        }

        i = 1; // Reset row index for CGPA extraction

        // Extract and write CGPA
        while (cgpaMatcher.find()) {
            String cgpa = cgpaMatcher.group(1);

            // Write CGPA value under respective column
            Row row = sheet.getRow(i);
            if (row == null) {
                row = sheet.createRow(i);
            }
            row.createCell(sgpaCgpaColumnIdx + 1).setCellValue(cgpa);

            i++; // Move to the next row
        }


        i = 1;
        while (resultMatcher.find()) {
            String result = resultMatcher.group(1);

            // Write CGPA value under respective column
            Row row = sheet.getRow(i);
            if (row == null) {
                row = sheet.createRow(i);
            }
            row.createCell(sgpaCgpaColumnIdx + 2).setCellValue(result);

            i++; // Move to the next row
        }
    }

    // Method to extract text from a PDF file
    private static String extractTextFromPDF(String filePath) throws IOException {
        PDDocument document = PDDocument.load(new File(filePath));
        PDFTextStripper pdfStripper = new PDFTextStripper();
        String pdfText = pdfStripper.getText(document);
        document.close();
        return pdfText;
    }
}



