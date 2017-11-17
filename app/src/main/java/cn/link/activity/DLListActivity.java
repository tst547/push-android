package cn.link.activity;

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.link.box.App;
import cn.link.box.ConstStrings;
import cn.link.box.Key;
import cn.link.common.MyMath;
import cn.link.net.download.DownLoadMsg;
import cn.link.net.download.DownLoadTask;
import com.daimajia.numberprogressbar.NumberProgressBar;

/**
 * 下载列表Activity
 * @author hanyu
 */
public class DLListActivity extends BaseActivity {

	ListView lsv;// 列表view

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.removeTitle();
		setContentView(R.layout.activity_down_list);
		lsv = (ListView) findViewById(R.id.down_load_list);
		lsv.setAdapter(new CustomAdapter(App.downloadMsgs));
		lsv.setOnItemClickListener((adapterView, view, pos, n) -> {
			//todo 暂停or继续任务下载
		});
		lsv.setOnItemLongClickListener((adapterView, view, pos, n) -> {
			//todo 暂停or继续任务下载
			return true;
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	class CustomAdapter extends BaseAdapter {

		private List<DownLoadMsg> loadMsg;

		protected CustomAdapter(List<DownLoadMsg> loadMsg) {
			this.loadMsg = loadMsg;
		}

		@Override
		public int getCount() {
			return loadMsg.size();
		}

		@Override
		public Object getItem(int position) {
			return loadMsg.get(position);
		}

		@Override
		public long getItemId(int position) {
			return loadMsg.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			DownLoadMsg loadMsg = (DownLoadMsg) getItem(position);
			LayoutInflater inflater = DLListActivity.this.getLayoutInflater();
			View view = inflater.inflate(R.layout.down_item, null);
			TextView text = (TextView) view.findViewById(R.id.down_item_text);
			NumberProgressBar pro = (NumberProgressBar) view.findViewById(R.id.down_pro);
			text.setText(loadMsg.getFile().getName()
					+"   "
					+MyMath.divide(loadMsg.getProgress().getMax(), Key.MB, ConstStrings.DivideFormat)
					+ConstStrings.FileUnits);
			pro.setProgress(loadMsg.getProgress().getCurrent());
			return view;
		}
	}
}
