package com.example.myapplication.callback;

public interface CallBack {

    // 初始化成功
    void initSuccess();

    // 情景
    void DramaOperation(int index);

    // 电量
    void dianliang(int CarIndex);

    // 检查车辆正常
    void CarStateOK(int carIndex);


    //
    void DramaFinish(int dramaIndex);




}
