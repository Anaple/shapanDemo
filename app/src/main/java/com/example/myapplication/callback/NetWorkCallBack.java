package com.example.myapplication.callback;

import java.net.Socket;

public interface NetWorkCallBack {

    void success(Socket socket);

    void error();
}
