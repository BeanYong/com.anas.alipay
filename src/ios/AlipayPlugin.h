#import <Foundation/Foundation.h>

#import <Cordova/CDV.h>
#import <Cordova/CDVPlugin.h>

@interface AlipayPlugin : CDVPlugin
- (void)alipay:(CDVInvokedUrlCommand*)command;
@end
