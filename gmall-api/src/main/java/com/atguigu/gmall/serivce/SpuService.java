package com.atguigu.gmall.serivce;

import com.atguigu.gmall.bean.PmsProductInfo;

import java.util.List;

public interface SpuService {

    List<PmsProductInfo> spuList(String catalog3Id);
}
