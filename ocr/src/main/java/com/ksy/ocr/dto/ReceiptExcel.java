package com.ksy.ocr.dto;

import com.ksy.ocr.core.ExcelColumn;
import lombok.Getter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
public class ReceiptExcel {

    @ExcelColumn(name = "사업자번호")
    private String compNo;

    @ExcelColumn(name = "일자")
    private String date;

    @ExcelColumn(name = "금액")
    private String total;

    @ExcelColumn(name = "가맹점명")
    private String store;

    public ReceiptExcel(String comp, String date, String total, String store) throws ParseException {
        this.compNo = comp;
        this.date = stringToDate(date);
        this.total = extractPrice(total);
        this.store = store;
    }

    private String stringToDate(String excelDate) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(excelDate);
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private String extractPrice(String total) {
        return total.substring(0, total.length() - 1);
    }
}
