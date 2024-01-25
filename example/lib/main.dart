import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:mi_pay/mi_pay.dart';
import 'package:mi_pay/mi_pay_callback.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> with SDKCallback {
  String _platformVersion = 'Unknown';
  final _miPayPlugin = MiPay();

  @override
  void initState() {
    super.initState();
    //initPlatformState();

    Future.delayed(const Duration(milliseconds: 200),(){
      MiPay().initSDK(
          appId: '2882303761517996496', appKey: '5641799683496', isDebug: true);
    });
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    MiPay().addCallback(this);

    // await MiPay().login();
    //bool isLogin = await MiPay().isLogin();
    MiPay().pay(feeValue: 1000,orderId: 'rx0000192');
  }

  @override
  void onInitSuccess(String info) {}

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Text('Running on: $_platformVersion\n'),
        ),
        floatingActionButton: FloatingActionButton(
          onPressed: () {
            initPlatformState();
          },
          child: Text('+'),
        ),
      ),
    );
  }

  @override
  void onInitFailed(String info) {}

  @override
  void onLoginSuccess(String accountInfo) {}

  @override
  void onLoginFailed(int code) {}

  @override
  void onPaySuccess(int type) {}

  @override
  void onPayFailed(int type, int code, String info) {}

  @override
  void onPayCancel(int type) {}
}
