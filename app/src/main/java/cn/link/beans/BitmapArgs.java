package cn.link.beans;
/**
 * 图像参数传输使用bean
 * @author hanyu
 *
 */
public class BitmapArgs {

	public String fliter;
	public String frame;
	
	public String getFliter() {
		return fliter;
	}
	public void setFliter(String fliter) {
		this.fliter = fliter;
	}
	public String getFrame() {
		return frame;
	}
	public void setFrame(String frame) {
		this.frame = frame;
	}
	public BitmapArgs(String fliter, String frame) {
		this.fliter = fliter;
		this.frame = frame;
	}
	public BitmapArgs() {
	}

}
