var exec = require('cordova/exec');

var Alipay = {
  /**
   * 支付宝支付业务
   *
   * @param onSuccess 支付成功回调函数
   * @param onError   支付失败回调函数
   * @param orderInfo 加签后的交易信息
   */
    pay:function(onSuccess, onError, orderInfo) {
        exec(
            onSuccess,
            onError,
            "AlipayPlugin",
            // 此处的alipay字符串，在Android平台中为行为标识，在iOS平台中为入口方法名称
            "alipay",
            [orderInfo]
        );
    }
}

module.exports = Alipay;