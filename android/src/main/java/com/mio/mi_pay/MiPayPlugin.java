package com.mio.mi_pay;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mio.mi_pay.listener.IInitSDKListener;
import com.mio.mi_pay.listener.ILoginListener;
import com.mio.mi_pay.listener.IPaymentListener;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * MiPayPlugin
 */
public class MiPayPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity

    private static final String TAG = "MiPayPlugin";

    private MethodChannel channel;
    private Activity activity;

    private Handler mHandler;

    private Boolean isDebug = true;

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        activity = binding.getActivity();
        MiPayManager.getInstance().initApplication(activity.getApplication());
        logInfo("onAttachedToActivity");
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        logInfo("onDetachedFromActivityForConfigChanges");
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        logInfo("onReattachedToActivityForConfigChanges");
    }

    @Override
    public void onDetachedFromActivity() {
        logInfo("onDetachedFromActivity");
        MiPayManager.getInstance().onDestroyed();
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        logInfo("onAttachedToEngine");
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "mi_pay");
        channel.setMethodCallHandler(this);
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        // 初始化SDK
        if (call.method.equals("initSDK")) {
            String appId = call.argument("appId");
            String appKey = call.argument("appKey");
            this.isDebug = call.argument("isDebug");
            MiPayManager.getInstance().initSDK(activity, appId, appKey, initSDKListener);
            result.success(null);
        } else if (call.method.equals("login")) {
            MiPayManager.getInstance().login(loginListener);
            result.success(null);
        } else if (call.method.equals("isLogin")) {
            boolean isLogin = MiPayManager.getInstance().isLogin();
            result.success(isLogin);
        } else if (call.method.equals("payProduct")) {
            String productCode = call.argument("productCode");
            int amount = call.argument("amount");
            String orderId = call.argument("orderId");
            MiPayManager.getInstance().pay(productCode, amount, orderId, paymentListener);
            result.success(null);
        } else if (call.method.equals("pay")) {
            int feeValue = call.argument("feeValue");
            String orderId = call.argument("orderId");
            MiPayManager.getInstance().pay(feeValue, orderId, paymentListener);
            result.success(null);
        } else if (call.method.equals("subscribeProduct")) {
            String productCode = call.argument("productCode");
            int amount = call.argument("amount");
            String orderId = call.argument("orderId");
            MiPayManager.getInstance().subscribe(productCode, amount, orderId, paymentListener);
            result.success(null);
        } else if (call.method.equals("subscribe")) {
            int feeValue = call.argument("feeValue");
            String orderId = call.argument("orderId");
            MiPayManager.getInstance().subscribe(feeValue, orderId, paymentListener);
            result.success(null);
        } else {
            result.notImplemented();
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    private IInitSDKListener initSDKListener = new IInitSDKListener() {
        @Override
        public void onInitSuccess(String info) {
            mHandler.post(() -> channel.invokeMethod("onInitSuccess", info));
            logInfo("【mi-sdk初始化成功】, " + info);
        }

        @Override
        public void onInitFailed(String info) {
            mHandler.post(() -> channel.invokeMethod("onInitFailed", info));
            logInfo("【mi-sdk初始化失败】, " + info);
        }
    };

    private ILoginListener loginListener = new ILoginListener() {
        @Override
        public void onLoginSuccess(String accountInfo) {
            mHandler.post(() -> {
                channel.invokeMethod("onLoginSuccess", accountInfo);
            });
            logInfo("【mi-登录成功】, " + accountInfo);
        }

        @Override
        public void onLoginFailed(int code) {
            mHandler.post(() -> channel.invokeMethod("onLoginFailed", code));
            logInfo("【mi-登录失败】, 错误代码 = " + code);
        }
    };

    private IPaymentListener paymentListener = new IPaymentListener() {
        @Override
        public void onPaySuccess(int type) {
            mHandler.post(() -> channel.invokeMethod("onPaySuccess", type));
            logInfo("【mi-" + (type == 1 ? "支付" : "订阅") + "成功】");
        }

        @Override
        public void onPayFailed(int type, int code, String info) {
            Map<String, Object> map = new HashMap<>();
            map.put("type", type);
            map.put("code", code);
            map.put("info", info);
            mHandler.post(() -> channel.invokeMethod("onPayFailed", map));
            logInfo("【mi-" + (type == 1 ? "支付" : "订阅") + "失败】code = " + code + " info = " + info);
        }

        @Override
        public void onPayCancel(int type) {
            mHandler.post(() -> channel.invokeMethod("onPayCancel", type));
            logInfo("【mi-" + (type == 1 ? "支付" : "订阅") + "取消】");
        }
    };

    private void logInfo(String msg) {
        if (isDebug) {
            Log.d(TAG, msg);
        }
    }
}
