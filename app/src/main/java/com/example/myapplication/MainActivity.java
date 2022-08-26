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

import com.example.myapplication.bean.DramaBean;
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
    public ImageView dramaImg;
    public TextView dramaText;
    public TextView carBattery;
    public TextView carConnect;
    public TextView carConnectIcon;

    public static ArrayList<ConnectedCarBean> connectedCarArr = new ArrayList<>();
    public static ArrayList<DramaBean> dramaBeans = new ArrayList<>();


    public void initDramaBeans(){

        dramaBeans.add(new DramaBean(R.drawable.drama_a1_icon,"车联网密码应用测试床：灌装测试床",false,false));
        dramaBeans.add(new DramaBean(R.drawable.drama_a2_icon,"车联网密码应用测试床：车人测试床",false,false));
        dramaBeans.add(new DramaBean(R.drawable.drama_a3_icon,"车联网密码应用测试床：车内测试床",false,false));
        dramaBeans.add(new DramaBean(R.drawable.drama_a4_icon,"车联网密码应用测试床：车际测试床-车路通信",false,false));
        dramaBeans.add(new DramaBean(R.drawable.drama_a5_icon,"车联网密码应用测试床：车际测试床-车车通信",false,false));
        dramaBeans.add(new DramaBean(R.drawable.drama_a6_icon,"车联网密码应用测试床：车云测试床",false,false));
    }


    public final static int [] PIC_SRC={R.drawable.deafult_drama,R.drawable.drama_pic_1,R.drawable.drama_pic_2,R.drawable.drama_pic_3,R.drawable.drama_pic_4,R.drawable.drama_pic_5,R.drawable.drama_pic_6};
    public final static int [] STRING_SRC = {R.string.deafult_text,R.string.drama_text_1,R.string.drama_text_2,R.string.drama_text_3,R.string.drama_text_4,R.string.drama_text_5,R.string.drama_text_6};
    @SuppressLint("HandlerLeak")
    public final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            dramaBeans.clear();
            initDramaBeans();
            DramaAdapter.notifyDataSetChanged();
        }
    };

    public final Handler handlerDrama = new Handler() {
        @SuppressLint("HandlerLeak")
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
            dramaText.setText(STRING_SRC[msg.what]);
            dramaImg.setImageResource(PIC_SRC[msg.what]);
        }
    };

    @SuppressLint("HandlerLeak")
    public Handler handler2 = new Handler() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what > 0) {
                //有效车辆
                carConnectIcon.setTextColor(getResources().getColor(R.color.start));
                carConnect.setText(R.string.connected);

                if (MyServer.MySocket != null) {
                    new Thread(() -> {

                        try {
                            MyServer.MySocket.getOutputStream().write(Agreement.getBattery((byte) 0));

                        } catch (Exception e) {
                            Log.e("CarClick", "SOCKET", e);
                        }
                    }).start();
                }

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
           // carBattery.setTextSize(COMPLEX_UNIT_DIP,24);
            //Toast.makeText(MainActivity.this, msg.what+ "%", Toast.LENGTH_SHORT).show();
        }
    };


    //网络检查

    public NetWorkCallBack netWorkCallBack = new NetWorkCallBack() {
        @Override
        public void success(Socket socket) {

            Log.i("SOCKET", getResources().getString(R.string.network_success));
            MyServer.MySocket = socket;
            try {

            } catch (Exception e) {
                Log.e("CarClick", "SOCKET", e);
            }
        }

        @Override
        public void error() {
            Toast.makeText(MainActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            carBattery.setText(getResources().getString(R.string.network_error));
        }
    };

    //Socket通讯回调
    public CallBack callBack = new CallBack() {
        @Override
        public void initSuccess() {
            handler.sendMessage(new Message());
        }

        @Override
        public void DramaOperation(int index) {
            if (index == 6) {

                handler.sendMessage(new Message());
            } else if (index == 0) {

                handler.sendMessage(new Message());
            } else {

                handler.sendMessage(new Message());
            }

        }

        @Override
        public void dianliang(int CarIndex) {
            handler4.sendEmptyMessage(CarIndex);
        }

        @Override
        public void CarStateOK( int canUse) {
            if (connectedCarArr.isEmpty()) {
                if(canUse == 0) {
                    handler2.sendEmptyMessage(1);
                }
            } else {
                int flag = 0;
                for (ConnectedCarBean carBean : connectedCarArr
                ) {
                    if (carBean.getCarIndex() != 1) {
                        flag++;
                    } else if (1 == carBean.getCarIndex() && (carBean.isCanUse() != (canUse == 0))) {
                        carBean.setCanUse(canUse == 0);
                        handler2.sendEmptyMessage(1);
                    }

                }
                if (flag >= connectedCarArr.size()) {
                    connectedCarArr.add(new ConnectedCarBean(1, canUse == 0,0));
                    handler2.sendEmptyMessage(1);
                }
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
        NetworkCheck = findViewById(R.id.network_check);
        CarCheck = findViewById(R.id.car_check);
        carBattery = findViewById(R.id.car_battery);
        carConnectIcon = findViewById(R.id.car_connect_ic);
        carConnect =findViewById(R.id.car_connect);
        dramaImg = findViewById(R.id.drama_picture);
        dramaText = findViewById(R.id.drama_text);
        DramaList.setAdapter(DramaAdapter);
        initDramaBeans();
        dramaText.setText(R.string.deafult_text);

    }

    //系统命令模块按钮
    private void initOnClickListener() {

        NetworkCheck.setOnClickListener(view -> {
            if (MyServer.MySocket != null) {



                Toast.makeText(MainActivity.this, getResources().getString(R.string.network_success), Toast.LENGTH_SHORT).show();
            } else {

                MyServer.BeginConnection(netWorkCallBack);
                MyServer.Begin(callBack);

                Toast.makeText(MainActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                connectedCarArr.clear();
            }
        });
        CarCheck.setOnClickListener(view -> {
            if (MyServer.MySocket != null) {
                new Thread(() -> {

                    try {
                        MyServer.MySocket.getOutputStream().write(Agreement.getCar((byte) 1));

                    } catch (Exception e) {
                        Log.e("CarClick", "SOCKET", e);
                    }
                }).start();

            } else {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();

            }
        });


    }


    //情景部分
    public  static int CurrentDrama = dramaBeans.size();
    // 情景列表
    public BaseAdapter DramaAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return dramaBeans.size();
        }

        @Override
        public DramaBean getItem(int position) {
            return dramaBeans.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint({"SetTextI18n", "ResourceType"})
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            @SuppressLint("ViewHolder")
            View view = View.inflate(MainActivity.this, R.layout.item_drama, null);
            ImageView img = view.findViewById(R.id.image_ico);
            ImageView OnOff = view.findViewById(R.id.on_off);
            TextView drama = view.findViewById(R.id.drama);

            OnOff.setOnClickListener(v -> {

                if (MyServer.MySocket != null)
                {

                    new Thread(() -> {
                        try {
                            byte[] data = Agreement.getDrama((byte) (position + 1));
                            MyServer.MySocket.getOutputStream().write(data);
                            CurrentDrama = 6;

                            if(!getItem(position).isStop&&!getItem(position).isPending) {
                                for (int i = 0; i < CurrentDrama; i++) {
                                    if (i == position) {
                                        getItem(position).isStop = true;
                                        getItem(position).isPending = false;
                                    } else {
                                        getItem(i).isStop = false;
                                        getItem(i).isPending = true;
                                    }

                                }
                                handlerDrama.sendMessage(new Message());

                            }else {
                                Toast.makeText(MainActivity.this, getResources().getString(R.string.drama_wait), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            Log.e("SYSTEM_CLICK", "SOCKET", e);
                        }
                    }).start();
                } else {
                    if(!getItem(position).isStop&&!getItem(position).isPending){
                        CurrentDrama = 6;
                        for (int i=0 ; i<CurrentDrama ;i++){
                            if(i==position){
                                getItem(position).isStop = true;
                                getItem(position).isPending =false;
                            }else {
                                getItem(i).isStop =false;
                                getItem(i).isPending = true;
                            }
                        }
                        DramaAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, getResources().getString(R.string.drama_wait), Toast.LENGTH_SHORT).show();
                    }else {

                    }

                }
            });
            img.setImageResource(getItem(position).DramaImgId);
            drama.setText(getItem(position).DramaName);
            if(getItem(position).isPending&&!getItem(position).isStop){
                OnOff.setImageResource(R.drawable.pending);
            }
            if(getItem(position).isStop&&!getItem(position).isPending){
                OnOff.setImageResource(R.drawable.stop);
            }else if(!getItem(position).isStop && !getItem(position).isPending) {
                OnOff.setImageResource(R.drawable.start);
            }


            return view;
        }
    };


}