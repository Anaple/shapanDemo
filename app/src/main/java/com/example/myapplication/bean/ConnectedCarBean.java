package com.example.myapplication.bean;

public class ConnectedCarBean {

    private int carIndex;
    private boolean canUse;

    public ConnectedCarBean(){}

    public ConnectedCarBean(int carIndex, boolean canUse) {
        this.carIndex = carIndex;
        this.canUse = canUse;
    }

    public int getCarIndex() {
        return carIndex;
    }

    public void setCarIndex(int carIndex) {
        this.carIndex = carIndex;
    }

    public boolean isCanUse() {
        return canUse;
    }

    public void setCanUse(boolean canUse) {
        this.canUse = canUse;
    }
}
