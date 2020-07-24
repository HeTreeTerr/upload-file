package com.hss.controller;

import com.hss.util.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
public class MessageController {

    @Autowired
    private WebSocket webSocket;

    @ResponseBody
    @GetMapping("/sendTo")
    public String sendTo(@RequestParam("userName") String userName, @RequestParam("msg") String msg){
        try {
            webSocket.sendMessageTo(msg,userName);
            return "推送成功";
        } catch (IOException e) {
            e.printStackTrace();
            return "小异常，问题不大";
        }
    }

    @ResponseBody
    @GetMapping("/sendAll")
    public String sendAll(@RequestParam("msg") String msg) throws IOException {

        String fromUserId="system";//其实没用上
        webSocket.sendMessageAll(msg,fromUserId);

        return "推送成功";
    }
}
