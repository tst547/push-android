package cn.link.net;

import cn.link.box.App;
import cn.link.common.MyGson;
import okhttp3.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.*;

/**
 * Created by hanyu on 2017/11/16 0016.
 */
public class Session {

    private String urlBase;

    private static Session session;

    private Session(String urlBase){
        this.urlBase = urlBase;
    }

    /**
     * @return
     */
    public static Session create() {
        if (null==session){
            session = new Session("http://" + App.HostIp() + ":" + App.HostPort());
        }
        return session;
    }

    /**
     * 连接测试
     * @return
     */
    public boolean isConnected(){
        String result = get("/test",null);
        if (null != result && result.contains("finish"))
            return true;
        return false;
    }

    /**
     * 文件列表
     * @param filePath
     * @return
     */
    public List<Base.File> getFileList(String filePath){
        Map<String,Object> temp = new HashMap<>();
        if (null!=filePath)
            temp.put("filePath",filePath);
        String result = get("/list",temp);
        if (null != result){
            Base base = new Base();
            Base.BaseMsg<List<Base.File>> type = base.new BaseMsg<>();
            type  = MyGson.getObject(result, type.getClass());
            if (type.err!=1)
                return type.msg;
        }
        return new ArrayList<>();
    }

    /**
     * 文件下载
     * @param filePath
     */
    public void fileDL(String filePath){
        OkHttpClient okHttpClient = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("filePath", filePath)
                .build();

        Request request = new Request.Builder()
                .url(urlBase+"/fileDL")
                .post(body)
                .build();

        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            BufferedInputStream bis = new BufferedInputStream(response.body().byteStream());
            int read = 0;
            while((read = bis.available())>0){
                byte[] temp = new byte[read];
                bis.read(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 简单http get请求拼装
     * @param path
     * @param params
     * @return
     */
    private String get(String path, Map<String,Object> params){
        if (null!=params)
            for (Map.Entry<String,Object> entry:params.entrySet()){
                if (!path.endsWith("&")&&!path.contains("&"))
                    path.concat("?"+entry.getKey()+"="+entry.getValue());
                else
                    path.concat("&"+entry.getKey()+"="+entry.getValue());
            }
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlBase+path)
                .build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
