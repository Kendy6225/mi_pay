typedef OnInitSuccess = void Function(String info);

typedef OnInitFailed = void Function(String info);

typedef OnLoginSuccess = void Function(String accountInfo);

typedef OnLoginFailed = void Function(int code);

typedef OnPaySuccess = void Function(int type);

typedef OnPayCancel = void Function();

mixin SDKCallback{

  void onInitSuccess(String info);
  void onInitFailed(String info);

  void onLoginSuccess(String accountInfo);
  void onLoginFailed(int code);

  void onPaySuccess(int type);
  void onPayFailed(int type,int code ,String info);
  void onPayCancel(int type);
}


class PayType {
  static const int pay = 1; //支付
  static const int subscribe = 2; //订阅
}
