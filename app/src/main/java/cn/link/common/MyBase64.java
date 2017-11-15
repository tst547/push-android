package cn.link.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

public class MyBase64 {
	static{
		
	}

	/**
	 * base64×Ö·û´®×ªÍ¼Æ¬
	 * @param string
	 * @return
	 */
	public static Bitmap stringtoBitmap(String base64) {
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(base64, Base64.NO_WRAP);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
					bitmapArray.length);
		} catch (Exception e) {
			Log.e("stringtoBitmap", e.getMessage());
		}
		return bitmap;
	}

}
