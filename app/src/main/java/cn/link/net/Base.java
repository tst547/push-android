package cn.link.net;

import java.io.Serializable;

/**
 * Created by hanyu on 2017/11/15 0015.
 */
public class Base {

    public class BaseMsg<T> implements Serializable{
        public int err;//是否报错
        public T msg;//信息
    }

    public class IpMsg implements Serializable{
        public int port;//端口
    }

    public class File implements Serializable{
        public String name;//文件名
        public String path;//路径
        public long size;//大小
        public boolean isDir;//是否是目录
    }
}
