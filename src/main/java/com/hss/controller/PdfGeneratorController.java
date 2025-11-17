package com.hss.controller;

import com.hss.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.UUID;

/**
 * <p>
 *
 * </p>
 *
 * @author Hss
 * @date 2025-11-17
 */
@Controller
public class PdfGeneratorController {

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @ResponseBody
    @GetMapping("/testGenPdf")
    public String testGenPdf() {
        try {
            //构建html文件内容
            Context context = new Context();
            context.setVariable("title", "Spring Boot 使用 Flying Saucer 生成 PDF");
            context.setVariable("content", "这是一段包含中文的示例内容。");

            //html2pdf核心
            String outputPdfPath = UUID.randomUUID() + "output.pdf";
            pdfGeneratorService.generatePdfFromTemplate("demo-template", context, outputPdfPath);
            System.out.println("PDF generated successfully!");
        }catch (Exception e){
            e.printStackTrace();
        }
        return "success";
    }
}
