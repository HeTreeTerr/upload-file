# upload-file
文件上传

#webSocket基本使用
> 实例源码来源于：
https://blog.csdn.net/qq_35387940/article/details/93483678

- 用户消息发送  
> 用户消息框中输入(私发)：
 {
 "message":"I love you baby",
 "userName":"001",
 "to":"002"
 }  
 
> 用户消息框中输入(群发)：
 {
  "message":"I love you baby",
  "userName":"001",
  "to":"All"
  }
  
- 服务器主动推送
> http://localhost:8088/sendTo?userName=003&msg=helloWord(私发)
> http://localhost:8088/sendAll?msg=hello%20every%20body(群发)