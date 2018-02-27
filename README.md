# Anas Alipay Cordova Plugin

## 概述

Anas Alipay Cordova Plugin，基于支付宝官方SDK进行封装，支持Android、iOS两个平台。  

## 安装

### clone

首先，使用如下命令将插件代码clone到本地

``` 
git clone https://github.com/BeanYong/com.anas.alipay.git
``` 

### 修改scheme

在插件目录com.anas.alipay/src/ios下，找到AlipayPlugin.m文件，对其中your_scheme进行编辑，将其修改为自定义的Scheme，具体为如下一行

``` 
NSString *your_scheme = @"your_scheme";
``` 

例如修改为

``` 
NSString *your_scheme = @"qazxcfghjklmnbgfdw";
``` 

**scheme为自己应用的唯一标识，作为支付宝支付完成后跳转的凭据，可以设置为随机值，但要尽量确保scheme在用户手机上的唯一性。**

### 安装插件

``` 
cordova plugin add 插件在本机的路径
``` 

例如

``` 
cordova plugin add E:\Anasit\plugins\com.anas.alipay
``` 

## 配置

插件安装完成后，需要对iOS平台进行一些回调配置，以在支付完成后，正确跳转及接收回调参数。

### AppDelegate配置

在AppDelegate.h文件中加入如下代码

``` 
#import "AlipayPlugin.h"
``` 

在AppDelegate.m文件中加入如下代码

```
- (BOOL)application:(UIApplication *)application openURL:(NSURL *)url sourceApplication:(NSString *)sourceApplication annotation:(id)annotation
{
    if ([url.host isEqualToString:@"safepay"]) {

        // 支付跳转支付宝钱包进行支付，处理支付结果
        [[AlipaySDK defaultService] processOrderWithPaymentResult:url standbyCallback:^(NSDictionary *resultDic) {
            NSLog(@"result = %@",resultDic);
            AlipayPlugin *plugin = [[AlipayPlugin alloc] init];
            [plugin backToJs:resultDic];
        }];

    }
    return YES;
}

// NOTE: 9.0以后使用新API接口
- (BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary<NSString*, id> *)options
{
    if ([url.host isEqualToString:@"safepay"]) {
        
        // 支付跳转支付宝钱包进行支付，处理支付结果
        [[AlipaySDK defaultService] processOrderWithPaymentResult:url standbyCallback:^(NSDictionary *resultDic) {
            NSLog(@"result = %@",resultDic);
            AlipayPlugin *plugin = [[AlipayPlugin alloc] init];
            [plugin backToJs:resultDic];
        }];
        
    }
    return YES;
}
```

### scheme配置

使用xcode打开项目，在Targets中，点击info选项卡，在URL Types中添加一个item，其中URL Schemes填入刚才写入插件的自定义scheme，即qazxcfghjklmnbgfdw（此字符串作为示例，请自行填入自定义的scheme）。

## 使用

``` 
  cordova.plugins.AlipayPlugin.pay(
    /**
     * 成功回调
     *
     * @param result 支付结果（其中包含result、memo、resultStatus三个参数）
     */
    function(result){
        var resultJson = JSON.parse(result);
        if(resultJson == 9000){
            // TODO 成功支付
            // 注：支付宝支付结果应该依赖于支付宝发送到服务器的异步回调，故此处应该将向服务器询问是否确实支付成功
        } else {
            // TODO 未成功支付
        }
    },

    //失败回调
    function(){
        Toast.show("服务器开小差了，稍等一会儿在'我的订单'里付款吧");
    },

    //服务器组装的加签支付信息，预先向服务器请求获取
    orderInfo
  );
```

## 作者

**闫斌（BeanYon）**

- Email：1054639005@qq.com

