package com.example.myapplication.model;

import android.util.Log;

import com.example.myapplication.callback.CallBack;
import com.example.myapplication.callback.NetWorkCallBack;

import java.io.InputStream;
import java.net.Socket;
import java.util.Arrays;

public class MyServer
{

    public static String IP   = "192.168.1.100";
    public static int    PORT = 1235;
    public static volatile Socket MySocket = null;


    //socket 协议 55 ** ** **  56
    // 55 [2] [3] 56

    // 0A 执行情景 0B 检查车辆 0C 检查电量 0D 切换视角 08检查情景
    // 默认为车号
    // 服务器信息


    // 第三位 定义
    // [2] 为车号时：
    // 00 连接成功 01 连接失败
    // [2]为场景时：
    // 00 场景初始化成功， 1-12 为每个场景是否执行完毕
    // [2] 为电量时：
    // 0-99 当前车辆电量




    public static void BeginConnection(NetWorkCallBack netWorkCallBack)
    {
        new Thread(() -> {
            try{
                Socket socket = new Socket(IP,PORT);
                netWorkCallBack.success(socket);
            }catch (Exception e)
            {
                System.out.println("日志消息:"+e.getMessage());
            }
        }).start();
    }
    public static void Begin(CallBack callBack)
    {
        new Thread(() -> {
            try{
                while (MySocket == null);
                InputStream inputStream = MySocket.getInputStream();
                while(true)
                {
                    byte[] data = new byte[5];
                    inputStream.read(data);
                    if(data.length == 1) continue;
                    // 车辆初始化
                    if(data[1] == 0)
                    {
                        callBack.initSuccess();
                    }
                    // 情景
                    if(data[1] == 10)
                    {
                        callBack.DramaOperation(data[2]);
                    }
                    // 电量
                    if(data[1] == 11)
                    {
                        callBack.dianliang(data[2]);
                    }
                    // 车辆检查
                    if(data[1] == 12)
                    {
                        callBack.CarStateOK(data[2]);
                    }
                    if(data[1] == 14){
                        callBack.DramaFinish(data[2]);
                    }

                    Log.i("SOCKET", Arrays.toString(data));

                }
            }catch (Exception e)
            {
                Log.e("ERROR","SOCKET",e);
            }
        }).start();
    }


}
