package com.ksy.ocr.service;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import com.ksy.ocr.core.ExcelUtils;
import com.ksy.ocr.core.FieldIndex;
import com.ksy.ocr.dto.ReceiptExcel;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class VisionService {

    private final ExcelUtils excelUtils;


    public String getTextFromImg(List<byte[]> data, HttpServletResponse resp) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i<data.size(); i++) {


            ByteString imgBytes = ByteString.copyFrom(data.get(i));


            Image img = Image.newBuilder().setContent(imgBytes).build();

            Feature feat = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                    .addFeatures(feat)
                    .setImage(img)
                    .build();
            List<AnnotateImageRequest> requests = new ArrayList<>();
            requests.add(request);

            try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
                BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);

                for (AnnotateImageResponse res : response.getResponsesList()) {
                    List<EntityAnnotation> abc =  res.getTextAnnotationsList();


                    if (res.hasError()) {
                        System.out.printf("Error: %s\n", res.getError().getMessage());
                        return "Error detected";
                    }

//                    for (EntityAnnotation annotation : res.getTextAnnotationsList()) {
//                        System.out.format("Text: %s%n", annotation.getDescription());
//                        System.out.format("Position : %s%n", annotation.getBoundingPoly());
//                    }
                    stringBuilder.append(res.getFullTextAnnotation().getText());

                }

            }
        }
        List<String> t1 = Arrays.stream(stringBuilder.toString().split("\n")).toList();
        System.out.println(t1);
        Stream<String> a = t1.stream().filter(x->x.contains("사업자"));
        System.out.println();
        return stringBuilder.toString();
    }


    public String getTextFromPdf(List<byte[]> data, HttpServletResponse resp) throws IOException, IllegalAccessException, ParseException {

        List<ReceiptExcel> rcList = new ArrayList<>();

        for(int i=0; i<data.size(); i++) {
            StringBuilder stringBuilder = new StringBuilder();

            ByteString contents = ByteString.copyFrom(data.get(i));
            InputConfig inputConfig = InputConfig.newBuilder().setMimeType("application/pdf").setContent(contents).build();

            Feature feature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build();

            AnnotateFileRequest fileRequest =
                    AnnotateFileRequest.newBuilder()
                            .setInputConfig(inputConfig)
                            .addFeatures(feature)
                            .addPages(1) // Process the first page
                            .addPages(2) // Process the second page
                            .addPages(-1) // Process the last page
                            .build();

            // Add each `AnnotateFileRequest` object to the batch request.
            BatchAnnotateFilesRequest request =
                    BatchAnnotateFilesRequest.newBuilder().addRequests(fileRequest).build();



            List<BatchAnnotateFilesRequest> requests = new ArrayList<>();
            requests.add(request);

            try (ImageAnnotatorClient imageAnnotatorClient = ImageAnnotatorClient.create()) {
                // Make the synchronous batch request.
                BatchAnnotateFilesResponse response = imageAnnotatorClient.batchAnnotateFiles(request);

                for (AnnotateImageResponse  res : response.getResponsesList().get(0).getResponsesList()) {


                    if (res.hasError()) {
                        System.out.printf("Error: %s\n", res.getError().getMessage());
                        return "Error detected";
                    }

                    stringBuilder.append(res.getFullTextAnnotation().getText());
                }

            }
            List<String> parsedResult = Arrays.stream(stringBuilder.toString().split("\n")).toList();
            System.out.println("parsedResult: " + parsedResult);
            int a1 = parsedResult.indexOf("거래일자");
            int a2 = parsedResult.indexOf("합계");
            int a3 = parsedResult.indexOf("사업자번호");

            String compNo = parsedResult.get(a3+ FieldIndex.COMP.getIndex());
            String total = parsedResult.get(a2+FieldIndex.TOTAL.getIndex());
            String date = parsedResult.get(a1+FieldIndex.DATE.getIndex());

            rcList.add(new ReceiptExcel(compNo,date,total));
        }

        excelUtils.download(ReceiptExcel.class, rcList, "download", resp); //엑셀 다운로드
        return "ok";
    }


}
