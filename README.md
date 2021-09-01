# upload-file
## 1.springBoot文件上传
### 1.1配置临时目录
application.properties文件中配置：
```
#默认支持文件上传
spring.servlet.multipart.enabled=true
#支持文件写入磁盘.
spring.servlet.multipart.file-size-threshold=0
# 上传文件的临时目录
spring.servlet.multipart.location=d://uploadFiles/
#静态资源对外暴露的访问路径
file.staticAccessPath=/uploadFiles/**
```
com.hss.config.UploadFilePathConfig中读取配置：
```java
@Value("${spring.servlet.multipart.location}")
    private String location;

    @Value("${file.staticAccessPath}")
    private String staticAccessPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(staticAccessPath).addResourceLocations("file:" + location);
    }
```

### 1.2文件上传接口
请求方式：POST  
接口地址：http://localhost:8088/file  
参数：name=img type=file  
实现多附件上传

## 2.webSocket基本使用
> webSocket实现后端主动推送信息至前端页面

### 2.1用户消息发送
```
# 用户消息框中输入(私发)：
{
"message":"I love you baby",
"userName":"001",
"to":"002"
}
 
#用户消息框中输入(群发)：
{
"message":"I love you baby",
"userName":"001",
"to":"All"
}  
```
  
### 2.2服务器主动推送
* (私发)http://localhost:8088/sendTo?userName=003&msg=helloWord
* (群发)http://localhost:8088/sendAll?msg=hello%20every%20body

### 3.微信html页面开发
待补齐todo..