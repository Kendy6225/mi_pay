import 'package:mi_pay/mi_pay_callback.dart';

import 'mi_pay_platform_interface.dart';

class MiPay {
  /// 初始化sdk
  Future<void> initSDK({
    required String appId,
    required String appKey,
    bool isDebug = false,
  }) async {
    return await MiPayPlatform.instance.initSDK(
      appId: appId,
      appKey: appKey,
      isDebug: isDebug,
    );
  }

  /// 调用支付之前添加回调
  void addCallback(SDKCallback callback) {
    MiPayPlatform.instance.addCallback(callback);
  }

  /// 获取登录状态
  Future<bool> isLogin() async {
    return await MiPayPlatform.instance.isLogin();
  }

  /// 开始登录
  Future<void> login() async {
    return await MiPayPlatform.instance.login();
  }

  /// 支付（非消耗类商品）
  Future<void> payProduct({
    required String productCode,
    required int amount,
    String? orderId,
  }) async {
    return await MiPayPlatform.instance.payProduct(
      productCode: productCode,
      amount: amount,
      orderId: orderId,
    );
  }

  /// 支付（消耗型商品）
  Future<void> pay({
    required int feeValue,
    String? orderId,
  }) async {
    return await MiPayPlatform.instance.pay(
      feeValue: feeValue,
      orderId: orderId,
    );
  }

  /// 订阅（非消耗型商品）
  Future<void> subscribeProduct({
    required String productCode,
    required int amount,
    String? orderId,
  }) async {
    return await MiPayPlatform.instance.subscribeProduct(
      productCode: productCode,
      amount: amount,
      orderId: orderId,
    );
  }

  /// 订阅（消耗型商品）
  Future<void> subscribe({required int feeValue, String? orderId}) async {
    return await MiPayPlatform.instance.subscribe(
      feeValue: feeValue,
      orderId: orderId,
    );
  }
}
