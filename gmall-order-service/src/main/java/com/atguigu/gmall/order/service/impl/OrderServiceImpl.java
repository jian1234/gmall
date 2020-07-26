package com.atguigu.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.OmsOrder;
import com.atguigu.gmall.bean.OmsOrderItem;
import com.atguigu.gmall.order.mapper.OmsOrderItemMapper;
import com.atguigu.gmall.order.mapper.OmsOrderMapper;
import com.atguigu.gmall.serivce.OrderService;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    OmsOrderMapper omsOrderMapper;

    @Autowired
    OmsOrderItemMapper omsOrderItemMapper;

    @Override
    public String checkTradeCode(String memberId, String tradeCode) {
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            String tradekey = "user:"+memberId+"tradecode";
            String tradeCodeFromCache = jedis.get(tradekey);
            //if (StringUtils.isNotBlank(tradeCodeFromCache)&&tradeCodeFromCache.equals(tradeCode)){
                // 使用lua脚本在发现key的同时就删除key,防止并发订单攻击
                //对比防重删令牌
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                Long eval = (Long) jedis.eval(script, Collections.singletonList(tradekey), Collections.singletonList(tradeCode));
                if (eval!= null && eval != 0){
                    jedis.del(tradekey);
                    return "success";
                }else{
                    return "fail";
                }
           // }
        }finally {
            jedis.close();
        }
    }

    @Override
    public String genTradeCode(String memberId) {
        Jedis jedis = redisUtil.getJedis();
        String tradekey = "user:"+memberId+"tradecode";
        String tradeCode = UUID.randomUUID().toString();
        jedis.setex(tradekey,60*15,tradeCode);
        jedis.close();
        return tradeCode;
    }

    @Override
    public void saveOrder(OmsOrder omsOrder) {
        // 保存订单表
        omsOrderMapper.insertSelective(omsOrder);
        String orderId = omsOrder.getId();
        // 保存订单详情
        List<OmsOrderItem> omsOrderItems = omsOrder.getOmsOrderItems();
        for (OmsOrderItem omsOrderItem : omsOrderItems) {
            omsOrderItem.setOrderId(orderId);
            omsOrderItemMapper.insertSelective(omsOrderItem);
            // 删除购物车数据
            // cartService.delCart();
        }
    }

    @Override
    public OmsOrder getOrderByOutTradeNo(String outTradeNo) {
        return null;
    }

    @Override
    public void updateOrder(OmsOrder omsOrder) {

    }
}
