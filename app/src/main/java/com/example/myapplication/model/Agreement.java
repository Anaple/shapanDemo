package com.example.myapplication.model;
// socket 通讯操作封装
public class Agreement {

    // 情景控制
    public static byte[] getDrama(byte dramaindex)
    {
        byte[] data = new byte[]{0x55,0x0A,dramaindex,0x56};
        return data;
    }


    // 电量检查
    public static byte[] getDianLiang(byte CarIndex)
    {
        byte[] data = new byte[]{0x55,0x05,CarIndex,0x56};
        return data;
    }

    // 车辆检查
    public static byte[] getCar(byte CarIndex)
    {
        byte[] data = new byte[]{0x55,0x01,CarIndex,0x56};
        return data;
    }

    // 查询是否执行情景中
    public static byte[] getDramaState()
    {
        byte[] data = new byte[]{0x55,0x08,0x00,0x56};
        return data;
    }


    // 查询情景
    public static byte[] getDramaIndex()
    {
        byte[] data = new byte[]{0x55,0x07,0x00,0x56};
        return data;
    }


    // 切换视角
    public static byte[] getViewControl(byte CarIndex,byte angle)
    {
        byte[] data = new byte[]{0x55,0x06,angle,0x56};
        return data;
    }

    


}
