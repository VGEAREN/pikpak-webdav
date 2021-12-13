package com.vgearen.pikpakwebdav.client;

import com.vgearen.pikpakwebdav.config.PikpakProperties;
import com.vgearen.pikpakwebdav.model.login.LoginRequest;
import com.vgearen.pikpakwebdav.util.JsonUtil;
import net.sf.webdav.exceptions.WebdavException;
import okhttp3.*;
import okio.Buffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PikpakDriverClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(PikpakDriverClient.class);
    private OkHttpClient okHttpClient;
    private PikpakProperties pikpakProperties;
    public PikpakDriverClient(PikpakProperties pikpakProperties) {

        String username = pikpakProperties.getUsername();
        String password = pikpakProperties.getPassword();
        String host = pikpakProperties.getProxy().getHost();
        Integer port = pikpakProperties.getProxy().getPort();
        String proxyType = pikpakProperties.getProxy().getProxyType();
        Proxy.Type type = Proxy.Type.HTTP;
        switch (proxyType){
            case "HTTP": type = Proxy.Type.HTTP;break;
            case "SOCKS": type = Proxy.Type.SOCKS;break;
            case "DIRECT": type = Proxy.Type.DIRECT;break;
        }
        Proxy proxy = new Proxy(type,new InetSocketAddress(host, port));
        if(!StringUtils.hasLength(username)){
            LOGGER.error("username为空");
        }else if(!StringUtils.hasLength(password)){
            LOGGER.error("password为空");
        }else {
            LOGGER.info("\nusername: {},\npassword: {}",username,password);
        }
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if(request.url().url().getHost().equals("api-drive.mypikpak.com")){
                    request = request.newBuilder()
                            .addHeader("Authorization",pikpakProperties.getTokenType()
                                    + " " + pikpakProperties.getAccessToken())
                            .build();
                }
                return chain.proceed(request);
            }
        }).authenticator(new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) throws IOException {
                if (response.code() == 401) {
                    LoginRequest loginRequest = new LoginRequest();
                    loginRequest.setClient_id("YNxT9w7GMdWvEOKa");
                    loginRequest.setClient_secret("dbw2OtmVEeuUvIptb1Coyg");
                    loginRequest.setUsername(username);
                    loginRequest.setPassword(password);
                    loginRequest.setCaptcha_token("");
                    String res = post("https://user.mypikpak.com/v1/auth/signin", loginRequest);
                    String accessToken = (String) JsonUtil.getJsonNodeValue(res, "access_token");
                    String tokenType = (String) JsonUtil.getJsonNodeValue(res, "token_type");
                    Assert.hasLength(accessToken, "获取accessToken失败");
//                    writeAccessToken(accessToken);
                    pikpakProperties.setAccessToken(accessToken);
                    pikpakProperties.setTokenType(tokenType);

                    return response.request().newBuilder()
                            .removeHeader("authorization")
                            .header("authorization", accessToken)
                            .build();
                }
                return null;
            }
        })
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES)
            .proxy(proxy)
            .build();
        this.okHttpClient = okHttpClient;
        this.pikpakProperties = pikpakProperties;
        this.login(username,password);
    }

    private static String bodyToString(final Request request){
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (Exception e) {
            LOGGER.error("{} in error, {}",request.url().url().getHost(),e);
            return "";
        }
    }
    private void login(String username,String password) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setClient_id("YNxT9w7GMdWvEOKa");
        loginRequest.setClient_secret("dbw2OtmVEeuUvIptb1Coyg");
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        loginRequest.setCaptcha_token("");
        String res = post("https://user.mypikpak.com/v1/auth/signin", loginRequest);
        String accessToken = (String) JsonUtil.getJsonNodeValue(res, "access_token");
        String tokenType = (String) JsonUtil.getJsonNodeValue(res, "token_type");
        Assert.hasLength(accessToken, "获取accessToken失败");
