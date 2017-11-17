package cn.link.net.download;

import android.os.AsyncTask;
import android.view.View;
import cn.link.activity.DLListActivity;
import cn.link.activity.R;
import cn.link.box.Key;
import com.daimajia.numberprogressbar.NumberProgressBar;

import java.util.List;

public class ProgressTask extends AsyncTask<DownLoadMsg, Integer, Integer> {

    private volatile boolean run = true;

    private View v;

    private List<View> viewList;

    private DownLoadMsg loadMsg;

    public ProgressTask(List<View> viewList) {
        this.viewList = viewList;
    }

    @Override
    protected Integer doInBackground(DownLoadMsg... downLoadMsgs) {
        if (null == downLoadMsgs[0])
            return null;
        this.loadMsg = downLoadMsgs[0];
        Progress pro = loadMsg.getProgress();
        while (run) {
            publishProgress(pro.getCurrent());
            try {
                Thread.sleep(Key.ProRefresh);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        for (View view : viewList)
            if (view.getTag(R.id.down_load_list).equals(loadMsg.getId())){
                v = view;
                DLListActivity.ViewHolder viewHolder = (DLListActivity.ViewHolder) v.getTag();
                NumberProgressBar pro = viewHolder.pro;
                if (pro.getProgress() < 0)
                    pro.setProgress(0);
                if (pro.getProgress() < 100)
                    pro.setProgress(values[0]);
            }
    }

    public ProgressTask finish() {
        this.run = false;
        return this;
    }
}
