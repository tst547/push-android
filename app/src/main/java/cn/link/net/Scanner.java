package cn.link.net;

import cn.link.common.MyGson;

import java.io.IOException;
import java.net.*;

/**
 * Created by hanyu on 2017/11/15 0015.
 */
public class Scanner {

    private int port;
    private String broadcastAddr;
    private DatagramSocket socket;
    private DatagramPacket packet;

    public Scanner(String broadcastAddr,int port){
        this.broadcastAddr = broadcastAddr;
        this.port = port;
    }

    public Scanner init() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        socket.setSoTimeout(2500);
        socket.setBroadcast(true);
        byte[] buf = new byte[1];
        buf[0] = 0x01;
        InetAddress addr = InetAddress.getByName(broadcastAddr);
        packet = new DatagramPacket(buf,buf.length,addr,port);
        return this;
    }

    /**
     * 广播地址查询主机
     * @return
     * @throws IOException
     */
    public Base.BaseMsg<Base.IpMsg> conn() throws IOException {
        socket.send(packet);
        byte[] buf = new byte[2048];
        DatagramPacket recv = new DatagramPacket(buf,buf.length);
        socket.receive(recv);
        Base base = new Base();
        Base.BaseMsg<Base.IpMsg> type = base.new BaseMsg<>();
        Base.BaseMsg<Base.IpMsg> bean  = MyGson.getObject(
                new String(recv.getData() , 0 ,recv.getLength()),
                type.getClass()
        );
        return bean;
    }

    /**
     * 1-254段扫描
     * @return
     * @throws Exception
     */
    public Base.BaseMsg<Base.IpMsg> scan() throws Exception {
        socket.setSoTimeout(100);
        byte[] buff = new byte[2048];
        String temp = broadcastAddr.substring(0,broadcastAddr.lastIndexOf("."));
        byte[] buf = new byte[1];
        buf[0] = 0x01;
        for (int i=1;i<255;i++){
            String host = temp.concat(".").concat(String.valueOf(i));
            InetAddress addr = InetAddress.getByName(host);
            if (host.equals(broadcastAddr)) {
                continue;
            }
            DatagramPacket tempPacket = new DatagramPacket(buf,buf.length,addr,port);
            socket.send(tempPacket);
            DatagramPacket recv = new DatagramPacket(buf,buf.length);
            try {
                socket.receive(recv);
                Base base = new Base();
                Base.BaseMsg<Base.IpMsg> type = base.new BaseMsg<>();
                Base.BaseMsg<Base.IpMsg> bean  = MyGson.getObject(
                        new String(recv.getData() , 0 ,recv.getLength()),
                        type.getClass()
                );
                return bean;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        throw new Exception("can't find host");
    }


}
