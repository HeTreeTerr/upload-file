package com.hss.util;

import com.alibaba.fastjson.JSONObject;
import com.hss.Domian.WechatUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信工具类
 */
@Component
public class WechatUtil {

    public final static String AppId = "wx05a8ddbcc54e0d4f";//第三方用户唯一凭证

    public final static String Secret = "2fafe98318eeef07c1286b4b30e2235b";//第三方用户唯一凭证密钥，即appsecret

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取accessToken
     * @return
     */
    public String getAccessToken() {
        String access_token = "";
        Object cacheToken = redisUtil.get("cacheToken");
        if(null != cacheToken && !"".equals(cacheToken)){
            access_token = cacheToken.toString();
            return access_token;
        }
        String grant_type = "client_credential";//获取access_token填写client_credential
        //这个url链接地址和参数皆不能变
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type="+grant_type+"&appid="+AppId+"&secret="+Secret;

        try {
            URL urlGet = new URL(url);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET"); // 必须是get方式请求
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
            http.connect();
            InputStream is = http.getInputStream();
            int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            String message = new String(jsonBytes, "UTF-8");
            JSONObject demoJson = JSONObject.parseObject(message);
            System.out.println("JSON字符串："+demoJson);
            access_token = demoJson.getString("access_token");
            redisUtil.set("cacheToken",access_token,7200);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return access_token;
    }

    /**
     * 获取jsapi_ticket
     * @param access_token
     * @return
     */
    public String getJsapiTicket(String access_token) {
        String ticket = null;
        Object cacheTicket = redisUtil.get("cacheTicket");
        if(null !=cacheTicket && !"".equals(cacheTicket)){
            ticket = cacheTicket.toString();
            return ticket;
        }
        String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+ access_token +"&type=jsapi";//这个url链接和参数不能变
        try {
            URL urlGet = new URL(url);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET"); // 必须是get方式请求
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
            http.connect();
            InputStream is = http.getInputStream();
            int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            String message = new String(jsonBytes, "UTF-8");
            JSONObject demoJson = JSONObject.parseObject(message);
            System.out.println("JSON字符串："+demoJson);
            ticket = demoJson.getString("ticket");
            redisUtil.set("cacheTicket",ticket,7200);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ticket;
    }

    /**
     * 获取页面版accessToken
     * @return
     */
    public Map<String,Object> getPageAccessToken(String code){
        Map<String,Object> map = new HashMap<>();
        if(code == null){
            return null;
        }
        String grant_type = "authorization_code";
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+AppId+"&secret="+Secret+"&code="+code+"&grant_type="+grant_type;
        try {
            URL urlGet = new URL(url);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET"); // 必须是get方式请求
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
            http.connect();
            InputStream is = http.getInputStream();
            int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            String message = new String(jsonBytes, "UTF-8");
            JSONObject demoJson = JSONObject.parseObject(message);
            is.close();
            System.out.println("JSON字符串："+demoJson);
            //结果解析
            if(demoJson.containsKey("errcode")){
                return null;
            }
            map.put("accessToken",demoJson.getString("access_token"));
            map.put("expiresIn",demoJson.getString("expires_in"));
            map.put("refreshToken",demoJson.getString("refresh_token"));
            map.put("openid",demoJson.getString("openid"));
            map.put("scope",demoJson.getString("scope"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 获取微信用户信息
     * @return
     */
    public WechatUser getWechatUserInfo(String pageAccessToken){
        if(pageAccessToken == null){
            return null;
        }
        WechatUser wechatUser = null;
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token="+pageAccessToken+"&openid="+AppId+"&lang=zh_CN";

        try {
            URL urlGet = new URL(url);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            http.setRequestMethod("GET"); // 必须是get方式请求
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");// 连接超时30秒
            System.setProperty("sun.net.client.defaultReadTimeout", "30000"); // 读取超时30秒
            http.connect();
            InputStream is = http.getInputStream();
            int size = is.available();
            byte[] jsonBytes = new byte[size];
            is.read(jsonBytes);
            String message = new String(jsonBytes, "UTF-8");
            JSONObject demoJson = JSONObject.parseObject(message);
            is.close();
            System.out.println("JSON字符串："+demoJson);
            //结果解析
            if(demoJson.containsKey("errcode")){
                return null;
            }
            wechatUser = JSONObject.parseObject(message, WechatUser.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return wechatUser;
    }

    /**
     * 效验签章信息
     * @param token
     * @param signature
     * @param timestamp
     * @param nonce
     * @return
     */
    public boolean checkSignature(String token,String signature, String timestamp, String nonce) {
        if(StringUtils.isEmpty(token)) {
            return false;
        }
        String[] arr = new String[] { token, timestamp, nonce };
        // 将token、timestamp、nonce三个参数进行字典序排序
        Arrays.sort(arr);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        MessageDigest md = null;
        String tmpStr = null;

        try {
            md = MessageDigest.getInstance("SHA-1");
            // 将三个参数字符串拼接成一个字符串进行sha1加密
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        content = null;
        // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
        return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
    }

    /**
     * 获取签章
     * @param decript
     * @return
     */
    public String SHA1(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("SHA-1");
            digest.update(decript.getBytes());
            byte messageDigest[] = digest.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param byteArray
     * @return
     */
    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     *
     * @param mByte
     * @return
     */
    private static String byteToHexStr(byte mByte) {

        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];

        String s = new String(tempArr);
        return s;
    }

}
