package com.hss.util;

import java.util.UUID;

import static org.junit.Assert.*;

public class WechatUtilTest {

    public static void main(String[] args) {
        WechatUtil wechatUtil = new WechatUtil();
        //1、获取AccessToken
        String accessToken = wechatUtil.getAccessToken();

        //2、获取Ticket
        String jsapi_ticket = wechatUtil.getJsapiTicket(accessToken);

        //3、时间戳和随机字符串
        String noncestr = UUID.randomUUID().toString().replace("-", "").substring(0, 16);//随机字符串
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);//时间戳

        System.out.println("accessToken:"+accessToken+"\njsapi_ticket:"+jsapi_ticket+"\n时间戳："+timestamp+"\n随机字符串："+noncestr);

        //4、获取url
        String url="http://hsw7y5.natappfree.cc/index1.html";
        /*根据JSSDK上面的规则进行计算，这里比较简单，我就手动写啦
        String[] ArrTmp = {"jsapi_ticket","timestamp","nonce","url"};
        Arrays.sort(ArrTmp);
        StringBuffer sf = new StringBuffer();
        for(int i=0;i<ArrTmp.length;i++){
            sf.append(ArrTmp[i]);
        }
        */

        //5、将参数排序并拼接字符串
        String str = "jsapi_ticket="+jsapi_ticket+"&noncestr="+noncestr+"&timestamp="+timestamp+"&url="+url;

        //6、将字符串进行sha1加密
        String signature =wechatUtil.SHA1(str);
        System.out.println("参数："+str+"\n签名："+signature);
    }
}