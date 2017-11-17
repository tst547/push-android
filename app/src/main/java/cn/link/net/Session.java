package cn.link.net;

import cn.link.box.App;
import cn.link.box.ConstStrings;
import cn.link.box.Key;
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

    private Session(String urlBase) {
        this.urlBase = urlBase;
    }

    /**
     * @return
     */
    public static Session create() {
        if (null == session) {
            session = new Session("http://" + App.HostIp() + ":" + App.HostPort());
        }
        return session;
    }

    /**
     * 文件列表
     *
     * @param filePath
     * @return
     */
    public void getFileList(String filePath, MyCallback.Netable able) {
        Map<String, Object> temp = new HashMap<>();
        if (null != filePath)
            temp.put(Key.FilePathKey, filePath);
        get(Key.PathListKey, temp, able);
    }

    /**
     * 文件下载
     *
     * @param downLoadTask
     */
    public void fileDownLoad(DownLoadTask downLoadTask, DownLoadMsg downLoadMsg) {
        downLoadTask.setNetable((loadTask, msg, view) -> {
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
                    .add(Key.FilePathKey, msg.getBaseFile().path)
                    .build();

            Request request = new Request.Builder()
                    .url(urlBase + "/fileDL")
                    .post(body)
                    .build();

            Call call = okHttpClient.newCall(request);
            try {
                Response response = call.execute();
                BufferedInputStream bis = new BufferedInputStream(response.body().byteStream());
                int read;
                while ((read = bis.available()) > 0) {
                    byte[] temp = new byte[read];
                    bis.read(temp);
                    bos.write(temp);
                    progress.setOffset(progress.getOffset() + read);
                    progress.setCurrent(MyMath.divideMax100(progress.getOffset(), progress.getMax()));
                    loadTask.updatePro(progress.getCurrent());
                    if (!msg.isRunFlag()) {
                        bis.close();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                msg.setRunFlag(false);
                msg.setMsg(ConstStrings.DownLoadStreamFailed);
                return false;
            } finally {
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
     *
     * @param path
     * @param params
     * @return
     */
    private void get(String path, Map<String, Object> params, MyCallback.Netable able) {
        if (null != params)
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (!path.endsWith("&") && !path.contains("&"))
                    path = path.concat("?" + entry.getKey() + "=" + entry.getValue());
                else
                    path = path.concat("&" + entry.getKey() + "=" + entry.getValue());
            }
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlBase + path)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new MyCallback(able));
    }


}
