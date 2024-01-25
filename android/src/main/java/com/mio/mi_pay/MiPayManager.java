package com.mio.mi_pay;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.mio.mi_pay.listener.IInitSDKListener;
import com.mio.mi_pay.listener.ILoginListener;
import com.mio.mi_pay.listener.IPaymentListener;
import com.xiaomi.gamecenter.appjoint.MiAccountType;
import com.xiaomi.gamecenter.appjoint.MiCode;
import com.xiaomi.gamecenter.appjoint.MiCommplatform;
import com.xiaomi.gamecenter.appjoint.MiLoginType;
import com.xiaomi.gamecenter.appjoint.OnInitProcessListener;
import com.xiaomi.gamecenter.appjoint.entry.MiAppInfo;
import com.xiaomi.gamecenter.appjoint.entry.MiBuyInfo;


public class MiPayManager {

    private static MiPayManager instance;
    private Activity mActivity;

    private MiPayManager() {
    }

    public static synchronized MiPayManager getInstance() {
        if (instance == null)
            instance = new MiPayManager();
        return instance;
    }

    public void initApplication(Application application) {
        MiCommplatform.setApplication(application);
    }

    public void initSDK(Activity activity, @NonNull String appId, @NonNull String appKey, @NonNull IInitSDKListener initSDKListener) {
        this.mActivity = activity;
        MiAppInfo appInfo = new MiAppInfo();
        //AppId,AppKey在开发者站获取 请确保AppId,AppKey,包名,应用签名和开发者站的信息一致
        appInfo.setAppId(appId);
        appInfo.setAppKey(appKey);
        MiCommplatform.Init(activity, appInfo, new OnInitProcessListener() {
            /**
             * 初始化结果
             * @param returnCode 返回码
             * @param returnInfo 返回信息
             */
            @Override
            public void finishInitProcess(int returnCode, @Nullable String returnInfo) {
                switch (returnCode) {
                    case MiCode.MI_INIT_SUCCESS:
                        //初始化成功
                        initSDKListener.onInitSuccess(returnInfo);
                        break;
                    default:
                        //初始化失败(请检查是否已配计费):" + returnInfo
                        initSDKListener.onInitFailed("code=" + returnCode + " info=" + returnInfo);
                        break;
                }
            }
        });
        /**
         * 关闭额外错误信息的Toast提示
         */
        MiCommplatform.getInstance().setToastDisplay(false);
        MiCommplatform.getInstance().setAlertDialogDisplay(false);
    }

    public void login(@NonNull ILoginListener listener) {
        new Thread(() -> MiCommplatform.getInstance().miLogin(mActivity, (code, miAccountInfo) -> {
                    switch (code) {
                        case MiCode.MI_LOGIN_SUCCESS:
                            listener.onLoginSuccess(new Gson().toJson(miAccountInfo));
                            break;
                        case MiCode.MI_ERROR_ACTION_EXECUTED:
                        case MiCode.MI_ERROR_PAY_INVALID_PARAMETER:
                        default:
                            listener.onLoginFailed(code);
                            break;
                    }
                },
                MiLoginType.AUTO_FIRST, MiAccountType.MI_SDK, null)).start();
    }

    public boolean isLogin() {
        return MiCommplatform.getInstance().getLoginInfo() != null;
    }


    public void onDestroyed() {
        //注意销毁时移除监听
        MiCommplatform.getInstance().removeAllListener();
    }

    /**
     * 创建按计费代码计费的MiBuyInfo对象
     * 请不要设置 feeValue
     *
     * @param productCode 商品计费代码
     * @param quantity    商品数量
     * @param orderId     自定义订单号
     * @return MiBuyInfo对象
     */
    private MiBuyInfo createMiBuyInfo(String productCode, int quantity, String orderId) {
        MiBuyInfo miBuyInfo = new MiBuyInfo();
        miBuyInfo.setProductCode(productCode);
        miBuyInfo.setQuantity(quantity);
        miBuyInfo.setCpOrderId(orderId);
        return miBuyInfo;
    }

    /**
     * 创建按金额计费的MiBuyInfo对象
     * 请不要设置 productCode和quantity
     *
     * @param feeValue 商品金额(单位:分)
     * @param orderId  自定义订单号
     * @return MiBuyInfo对象
     */
    private MiBuyInfo createMiBuyInfo(int feeValue, String orderId) {
        MiBuyInfo miBuyInfo = new MiBuyInfo();
        miBuyInfo.setFeeValue(feeValue);
        miBuyInfo.setCpOrderId(orderId);
        return miBuyInfo;
    }

