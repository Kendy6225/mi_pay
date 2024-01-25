# mi_pay

Android小米联运支付SDK
- 微信sdk已内置

### 初始化sdk
```dart
Future<void> initSDK({
required String appId,
required String appKey,
bool isDebug = false,
});
```

### SDK状态回调
```dart
mixin SDKCallback{

  void onInitSuccess(String info);
  void onInitFailed(String info);

  void onLoginSuccess(String accountInfo);
  void onLoginFailed(int code);

  void onPaySuccess(int type);
  void onPayFailed(int type,int code ,String info);
  void onPayCancel(int type);
}
```

### 获取登录状态
```dart
Future<bool> isLogin() ;
```

### 登录（自动登录，如果未登录则手动登录）
```dart
Future<void> login();
```

### 支付（非消耗类商品）
```dart
Future<void> payProduct({
required String productCode,
required int amount,
String? orderId,
});
```

### 支付（消耗型商品）
```dart
Future<void> pay({
required int feeValue,
String? orderId,
});
```

### 订阅（非消耗型商品）
```dart
Future<void> subscribeProduct({
required String productCode,
required int amount,
String? orderId,
});
```

### 订阅（消耗型商品）
```dart
Future<void> subscribe({required int feeValue, String? orderId})
```