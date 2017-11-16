package cn.link.activity;

import java.io.Serializable;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.*;
import android.widget.*;
import cn.link.box.App;
import cn.link.box.ConstStrings;
import cn.link.box.Key;
import cn.link.net.Base;

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
        handler = new Handler(this.getBaseContext().getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
            }
        };

        progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.drawable.ic_launcher);
        progressDialog.setTitle(ConstStrings.DownLoad);
        progressDialog.setMessage(ConstStrings.PlzWait);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置进度条对话框//样式（水平，旋转）
        // 进度最大值
        progressDialog.setMax(100);
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
                Intent intent = new Intent(FileActivity.this, FileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Key.FileListKey, (Serializable) App.getSession().getFileList(file.Path));
                intent.putExtras(bundle);
                startActivity(intent);
                return;
            }
        });
        hlv.setOnItemLongClickListener((adapterView, view, pos, n) -> {
            Base.File file = (Base.File) hlv.getItemAtPosition(pos);
            AlertDialog.Builder builder = new AlertDialog.Builder(FileActivity.this);
            String[] options = {ConstStrings.Detail, ConstStrings.DownLoad};
            builder
                .setItems(options, (dialog, which) -> {
                    if (which==0)
                        dialogShowMsg(ConstStrings.Detail, App.getFileMsg(file));
                    if (which==1)
                        openDialog();
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

    /**
     * 选择菜单将执行该方法
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_setting:
                // Toast.makeText(FirstActivity.this, "you click setting",
                // Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(FileActivity.this, SettingActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.app_quit:
                finish();
                break;
            default:
        }
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
    private Handler handler;
    private final int PRO = 10;
    private ProgressDialog progressDialog;
    /**
     * 加载进度条对话框 根据与FileDownload共用的progress 对象来显示下载进度
     */
    public void openDialog() {
        // 显示
        progressDialog.show();
        // 必须设置到show之后
        progressDialog.setProgress(0);
        // 线程
        handler.sendEmptyMessage(PRO);

    }

}
