package com.mio.mi_pay.listener;

public interface ILoginListener {

    void onLoginSuccess(String accountInfo);

    void onLoginFailed(int code);
}
