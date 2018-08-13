import 'dart:async';

import 'package:flutter/services.dart';

class FlutterDeviceInfo {

  Map<dynamic, dynamic>constants;

  static const MethodChannel _channel =
      const MethodChannel('flutter_device_info');

  FlutterDeviceInfo() {
    this.init();
  }

  void init() async {
    this.constants = await _channel.invokeMethod('getConstants');
  }

  // 应用名称
  String get appName {
    return this.constants["appName"];
  }

  // 电池电量
  Future<double> get batteryLevel async {
    final dynamic battery = await _channel.invokeMethod('batteryLevel');
    return battery;
  }

  // 手机品牌
  String get brand {
    return this.constants["brand"];
  }

  // 获取应用程序内部版本号
  String get buildNumber {
    return this.constants["buildNumber"];
  }

  // 获取包名
  String get bundleId {
    return this.constants["bundleId"];
  }

  // 获取运营商
  String get carrier {
    return this.constants["carrier"];
  }

  // 根据区域设置信息获取设备国家/地区
  String get deviceCountry {
    return this.constants["deviceCountry"];
  }

  // 获取 device ID iOS: "iPhone7,2" Android: "goldfish"
  String get deviceId {
    return this.constants["deviceId"];
  }

  // 获取设备区域设置
  String get deviceLocale  {
    return this.constants["deviceLocale"];
  }

  // 获取设备自定义名称 王奇的iphone
  String get deviceName {
    return this.constants["deviceName"];
  }

  // 获取IP地址
  static Future<String> get IPAddress async {
    final dynamic ip = await _channel.invokeMethod('IPAddress');
    return ip;
  }

  // 设备制造商
  String get manufacturer {
    return this.constants["manufacturer"];
  }

  //
  String get model {
    return this.constants["model"];
  }

  // 应用版本
  String get readableVersion {
    return this.constants["appVersion"] + '.' + this.constants["buildNumber"];
  }

  // 应用版本号
  String get version {
    return this.constants["appVersion"];
  }

  // 获取设备操作系统
  String get systemName {
    return this.constants["systemName"];
  }

  // 获取设备操作系统版本
  String get systemVersion {
    return this.constants["systemVersion"];
  }

  // 获取时区
  String get timezone {
    return this.constants["timezone"];
  }

  // 获取磁盘存储大小（以字节为单位）
  int get totalDiskCapacity {
    return this.constants["totalDiskCapacity"];
  }

  // 获取设备内存（以字节为单位）
  int get totalMemory {
    return this.constants["totalMemory"];
  }

  // 获取设备唯一ID
  String get uniqueId {
    return this.constants["totalMemory"];
  }

}
