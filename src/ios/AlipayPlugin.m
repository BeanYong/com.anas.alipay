#import "AlipayPlugin.h"
#import "Order.h"
#import "RSADataSigner.h"
#import <AlipaySDK/AlipaySDK.h>

@implementation AlipayPlugin

- (void)alipay:(CDVInvokedUrlCommand *)command{

    // 获取插件传参，即后台加签后的支付信息
	NSString* tradeInfo = [command.arguments objectAtIndex:0];
        
    // 应用注册scheme,在AliSDKDemo-Info.plist定义URL types，用于支付完成后支付宝正确返回到App，注意，字符串可以为随机字符串，但需要足够随机，否则无法支付完成后App无法正确跳转
    NSString *your_scheme = @"alipay5d53f54sd6576h";
        
    // 开始支付
    [[AlipaySDK defaultService] payOrder:tradeInfo fromScheme:your_scheme callback:^(NSDictionary *resultDic) {

        // 在控制台输出支付结果
        NSLog(@"reslut = %@",resultDic);
    
    }];
}

@end
