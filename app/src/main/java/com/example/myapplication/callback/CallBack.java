package com.example.myapplication.callback;

public interface CallBack {

    // 初始化成功
    void initSuccess();

    // 情景
    void DramaOperation(int index);

    // 电量
    void dianliang(int CarIndex,int number);

    // 检查车辆正常
    void CarStateOK(int carIndex,int canUse);

    // 检查车辆错误
    void CarStateError(int carIndex);

    //
    void DramaSuccess(int carIndex);



}
