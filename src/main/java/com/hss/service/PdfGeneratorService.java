package com.hss.service;


import org.thymeleaf.context.Context;

/**
 * <p>
 *
 * </p>
 *
 * @author Hss
 * @date 2025-11-17
 */
public interface PdfGeneratorService {

    /**
     * 构建html2pdf
     * @param templateName
     * @param context
     * @param outputPdfPath
     * @throws Exception
     */
    void generatePdfFromTemplate(String templateName, Context context, String outputPdfPath) throws Exception;
}
