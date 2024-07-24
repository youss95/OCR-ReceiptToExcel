package com.ksy.ocr.service;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Service
public class VisionService {

    public String getTextFromImg(List<byte[]> data) throws IOException {
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


    public String getTextFromPdf(List<byte[]> data) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i<data.size(); i++) {


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

                for (AnnotateImageResponse res : response.getResponsesList().get(0).getResponsesList()) {

                    if (res.hasError()) {
                        System.out.printf("Error: %s\n", res.getError().getMessage());
                        return "Error detected";
                    }
                    System.out.println(res.getFullTextAnnotation().getText());
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

}
