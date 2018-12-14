package cn.link.net;
/**
 * 网络配置信息
 */
public class NetWorkInfo {

    private long ip;//自身ip地址
    private int scanPort;//广播扫描端口
    private long broadcastAddr;// 广播地址
    private long hostIp;// 主机IP
    private int hostPort;//  主机服务端口
    private long gateWay;// 网关IP
    private long netmask;//子网掩码

    public long getIp() {
        return ip;
    }

    public void setIp(long ip) {
        this.ip = ip;
    }

    public int getScanPort() {
        return scanPort;
    }

    public void setScanPort(int scanPort) {
        this.scanPort = scanPort;
    }

    public long getBroadcastAddr() {
        return broadcastAddr;
    }

    public void setBroadcastAddr(long broadcastAddr) {
        this.broadcastAddr = broadcastAddr;
    }

    public long getHostIp() {
        return hostIp;
    }

    public void setHostIp(long hostIp) {
        this.hostIp = hostIp;
    }

    public int getHostPort() {
        return hostPort;
    }

    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }

    public long getGateWay() {
        return gateWay;
    }

    public void setGateWay(long gateWay) {
        this.gateWay = gateWay;
    }

    public long getNetmask() {
        return netmask;
    }

    public void setNetmask(long netmask) {
        this.netmask = netmask;
    }

    public void setBroadcastAddr(long netmask, long hostIp) {
        this.broadcastAddr = ~netmask|hostIp;
    }
}
