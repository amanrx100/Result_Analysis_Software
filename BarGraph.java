package projectfinalpackage;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class BarGraph {
    public static void createGraph(
            String existingWordFilePath,
            ArrayList<Double> passPercentPerSubjectList,
            ArrayList<String> subjectList,
            String title
    ) throws Exception {


        // Load the existing Word document
        FileInputStream fis = new FileInputStream(existingWordFilePath);
        XWPFDocument document = new XWPFDocument(fis);
        fis.close();

        // Find the last paragraph in the document
        XWPFParagraph lastParagraph = document.getBodyElements().stream()
                .filter(element -> element instanceof XWPFParagraph)
                .map(element -> (XWPFParagraph) element)
                .reduce((first, second) -> second)
                .orElse(null);

        // Add a new paragraph for the graph
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);

        // Add the title to the paragraph
        XWPFRun run = paragraph.createRun();
        run.setText(title);

        // Create the chart data
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Add data from the lists to the dataset
        for (int i = 0; i < passPercentPerSubjectList.size(); i++) {
            double passPercent = passPercentPerSubjectList.get(i);
            String subject = subjectList.get(i);
            dataset.addValue(passPercent, "Pass Percentage", subject);
        }

        // Create the chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Bar Chart",
                "Subjects",
                "Pass Percentage",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Change bar colors
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.getRenderer().setSeriesPaint(0, new Color(0, 153, 255)); // Change the color of the first series

        // Customize the Y-axis range and tick units
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setRange(0, 100); // Set the range of values on the Y-axis
        yAxis.setTickUnit(new NumberTickUnit(10)); // Set the tick unit to increment by 10

        // Save the chart as an image
        File chartFile = new File("chart.png");
        ChartUtils.saveChartAsPNG(chartFile, chart, 800, 600);

        // Insert the image into the Word document
        try (FileInputStream inputStream = new FileInputStream(chartFile)) {
            XWPFRun imgRun = paragraph.createRun();
            imgRun.addPicture(inputStream, Document.PICTURE_TYPE_PNG, "chart.png", Units.toEMU(400), Units.toEMU(300));
        }

        // Delete the temporary chart image file
        chartFile.delete();

        // Save the updated Word document
        FileOutputStream out = new FileOutputStream(existingWordFilePath);
        document.write(out);
        out.close();

        // Close the document
        document.close();
    }
}
