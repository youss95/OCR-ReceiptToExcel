package com.ksy.ocr.controller;

import com.ksy.ocr.core.ExcelUtils;
import com.ksy.ocr.dto.ReceiptExcel;
import com.ksy.ocr.service.VisionService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class VisionController {

    private final VisionService visionService;
    private  final ExcelUtils excelUtils;
    @PostMapping("/export")
    public String export(List<MultipartFile> imgFile) throws IOException {
        String contentType = Objects.requireNonNull(imgFile.get(0).getContentType(), "File not exist").split("/")[0];
        List<byte[]> uploadImgList = new ArrayList<>();

        for(MultipartFile file : imgFile) {
            byte[] imgBytes = file.getBytes();
            uploadImgList.add(imgBytes);
        }

        try {
            return contentType.equals("image") ?
                    visionService.getTextFromImg(uploadImgList) : visionService.getTextFromPdf(uploadImgList);
        } catch (Exception e) {
            //e.printStackTrace();
            return "Failed to extract text: " + e.getMessage();
        }

    }

    @GetMapping("/extract")
    public String extract() {
        /*
        * TODO --
        * 이미지인 경우 필요 요수 추출
         */
        List<String> n = Arrays.asList("jo", "ko", "ja", "[사업자]: 536-37-00183");

        String a = n.stream().filter(x->x.contains("사업자")).collect(Collectors.joining(""));
        String[] k = a.split(" ");
        System.out.println(k[1]);
        //Arrays.stream(k).findAny()
        //String pattern = "^\\d{3}-\\d{2}-\\d{5}$"
        return null;

    }

    @GetMapping("/excel")
    public void download(HttpServletResponse response) throws IOException, IllegalAccessException {

        List<ReceiptExcel> result = new ArrayList<>();
        result.add(new ReceiptExcel("2","3","4"));
        excelUtils.download(ReceiptExcel.class, result, "download", response);
    }
}
