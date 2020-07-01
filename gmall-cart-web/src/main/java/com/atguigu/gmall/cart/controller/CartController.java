package com.atguigu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.OmsCartItem;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.serivce.CartService;
import com.atguigu.gmall.serivce.SkuService;
import com.atguigu.gmall.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class CartController {

    @Reference
    SkuService skuService;
    @Reference
    CartService cartService;

    @RequestMapping("cartList")
    public String cartList(String skuId, int quantity, HttpServletRequest request, HttpServletResponse respose, HttpSession session, ModelMap modelMap){
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        String userId="";
        if (StringUtils.isNotBlank(userId)){
            // 已经登录
            omsCartItems = cartService.cartList(userId);
        }else{
            // 未登录
            String cartListCookie = CookieUtil.getCookieValue(request,"cartListCookie",true);
            if (StringUtils.isNotBlank(cartListCookie)){
                omsCartItems = JSON.parseArray(cartListCookie,OmsCartItem.class);
            }
        }
        for (OmsCartItem omsCartItem : omsCartItems){
            omsCartItem.setTotalPrice(omsCartItem.getTotalPrice());
            omsCartItem.getQuantity();
        }
        modelMap.put("cartList",omsCartItems);
        // 被勾选商品的总额
        BigDecimal totalAmount =getTotalAmount(omsCartItems);
        modelMap.put("totalAmount",totalAmount);
        return "cartList";
    }

    private BigDecimal getTotalAmount(List<OmsCartItem> omsCartItems) {
        BigDecimal totalAmount = new BigDecimal("0");

        for (OmsCartItem omsCartItem : omsCartItems) {
            BigDecimal totalPrice = omsCartItem.getTotalPrice();

            if(omsCartItem.getIsChecked().equals("1")){
                totalAmount = totalAmount.add(totalPrice);
            }
        }

        return totalAmount;
    }

    @RequestMapping("addToCart")
    public String addToCart(String skuId, int quantity, HttpServletRequest request, HttpServletResponse respose){

        //调用商品服务查询商品信息
        PmsSkuInfo skuInfo = skuService.getSkuById(skuId);
        //将商品信息封装购物车信息
        OmsCartItem omsCartItem = new OmsCartItem();
        omsCartItem.setCreateDate(new Date());
        omsCartItem.setDeleteStatus(0);
        omsCartItem.setModifyDate(new Date());
        omsCartItem.setPrice(skuInfo.getPrice());
        omsCartItem.setProductAttr("");
        omsCartItem.setProductBrand("");
        omsCartItem.setProductCategoryId(skuInfo.getCatalog3Id());
        omsCartItem.setProductId(skuInfo.getProductId());
        omsCartItem.setProductName(skuInfo.getSkuName());
        omsCartItem.setProductPic(skuInfo.getSkuDefaultImg());
        omsCartItem.setProductSkuCode("11111111111");
        omsCartItem.setProductSkuId(skuId);
        omsCartItem.setQuantity(new BigDecimal(quantity));
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        //判断用户是否登录
        String memberId = request.getAttribute("memberId").toString();
        String nickname = request.getAttribute("nickname").toString();
        if (StringUtils.isBlank(memberId)){
            //用户登录
            // cookie里原有的购物车数据
            String cartListCookie = CookieUtil.getCookieValue(request, "cartListCookie", true);
            if (StringUtils.isBlank(cartListCookie)){
                omsCartItems.add(omsCartItem);
            }else{
                // cookie不为空
                omsCartItems = JSON.parseArray(cartListCookie,OmsCartItem.class);
                // 判断添加的购物车数据在cookie中是否存在
                boolean exist = if_cart_exist(omsCartItems, omsCartItem);
                if (exist){
                    for (OmsCartItem cartItem : omsCartItems){
                        if (cartItem.getProductSkuId().equals(omsCartItem.getProductSkuId())){
                            cartItem.setQuantity(cartItem.getQuantity());
                            omsCartItem.getQuantity();
                        }
                    }
                }else{
                    // 之前没有添加，新增当前的购物车
                    omsCartItems.add(omsCartItem);
                }
            }
            CookieUtil.setCookie(request,respose,"cartListConkie",JSON.toJSONString(omsCartItems),60*60*72,true);

        }else{
                //用户已经登录
            OmsCartItem omsCartItemFromDb = cartService.ifCartExistByUser(memberId,skuId);
            if(omsCartItemFromDb==null){
                // 该用户没有添加过当前商品
                omsCartItem.setMemberId(memberId);
                omsCartItem.setMemberNickname("test小明");
                omsCartItem.setQuantity(new BigDecimal(quantity));
                cartService.addCart(omsCartItem);

            }else{
                // 该用户添加过当前商品
                omsCartItemFromDb.setQuantity(omsCartItemFromDb.getQuantity().add(omsCartItem.getQuantity()));
                cartService.updateCart(omsCartItemFromDb);
            }
            // 同步缓存
            cartService.flushCartCache(memberId);
        }
        return "redirect:/success.html";
    }
    private boolean if_cart_exist(List<OmsCartItem> omsCartItems, OmsCartItem omsCartItem) {
        boolean b = false;
        for (OmsCartItem cartItem : omsCartItems) {
            String productSkuId = cartItem.getProductSkuId();

            if (productSkuId.equals(omsCartItem.getProductSkuId())) {
                b = true;
            }
        }
        DateFormat df1 = new SimpleDateFormat("yyyyMMdd");

        return b;
    }

    public static void main(String[] args) throws Exception{
        String dateToparse1="2005-06-09 10:10:10";
        String dateToparse2="2005-06-09 10:10:10";
        Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(dateToparse1);
        Date date2 = new SimpleDateFormat("yyyy-MM-dd").parse(dateToparse2);
        String now1 = new SimpleDateFormat("yyyyMMdd").format(date1);
        String now2 = new SimpleDateFormat("yyyyMM").format(date2);
        System.out.println(now1);
        System.out.println(now2);
    }
}
