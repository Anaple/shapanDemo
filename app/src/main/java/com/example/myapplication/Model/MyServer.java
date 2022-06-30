package com.example.myapplication.Model;

import com.example.myapplication.callback.CallBack;
import com.example.myapplication.callback.NetWorkCallBack;

import java.io.InputStream;
import java.net.Socket;

public class MyServer
{

    public static String IP   = "192.168.1.139";
    public static int    PORT = 1235;
    public static Socket MySocket = null;

    public static void BeginConnection(NetWorkCallBack netWorkCallBack)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Socket socket = new Socket(IP,PORT);
                    netWorkCallBack.success(socket);
                }catch (Exception e)
                {
                    System.out.println("日志消息:"+e.getMessage());
                }
            }
        }).start();
    }

    public static void Begin(CallBack callBack)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    while (MySocket == null);
                    InputStream inputStream = MySocket.getInputStream();
                    while(true)
                    {
                        byte[] data = new byte[5];
                        inputStream.read(data);
                        // 车辆初始化
                        if(data[1] == 0 && data[1] == 0)
                        {
                            callBack.initSuccess();
                        }

                        // 情景
                        if(data[1] == 4)
                        {
                            callBack.DramaOperation(data[2]);
                        }

                        // 情景
                        if(data[1] == 8)
                        {
                            callBack.DramaSuccess(data[2]);
                        }
                        // 电量
                        if(data[1] == 5)
                        {
                            callBack.dianliang(data[2],data[3]);
                        }
                        // 车辆检查
                        if(data[1] == 1||data[1] == 2||data[1] == 3)
                        {
                            if(data[2] == 0)
                            {
                                callBack.CarStateOK(data[2]);
                            }
                            else
                            {
                                callBack.CarStateError(data[2]);
                            }
                        }
                    }
                }catch (Exception e)
                {
                }
            }
        }).start();
    }


}
