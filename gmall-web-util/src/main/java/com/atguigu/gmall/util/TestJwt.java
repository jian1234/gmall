package com.atguigu.gmall.util;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.impl.Base64UrlCodec;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestJwt {

    public static void main(String[] args) {
        Map<String,Object> map = new HashMap<>();
        map.put("memberId","1");
        map.put("nickname","zhangsan");
        String ip = "127.0.0.1";
        String time = new SimpleDateFormat("yyyyMMdd HHmm").format(new Date());
        String encode = JwtUtil.encode("2019gmall0105", map, ip + time);
        System.err.println(encode);


        // String tokenUserInfo = StringUtils.substringBetween(encode, ".");
        Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
        byte[] tokenBytes = base64UrlCodec.decode("eyJuaWNrbmFtZSI6InpoYW5nc2FuIiwibWVtYmVySWQiOiIxIn0");
        String tokenJson = null;
        try {
            tokenJson = new String(tokenBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map map1 = JSON.parseObject(tokenJson, Map.class);
        System.out.println("64="+map1);


    }
}
