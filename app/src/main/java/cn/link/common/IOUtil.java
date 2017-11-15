package cn.link.common;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Arrays;

public class IOUtil {

	public static byte[] StreamToBytes(BufferedInputStream bis)
			throws IOException {
		int i = 0;
		byte[] bsa = new byte[1024];
		while (bis.available() > 1024) {
			byte[] bs = new byte[1024];
			bis.read(bs);
			// write data to client socket channel
			Arrays.fill(bs, (byte) 0);
			if (i == 0) {
				bsa = bs;
			} else {
				bsa = byteMerger(bsa, bs);
			}
			i++;
		}
		// 处理不足512的剩余部分
		int remain = bis.available();
		byte[] last = new byte[remain];
		bis.read(last);
		bis.close();
		return byteMerger(bsa, last);
	}

	public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
		byte[] byte_3 = new byte[byte_1.length + byte_2.length];
		System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
		System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
		return byte_3;
	}
}
