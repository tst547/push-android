package cn.link.activity;

import java.io.*;
import java.util.List;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import cn.link.box.App;
import cn.link.box.ConstStrings;
import cn.link.box.Key;
import cn.link.common.MyGson;
import cn.link.common.MyMath;
import cn.link.net.download.DownLoadMsg;
import cn.link.net.download.Progress;
import cn.link.net.Base;
import com.google.gson.reflect.TypeToken;

public class FileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.removeTitle();
        setContentView(R.layout.file_layout);
        // 获取上一个Activity传过来的文件列表(String类型的JSON)
        List<Base.File> fileList = (List<Base.File>) intent.getExtras().get(Key.FileListKey);
        // 加载该文件列表
        initList(fileList);
    }


    /**
     * 初始化列表
     *
     * @param fileList
     */
    protected void initList(List<Base.File> fileList) {
        ListView hlv = (ListView) findViewById(R.id.hostFileView);
        CustomAdapter customAdapter = new CustomAdapter(fileList);
        hlv.setAdapter(customAdapter);
        hlv.setOnItemClickListener((adapterView, view, pos, n) -> {
            Base.File file = (Base.File) hlv.getItemAtPosition(pos);
            if (file.isDir) {
                App.getSession().getFileList(file.path, ((call, response) -> {
                    Base.BaseMsg<List<Base.File>> baseMsg;
                    try {
                        baseMsg = (Base.BaseMsg<List<Base.File>>) MyGson.getObject(response.body().string()
                                , new TypeToken<Base.BaseMsg<List<Base.File>>>() {
                                }.getType());
                        Intent intent = new Intent(FileActivity.this, FileActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Key.FileListKey, (Serializable) baseMsg.msg);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } catch (IOException e) {
                        e.printStackTrace();
                        toastMsg(getBaseContext(), ConstStrings.FailedFileList);
                    }
                }));
            }
        });
        hlv.setOnItemLongClickListener((adapterView, view, pos, n) -> {
            Base.File file = (Base.File) hlv.getItemAtPosition(pos);
            AlertDialog.Builder builder = new AlertDialog.Builder(FileActivity.this);
            String[] options = {ConstStrings.Detail, ConstStrings.DownLoad};
            builder
                    .setItems(options, (dialog, which) -> {
                        if (which == 0)
                            dialogShowMsg(ConstStrings.Detail, App.getFileMsg(file));
                        if (which == 1) {
                            try {
                                Progress progress = new Progress();
                                progress.setMax(file.size);
                                DownLoadMsg downLoadMsg = new DownLoadMsg();
                                downLoadMsg.setBaseFile(file);
                                downLoadMsg.setRunFlag(true);
                                downLoadMsg.setId(System.currentTimeMillis());
                                downLoadMsg.setProgress(progress);
                                downLoadMsg.setFile(App.createFileByBaseFile(file));
                                App.getSession().fileDownLoad(downLoadMsg, ((call, response) -> {
                                    File fl = downLoadMsg.getFile();
                                    FileOutputStream bos = null;
                                    try {
                                        bos = new FileOutputStream(fl);
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                        downLoadMsg.setRunFlag(false);
                                        downLoadMsg.setMsg(ConstStrings.DownLoadFileOutPutFailed);
                                    }
                                    try {
                                        long offset = downLoadMsg.getProgress().getOffset();
                                        long max = downLoadMsg.getProgress().getMax();
                                        InputStream is = response.body().byteStream();
                                        int read;
                                        byte[] b = new byte[4096];
                                        while ((read = is.read(b)) != -1 && downLoadMsg.isRunFlag() && offset < max) {
                                            bos.write(b, 0, read);
                                            offset += Integer.valueOf(read).longValue();
                                            progress.setOffset(progress.getOffset() + Integer.valueOf(read).longValue());
                                            progress.setCurrent(MyMath.divideMax100(progress.getOffset(), progress.getMax()));
                                            //loadTask.updatePro(progress.getCurrent());
                                            b = new byte[4096];
                                        }
                                        is.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        downLoadMsg.setRunFlag(false);
                                        downLoadMsg.setMsg(ConstStrings.DownLoadStreamFailed);
                                    } finally {
                                        try {
                                            bos.close();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }));
                                App.downloadMsgs.add(downLoadMsg);
                            } catch (IOException e) {
                                e.printStackTrace();
                                toastMsg(getBaseContext(), ConstStrings.DownLoadCreateFailed);
                            }
                        }
                    })
                    .show();
            return true;
        });
    }

    /**
     * 长按Android的菜单键将执行该方法
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    class CustomAdapter extends BaseAdapter {

        private List<Base.File> files;

        protected CustomAdapter(List<Base.File> files) {
            this.files = files;
        }

        @Override
        public int getCount() {
            return files.size();
        }

        @Override
        public Object getItem(int position) {
            return files.get(position);
        }

        @Override
        public long getItemId(int position) {
            return files.get(0).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Base.File file = files.get(position);
            LayoutInflater inflater = FileActivity.this.getLayoutInflater();
            View view = inflater.inflate(R.layout.file_item, null);
            TextView text = (TextView) view.findViewById(R.id.item_text);
            ImageView image = (ImageView) view.findViewById(R.id.icon);
            text.setText(file.name);
            if (file.isDir)
                image.setImageResource(R.drawable.icons_folder);
            else
                image.setImageResource(R.drawable.icons_file);
            return view;
        }
    }

}
