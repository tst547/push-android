package cn.link.modular;

import java.util.List;

import cn.link.beans.FileInfo;

public class Progress {

	private List<FileInfo> fls;
	private long Progress;
	private long max;
	public List<FileInfo> getFls() {
		return fls;
	}
	public void setFls(List<FileInfo> fls) {
		this.fls = fls;
	}
	public long getProgress() {
		return Progress;
	}
	public void setProgress(long progress) {
		Progress = progress;
	}
	public long getMax() {
		return max;
	}
	public void setMax(long max) {
		this.max = max;
	}
	public Progress(List<FileInfo> fls, long progress, long max) {
		this.fls = fls;
		Progress = progress;
		this.max = max;
	}
	public Progress() {
	}

}
