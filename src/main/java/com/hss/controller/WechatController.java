package com.hss.controller;

import com.hss.Domian.WechatUser;
import com.hss.util.RedisUtil;
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

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取页面签章信息
     * @param url
     * @return
     */
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

    /**
     * 公众平台配置服务器信息
     * @param request
     * @param response
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     */
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

    /**
     * 核心逻辑用户信息录入
     * @param name
     * @param iphone
     * @return
     */
    @RequestMapping("/addUserInfo")
    public Boolean addUserInfo(@RequestParam(value = "name",required = true) String name,
                               @RequestParam(value = "iphone",required = true) String iphone,
                               @RequestParam(value = "nickName",required = true)String nickName){
        System.out.println("name="+name+"--------------iphone="+iphone+"----------------------nickName="+nickName);
        return true;
    }

    /**
     * 获取微信用户的信息
     * @return
     */
    @RequestMapping("/getWechatUserInf")
    public WechatUser getWechatUserInf(@RequestParam(value = "code",required = true) String code){
        String accessToken = null;
        if(redisUtil.get("page_Token_" + code) != null){
            accessToken = redisUtil.get("page_Token_" + code).toString();
        }else{
            Map<String, Object> pageAccessToken = wechatUtil.getPageAccessToken(code);
            if(null != pageAccessToken &&
                    pageAccessToken.containsKey("accessToken") &&
                    null !=pageAccessToken.get("accessToken")){

                accessToken = pageAccessToken.get("accessToken").toString();
                redisUtil.set("page_Token_" + code,accessToken,7200);
            }
        }
        WechatUser wechatUser = wechatUtil.getWechatUserInfo(accessToken);
        return wechatUser;
    }
}
