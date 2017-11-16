package cn.link.modular;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.link.activity.R;
import cn.link.common.MyMath;
import com.daimajia.numberprogressbar.NumberProgressBar;

public class ProgressAdapter extends ArrayAdapter<DownLoadTask>{
	
	private int res;
	
	private List<RefreshCop> proList = new ArrayList<RefreshCop>();
	
	public ProgressAdapter(Context context,
			int textViewResourceId, List<DownLoadTask> objects) {
		super(context, textViewResourceId, objects);
		this.res = textViewResourceId;
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DownLoadTask dt = getItem(position);
		View view = LayoutInflater.from(getContext()).inflate(res, null);
		ImageView img = (ImageView) view.findViewById(R.id.down_icon);
		TextView text = (TextView) view.findViewById(R.id.down_item_text);
		NumberProgressBar pro = (NumberProgressBar) view.findViewById(R.id.down_pro);
		TextView pct = (TextView) view.findViewById(R.id.down_pct);
		if(dt.getProgress().getFls().size()!=1){
			img.setImageResource(R.drawable.folder_new2);
		}else{
			img.setImageResource(R.drawable.ex_new2);
		}
		long current = dt.getProgress().getProgress();
		long max = dt.getProgress().getMax();
		text.setText(dt.getFile().getName()+"   "+MyMath.divide(max, 1048576, "0.00")+"mb");
		if(current!=0){
			pct.setText(MyMath.divideMax100(current, max)+"%");
		}else if(current==0){
			pct.setText("0%");
		}
		if(current==max){
			pro.setProgress(100);
		}else{
			int last_progress = MyMath.divideMax100(pro.getProgress(),pro.getMax());
			int current_progress = MyMath.divideMax100(current, max);
			if(current_progress>last_progress){
				pro.incrementProgressBy(current_progress-last_progress);
			}
		}
		if(current!=max)
			proList.add(new RefreshCop(dt.getProgress(), pro));
		return view;
	}
	
	public List<RefreshCop> getRunProList(){
		return proList;
	}
	
}
