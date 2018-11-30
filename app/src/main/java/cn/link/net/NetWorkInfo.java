package cn.link.net;
/**
 * 网络配置信息
 */
public class NetWorkInfo {

    private int ip;//自身ip地址
    private int scanPort;//广播扫描端口
    private int broadcastAddr;// 广播地址
    private int hostIp;// 主机IP
    private int hostPort;//  主机服务端口
    private int gateWay;// 网关IP
    private int netmask;//子网掩码

    public int getBroadcastAddr() {
        return broadcastAddr;
    }

    public void setBroadcastAddr(int broadcastAddr) {
        this.broadcastAddr = broadcastAddr;
    }

    public void setBroadcastAddr(int netmask,int hostIp) {
        this.broadcastAddr = (~netmask)|hostIp;
    }

    public int getHostIp() {
        return hostIp;
    }

    public void setHostIp(int hostIp) {
        this.hostIp = hostIp;
    }

    public int getHostPort() {
        return hostPort;
    }

    public void setHostPort(int hostPort) {
        this.hostPort = hostPort;
    }

    public int getGateWay() {
        return gateWay;
    }

    public void setGateWay(int gateWay) {
        this.gateWay = gateWay;
    }

    public int getNetmask() {
        return netmask;
    }

    public void setNetmask(int netmask) {
        this.netmask = netmask;
    }

    public int getScanPort() {
        return scanPort;
    }

    public void setScanPort(int scanPort) {
        this.scanPort = scanPort;
    }

    public int getIp() {
        return ip;
    }

    public void setIp(int ip) {
        this.ip = ip;
    }
}
