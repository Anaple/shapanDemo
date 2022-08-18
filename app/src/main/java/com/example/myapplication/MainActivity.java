package com.example.myapplication;

import static android.util.TypedValue.COMPLEX_UNIT_DIP;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.percentlayout.widget.PercentRelativeLayout;
import android.annotation.SuppressLint;


import android.media.MediaPlayer;
import android.media.session.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.myapplication.config.UIOperation;
import com.example.myapplication.model.Agreement;
import com.example.myapplication.model.MyServer;
import com.example.myapplication.bean.ConnectedCarBean;
import com.example.myapplication.callback.CallBack;
import com.example.myapplication.callback.NetWorkCallBack;

import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    public PercentRelativeLayout NetworkCheck;
    public PercentRelativeLayout CarCheck;


    public ListView DramaList;
    public VideoView dramaVideo;
    public TextView carBattery;

    public static ArrayList<ConnectedCarBean> connectedCarArr = new ArrayList<>();


    public final static int [] VIDEO_SRC={R.raw.test,R.raw.test2};

    @SuppressLint("HandlerLeak")
    public final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            DramaAdapter.notifyDataSetChanged();
        }
    };

    @SuppressLint("HandlerLeak")
    public final Handler handlerVideo = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            dramaVideo.setVideoURI(Uri.parse("android.resource://" + getPackageName() + "/" +VIDEO_SRC[msg.what-1] ));
            Toast.makeText(MainActivity.this, "播放场景:"+ "" + msg.what, Toast.LENGTH_SHORT).show();
            dramaVideo.start();
        }
    };

    @SuppressLint("HandlerLeak")
    public Handler handler2 = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what > 0) {
                //有效车辆
                Toast.makeText(MainActivity.this, getResources().getString(R.string.car_connected) + "" + msg.what, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.no_car_connected), Toast.LENGTH_SHORT).show();
            }
        }
    };

    @SuppressLint("HandlerLeak")
    public  Handler handler3 = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.cant_use_car) + "", Toast.LENGTH_SHORT).show();
        }
    };

    @SuppressLint("HandlerLeak")
    public  Handler handler4 = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            carBattery.setText(msg.what+"%");
            carBattery.setTextSize(COMPLEX_UNIT_DIP,50);
            Toast.makeText(MainActivity.this, msg.what+ "%", Toast.LENGTH_SHORT).show();
        }
    };


    //网络检查

    public NetWorkCallBack netWorkCallBack = new NetWorkCallBack() {
        @Override
        public void success(Socket socket) {

            Log.i("SOCKET", getResources().getString(R.string.network_success));
            MyServer.MySocket = socket;
            try {
                socket.getOutputStream().write(Agreement.getDramaState());
            } catch (Exception e) {
                Log.e("CarClick", "SOCKET", e);
            }
        }

        @Override
        public void error() {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    };

    //Socket通讯回调
    public CallBack callBack = new CallBack() {
        @Override
        public void initSuccess() {
            CurrentDrama = -1;
            handler.sendMessage(new Message());
        }

        @Override
        public void DramaOperation(int index) {
            if (index == 6) {
                CurrentDrama = -1;
                handler.sendMessage(new Message());
            } else if (index == 0) {
                CurrentDrama = -1;
                handler.sendMessage(new Message());
            } else {
                CurrentDrama = index - 1;
                handler.sendMessage(new Message());
            }

        }

        @Override
        public void dianliang(int CarIndex, byte number) {
            handler4.sendEmptyMessage(number);
        }

        @Override
        public void CarStateOK(int carIndex, int canUse) {
            if (connectedCarArr.isEmpty()) {
                connectedCarArr.add(new ConnectedCarBean(carIndex, canUse == 0,0));
                handler2.sendEmptyMessage(carIndex);
            } else {
                int flag = 0;
                for (ConnectedCarBean carBean : connectedCarArr
                ) {
                    if (carBean.getCarIndex() != carIndex) {
                        flag++;
                    } else if (carIndex == carBean.getCarIndex() && (carBean.isCanUse() != (canUse == 0))) {
                        carBean.setCanUse(canUse == 0);
                        handler2.sendEmptyMessage(carIndex);
                    }

                }
                if (flag >= connectedCarArr.size()) {
                    connectedCarArr.add(new ConnectedCarBean(carIndex, canUse == 0,0));
                    handler2.sendEmptyMessage(carIndex);
                }
            }


        }

        @Override
        public void CarStateError(int carIndex) {
            //弃用
            if (connectedCarArr.isEmpty() || !connectedCarArr.contains(new ConnectedCarBean(carIndex, false,0))) {
            }

        }

        @Override
        public void DramaSuccess(int index) {
            if (index != -2) {
                CurrentDrama = index;
                handler.sendMessage(new Message());
                try {
                    MyServer.MySocket.getOutputStream().write(Agreement.getDramaIndex());
                } catch (Exception e) {
                    Log.e("DRAMA", "SOCKET", e);
                }
            } else if (index == 12) {
                CurrentDrama = -1;
                handler.sendMessage(new Message());
            }
        }
        @Override
        public void DramaFinish(int DramaIndex){

            handlerVideo.sendEmptyMessage(DramaIndex);

        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UIOperation.SetFullScreen(this);
        initView();
        initOnClickListener();
        MyServer.BeginConnection(netWorkCallBack);
        MyServer.Begin(callBack);

    }

    private void initView() {
        DramaList = findViewById(R.id.drama_choose);
        dramaVideo = findViewById(R.id.drama_video);
        NetworkCheck = findViewById(R.id.network_check);
        CarCheck = findViewById(R.id.car_check);
        carBattery = findViewById(R.id.car_battery);
        DramaList.setAdapter(DramaAdapter);

    }

    //系统命令模块按钮
    private void initOnClickListener() {

        NetworkCheck.setOnClickListener(view -> {
            if (MyServer.MySocket != null) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.network_success), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                connectedCarArr.clear();
            }
        });
        CarCheck.setOnClickListener(view -> {
            if (MyServer.MySocket != null) {
                new Thread(() -> {

                    try {
                        for (int i = 0; i < 3; i++) {
                            MyServer.MySocket.getOutputStream().write(Agreement.getCar((byte) i));

                        }
                    } catch (Exception e) {
                        Log.e("CarClick", "SOCKET", e);
                    }
                }).start();

            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();

                //TEST
                callBack.CarStateOK(1, 0);

            }
        });


    }


    //情景部分
    public static int CurrentDrama = 6;
    // 情景列表
    public BaseAdapter DramaAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return CurrentDrama;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("SetTextI18n")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint("ViewHolder")
            View view = View.inflate(MainActivity.this, R.layout.item_drama, null);
            TextView img = view.findViewById(R.id.image_ico);
            ImageView OnOff = view.findViewById(R.id.on_off);
            TextView dramaContent = view.findViewById(R.id.drama_content);
            TextView drama = view.findViewById(R.id.drama);


            OnOff.setOnClickListener(v -> {
                if (MyServer.MySocket != null)
                {
                    CurrentDrama = position;

                   // DramaAdapter.notifyDataSetChanged();

                    new Thread(() -> {
                        try {
                            byte[] data = Agreement.getDrama((byte) (position + 1));
                            MyServer.MySocket.getOutputStream().write(data);

                            CurrentDrama = 6;
                          //  DramaAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            Log.e("SYSTEM_CLICK", "SOCKET", e);
                        }
                    }).start();
                } else {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.drama_wait), Toast.LENGTH_SHORT).show();
                }
            });
            switch (position) {
                case 0:
                    img.setText("A1");
                    drama.setText("场景1：灌装测试");
                    dramaContent.setText("车辆停在起点车间位置，展示车辆下线灌装场景。沙盘场景位置建筑为车间工厂生产线模型，安装场景名称灯牌，车辆位置安装灯带，配合灌装过程变化灯光效果。");
                    break;
                case 1:
                    img.setText("A2");
                    drama.setText("场景2：车人交互场景");
                    dramaContent.setText("新车下线交到用户使用。车辆从车间蓝色点位出发，驶向场景2，停在场景2展示（黄色点）位置，触发平台端动画效果。沙盘场景位置建筑为住宅区模型，路端设立假人模型，安装场景名称灯牌。");
                    break;
                case 2:
                    img.setText("A3");
                    drama.setText("场景3：车内场景");
                    dramaContent.setText("车辆从场景2蓝色点位出发，驶向场景3，通过路口停在场景3展示（黄色点）位置（靠近沙盘边缘，方便人员操作），触发平台端动画效果，展示车内CAN过程。沙盘场景位置建筑为4S店模型，安装场景名称灯牌。");
                    break;
                case 3:
                    img.setText("A4");
                    drama.setText("场景4：车际交互场景");
                    dramaContent.setText("车辆从场景3蓝色点位出发，驶向场景4，通过路口标志点（绿色）时，触发红绿灯预警动画（车路通信，提前告知路侧设备状态），车辆驶向黄色点位置（红灯停车、绿灯正常驶过路口）。沙盘安装场景名称灯牌。");
                    break;
                case 4:
                    img.setText("A5");
                    drama.setText("场景5：车际交互场景（车车通信）");
                    dramaContent.setText("车辆从场景4继续行驶，驶向场景5，通过路口标志点（绿色）时，触发车车通信交互动画（与前方故障车通信，提前前方故障车辆，判断是否影响当前行驶路线），实际为同向邻车道，对当前路线无影响，车辆正常继续行驶。沙盘安装场景名称灯牌。");
                    break;
                case 5:
                    img.setText("A6");

                    drama.setText("场景6：车云OTA场景");
                    dramaContent.setText("车辆从场景5继续行驶，驶向场景6达到黄色点位停止，触发场景动画效果。沙盘建筑场景为停车场（停车场出入口可安装自动闸机，进出不停车自动抬杆），车辆停在车场停车位（车位可安装LED灯带，显示停车位置），安装场景名称灯牌。");
                    break;
            }
            return view;
        }
    };


}