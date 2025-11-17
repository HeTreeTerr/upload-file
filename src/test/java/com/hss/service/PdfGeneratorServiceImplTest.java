package com.hss.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PdfGeneratorServiceImplTest {

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Test
    public void generatePdfFromTemplate() {
        try {
            //构建html文件内容
            Context context = new Context();
            context.setVariable("title", "Spring Boot 使用 Flying Saucer 生成 PDF");
            context.setVariable("content", "这是一段包含中文的示例内容。");

            //html2pdf核心
            pdfGeneratorService.generatePdfFromTemplate("demo-template", context, "output.pdf");
            System.out.println("PDF generated successfully!");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}