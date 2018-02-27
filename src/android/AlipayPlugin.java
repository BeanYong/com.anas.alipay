package com.anas.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Map;

/**
 * Created by BeanYon on 2017/1/6.
 */
public class AlipayPlugin extends CordovaPlugin {
  /**
   * 上下文对象
   */
  private Context mContext = null;
  /**
   * 插件钩子方法alipay.js传入的action参照，标识行为
   */
  private final String ACTION_FLAG = "alipay";
  /**
   * 回调对象，传递结果
   */
  private CallbackContext currentCallbackContext;

  /**
   * 支付消息标识
   */
  private static final int SDK_PAY_FLAG = 1;

  /**
   * 支付结果，回调时传递给alipay.js
   */
  private String mResult = null;

  @Override
  public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {

    // 持久化回调对象
    currentCallbackContext = callbackContext;

    // 实例化上下文对象
    mContext = cordova.getActivity();

    // 判断行为标识是否匹配
    if (ACTION_FLAG.equals(action)) {

      // 调用支付宝付款，并根据支付情况返回插件调用结果
      return payV2(args.getString(0));

    }

    // true表示插件调用成功，回调调用onSuccess方法；false表示插件调用过程出现异常，回调调用onError方法
    return true;
  }

  /**
   * 异步消息处理器，根据消息类型进行UI响应及回调
   */
  @SuppressLint("HandlerLeak")
  private Handler mHandler = new Handler() {
    @SuppressWarnings("unused")
    public void handleMessage(Message msg) {
      // 判断消息类型是否为支付完成
      if (SDK_PAY_FLAG == msg.what) {

        // 构造支付结果对象
        @SuppressWarnings("unchecked")
        PayResult payResult = new PayResult((Map<String, String>) msg.obj);

        // 获取支付结果，对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知
        String resultInfo = payResult.getResult();

        // 获取支付状态码
        String resultStatus = payResult.getResultStatus();

        // 支付结果回调传入alipay.js，该笔订单是否真实支付成功，需要依赖服务端的异步通知
        currentCallbackContext.success(mResult);
      }
    }

    ;
  };

  /**
   * 支付宝支付业务
   *
   * @param orderInfo 加签后的订单信息
   */
  public boolean payV2(final String orderInfo) {

    // 构造支付线程
    Runnable payRunnable = new Runnable() {

      @Override
      public void run() {

        // 构建支付任务
        PayTask alipay = new PayTask((Activity) mContext);

        // 执行支付
        Map<String, String> result = alipay.payV2(orderInfo, true);

        // 控制台输出支付结果
        Log.i("msp", result.toString());

        // 保存支付结果
        mResult = formatResult(result);

        // 构建并发送支付消息，通知消息处理器进行响应
        Message msg = new Message();
        msg.what = SDK_PAY_FLAG;
        msg.obj = result;
        mHandler.sendMessage(msg);
      }

    };

    // 启动支付线程
    Thread payThread = new Thread(payRunnable);
    payThread.start();

    // 支付线程正常启动
    return true;
  }

  /**
   * 格式化支付结果
   *
   * @param resultMap 支付结果
   * @return 格式化后的字符串
   */
  private String formatResult(Map<String, String> resultMap) {
    String result = resultMap.get("result");
    // 如果result为空，则构造空数据用于拼装
    if (TextUtils.isEmpty(result)) {
      result = "\"\"";
    }

    // 开始拼装
    String formatResult = "{";
    formatResult += "\"memo\":";
    formatResult += "\"";
    formatResult += resultMap.get("memo");
    formatResult += "\"";
    formatResult += ",\"result\":";
    formatResult += result;
    formatResult += ",\"resultStatus\":";
    formatResult += resultMap.get("resultStatus");
    formatResult += "}";
    Log.i("BeanYon", formatResult);
    return formatResult;
  }
}
