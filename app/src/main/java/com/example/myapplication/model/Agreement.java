package com.example.myapplication.model;
// socket 通讯操作封装
public class Agreement {

    // 情景控制
    public static byte[] getDrama(byte dramaindex)
    {
        byte[] data = new byte[]{0x55,0x0A,dramaindex,0x56};
        return data;
    }

    // 车辆检查
    public static byte[] getCar(byte CarIndex)
    {
        byte[] data = new byte[]{0x55,0x0C,CarIndex,0x56};
        return data;
    }


    // 查询电量
    public static byte[] getBattery(byte CarIndex)
    {
        byte[] data = new byte[]{0x55,0x0B,CarIndex,0x56};
        return data;
    }


    // 点位到达回报

    public static byte[] getDramaFinish(byte dramaIndex){
        byte[] data = new byte[]{0x55,0x0E,dramaIndex,0x56};
        return data;
    }





}
