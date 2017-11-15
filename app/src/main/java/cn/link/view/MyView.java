package cn.link.view;

import cn.link.client.HandleClient;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MyView extends ImageView {

	private float bitmapX;
	private float bitmapY;
	
	private int width;
	private int height;
	
	private int resid;
	private HandleClient hc;
	private int key;
	private Bitmap bitmap;
	
	public MyView(Context context, float x, float y,Bitmap bitmap,HandleClient hc) {
		super(context);
		this.bitmapX = x;
		this.bitmapY = y;
		this.hc = hc;
		this.bitmap = bitmap;
		height = (int) (bitmap.getHeight()+bitmapY);
		width = (int) (bitmap.getWidth()+bitmapX);
	}
	
	public boolean isTouch(int x,int y){
		int view_x = (int) bitmapX;
		int view_y = (int) bitmapY;
		if(x>view_x&&x<width)
			if(y>view_y&&y<height)
				return true;
		return false;
	}
	
	public void click(){
		hc.out(String.valueOf(key));
	}

	/**
	 * 重写View类的onDraw()方法
	 */
    @SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //创建,并且实例化Paint的对象
        Paint paint = new Paint();
        //绘制
        canvas.drawBitmap(bitmap, bitmapX, bitmapY,paint);
        //判断图片是否回收,木有回收的话强制收回图片
        if(bitmap.isRecycled())
        {
            bitmap.recycle();
        }
         
    }
    
}
