import 'package:mi_pay/mi_pay_callback.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'mi_pay_method_channel.dart';

abstract class MiPayPlatform extends PlatformInterface {
  /// Constructs a MiPayPlatform.
  MiPayPlatform() : super(token: _token);

  static final Object _token = Object();

  static MiPayPlatform _instance = MethodChannelMiPay();

  /// The default instance of [MiPayPlatform] to use.
  ///
  /// Defaults to [MethodChannelMiPay].
  static MiPayPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [MiPayPlatform] when
  /// they register themselves.
  static set instance(MiPayPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<void> initSDK({
    required String appId,
    required String appKey,
    bool isDebug = false,
  }) {
    throw UnimplementedError('initSDK() has not been implemented.');
  }

  Future<void> login() {
    throw UnimplementedError('login() has not been implemented.');
  }

  Future<bool> isLogin() {
    throw UnimplementedError('isLogin() has not been implemented.');
  }

  Future<void> payProduct({
    required String productCode,
    required int amount,
    String? orderId,
  }) {
    throw UnimplementedError('payProduct() has not been implemented.');
  }

  Future<void> pay({
    required int feeValue,
    String? orderId,
  }) {
    throw UnimplementedError('pay() has not been implemented.');
  }

  Future<void> subscribeProduct({
    required String productCode,
    required int amount,
    String? orderId,
  }) {
    throw UnimplementedError('subscribeProduct() has not been implemented.');
  }

  Future<void> subscribe({
    required int feeValue,
    String? orderId,
  }) {
    throw UnimplementedError('subscribe() has not been implemented.');
  }

  void addCallback(SDKCallback callback) {
    throw UnimplementedError('addCallback() has not been implemented.');
  }

  void dispose(){
    throw UnimplementedError('dispose() has not been implemented.');
  }
}
