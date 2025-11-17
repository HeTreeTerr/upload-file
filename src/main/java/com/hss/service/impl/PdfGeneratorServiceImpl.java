package com.hss.service.impl;

import com.hss.service.PdfGeneratorService;
import com.itextpdf.text.pdf.BaseFont;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * <p>
 *
 * </p>
 *
 * @author Hss
 * @date 2025-11-17
 */
@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    @Autowired
    private TemplateEngine templateEngine;

    @Override
    public void generatePdfFromTemplate(String templateName, Context context, String outputPdfPath) throws Exception {
        // 1. 使用 Thymeleaf 渲染模板，得到 HTML 字符串
        String htmlContent = templateEngine.process(templateName, context);
        System.out.println("htmlContent======>" + htmlContent);
        // 2. 配置 Flying Saucer
        try (OutputStream outputStream = new FileOutputStream(outputPdfPath)) {
            ITextRenderer renderer = new ITextRenderer();

            // ！！！关键：解决中文问题 ！！！
            // 获取字体文件路径，假设字体文件放在 resources/fonts 下
            String fontPath = getClass().getResource("/fonts/").getPath();
            renderer.getFontResolver().addFont(fontPath + "simsun.ttf",
                    BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(outputStream);
        }
    }
}
