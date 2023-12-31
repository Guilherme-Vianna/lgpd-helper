package com.soluctiontree.lgpd.helper;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.util.regex.Pattern;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.layout.Document;
import com.itextpdf.pdfcleanup.autosweep.ICleanupStrategy;
import com.itextpdf.pdfcleanup.autosweep.RegexBasedCleanupStrategy;

import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.pdfcleanup.PdfCleaner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Matcher;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;


import javax.imageio.ImageIO;
import java.io.File;

public class PDF {
    public static void RedateRegex(String path, String regex) throws IOException{
        String outputPath = path.substring(0, path.lastIndexOf('.')) + "_redate.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(path), new PdfWriter(new FileOutputStream(outputPath)));
        Document doc = new Document(pdfDoc);
        ICleanupStrategy cleanupStrategy = new RegexBasedCleanupStrategy(Pattern.compile(regex));
        PdfCleaner.autoSweepCleanUp(pdfDoc, cleanupStrategy);
        doc.close();
        
        
        File renamedFile = new File(outputPath);
        new File(path).renameTo(renamedFile);
    }
    
    public static void RedateCEP(String path) throws IOException {
        String outputPath = path.substring(0, path.lastIndexOf('.')) + "_redate.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(path), new PdfWriter(new FileOutputStream(outputPath)));
        Document doc = new Document(pdfDoc);
        ICleanupStrategy cleanupStrategy = new RegexBasedCleanupStrategy(Pattern.compile("[0-9]{2}.[0-9]{3}-[0-9]{3}"));
        PdfCleaner.autoSweepCleanUp(pdfDoc, cleanupStrategy);
        doc.close();        
        File renamedFile = new File(outputPath);
        new File(path).renameTo(renamedFile);
    }


    public static void RedateCPFAndRG(String path) throws IOException {
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(path), new PdfWriter(new FileOutputStream(path.substring(0, path.lastIndexOf('.')) + "_redate_.pdf")));
        Document doc = new Document(pdfDoc);
        ICleanupStrategy cpfcleanupStrategy = new RegexBasedCleanupStrategy(Pattern.compile("[0-9]{3}.[0-9]{3}.[0-9]{3}-[0-9]{2}"));
        ICleanupStrategy rgcleanupStrategy = new RegexBasedCleanupStrategy(Pattern.compile("\\b[0-9]{2}.[0-9]{3}.[0-9]{3}-[0-9]\\b"));
       
        PdfCleaner.autoSweepCleanUp(pdfDoc, cpfcleanupStrategy);
        PdfCleaner.autoSweepCleanUp(pdfDoc, rgcleanupStrategy);
        
        doc.close();
        String outputPath = path.substring(0, path.lastIndexOf('.')) + "_redate_.pdf";
        File renamedFile = new File(outputPath);
        new File(outputPath).renameTo(renamedFile);
    }
    
    public static void PDFToImage(String path) throws IOException {
        PDDocument document = Loader.loadPDF(new RandomAccessReadBufferedFile(path));
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        int pageNum = 0;
        for (PDPage page : document.getPages()) {
            BufferedImage image = pdfRenderer.renderImageWithDPI(pageNum, 300);
            File outputImage = new File(path + "output" + pageNum + ".png");
            boolean png = ImageIO.write(image, "PNG", outputImage);
            pageNum++;
        }
    } 
    
    public static void RedateCPFAndRGDocx (String path) throws FileNotFoundException, FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(path);
        XWPFDocument document = new XWPFDocument(fis);
        fis.close();
        
        Pattern cpfPattern = Pattern.compile("\\b[0-9]{3}.[0-9]{3}.[0-9]{3}-[0-9]{2}\\b");
        
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            for (XWPFRun run : paragraph.getRuns()) {
                String text = run.getText(0);
                if (text != null) {
                    Matcher matcher = cpfPattern.matcher(text);
                    StringBuffer sb = new StringBuffer();

                    while (matcher.find()) {
                        String cpf = matcher.group();
                        String maskedCPF = "***" + cpf.substring(3, 11) + "-**";
                        matcher.appendReplacement(sb, maskedCPF);
                    }
                    
                    matcher.appendTail(sb);
                    run.setText(sb.toString(), 0);
                }
            }
            
        }
        
        Pattern rgPattern = Pattern.compile("\\b[0-9]{2}.[0-9]{3}.[0-9]{3}-[0-9]\\b");
        
        for (XWPFParagraph paragraph : document.getParagraphs()) {
            for (XWPFRun run : paragraph.getRuns()) {
                String text = run.getText(0);
                if (text != null) {
                    Matcher matcher = rgPattern.matcher(text);
                    StringBuffer sb = new StringBuffer();

                    while (matcher.find()) {
                        String rg = matcher.group();
                        String maskedRG = "**." + rg.substring(3, 10) + "-*";
                        matcher.appendReplacement(sb, maskedRG);
                    }
                    
                    matcher.appendTail(sb);
                    run.setText(sb.toString(), 0);
                }
            }
            
        }
        
        String outputPath = path.substring(0, path.lastIndexOf('.')) + "_redate.docx";
        FileOutputStream fos = new FileOutputStream(outputPath);
        document.write(fos);
        fos.close();
        document.close();
    } 
}
