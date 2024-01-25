package com.mio.mi_pay.listener;

public interface IInitSDKListener {

    void onInitSuccess(String info);

    void onInitFailed(String info);
}
