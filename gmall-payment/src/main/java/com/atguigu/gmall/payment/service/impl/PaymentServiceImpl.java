package com.atguigu.gmall.payment.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.gmall.payment.mapper.PaymentInfoMapper;
import com.atguigu.gmall.serivce.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    PaymentInfoMapper paymentInfoMapper;

    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {
        paymentInfoMapper.insertSelective(paymentInfo);
    }

    @Override
    public void updatePayment(PaymentInfo paymentInfo) {
        String orderSn = paymentInfo.getOrderSn();
        Example e = new Example(PaymentInfo.class);
        e.createCriteria().andEqualTo("orderSn",orderSn);
        paymentInfoMapper.updateByExampleSelective(paymentInfo,e);
    }

    @Override
    public void sendDelayPaymentResultCheckQueue(String outTradeNo, int count) {

    }

    @Override
    public Map<String, Object> checkAlipayPayment(String out_trade_no) {
        return null;
    }
}
