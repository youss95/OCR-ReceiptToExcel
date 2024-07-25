package com.ksy.ocr.core;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public final class ExcelUtils implements ExcelSupport{

    private static final int MAX_ROW = 500;
    @Override
    public void download(Class<?> clazz, List<?> data, String fileName, HttpServletResponse response) throws IOException, IllegalAccessException {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            int loop = 1;
            int listSize = data.size();

            for(int start=0; start<listSize; start++) {
                getWorkBook(clazz, workbook, start, findHeaderNames(clazz), data, listSize);
            }

            response.setContentType("ms-vnd/excel");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();


        } catch (IOException | IllegalAccessException e) {
            System.out.println("Excel download error");
            throw new RuntimeException(e);
        }

    }

    private SXSSFWorkbook getWorkBook(Class<?> clazz, SXSSFWorkbook workbook, int rowIdx, List<String> headerNames, List<?> data, int maxSize) throws IllegalAccessException, IOException {
        // 각 시트 당 MAX_ROW 개씩
        String sheetName = "Sheet" + (rowIdx / MAX_ROW + 1);

        Sheet sheet = ObjectUtils.isEmpty(workbook.getSheet(sheetName)) ? workbook.createSheet(sheetName) : workbook.getSheet(sheetName);
        Row row = null;
        Cell cell = null;

        row = sheet.createRow(0);
        createHeaders(workbook, row, cell, headerNames);
        createBody(clazz, data, sheet, row, cell, rowIdx);

        return workbook;
    }

    //header
    private void createHeaders(SXSSFWorkbook workbook, Row row, Cell cell, List<String> headerNames) {
        for (int i = 0, size = headerNames.size(); i < size; i++) {
            cell = row.createCell(i);
            cell.setCellValue(headerNames.get(i));
        }

    }

    //body row
    private void createBody(Class<?> clazz, List<?> data, Sheet sheet, Row row, Cell cell, int rowNo) throws IllegalAccessException, IOException {
        int startRow = 0;

        for (Object o : data) {
            List<Object> fields = findFieldValue(clazz, o);
            row = sheet.createRow(++startRow);
            for (int i = 0, fieldSize = fields.size(); i < fieldSize; i++) {
                cell = row.createCell(i);
                cell.setCellValue(String.valueOf(fields.get(i)));
            }
        }
    }


    private List<String> findHeaderNames(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(ExcelColumn.class))
                .map(field -> field.getAnnotation(ExcelColumn.class).name())
                .collect(Collectors.toList());
    }


    private List<Object> findFieldValue(Class<?> clazz, Object obj) throws IllegalAccessException {
        List<Object> result = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            result.add(field.get(obj));
        }
        return result;
    }
}
