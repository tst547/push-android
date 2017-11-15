package cn.link.activity;

import java.util.List;

import com.example.qr_codescan.MipcaActivityCapture;

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
import android.widget.Toast;
import cn.link.box.App;
import cn.link.common.CHexConver;
import cn.link.common.MyGson;
import cn.link.common.WifiUtil;
/**
 * 主Activity
 * 程序打开后创建的第一个Activity
 * @author hanyu
 *
 */
public class LoadActivity extends BaseActivity{

	/**
	 * 当Activity打开执行的 函数
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.removeTitle();
		setContentView(R.layout.activity_load);//加载布局   布局等资源文件都存放在res文件夹里
		new RunNet(this).execute(App.FIND);//尝试连接网络 RunNet是该类的内部类
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
			Intent inten = new Intent(LoadActivity.this, DLListActivity.class);
			startActivity(inten);
			break;
		case R.id.app_setting:
			//启动另一个Activity
			Intent intent = new Intent(LoadActivity.this, SettingActivity.class);
			startActivityForResult(intent, 2);
			break;
		case R.id.app_quit:
			break;
		default:
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if(resultCode==RESULT_OK){
				String mobile_gateWay = App.getGateWay();
				String ipJson = CHexConver.hexStr2Str(data.getStringExtra("ip"));
				List<String> ls = MyGson.getList(ipJson, String.class);
				for(String hs: ls){
					if(WifiUtil.isSameSegment(mobile_gateWay, hs)){
						new RunNet(hs).execute("");
						return;
					}
				}
				Toast.makeText(LoadActivity.this, "没有连接wifi或与主机处于不同网段",Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	/**
	 * Android程序设计中不允许在
	 * 主线程中作出尝试网络连接等耗时长的
	 * 阻塞主线程的行为
	 * 
	 * AsyncTask 正是Android为耗时操作提供
	 * 的执行类
	 * @author hanyu
	 *
	 */
	class RunNet extends AsyncTask<Object, Object, Boolean> {

		/**
		 * 构造器
		 * @param load
		 */
		public RunNet(LoadActivity load) {
			WifiManager wm = (WifiManager)load.getSystemService(Context.WIFI_SERVICE);//获取Android WIFI管理工具
			App.readWifiInfo(wm);//根据WIFI读取网络信息
		}
		
		public RunNet(String ip) {
			App.HostIp(ip);
		}

		/**
		 * 在后台线程中尝试连接主机
		 * 
		 * Android指定在该函数中执行耗时操作
		 */
		@Override
		protected Boolean doInBackground(Object... params) {
			if (Integer.valueOf(String.valueOf(params[0]))==App.FIND)
				if (App.findHost()) {
					Log.i("conn", "Success");
					return true;
				} else {
					Log.i("conn", "failed");
					return false;
				}
			else if (Integer.valueOf(String.valueOf(params[0]))==App.SCAN){
				if (App.scanHost()) {
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
		 * 
		 * Android指定在该函数中执行涉及UI操作
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {//如果成功连接,则主界面初始化 "我的电脑","推送文件" 这两个按钮
				Button pushBtn = (Button) findViewById(R.id.btn_win);
				pushBtn.setVisibility(View.VISIBLE);
				pushBtn.setOnClickListener((v)->
					startActivity(new Intent(LoadActivity.this, PushActivity.class))
				);
				
				Button fileBtn = (Button) findViewById(R.id.btn_file);
				fileBtn.setVisibility(View.VISIBLE);
				fileBtn.setOnClickListener((v)->
						startActivity(new Intent(LoadActivity.this, FileActivity.class))
				);
				
				Button btn = (Button) findViewById(R.id.button_ew);
				btn.setVisibility(View.GONE);
				
			} else {//若连接失败,则主界面将"最前端程序","我的电脑"按钮隐藏,显示"二维码连接"按钮
				
				Button winBtn = (Button) findViewById(R.id.btn_win);
				winBtn.setVisibility(View.GONE);
				
				Button fileBtn = (Button) findViewById(R.id.btn_file);
				fileBtn.setVisibility(View.GONE);
				
				Button btn = (Button) findViewById(R.id.button_ew);
				btn.setVisibility(View.VISIBLE);

				btn.setOnClickListener((v)->
					new RunNet(App.getGateWay()).execute(App.SCAN)
				);
			}
		}

	}
	
}
