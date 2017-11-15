package cn.link.net;

import java.util.Map;

/**
 * Created by hanyu on 2017/11/15 0015.
 */
public class Base {

    public class BaseMsg<T>{
        public int err;//是否报错
        public T msg;//信息
    }

    public class IpMsg{
        public String addr;//地址
        public int port;//端口
    }

    public class File{
        public String name;//文件名
        public String Path;//路径
        public String size;//大小
        public boolean isDir;//是否是目录
    }
}
