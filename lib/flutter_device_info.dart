import 'dart:async';

import 'package:flutter/services.dart';

class FlutterDeviceInfo {

  static const MethodChannel _channel =
      const MethodChannel('flutter_device_info');

  FlutterDeviceInfo() {}

  Future<FlutterDeviceInfoFields> getFields() async {
    Map<dynamic, dynamic> constants = await _channel.invokeMethod('getConstants');
    return FlutterDeviceInfoFields(constants);
  }

  
  // 电池电量
  Future<double> get batteryLevel async {
    final dynamic battery = await _channel.invokeMethod('batteryLevel');
    return battery;
  }

  // 获取IP地址
  static Future<String> get IPAddress async {
    final dynamic ip = await _channel.invokeMethod('IPAddress');
    return ip;
  }

}

class FlutterDeviceInfoFields {

  Map<dynamic, dynamic>constants;

  FlutterDeviceInfoFields(this.constants);

// 应用名称
  String get appName => this.constants["appName"];



  // 手机品牌
  String get brand => this.constants["brand"];


  // 获取应用程序内部版本号
  String get buildNumber => this.constants["buildNumber"];


  // 获取包名
  String get bundleId => this.constants["bundleId"];


  // 获取运营商
  String get carrier => this.constants["carrier"];


  // 根据区域设置信息获取设备国家/地区
  String get deviceCountry => this.constants["deviceCountry"];


  // 获取 device ID iOS: "iPhone7,2" Android: "goldfish"
  String get deviceId => this.constants["deviceId"];


  // 获取设备区域设置
  String get deviceLocale => this.constants["deviceLocale"];


  // 获取设备自定义名称 王奇的iphone
  String get deviceName => this.constants["deviceName"];


  // 设备制造商
  String get manufacturer => this.constants["manufacturer"];


  //
  String get model => this.constants["model"];


  // 应用版本
  String get readableVersion => this.constants["appVersion"] + '.' + this.constants["buildNumber"];


  // 应用版本号
  String get version => this.constants["appVersion"];


  // 获取设备操作系统
  String get systemName => this.constants["systemName"];


  // 获取设备操作系统版本
  String get systemVersion => this.constants["systemVersion"];


  // 获取时区
  String get timezone => this.constants["timezone"];


  // 获取磁盘存储大小（以字节为单位）
  int get totalDiskCapacity => this.constants["totalDiskCapacity"];


  // 获取设备内存（以字节为单位）
  int get totalMemory => this.constants["totalMemory"];


  // 获取设备唯一ID
  String get uniqueId => this.constants["uniqueId"];

  // 仅适用与安卓
  String get imei => this.constants["imei"];

}
