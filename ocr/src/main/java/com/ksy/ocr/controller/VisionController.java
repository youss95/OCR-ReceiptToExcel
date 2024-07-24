package com.ksy.ocr.controller;

import com.ksy.ocr.service.VisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class VisionController {

    private final VisionService visionService;

    @PostMapping("/export2")
    public String export2(List<MultipartFile> imgFile) throws IOException {
        System.out.println(imgFile.get(0).getContentType());
        List<byte[]> uploadImgList = new ArrayList<>();
        for(MultipartFile file : imgFile) {
            byte[] imgBytes = file.getBytes();
            uploadImgList.add(imgBytes);
        }

        try {
            return visionService.getTextFromPdf(uploadImgList);
        } catch (Exception e) {
            //e.printStackTrace();
            return "Failed to extract text: " + e.getMessage();
        }

    }

    @GetMapping("/extract")
    public String extract() {

        List<String> n = Arrays.asList("jo", "ko", "ja", "[사업자]: 536-37-00183");

        String a = n.stream().filter(x->x.contains("사업자")).collect(Collectors.joining(""));
        String[] k = a.split(" ");
        System.out.println(k[1]);
        //Arrays.stream(k).findAny()
        //String pattern = "^\\d{3}-\\d{2}-\\d{5}$"
        return null;

    }
}
