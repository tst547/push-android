package cn.link.beans;
/**
 * 主机信息
 * @author hanyu
 *
 */
public class HostInfo {

	private String ip;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public HostInfo(String ip) {
		super();
		this.ip = ip;
	}

	public HostInfo() {
		super();
	}

	@Override
	public String toString() {
		return ip;
	}
	
	
}