    /**
     * @param productCode
     * @param quantity
     * @param orderId
     */
    public void pay(String productCode, int quantity, String orderId, @NonNull IPaymentListener listener) {
        MiBuyInfo miBuyInfo = createMiBuyInfo(productCode, quantity, orderId);
        MiCommplatform.getInstance().miUniPay(mActivity, miBuyInfo, (code, info) -> {
            switch (code) {
                case MiCode.MI_PAY_SUCCESS: //支付成功
                    listener.onPaySuccess(1);
                    break;
                case MiCode.MI_SUB_SUCCESS: //订阅成功
                    listener.onPaySuccess(2);
                    break;
                case MiCode.MI_ERROR_PAY_CANCEL: //支付取消
                    listener.onPayCancel(1);
                    break;
                case MiCode.MI_ERROR_BIND_CANCEL: //订阅取消
                    listener.onPayCancel(2);
                    break;
                case MiCode.MI_ERROR_ACTION_EXECUTED: //有未完成的登录/支付流程
                case MiCode.MI_ERROR_PAY_INVALID_PARAMETER: //支付错误
                default:
                    listener.onPayFailed(1, code, info);
                    break;
            }
        });
    }

    /**
     * @param productCode
     * @param quantity
     * @param orderId
     * @param listener
     */
    public void subscribe(String productCode, int quantity, String orderId, @NonNull IPaymentListener listener) {
        MiBuyInfo miBuyInfo = createMiBuyInfo(productCode, quantity, orderId);
        MiCommplatform.getInstance().miSubscribe(mActivity, miBuyInfo, (code, info) -> {
            switch (code) {
                case MiCode.MI_PAY_SUCCESS: //支付成功
                    listener.onPaySuccess(1);
                    break;
                case MiCode.MI_SUB_SUCCESS: //订阅成功
                    listener.onPaySuccess(2);
                    break;
                case MiCode.MI_ERROR_PAY_CANCEL: //支付取消
                    listener.onPayCancel(1);
                    break;
                case MiCode.MI_ERROR_BIND_CANCEL: //订阅取消
                    listener.onPayCancel(2);
                    break;
                case MiCode.MI_ERROR_ACTION_EXECUTED: //有未完成的登录/支付流程
                case MiCode.MI_ERROR_PAY_INVALID_PARAMETER: //支付错误
                default:
                    listener.onPayFailed(2, code, info);
                    break;
            }
        });
    }

    /**
     * @param feeValue
     * @param orderId
     */
    public void pay(int feeValue, String orderId, @NonNull IPaymentListener listener) {
        MiBuyInfo miBuyInfo = createMiBuyInfo(feeValue, orderId);
        MiCommplatform.getInstance().miUniPay(mActivity, miBuyInfo, (code, info) -> {
            switch (code) {
                case MiCode.MI_PAY_SUCCESS: //支付成功
                    listener.onPaySuccess(1);
                    break;
                case MiCode.MI_SUB_SUCCESS: //订阅成功
                    listener.onPaySuccess(2);
                    break;
                case MiCode.MI_ERROR_PAY_CANCEL: //支付取消
                    listener.onPayCancel(1);
                    break;
                case MiCode.MI_ERROR_BIND_CANCEL: //订阅取消
                    listener.onPayCancel(2);
                    break;
                case MiCode.MI_ERROR_ACTION_EXECUTED: //有未完成的登录/支付流程
                case MiCode.MI_ERROR_PAY_INVALID_PARAMETER: //支付错误
                default:
                    listener.onPayFailed(1, code, info);
                    break;
            }
        });
    }

    /**
     * 订阅
     *
     * @param feeValue
     * @param orderId
     */
    public void subscribe(int feeValue, String orderId, @NonNull IPaymentListener listener) {
        MiBuyInfo miBuyInfo = createMiBuyInfo(feeValue, orderId);
        MiCommplatform.getInstance().miSubscribe(mActivity, miBuyInfo, (code, info) -> {
            switch (code) {
                case MiCode.MI_PAY_SUCCESS: //支付成功
                    listener.onPaySuccess(1);
                    break;
                case MiCode.MI_SUB_SUCCESS: //订阅成功
                    listener.onPaySuccess(2);
                    break;
                case MiCode.MI_ERROR_PAY_CANCEL: //支付取消
                    listener.onPayCancel(1);
                    break;
                case MiCode.MI_ERROR_BIND_CANCEL: //订阅取消
                    listener.onPayCancel(2);
                    break;
                case MiCode.MI_ERROR_ACTION_EXECUTED: //有未完成的登录/支付流程
                case MiCode.MI_ERROR_PAY_INVALID_PARAMETER: //支付错误
                default:
                    listener.onPayFailed(2, code, info);
                    break;
            }
        });
    }

}
