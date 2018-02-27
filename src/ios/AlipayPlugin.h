#import <Foundation/Foundation.h>

#import <Cordova/CDV.h>
#import <Cordova/CDVPlugin.h>

@interface AlipayPlugin : CDVPlugin

/**
 * 支付入口函数
 *
 * @param command Cordova Plugin执行帮助对象
 */
- (void)alipay:(CDVInvokedUrlCommand*)command;

/**
 * 返回调起插件的js逻辑
 *
 * @param resultDic 支付结果字典
 */
- (void)backToJs:(NSDictionary *)resultDic;

/**
 * 字典转json格式字符串
 *
 * @param dic 需要转换为字符串的字典
 * @return 转换后的字符串
 */
- (NSString*)dictionaryToJson:(NSDictionary *)dic;
@end
