package cn.link.activity;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.link.box.App;
import cn.link.box.ConstStrings;
import cn.link.box.Key;
import cn.link.common.MyMath;
import cn.link.net.IOStream;
import cn.link.net.download.DownLoadMsg;
import cn.link.net.download.ProgressTask;

/**
 * 下载列表Activity
 *
 * @author hanyu
 */
public class DownListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.removeTitle();
        App.viewList.clear();
        setContentView(R.layout.activity_down_list);
        ListView lsv = findViewById(R.id.down_load_list);
        lsv.setAdapter(new CustomAdapter(App.downloadMsgs, App.viewList));
        lsv.setOnItemClickListener((adapterView, view, pos, n) -> {
            if (adapterView.getItemAtPosition(pos) instanceof DownLoadMsg) {
                DownLoadMsg loadMsg = (DownLoadMsg) adapterView.getItemAtPosition(pos);
                if (loadMsg.getProgress().getCurrent()<100)
                    if (loadMsg.isRunFlag()){
                        loadMsg.setRunFlag(false);
                    }else{
                        loadMsg.setRunFlag(true);
                        IOStream.down(loadMsg);
                    }

            }
        });
        lsv.setOnItemLongClickListener((adapterView, view, pos, n) -> {

            //todo 暂停or继续任务下载
            return true;
        });
        for (DownLoadMsg loadMsg : App.downloadMsgs) {
            ProgressTask task = new ProgressTask(loadMsg);
            App.taskList.add(task);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    /**
     * Activity销毁同时
     * 销毁所有进度条刷新任务
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (ProgressTask task : App.taskList)
            task.finish().cancel(true);
        App.taskList.clear();
    }

    /**
     * ListView渲染规则
     */
    class CustomAdapter extends BaseAdapter {

        private List<DownLoadMsg> loadMsg;

        private List<View> viewList;

        protected CustomAdapter(List<DownLoadMsg> loadMsg, List<View> viewList) {
            this.loadMsg = loadMsg;
            this.viewList = viewList;
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
        public View getView(int position, View view, ViewGroup parent) {
            final ViewHolder viewHolder;//缓存View 避免重复刷新
            DownLoadMsg loadMsg = (DownLoadMsg) getItem(position);
            if (view == null) {
                viewHolder = new ViewHolder();
                LayoutInflater inflater = DownListActivity.this.getLayoutInflater();
                view = inflater.inflate(R.layout.down_item, null);
                view.setTag(viewHolder);
                TextView text = view.findViewById(R.id.down_item_text);
                ProgressBar pro = view.findViewById(R.id.down_pro);
                viewHolder.pro = pro;
                viewHolder.text = text;
                viewHolder.text.setText(loadMsg.getFile().getName()
                        + "   "
                        + MyMath.divide(loadMsg.getProgress().getMax(), Key.MB, ConstStrings.DivideFormat)
                        + ConstStrings.FileUnits);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            view.setTag(R.id.down_load_list, loadMsg.getId());
            viewHolder.pro.setProgress(loadMsg.getProgress().getCurrent());
            viewList.add(view);

            return view;
        }
    }

    /**
     * 缓存View数据
     */
    public class ViewHolder {
        public ProgressBar pro;
        public TextView text;
    }
}
