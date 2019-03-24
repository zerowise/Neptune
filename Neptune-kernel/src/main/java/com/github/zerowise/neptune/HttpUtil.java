package com.github.zerowise.neptune;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * @Author: hanyuanliang@hulai.com
 * @Date: 2019-03-24 20:18
 **/
public class HttpUtil {
    private static CloseableHttpClient httpclient = HttpClients.createDefault();

    public static String sendPost(String url, byte[] body) throws IOException {
        HttpPost post = new HttpPost(url);
        post.setEntity(new ByteArrayEntity(body, ContentType.APPLICATION_JSON));
        CloseableHttpResponse response = httpclient.execute(post);
        try {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String s = EntityUtils.toString(entity);
                System.out.println(s);
            }
        } finally {
            response.close();
        }
        return null;
    }

}
