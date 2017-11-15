package cn.link.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import cn.link.beans.BitmapArgs;
import cn.link.beans.Msg;
import cn.link.box.App;
import cn.link.common.MyGson;
/**
 * 设置图像 Activity
 * @author hanyu
 *
 */
public class SettingActivity extends BaseActivity{
	Spinner sp_fliter;
	Spinner sp_frame;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.removeTitle();
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_set);
		intent = getIntent();
		sp_fliter = (Spinner) findViewById(R.id.sp_fliter);
		sp_fliter.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int pos, long arg3) {
				App.fliter = getResources().getStringArray(R.array.filter)[pos];
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		sp_frame = (Spinner) findViewById(R.id.sp_frame);
		sp_frame.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int pos, long arg3) {
				App.frame = getResources().getStringArray(R.array.frame)[pos];
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		Button btn = (Button) findViewById(R.id.btn_submit);
		btn.setOnClickListener(new SetOnClick());
	}

	@Override
	public String invoke(String msg) {
		this.finish();
		return null;
	}

	class SetOnClick implements OnClickListener{
		@Override
		public void onClick(View v) {
			String argsJson = MyGson.getString(new BitmapArgs(App.fliter, App.frame));
			App.sendMsg(MyGson.getString(new Msg("frame", argsJson)));
			Log.i("set activity", "submitted");
		}

	}

}
