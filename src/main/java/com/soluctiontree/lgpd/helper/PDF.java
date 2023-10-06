/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import com.itextpdf.pdfocr.OcrPdfCreator;
import com.itextpdf.pdfocr.tesseract4.Tesseract4LibOcrEngine;
import com.itextpdf.pdfocr.tesseract4.Tesseract4OcrEngineProperties;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*; 
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
import java.util.List;

/**
 *
 * @author User
 */
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

    public static void RedateCPF(String path) throws IOException {
        String outputPath = path.substring(0, path.lastIndexOf('.')) + "_redate.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(path), new PdfWriter(new FileOutputStream(outputPath)));
        Document doc = new Document(pdfDoc);
        ICleanupStrategy cleanupStrategy = new RegexBasedCleanupStrategy(Pattern.compile("[0-9]{3}.[0-9]{3}.[0-9]{3}-[0-9]{2}"));
        PdfCleaner.autoSweepCleanUp(pdfDoc, cleanupStrategy);
        doc.close();
        File renamedFile = new File(outputPath);
        new File(outputPath).renameTo(renamedFile);
    }
    
    public static void OCRImage(File[] images, String path) throws FileNotFoundException, IOException{
        String outputPath = path.substring(0, path.lastIndexOf('.')) + "_ocr.pdf";
        
        Tesseract4OcrEngineProperties tesseract4OcrEngineProperties = new Tesseract4OcrEngineProperties();
        tesseract4OcrEngineProperties.setPathToTessData(new File("F:\\"));
        
        PdfWriter writer = new PdfWriter(outputPath); 
        OcrPdfCreator ocrPdfCreator = new OcrPdfCreator(new Tesseract4LibOcrEngine(tesseract4OcrEngineProperties));
        ocrPdfCreator.createPdf(Arrays.asList(images), writer).close();
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
    
    public static void RedateCPFDocx (String path) throws FileNotFoundException, FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(path);
        XWPFDocument document = new XWPFDocument(fis);
        fis.close();
        
        Pattern cpfPattern = Pattern.compile("[0-9]{3}.[0-9]{3}.[0-9]{3}-[0-9]{2}");

        for (XWPFParagraph paragraph : document.getParagraphs()) {
            for (XWPFRun run : paragraph.getRuns()) {
                String text = run.getText(0);
                if (text != null) {
                    Matcher matcher = cpfPattern.matcher(text);
                    StringBuffer sb = new StringBuffer();

                    while (matcher.find()) {
                        String cpf = matcher.group();
                        String maskedCpf = "***" + cpf.substring(3, 11) + "-**";
                        matcher.appendReplacement(sb, maskedCpf);
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

        System.out.println("CPF detection and renaming completed.");
    } 
}
