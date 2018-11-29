package cn.link.activity;


import android.content.Context;
import android.view.MenuItem;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import cn.link.box.ConstStrings;

public abstract class BaseActivity extends Activity{

	protected Intent intent;//获取Activity传递过来的数据
	
	/**
	 * 将当前Activity添加到App类中
	 * 该操作跟onRestart方法中不同
	 * 仅当Activity创建时才需要添加
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intent = getIntent();
	}

	/**
	 * 当前Activity销毁时
	 * 将App中对该Activity的引用
	 * 销毁
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 上层Activity移除
	 * 该Activity返回栈顶时
	 * 类App中设置该Activity为
	 * 最前端Activity
	 */
	@Override
	protected void onRestart() {
		super.onRestart();
	}

	/**
	 * 移除手机App头部的标题栏及顶部的状态栏
	 */
	public void removeTitle() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	
	/**
	 * 加载一个信息对话框,显示给定的标题及内容
	 * @param head
	 * @param msg
	 */
	protected void dialogShowMsg(String head,String msg) {
		AlertDialog.Builder builder = new Builder(BaseActivity.this);
		builder.setMessage(msg);
		builder.setTitle(head);
		builder.setPositiveButton(ConstStrings.Confirm, (dialog, which) ->dialog.cancel());
		builder.create().show();
	}

	/**
	 * 加载一条信息提示
	 * @param context
	 * @param msg
	 */
	protected void toastMsg(Context context, String msg){
		Toast.makeText(context, msg, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.app_down:
				Intent intent1 = new Intent(this, DownListActivity.class);
				startActivity(intent1);
				break;
			case R.id.app_setting:
				Intent intent2 = new Intent(this, SettingActivity.class);
				startActivityForResult(intent2, 2);
				break;
			case R.id.app_quit:
				break;
			default:
		}
		return true;
	}

}
