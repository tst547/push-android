package cn.link.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.link.beans.FileInfo;
import cn.link.beans.Msg;
import cn.link.box.App;
import cn.link.common.CHexConver;
import cn.link.common.MyGson;
import cn.link.common.MyMath;
import cn.link.modular.DownLoadTask;
import cn.link.modular.Progress;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class FileActivity extends BaseActivity {

	private Gson gson = new Gson();// 谷歌的JSON处理工具
	private ListView lv;// 列表
	private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();// 定义显示内容
	private SimpleAdapter simpleAdapter = null; // 进行数据的转换操作
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.removeTitle();
		setContentView(R.layout.file_layout);

		// 获取上一个Activity传过来的文件列表(String类型的JSON)
		String json = intent.getStringExtra("file_list");
		Msg objMsg = (Msg) MyGson.getObject(json, Msg.class);
		json = CHexConver.hexStr2Str(objMsg.getBody());
		List<FileInfo> file_list = gson.fromJson(json,
				new TypeToken<List<FileInfo>>() {
				}.getType());
		// 加载该文件列表
		initList(file_list);
		mContext = this.getBaseContext();
		handler = new Handler(mContext.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				if(null!=pro){
					switch (msg.what) {
					case PRO:
						if (pro.getProgress() >= pro.getMax()) {
							// 重新设置
							progressDialog.dismiss();// 销毁对话框
						} else {
							if (last_progress > progressDialog.getProgress()) {
								progressDialog.setProgress(last_progress);
							}
							int current_progress = MyMath.divideMax100(
									pro.getProgress(), pro.getMax());
							if (current_progress > last_progress) {
								progressDialog.incrementProgressBy(current_progress
										- last_progress);
								last_progress = current_progress;
								// 延迟发送消息
							}
							handler.sendEmptyMessageDelayed(PRO, 500);
						}
						break;
					default:
						break;
					}
				}else{
					handler.sendEmptyMessageDelayed(PRO, 500);
				}
			}
		};

		progressDialog = new ProgressDialog(this);
		progressDialog.setIcon(R.drawable.ic_launcher);
		progressDialog.setTitle("正在下载.....");
		progressDialog.setMessage("请稍后.....");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置进度条对话框//样式（水平，旋转）

		// 进度最大值
		progressDialog.setMax(100);
	}

	/**
	 * 初始化列表
	 * 
	 * @param file_list
	 */
	protected void initList(List<FileInfo> file_list) {
		lv = (ListView) findViewById(R.id.lv);
		TextView info = (TextView) findViewById(R.id.item_text);
		ImageView img = (ImageView) findViewById(R.id.icon);
		for (FileInfo fi : file_list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("item_text", fi.getName());
			map.put("path", fi.getPath());
			map.put("isDir", fi.isDir());
			map.put("length", fi.getLength());
			if (fi.isDir())
				map.put("icon", R.drawable.folder_new2);
			else
				map.put("icon", R.drawable.ex_new2);
			list.add(map);
		}
		simpleAdapter = new SimpleAdapter(this, this.list, R.layout.list_item,
				new String[] { "icon", "item_text" }, new int[] { R.id.icon,
						R.id.item_text });
		lv.setAdapter(simpleAdapter);
		lv.setOnItemClickListener(new MyItemClik());
		lv.setOnItemLongClickListener(new MyItemLongClik());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
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

	public String invoke(String msg) {
		if (msg.contains("downLoad")) {
			Msg objMsg = (Msg) MyGson.getObject(msg, Msg.class);
			StringBuilder sbd = new StringBuilder(CHexConver.hexStr2Str(objMsg
					.getBody().substring(8)));
			List<FileInfo> file_list = gson.fromJson(sbd.toString(),
					new TypeToken<List<FileInfo>>() {
					}.getType());
			try {
				DownLoadTask dt = App.getDownLoader().newTask(file_list,(String)App.getTemp("fileName"),(Boolean)App.getTemp("isDir"));
				App.getDownLoader().startTask(dt);
				if (App.getDownLoader().DownLoadQueueSize() > 1) {
					Toast.makeText(mContext, "已加入下载队列", Toast.LENGTH_SHORT);
				} else {
					pro = dt.getProgress();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Intent intent = new Intent(FileActivity.this, FileActivity.class);
			intent.putExtra("file_list", msg);
			startActivityForResult(intent, 1);
		}
		return null;
	}

	/**
	 * 点击事件
	 * 
	 * @author hanyu
	 * 
	 */
	class MyItemClik implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// 获取点击的item 向host请求该路径下信息
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) lv
					.getItemAtPosition(arg2);
			if(!(Boolean) map.get("isDir")){
				Toast.makeText(FileActivity.this, "文件没有下级目录", Toast.LENGTH_SHORT);
				return;
			}
			String msg = MyGson.getString(new Msg("file", "open:"
					+ (String) map.get("path")));
		}

	}

	/**
	 * 长按事件
	 * 
	 * @author hanyu
	 * 
	 */
	class MyItemLongClik implements OnItemLongClickListener {
		Map<String, Object> current_map;

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			current_map = (Map<String, Object>) lv.getItemAtPosition(arg2);
			AlertDialog.Builder builder = new AlertDialog.Builder(
					FileActivity.this);
			builder.setTitle("选项");
			String[] options = { "属性", "下载" };
			builder.setItems(options, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case 0:// 显示文件属性 按钮
						String head = "信息";
						String isDir = ((Boolean) current_map.get("isDir")) ? "文件夹"
								: "文件";
						String length;
						if (((Long) current_map.get("length")) != 0) {
							length = MyMath.divide(
									(Long) current_map.get("length"), 1048576,
									"0.00") + "MB";
						} else {

							length = "0MB";
						}
						String msg = "FileName: "
								+ current_map.get("item_text") + "\n"
								+ "Path: " + current_map.get("path") + "\n"
								+ "size: " + length + "\n";
						dialogShowMsg(head, msg);
						break;

					case 1:// 开始下载文件 按钮
						FileInfo fi = new FileInfo((String) current_map
								.get("item_text"), (String) current_map
								.get("path"), (Boolean) current_map
								.get("isDir"), (Long) current_map.get("length"),0);
						App.setTemp("fileName", current_map.get("item_text"));
						App.setTemp("isDir", current_map.get("isDir"));
						openDialog();
						break;
					default:
						break;
					}
					// Toast.makeText(FileActivity.this, "选择的城市为：" +
					// cities[which], Toast.LENGTH_SHORT).show();
				}
			});
			builder.show();
			return true;
		}

	}

	private int last_progress = 0;
	private Progress pro;
	private Handler handler;
	private final int PRO = 10;
	private ProgressDialog progressDialog;

	/**
	 * 加载进度条对话框 根据与FileDownload共用的progress 对象来显示下载进度
	 */
	public void openDialog() {
		/*
		 * progressDialog.setButton(ProgressDialog.BUTTON_POSITIVE,"暂停",new
		 * DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * //删除消息队列 handler.removeMessages(PRO);
		 * 
		 * } });
		 * 
		 * progressDialog.setButton(ProgressDialog.BUTTON_POSITIVE,"取消",new
		 * DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * //删除消息队列 handler.removeMessages(PRO); //恢复进度条初始值 progress=0;
		 * progressDialog.setProgress(progress); } });
		 */

		// 显示
		progressDialog.show();
		// 必须设置到show之后
		progressDialog.setProgress(0);
		// 线程
		handler.sendEmptyMessage(PRO);

	}

}