//        writeAccessToken(accessToken);
        pikpakProperties.setAccessToken(accessToken);
        pikpakProperties.setTokenType(tokenType);
    }

    public Response download(String url, HttpServletRequest httpServletRequest, long size ) {
        Request.Builder builder = new Request.Builder();
        String range = httpServletRequest.getHeader("range");
        if (range != null) {
            // 如果range最后 >= size， 则去掉
            String[] split = range.split("-");
            if (split.length == 2) {
                String end = split[1];
                if (Long.parseLong(end) >= size) {
                    range = range.substring(0, range.lastIndexOf('-') + 1);
                }
            }
            builder.header("range", range);
        }

        String ifRange = httpServletRequest.getHeader("if-range");
        if (ifRange != null) {
            builder.header("if-range", ifRange);
        }


        Request request = builder.url(url).build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            return response;
        } catch (IOException e) {
            throw new WebdavException(e);
        }
    }

    public void upload(String url, byte[] bytes, final int offset, final int byteCount,String taskId,long totalSize,long point, String fileName) {
        Request request = new Request.Builder()
                .addHeader("uploadtaskID",taskId)
                .addHeader("contentSize", String.valueOf(totalSize))
                .addHeader("Range","bytes="+point+"-"+(point+byteCount-1))
                .addHeader("Content-Type","text/plain")
                .addHeader("Content-Length",String.valueOf(byteCount))
//                .addHeader("Content-Type","text/plain;name="+ UrlEncodeUtil.encodeURIComponent(fileName))
                .post(RequestBody.create(MediaType.parse(""), bytes, offset, byteCount))
                .url(url).build();
        try (Response response = okHttpClient.newCall(request).execute()){
            LOGGER.info("upload: {}, code: {}", url, response.code());
            if (!response.isSuccessful()) {
                LOGGER.error("请求失败，url={}, code={}, resp={}", url, response.code(), response.body().string());
                throw new WebdavException("请求失败：" + url);
            }
        } catch (IOException e) {
            throw new WebdavException(e);
        }
    }

    public String post(String url, Object body) {
        String bodyAsJson = JsonUtil.toJson(body);
        Request request = new Request.Builder()
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), bodyAsJson))
                .url(getTotalUrl(url)).build();
        try (Response response = okHttpClient.newCall(request).execute()){
            String res = toString(response.body());
            LOGGER.info("post: {}, code: {}, body: {}", url,response.code(), bodyAsJson);
            if (!response.isSuccessful()) {
                LOGGER.error("请求失败，url={}, code={}, resp={}", url, response.code(), res);
                throw new WebdavException("请求失败：" + url);
            }
            return res;
        } catch (IOException e) {
            throw new WebdavException(e);
        }
    }
    public String patch(String url, Object body) {
        String bodyAsJson = JsonUtil.toJson(body);
        Request request = new Request.Builder()
                .patch(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), bodyAsJson))
                .url(getTotalUrl(url)).build();
        try (Response response = okHttpClient.newCall(request).execute()){
            String res = toString(response.body());
            LOGGER.info("patch: {}, code: {}, body: {}", url,response.code(), bodyAsJson);
            if (!response.isSuccessful()) {
                LOGGER.error("请求失败，url={}, code={}, resp={}", url, response.code(), res);
                throw new WebdavException("请求失败：" + url);
            }
            return res;
        } catch (IOException e) {
            throw new WebdavException(e);
        }
    }

    public String put(String url, Object body) {
        Request request = new Request.Builder()
                .put(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JsonUtil.toJson(body)))
                .url(getTotalUrl(url)).build();
        try (Response response = okHttpClient.newCall(request).execute()){
            LOGGER.info("put: {}, code: {}", url, response.code());
            if (!response.isSuccessful()) {
                LOGGER.error("请求失败，url={}, code={}, resp={}", url, response.code(), response.body().string());
                throw new WebdavException("请求失败：" + url);
            }
            return toString(response.body());
        } catch (IOException e) {
            throw new WebdavException(e);
        }
    }

    public String get(String url, Map<String, String> params)  {
        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(getTotalUrl(url)).newBuilder();
            params.forEach(urlBuilder::addQueryParameter);

            Request request = new Request.Builder().get().url(urlBuilder.build()).build();
            try (Response response = okHttpClient.newCall(request).execute()){
                LOGGER.info("get {}, code {}", urlBuilder.build(), response.code());
                if (!response.isSuccessful()) {
                    throw new WebdavException("请求失败：" + urlBuilder.build().toString());
                }
                return toString(response.body());
            }

        } catch (Exception e) {
            throw new WebdavException(e);
        }

    }

    private String toString(ResponseBody responseBody) throws IOException {
        if (responseBody == null) {
            return null;
        }
        return responseBody.string();
    }

    private String getTotalUrl(String url) {
        if (url.startsWith("http")) {
            return url;
        }
        return pikpakProperties.getUrl() + url;
    }

    private void deleteAccessTokenFile() {
        String accessTokenPath = pikpakProperties.getWorkDir() + "access-token";
        Path path = Paths.get(accessTokenPath);
        try {
            Files.delete(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readAccessToken() {
        String accessTokenPath = pikpakProperties.getWorkDir() + "access-token";
        Path path = Paths.get(accessTokenPath);

        if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
            try {
                Files.createDirectories(path.getParent());
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            byte[] bytes = Files.readAllBytes(path);
            if (bytes.length != 0) {
                return new String(bytes, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            LOGGER.warn("读取accessToken文件 {} 失败: ", accessTokenPath, e);
        }
//        writeAccessToken(pikpakProperties.getAccessToken());
        return pikpakProperties.getAccessToken();
    }

    private void writeAccessToken(String newAccessToken) {
        String accessToken = pikpakProperties.getWorkDir() + "access-token";
        try {
            Files.write(Paths.get(accessToken), newAccessToken.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOGGER.warn("写入accessToken文件 {} 失败: ", accessToken, e);
        }
        pikpakProperties.setAccessToken(newAccessToken);
    }
}
