package cn.link.activity;

import java.io.Serializable;

import cn.link.box.ConstStrings;
import cn.link.box.Key;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import cn.link.box.App;

/**
 * 主Activity
 * 程序打开后创建的第一个Activity
 *
 * @author hanyu
 */
public class LoadActivity extends BaseActivity {

    /**
     * 当Activity打开执行的 函数
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.removeTitle();
        setContentView(R.layout.activity_load);//加载布局   布局等资源文件都存放在res文件夹里
        new RunNet(this).execute(Key.FindKey);//尝试连接网络 RunNet是该类的内部类
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);//加载菜单
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_down:
                Intent intent1 = new Intent(LoadActivity.this, DLListActivity.class);
                startActivity(intent1);
                break;
            case R.id.app_setting:
                //启动另一个Activity
                Intent intent2 = new Intent(LoadActivity.this, SettingActivity.class);
                startActivityForResult(intent2, 2);
                break;
            case R.id.app_quit:
                break;
            default:
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Android程序设计中不允许在
     * 主线程中作出尝试网络连接等耗时长的
     * 阻塞主线程的行为
     * <p>
     * AsyncTask 正是Android为耗时操作提供
     * 的执行类
     *
     * @author hanyu
     */
    class RunNet extends AsyncTask<Object, Object, Boolean> {

        /**
         * 构造器
         *
         * @param load
         */
        public RunNet(LoadActivity load) {
            WifiManager wm = (WifiManager) load.getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);//获取Android WIFI管理工具
            App.readWifiInfo(wm);//根据WIFI读取网络信息
        }

        public RunNet(String ip) {
            App.HostIp(ip);
        }

        /**
         * 在后台线程中尝试连接主机
         * Android指定在该函数中执行耗时操作
         */
        @Override
        protected Boolean doInBackground(Object... params) {

            if (Integer.valueOf(String.valueOf(params[0])) == Key.FindKey)
                if (App.findHost()) {
                    App.getSession().connectTest();
                    Log.i("conn", "Success");
                    return true;
                } else {
                    Log.i("conn", "failed");
                    return false;
                }
            else if (Integer.valueOf(String.valueOf(params[0])) == Key.ScanKey) {
                if (App.scanHost()) {
                    App.getSession().connectTest();
                    Log.i("conn", "Success");
                    return true;
                } else {
                    Log.i("conn", "failed");
                    return false;
                }
            }
            return false;
        }

        /**
         * doInBackground执行完之后
         * 返回值会传入该函数(也就是方法)中
         * <p>
         * Android指定在该函数中执行涉及UI操作
         */
        @Override
        protected void onPostExecute(Boolean result) {
            Button conBtn = (Button) findViewById(R.id.button_ew);
            conBtn.setOnClickListener((v) -> {
                new RunNet(App.getGateWay()).execute(Key.ScanKey);
                conBtn.setVisibility(View.GONE);
            });
            Button pushBtn = (Button) findViewById(R.id.btn_win);
            Button fileBtn = (Button) findViewById(R.id.btn_file);
            pushBtn.setOnClickListener((v) -> {
                Intent intent = new Intent(LoadActivity.this, PushActivity.class);
                if (!App.getSession().isConnected()) {
                    toastMsg(getBaseContext(), ConstStrings.Ununited);
                    pushBtn.setVisibility(View.GONE);
                    fileBtn.setVisibility(View.GONE);
                    conBtn.setVisibility(View.VISIBLE);
                }
                startActivity(intent);
            });
            fileBtn.setOnClickListener((v) -> {
                Intent intent = new Intent(LoadActivity.this, FileActivity.class);
                if (!App.getSession().isConnected()) {
                    toastMsg(getBaseContext(), ConstStrings.ConnFailed);
                    pushBtn.setVisibility(View.GONE);
                    fileBtn.setVisibility(View.GONE);
                    conBtn.setVisibility(View.VISIBLE);
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable(Key.FileListKey,(Serializable)App.getSession().getFileList(null));
                intent.putExtras(bundle);
                startActivity(intent);
            });
            if (result) {//如果成功连接,则主界面初始化 "我的电脑","推送文件" 这两个按钮
                pushBtn.setVisibility(View.VISIBLE);
                fileBtn.setVisibility(View.VISIBLE);
                conBtn.setVisibility(View.GONE);
                toastMsg(getBaseContext(), ConstStrings.Connected);
            } else {//若连接失败 显示"扫描主机"按钮
                pushBtn.setVisibility(View.GONE);
                fileBtn.setVisibility(View.GONE);
                conBtn.setVisibility(View.VISIBLE);
                toastMsg(getBaseContext(), ConstStrings.ConnFailed);
            }
        }
    }

}
