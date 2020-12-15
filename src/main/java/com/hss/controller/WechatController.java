package com.hss.controller;

import com.hss.util.WechatUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class WechatController {

    @Autowired
    private WechatUtil wechatUtil;

    @GetMapping("/js-sdk-config")
    public Map<String,Object> getSdk(@RequestParam(value = "url") String url) {
        Map<String,Object> map = new HashMap<>();
        try {
            //1、获取AccessToken
            String accessToken = wechatUtil.getAccessToken();
            //2、获取Ticket
            String jsapi_ticket = wechatUtil.getJsapiTicket(accessToken);
            //3、时间戳和随机字符串
            String noncestr = UUID.randomUUID().toString().replace("-", "").substring(0, 16);//随机字符串
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);//时间戳
            //5、将参数排序并拼接字符串
            String str = "jsapi_ticket="+jsapi_ticket+"&noncestr="+noncestr+"&timestamp="+timestamp+"&url="+url;
            //6、将字符串进行sha1加密
            String signature =DigestUtils.shaHex(str);
            //String signature = wechatUtil.SHA2(str);
            System.out.println("参数："+str+"\n签名："+signature);
            map.put("appId",WechatUtil.AppId);
            map.put("timestamp",timestamp);
            map.put("nonceStr",noncestr);
            map.put("signature",signature);
        } catch (Exception e) {
            map.put("flag",false);
            return map;
        }
        return map;
    }

    @RequestMapping(value = "/wechat", method = RequestMethod.GET)
    public void wechatGet(HttpServletRequest request,
                          HttpServletResponse response,
                          @RequestParam(value = "signature") String signature,
                          @RequestParam(value = "timestamp") String timestamp,
                          @RequestParam(value = "nonce") String nonce,
                          @RequestParam(value = "echostr") String echostr) {
        try {
            String token = "weChatCenter";
            if(wechatUtil.checkSignature(token,signature,timestamp,nonce)){
                response.getWriter().print(echostr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
