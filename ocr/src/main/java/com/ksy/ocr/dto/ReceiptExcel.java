package com.ksy.ocr.dto;

import com.ksy.ocr.core.ExcelColumn;
import lombok.Getter;

@Getter
public class ReceiptExcel {

    @ExcelColumn(name = "사업자번호")
    private String compNo;

    @ExcelColumn(name = "일자")
    private String date;

    @ExcelColumn(name = "금액")
    private String total;

    public ReceiptExcel(String comp, String date, String total) {
        this.compNo = comp;
        this.date = date;
        this.total = total;
    }
}
