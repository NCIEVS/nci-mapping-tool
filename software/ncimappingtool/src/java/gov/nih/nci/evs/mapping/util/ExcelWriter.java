package gov.nih.nci.evs.mapping.util;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.List;

import gov.nih.nci.evs.mapping.bean.*;

public class ExcelWriter {

    private static String[] columns = {"Source Code", "Source Term", "Target Code", "Target Label"};

    public static void write(List<MappingEntry> entries, String outputfile) throws IOException, InvalidFormatException {
        Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file
        CreationHelper createHelper = workbook.getCreationHelper();

        Sheet sheet = workbook.createSheet("Mapping");

        Font headerFont = workbook.createFont();
        //headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.BLACK.getIndex());

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);
        Row headerRow = sheet.createRow(0);

        headerCellStyle.setFillBackgroundColor(IndexedColors.AQUA.getIndex());

        for(int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerCellStyle);
        }

        int rowNum = 1;
        for(MappingEntry entry: entries) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getSourceCode());
            row.createCell(1).setCellValue(entry.getSourceTerm());
            row.createCell(2).setCellValue(entry.getTargetCode());
            row.createCell(3).setCellValue(entry.getTargetLabel());
        }

        for(int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream fileOut = new FileOutputStream(outputfile);
        workbook.write(fileOut);
        fileOut.close();
        System.out.println(outputfile + " generated.");
    }
}
