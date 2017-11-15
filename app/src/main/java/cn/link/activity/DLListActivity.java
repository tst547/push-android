package cn.link.activity;

import java.util.List;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import cn.link.box.App;
import cn.link.common.MyMath;
import cn.link.modular.DownLoadTask;
import cn.link.modular.DownLoader;
import cn.link.modular.ProgressAdapter;
import cn.link.modular.RefreshCop;

/**
 * 下载列表Activity
 * 
 * @author hanyu
 * 
 */
public class DLListActivity extends BaseActivity {

	ProgressAdapter proa;// 列表适配器
	ListView lsv;// 列表view
	List<RefreshCop> proList;// 存放了进度条ProgressBar 及Progress供实时刷新进度
	RefreshPro ref;

	private final int PRO = 10;
	// private List<DownLoadTask> down_list = App.getDownLoadList();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.removeTitle();
		setContentView(R.layout.activity_down_list);
		proa = new ProgressAdapter(DLListActivity.this, R.layout.down_item, App
				.getDownLoader().TaskList());
		lsv = (ListView) findViewById(R.id.down_load_list);
		lsv.setAdapter(proa);
		lsv.setOnItemClickListener(new MyItemClik());
		proList = proa.getRunProList();

		// 开启handler 对界面上的进度条进行实时刷新
		ref = new RefreshPro();
		ref.sendEmptyMessage(PRO);
	}


	/*
	 * private Handler handler;
	 * 
	 * protected void handler() { handler = new Handler() {
	 * 
	 * @Override public void handleMessage(Message msg) { switch (msg.what) {
	 * case PRO: if(App.getDownLoader().DownLoadQueueSize()>0){
	 * Log.w("DL handler", "ListView refresh"); } break; default: break; }
	 * handler.sendEmptyMessageDelayed(PRO,600); } };
	 * handler.sendEmptyMessage(PRO); }
	 */

	@Override
	protected void onDestroy() {
		super.onDestroy();
		ref.removeMessages(PRO);
		proList.clear();
		proList = null;

	}

	/**
	 * 点击事件 下载或取消下载
	 * 
	 * @author hanyu
	 * 
	 */
	class MyItemClik implements OnItemClickListener {
		DownLoader dl = App.getDownLoader();

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// 获取点击的item 向host请求该路径下信息
			DownLoadTask dt = (DownLoadTask) lsv.getItemAtPosition(arg2);
			if (dt.isRun()) {
				dt.pause();
				dl.DownLoadQueue().remove(dt);
				Toast.makeText(getBaseContext(), "暂停下载任务", Toast.LENGTH_SHORT)
						.show();
			} else {
				dt.Run();
				dl.DownLoadQueue().add(dt);
				Toast.makeText(getBaseContext(), "开始下载任务", Toast.LENGTH_SHORT)
						.show();

			}

		}

	}

	/**
	 * 长按事件
	 * @author hanyu
	 * 
	 */
	class MyItemLongClik implements OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int arg2, long arg3) {
			DownLoadTask dt = (DownLoadTask) lsv.getItemAtPosition(arg2);
			Builder builder = new Builder(DLListActivity.this);
			builder.setMessage("确认删除吗？");
			builder.setTitle("提示");
			builder.setPositiveButton("确认", new DelClick(dt));
			builder.setNegativeButton("取消", new DialogDisClick());
			builder.create().show();
			return true;
		}

	}
	
	/**
	 * 删除按钮
	 * @author hanyu
	 *
	 */
	class DelClick implements OnClickListener{
		DownLoadTask dt;
		@Override
		public void onClick(DialogInterface dialog, int which) {
			App.getDownLoader().removeTask(dt);
			dialog.dismiss();
		}
		public DelClick(DownLoadTask dt){
			this.dt = dt;
		}
	}
	/**
	 * 取消按钮
	 * @author hanyu
	 *
	 */
	class DialogDisClick implements OnClickListener{
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	}


	/**
	 * 刷新界面上的进度条
	 * 
	 * @author hanyu
	 * 
	 */
	class RefreshPro extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case PRO:
				for (RefreshCop pro : proList) {
					int last_progress = MyMath.divideMax100(
							pro.prob.getProgress(), pro.prob.getMax());
					if (last_progress == 100) {
						proList.remove(pro);
					}
					int current_progress = MyMath.divideMax100(
							pro.pro.getProgress(), pro.pro.getMax());
					if (current_progress > last_progress) {
						pro.prob.incrementProgressBy(current_progress
								- last_progress);
						proa.notifyDataSetChanged();
						this.sendEmptyMessageDelayed(PRO, 600);// 给自身再次发送消息 实现循环
					}
				}
				if(proList.size()==0){
					return;
				}
				break;
			default:
				break;
			}
			Log.w("loop refresh", "proList size:" + proList.size());
			this.sendEmptyMessageDelayed(PRO, 600);// 给自身再次发送消息 实现循环
		}
	}
}
