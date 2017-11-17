package cn.link.net.download;

import android.os.AsyncTask;
import android.view.View;
import cn.link.activity.R;
import com.daimajia.numberprogressbar.NumberProgressBar;

public class DownLoadTask extends AsyncTask<DownLoadMsg,Integer,Integer>{

    private Netable netable;

    private View view;

    public DownLoadTask(View view){
        this.view = view;
    }

    public void updatePro(int current){
        publishProgress(current);
    }

    @Override
    protected Integer doInBackground(DownLoadMsg... downLoadMsgs) {
        netable.runDown(this,downLoadMsgs[0],view);
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        NumberProgressBar pro = (NumberProgressBar) view.findViewById(R.id.down_pro);
        if (pro.getProgress()<0)
            pro.setProgress(0);
        if (pro.getProgress()<100)
            pro.incrementProgressBy(
                    values[0]-pro.getProgress()>0?values[0]-pro.getProgress():pro.getProgress());
    }

    public DownLoadTask setNetable(Netable netable) {
        this.netable = netable;
        return this;
    }

    public interface Netable {

        boolean runDown(DownLoadTask downLoadTask, DownLoadMsg downLoadMsg, View view);

    }

}
