#import "AlipayPlugin.h"
#import "Order.h"
#import "RSADataSigner.h"
#import <AlipaySDK/AlipaySDK.h>

@implementation AlipayPlugin

// 返回的插件id
static NSString *callbackId;

// 当前插件实例
static AlipayPlugin *instance;

/**
 * 支付入口函数
 *
 * @param command Cordova Plugin执行帮助对象
 */
- (void)alipay:(CDVInvokedUrlCommand *)command {
    
    callbackId = [command callbackId];
    instance = self;

    // 全局并发队列的获取方法
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    dispatch_async(queue, ^{
        // 获取插件传参，即后台加签后的支付信息
        NSString* tradeInfo = [command.arguments objectAtIndex:0];
        
        // 应用注册scheme,在AliSDKDemo-Info.plist定义URL types，用于支付完成后支付宝正确返回到App，注意，字符串可以为随机字符串，但需要足够随机，否则无法支付完成后App无法正确跳转
        NSString *motoralipay = @"motoralipay";
        
        // 开始支付
        [[AlipaySDK defaultService] payOrder:tradeInfo fromScheme:motoralipay callback:^(NSDictionary *resultDic) {
            
            // 在控制台输出支付结果
            NSLog(@"reslut = %@",resultDic);
            
            CDVPluginResult *pluginResult = nil;
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary: resultDic];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            
        }];
    });
}

/**
 * 返回调起插件的js逻辑
 *
 * @param resultDic 支付结果字典
 */
- (void)backToJs:(NSDictionary *)resultDic {

	// 将支付结果转换为字符串
    NSString *strResult = [self dictionaryToJson: resultDic];

    // 构造插件返回参数
    CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString: strResult];
    
    // 执行带参返回
    [instance.commandDelegate sendPluginResult: pluginResult callbackId: callbackId];

}

/**
 * 字典转json格式字符串
 *
 * @param dic 需要转换为字符串的字典
 * @return 转换后的字符串
 */
- (NSString*)dictionaryToJson:(NSDictionary *)dic {
    NSError *parseError = nil;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dic options:NSJSONWritingPrettyPrinted error:&parseError];
    return [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
}

@end
