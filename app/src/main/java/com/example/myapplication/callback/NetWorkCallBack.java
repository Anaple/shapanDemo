package com.example.myapplication.callback;

import java.net.Socket;

public interface NetWorkCallBack {
    //连接成功
    void success(Socket socket);


    //连接失败
    void error();
}
