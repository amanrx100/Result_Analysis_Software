package projectfinalpackage;

import java.io.File;

import javax.swing.JFileChooser;

public class PDFSelection {

    public static File selectPdf() {
        // Create a file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true); // Allow multiple file selection

        // Show open dialog
        int returnValue = fileChooser.showOpenDialog(null);

        // Check if files are selected
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            // Get the selected files
            File selectedFiles = fileChooser.getSelectedFile();
            // Process each selected file
//            for (File file : selectedFiles) {
//                System.out.println("Selected file: " + file.getAbsolutePath());
//            }
            return selectedFiles; // Return the selected files
        } else {
//            System.out.println("No files selected.");
            return null; // Return null if no files are selected
        }
    }
}

