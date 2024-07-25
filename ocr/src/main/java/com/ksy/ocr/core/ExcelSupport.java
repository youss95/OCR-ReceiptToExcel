package com.ksy.ocr.core;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public interface ExcelSupport {

    void download(Class<?> clazz, List<?> data, String fileName, HttpServletResponse response) throws IOException, IllegalAccessException;
}
