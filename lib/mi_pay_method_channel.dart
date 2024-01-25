import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:mi_pay/mi_pay_callback.dart';

import 'mi_pay_platform_interface.dart';

/// An implementation of [MiPayPlatform] that uses method channels.
class MethodChannelMiPay extends MiPayPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('mi_pay');

  @override
  Future<void> initSDK({
    required String appId,
    required String appKey,
    bool isDebug = false,
  }) async {
    return await methodChannel.invokeMethod(
      'initSDK',
      {'appId': appId, 'appKey': appKey, 'isDebug': isDebug},
    );
  }

  @override
  Future<void> login() async {
    return await methodChannel.invokeMethod('login');
  }

  @override
  Future<bool> isLogin() async {
    return await methodChannel.invokeMethod('isLogin');
  }

  @override
  Future<void> payProduct({
    required String productCode,
    required int amount,
    String? orderId,
  }) async {
    return await methodChannel.invokeMethod(
      'payProduct',
      {'productCode': productCode, 'amount': amount, 'orderId': orderId},
    );
  }

  @override
  Future<void> pay({
    required int feeValue,
    String? orderId,
  }) async {
    return await methodChannel.invokeMethod(
      'pay',
      {'feeValue': feeValue, 'orderId': orderId},
    );
  }

  @override
  Future<void> subscribeProduct({
    required String productCode,
    required int amount,
    String? orderId,
  }) async {
    return await methodChannel.invokeMethod(
      'subscribeProduct',
      {'productCode': productCode, 'amount': amount, 'orderId': orderId},
    );
  }

  @override
  Future<void> subscribe({required int feeValue, String? orderId}) async {
    return await methodChannel.invokeMethod(
      'subscribe',
      {'feeValue': feeValue, 'orderId': orderId},
    );
  }

  @override
  void addCallback(SDKCallback callback) {
    methodChannel.setMethodCallHandler((call) {
      switch (call.method) {
        case "onInitSuccess":
          callback.onInitSuccess(call.arguments);
          break;
        case "":
          callback.onInitFailed(call.arguments);
          break;
        case "onLoginSuccess":
          callback.onLoginSuccess(call.arguments);
          break;
        case "onLoginFailed":
          callback.onLoginFailed(call.arguments);
          break;
        case "onPaySuccess":
          callback.onPaySuccess(call.arguments);
          break;
        case "onPayFailed":
          dynamic map = call.arguments;
          int type = map['type'];
          int code = map['code'];
          String info = map['info'];
          callback.onPayFailed(type, code, info);
          break;
        case "onPayCancel":
          callback.onPayCancel(call.arguments);
          break;
        default:
          break;
      }
      return Future(() => null);
    });
  }

  @override
  void dispose() {
    methodChannel.setMethodCallHandler(null);
  }
}
