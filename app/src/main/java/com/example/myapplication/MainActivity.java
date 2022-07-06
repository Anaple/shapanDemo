package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.percentlayout.widget.PercentRelativeLayout;
import android.annotation.SuppressLint;


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

import com.example.myapplication.config.UIOperation;
import com.example.myapplication.model.Agreement;
import com.example.myapplication.model.MyServer;
import com.example.myapplication.bean.ConnectedCarBean;
import com.example.myapplication.callback.CallBack;
import com.example.myapplication.callback.NetWorkCallBack;

import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    public PercentRelativeLayout SystemCheck;
    public PercentRelativeLayout NetworkCheck;
    public PercentRelativeLayout CarCheck;

    public ListView DramaList;
    public ListView CarControlList;

    public static ArrayList<ConnectedCarBean> connectedCarArr = new ArrayList<>();


    @SuppressLint("HandlerLeak")
    public final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            DramaAdapter.notifyDataSetChanged();
        }
    };

    @SuppressLint("HandlerLeak")
    public Handler handler2 = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what > 0) {
                //有效车辆
                CarControlAdapter.notifyDataSetChanged();
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
            if (index == 12) {
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
        public void dianliang(int CarIndex, int number) {

        }

        @Override
        public void CarStateOK(int carIndex, int canUse) {
            if (connectedCarArr.isEmpty()) {
                connectedCarArr.add(new ConnectedCarBean(carIndex, canUse == 0));
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
                    connectedCarArr.add(new ConnectedCarBean(carIndex, canUse == 0));
                    handler2.sendEmptyMessage(carIndex);
                }
            }


        }

        @Override
        public void CarStateError(int carIndex) {
            //弃用
            if (connectedCarArr.isEmpty() || !connectedCarArr.contains(new ConnectedCarBean(carIndex, false))) {
                //connectedCarArr.add(new ConnectedCarBean(carIndex, false));
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
        SystemCheck = findViewById(R.id.system_check);
        CarControlList = findViewById(R.id.car_list);
        NetworkCheck = findViewById(R.id.network_check);
        CarCheck = findViewById(R.id.car_check);
        DramaList.setAdapter(DramaAdapter);
        CarControlList.setAdapter(CarControlAdapter);
    }

    //系统命令模块按钮
    private void initOnClickListener() {

        NetworkCheck.setOnClickListener(view -> {
            if (MyServer.MySocket != null) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.network_success), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                connectedCarArr.clear();
                CarControlAdapter.notifyDataSetChanged();


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
                callBack.CarStateOK(2, 0);
                callBack.CarStateOK(3, 1);
                callBack.CarStateOK(4, 1);


            }
        });
    }


    //情景部分
    public static int CurrentDrama = 12;
    // 情景列表
    public BaseAdapter DramaAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return 12;
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
            ImageView img = view.findViewById(R.id.image_ico);
            ImageView OnOff = view.findViewById(R.id.on_off);
            TextView dramaContent = view.findViewById(R.id.drama_content);
            TextView drama = view.findViewById(R.id.drama);
            if (position <= CurrentDrama) {
                OnOff.setImageResource(R.drawable.off);
                DramaAdapter.notifyDataSetChanged();
            }

            OnOff.setOnClickListener(v -> {

                if (MyServer.MySocket != null) //&& position>CurrentDrama)
                {
                    CurrentDrama = position;
                    DramaAdapter.notifyDataSetChanged();

                    new Thread(() -> {
                        try {
                            byte[] data = Agreement.getDrama((byte) (position + 1));
                            MyServer.MySocket.getOutputStream().write(data);
                            CurrentDrama = 12;
                            DramaAdapter.notifyDataSetChanged();
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
                    img.setImageResource(R.drawable.drama1);
                    drama.setText("场景1：道路危险状况提示");
                    dramaContent.setText("蓝色主车停车场起点。达到路口红色点位开启预警提醒（同时开启雨雾模拟），车辆驶过湿滑路段，经过绿色点位场景结束。");
                    break;
                case 1:
                    img.setImageResource(R.drawable.drama2);
                    drama.setText("场景2：绿波车速引导");
                    dramaContent.setText("蓝色主车继续行驶。达到路口红色点位开启绿波车速引导提醒（提示当前红绿灯状态），车辆根据红绿灯状态驶过路口（红灯减速停止、绿灯正常通行），经过绿色点位场景结束。");
                    break;
                case 2:
                    img.setImageResource(R.drawable.drama3);
                    drama.setText("场景3：车内标牌");
                    dramaContent.setText("蓝色主车继续行驶。达到路口红色点位开启车内标牌提醒，车辆驶过路口，经过绿色点位场景结束。");
                    break;
                case 3:
                    img.setImageResource(R.drawable.drama4);
                    drama.setText("场景4：弱势交通参与者碰撞预警");
                    dramaContent.setText("蓝色主车继续行驶。达到路口红色点位开启弱势交通参与者碰撞预警提示（同时启动假人装置），车辆减速慢行停止到假人前，待假人复原，继续正常驶过路段，经过绿色点位场景结束。");
                    break;
                case 4:
                    img.setImageResource(R.drawable.drama5);
                    drama.setText("场景5：闯红灯预警");
                    dramaContent.setText("蓝色主车继续行驶。达到路口红色点位将前方路口路灯置为红灯（确保前方为红灯），开启闯红灯预警提示，车辆减速慢行停止，待路灯变绿，车辆继续正常驶过路口，经过绿色点位场景结束。");
                    break;
                case 5:
                    img.setImageResource(R.drawable.drama7);
                    drama.setText("场景6：限速预警");
                    dramaContent.setText("蓝色主车继续行驶。达到路口红色点位驶入南站辅路，开启限速预警提示（限速30），车辆减速慢行驶过路段，经过绿色点位场景结束。");
                    break;
                case 6:
                    img.setImageResource(R.drawable.drama8);
                    drama.setText("场景7：左转辅助");
                    dramaContent.setText("蓝色主车停车场起点。达到路口红色点位开启预警提醒（同时开启雨雾模拟），车辆驶过湿滑路段，经过绿色点位场景结束。");
                    break;
                case 7:
                    img.setImageResource(R.drawable.drama9);
                    drama.setText("场景8：交叉口碰撞预警");
                    dramaContent.setText("蓝色主车、绿色副车1继续行驶，达到路口红色点位开启交叉口碰撞预警，两辆车交叉驶入路口，副车1停在蓝色点位等待主车通过绿色点位，启动副车1驶回到最初位置。");
                    break;
                case 8:
                    img.setImageResource(R.drawable.drama10);
                    drama.setText("场景9：异常车辆提醒");
                    dramaContent.setText("蓝色主车继续行驶，达到路口红色点位开启异常车辆提醒，前方为预设好的静止车辆副车2，主车达到绿色点位停止在副车2后，场景结束。");
                    break;
                case 9:
                    img.setImageResource(R.drawable.drama11);
                    drama.setText("场景10：前向碰撞预警");
                    dramaContent.setText("启动蓝色主车和副车2继续行驶，开启前向碰撞预警提醒，副车2经过蓝色点位时关闭提醒。");
                    break;
                case 10:
                    img.setImageResource(R.drawable.drama12);
                    drama.setText("场景11：紧急制动预警");
                    dramaContent.setText("启动蓝色主车和副车2继续行驶，副车2在前，达到蓝色点位停止，主车开启紧急制动预警，主车减速停止达到绿色点位停止在副车2后，场景结束。");
                    break;
                case 11:
                    img.setImageResource(R.drawable.drama6);
                    drama.setText("场景12：近场支付");
                    dramaContent.setText("启动蓝色主车和副车2继续行驶，副车2在前驶回最初位置，主车驶向停车场，达到停车场门口开启闸机和近场支付场景动画，达到停车位，场景结束。");
                    break;
            }
            return view;
        }
    };

    // 车辆控制
    public BaseAdapter CarControlAdapter = new BaseAdapter() {

        @Override
        public int getCount() {
            return connectedCarArr.size();
        }

        @Override
        public ConnectedCarBean getItem(int position) {
            return connectedCarArr.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            @SuppressLint("ViewHolder")

            View view = View.inflate(MainActivity.this, R.layout.item_car, null);
            ImageView carImg = view.findViewById(R.id.car);
            ImageView ImgTwoD = view.findViewById(R.id.img_two_d);
            ImageView ImgSixty = view.findViewById(R.id.img_sixty);
            ImageView ImgNinety = view.findViewById(R.id.img_ninety);
            ImageView ImgThirty = view.findViewById(R.id.img_thirty);

            PercentRelativeLayout TwoView = view.findViewById(R.id.two_view);
            PercentRelativeLayout SixView = view.findViewById(R.id.sixty);
            PercentRelativeLayout NineView = view.findViewById(R.id.ninety);
            PercentRelativeLayout ThirtyView = view.findViewById(R.id.thirty);

            TwoView.setOnClickListener(view1 -> new Thread(() -> {
                try {
                    if (getItem(position).isCanUse()) {
                        MyServer.MySocket.getOutputStream().write(Agreement.getViewControl((byte) (getItem(position).getCarIndex()), (byte) 0));
                    }

                } catch (Exception e) {
                    Log.e("CAR_CLICK", "SOCKET", e);
                }

            }).start());

            SixView.setOnClickListener(view11 -> new Thread(() -> {
                try {

                    if (getItem(position).isCanUse()) {
                        MyServer.MySocket.getOutputStream().write(Agreement.getViewControl((byte) (getItem(position).getCarIndex()), (byte) (4 + ((getItem(position).getCarIndex()-1) * 3))));
                    }else {
                        handler3.sendEmptyMessage(0);
                    }

                } catch (Exception e) {
                    Log.e("CAR_CLICK", "SOCKET", e);
                }

            }).start());

            NineView.setOnClickListener(view12 -> new Thread(() -> {

                try {
                    if (getItem(position).isCanUse()) {
                        MyServer.MySocket.getOutputStream().write(Agreement.getViewControl((byte) (getItem(position).getCarIndex()), (byte) (5 + ((getItem(position).getCarIndex()-1) * 3))));
                    }else {
                        handler3.sendEmptyMessage(0);
                    }

                } catch (Exception e) {
                    Log.e("CAR_CLICK", "SOCKET", e);
                }

            }).start());

            ThirtyView.setOnClickListener(view13 -> new Thread(() -> {
                try {
                    if (getItem(position).isCanUse()) {
                        MyServer.MySocket.getOutputStream().write(Agreement.getViewControl((byte) (getItem(position).getCarIndex()), (byte) (3 + ((getItem(position).getCarIndex()-1) * 3))));
                    }else {
                        handler3.sendEmptyMessage(0);
                    }

                } catch (Exception e) {
                    Log.e("CAR_CLICK", "SOCKET", e);
                }
            }).start());

            if (getItem(position).isCanUse() && getItem(position).getCarIndex() ==1) {
                carImg.setImageResource(R.drawable.blue_car);
                ImgTwoD.setImageResource(R.drawable.switch_two_view);
                ImgSixty.setImageResource(R.drawable.switch_watch_blue);
                ImgNinety.setImageResource(R.drawable.switch_watch_blue);
                ImgThirty.setImageResource(R.drawable.switch_two_view);
            } else if (getItem(position).isCanUse() && getItem(position).getCarIndex() == 2) {
                carImg.setImageResource(R.drawable.yellow_car);
                ImgTwoD.setImageResource(R.drawable.switch_watch_yellow);
                ImgSixty.setImageResource(R.drawable.switch_watch_yellow);
                ImgNinety.setImageResource(R.drawable.switch_watch_yellow);
                ImgThirty.setImageResource(R.drawable.switch_watch_yellow);
            } else if (getItem(position).isCanUse() && getItem(position).getCarIndex() == 3) {
                carImg.setImageResource(R.drawable.green_car);
                ImgTwoD.setImageResource(R.drawable.switch_watch_green);
                ImgSixty.setImageResource(R.drawable.switch_watch_green);
                ImgNinety.setImageResource(R.drawable.switch_watch_green);
                ImgThirty.setImageResource(R.drawable.switch_watch_green);
            } else {
                carImg.setImageResource(R.drawable.not_car);
                ImgTwoD.setImageResource(R.drawable.switch_watch_not);
                ImgSixty.setImageResource(R.drawable.switch_watch_not);
                ImgNinety.setImageResource(R.drawable.switch_watch_not);
                ImgThirty.setImageResource(R.drawable.switch_watch_not);
            }


            return view;
        }
    };


}