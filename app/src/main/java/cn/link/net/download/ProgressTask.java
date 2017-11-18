package cn.link.net.download;

import android.os.AsyncTask;
import android.view.View;
import cn.link.activity.DownListActivity;
import cn.link.activity.R;
import cn.link.box.App;
import cn.link.box.Key;
import com.daimajia.numberprogressbar.NumberProgressBar;

/**
 * 进度条刷新任务
 */
public class ProgressTask extends AsyncTask<Integer, Integer, Integer> {

    private volatile boolean run = true;

    private DownListActivity.ViewHolder viewHolder;

    private DownLoadMsg loadMsg;

    public ProgressTask(DownLoadMsg loadMsg){
        super();
        this.loadMsg = loadMsg;
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        if (null == loadMsg)
            return null;
        Progress pro = loadMsg.getProgress();
        while (run) {
            publishProgress(pro.getCurrent());
            if (pro.getCurrent()==100)
                break;
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
        if (null==viewHolder)
            for (View view : App.viewList)
                if (view.getTag(R.id.down_load_list).equals(loadMsg.getId())){
                    viewHolder = (DownListActivity.ViewHolder) view.getTag();
                }
        if (null==viewHolder)
            return;
        NumberProgressBar progressBar = viewHolder.pro;
        if (progressBar.getProgress()<values[0])
            progressBar.incrementProgressBy(values[0]-progressBar.getProgress());
    }

    public synchronized ProgressTask finish() {
        this.run = false;
        return this;
    }
}
