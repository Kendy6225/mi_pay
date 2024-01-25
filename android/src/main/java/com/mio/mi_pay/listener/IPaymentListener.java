package com.mio.mi_pay.listener;

public interface IPaymentListener {

    // 成功 1=支付 2=订阅
    void onPaySuccess(int type);

    void onPayFailed(int type, int code, String info);

    // 取消 1=支付 2=订阅
    void onPayCancel(int type);
}
