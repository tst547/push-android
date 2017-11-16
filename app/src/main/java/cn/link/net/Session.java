package cn.link.net;

import cn.link.box.App;
import cn.link.box.ConstStrings;
import cn.link.common.MyGson;
import cn.link.common.MyMath;
import cn.link.net.download.DownLoadMsg;
import cn.link.net.download.DownLoadTask;
import cn.link.net.download.Progress;
import okhttp3.*;

import java.io.*;
import java.util.*;

/**
 * 跟主机相关交互的操作
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
     * @param downLoadTask
     */
    public void fileDownLoad(DownLoadTask downLoadTask, DownLoadMsg downLoadMsg){
        downLoadTask.setNetable((loadTask,msg,view)->{
            Progress progress = msg.getProgress();
            File file = msg.getFile();
            FileOutputStream bos;
            try {
                bos = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                msg.setRunFlag(false);
                msg.setMsg(ConstStrings.DownLoadFileOutPutFailed);
                return false;
            }

            OkHttpClient okHttpClient = new OkHttpClient();

            RequestBody body = new FormBody.Builder()
                    .add("filePath", msg.getBaseFile().path)
                    .build();

            Request request = new Request.Builder()
                    .url(urlBase+"/fileDL")
                    .post(body)
                    .build();

            Call call = okHttpClient.newCall(request);
            try {
                Response response = call.execute();
                BufferedInputStream bis = new BufferedInputStream(response.body().byteStream());
                int read;
                while((read = bis.available())>0){
                    byte[] temp = new byte[read];
                    bis.read(temp);
                    bos.write(temp);
                    progress.setOffset(progress.getOffset()+read);
                    progress.setCurrent(MyMath.divideMax100(progress.getOffset(),progress.getMax()));
                    if (!msg.isRunFlag()){
                        msg.setRunFlag(false);
                        bis.close();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                msg.setRunFlag(false);
                msg.setMsg(ConstStrings.DownLoadStreamFailed);
                return false;
            }finally {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }).execute(downLoadMsg);

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
