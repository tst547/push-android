package cn.link.beans;
/**
 * 文件信息
 * @author hanyu
 *
 */
public class FileInfo {

	private String name;
	private String path;
	private boolean isDir;
	private long length;
	private long current;

	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}


	public boolean isDir() {
		return isDir;
	}


	public void setDir(boolean isDir) {
		this.isDir = isDir;
	}


	public long getLength() {
		return length;
	}


	public void setLength(long length) {
		this.length = length;
	}


	public long getCurrent() {
		return current;
	}


	public void setCurrent(long current) {
		this.current = current;
	}


	public FileInfo() {
	}


	public FileInfo(String name, String path, boolean isDir, long length,
			long current) {
		this.name = name;
		this.path = path;
		this.isDir = isDir;
		this.length = length;
		this.current = current;
	}


	@Override
	public String toString() {
		return name+","+path+","+isDir;
	}
	
	
}
