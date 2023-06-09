package com.ruoyi.shop.huajia.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import com.ruoyi.shop.baozi.domain.ShopBaozi;
import com.ruoyi.shop.huajia.vo.HuaJiaTongJiVo;
import com.ruoyi.shop.huajia.vo.ZheXianTongJiVo;
import com.ruoyi.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.shop.huajia.mapper.ShopHuajiaMapper;
import com.ruoyi.shop.huajia.domain.ShopHuajia;
import com.ruoyi.shop.huajia.service.IShopHuajiaService;

/**
 * huajiaService业务层处理
 * 
 * @author ruoyi
 * @date 2023-05-20
 */
@Service
public class ShopHuajiaServiceImpl implements IShopHuajiaService 
{

    private static String NAME = "花甲粉丝";

    @Autowired
    private ShopHuajiaMapper shopHuajiaMapper;

    /**
     * 查询huajia
     * 
     * @param id huajia主键
     * @return huajia
     */
    @Override
    public ShopHuajia selectShopHuajiaById(Long id)
    {
        return shopHuajiaMapper.selectShopHuajiaById(id);
    }

    /**
     * 查询huajia列表
     * 
     * @param shopHuajia huajia
     * @return huajia
     */
    @Override
    public List<ShopHuajia> selectShopHuajiaList(ShopHuajia shopHuajia)
    {
        return shopHuajiaMapper.selectShopHuajiaList(shopHuajia);
    }

    /**
     * 新增huajia
     * 
     * @param shopHuajia huajia
     * @return 结果
     */
    @Override
    public int insertShopHuajia(ShopHuajia shopHuajia)
    {
        BigDecimal getPayMoney = shopHuajia.getGetPayMoney() == null ? BigDecimal.valueOf(0) : shopHuajia.getGetPayMoney();
        BigDecimal getWechatMoney = shopHuajia.getGetWechatMoney() == null ? BigDecimal.valueOf(0) : shopHuajia.getGetWechatMoney();
        BigDecimal getMoney = shopHuajia.getGetMoney() == null ? BigDecimal.valueOf(0) : shopHuajia.getGetMoney();
        String remark = shopHuajia.getRemark() == null ? " " : shopHuajia.getRemark();
        shopHuajia.setRemark(remark);
        //总收入=微信+支付宝+现金
        BigDecimal allMoney = getPayMoney.add(getWechatMoney).add(getMoney);
        shopHuajia.setName(NAME);
        Date createDate = shopHuajia.getCreateDate();
        //每天的数据只能有一条校验
        ShopHuajia checkDate = new ShopHuajia();
        checkDate.setCreateDate(createDate);
        List<ShopHuajia> checkList = shopHuajiaMapper.selectShopHuajiaList(checkDate);
        if(checkList.size()>0){
            return 0;
        }
        shopHuajia.setDay(DateUtil.getWeek(createDate));
        //设置总收入（当日营业额）
        shopHuajia.setGetAllMoney(allMoney);
        //设置当日净利润
        BigDecimal putHouseMoney = shopHuajia.getPutHouseMoney() == null ? BigDecimal.valueOf(0) : shopHuajia.getPutHouseMoney();
        BigDecimal putMoney = shopHuajia.getPutMoney() == null ? BigDecimal.valueOf(0) : shopHuajia.getPutMoney();
        //净利润 总收入-房租-进货
        BigDecimal actualMoney = allMoney.subtract(putHouseMoney).subtract(putMoney);
        shopHuajia.setActualMoney(actualMoney);
        //shuo_Salary 净利润 * 1/3 向下取整
        double a = 1;
        double b = 3;
        double c = a/b;
        BigDecimal shuoSalary = actualMoney.divide( BigDecimal.valueOf(3),0,BigDecimal.ROUND_DOWN).setScale(0,BigDecimal.ROUND_DOWN);
        //guo_salary 净利润 * 2/3 向下取整
        double d = 2;
        double e = 3;
        double f = d/e;
        BigDecimal guoSalary = actualMoney.divide( BigDecimal.valueOf(3),0,BigDecimal.ROUND_DOWN).multiply(BigDecimal.valueOf(2)).setScale(0,BigDecimal.ROUND_DOWN);
        shopHuajia.setShuoSalary(shuoSalary);
        shopHuajia.setGuoSalary(guoSalary);
        return shopHuajiaMapper.insertShopHuajia(shopHuajia);
    }

    /**
     * 修改huajia
     * 
     * @param shopHuajia huajia
     * @return 结果
     */
    @Override
    public int updateShopHuajia(ShopHuajia shopHuajia)
    {
        BigDecimal getPayMoney = shopHuajia.getGetPayMoney() == null ? BigDecimal.valueOf(0) : shopHuajia.getGetPayMoney();
        BigDecimal getWechatMoney = shopHuajia.getGetWechatMoney() == null ? BigDecimal.valueOf(0) : shopHuajia.getGetWechatMoney();
        BigDecimal getMoney = shopHuajia.getGetMoney() == null ? BigDecimal.valueOf(0) : shopHuajia.getGetMoney();
        //总收入=微信+支付宝+现金
        BigDecimal allMoney = getPayMoney.add(getWechatMoney).add(getMoney);
        shopHuajia.setName(NAME);
        Date createDate = shopHuajia.getCreateDate();
        //每天的数据只能有一条校验
        ShopHuajia checkDate = new ShopHuajia();
        checkDate.setCreateDate(createDate);
        List<ShopHuajia> checkList = shopHuajiaMapper.selectShopHuajiaList(checkDate);
        if(checkList.size()>0){
            if(!checkList.get(0).getId().equals(shopHuajia.getId())){
                return 0;
            }
        }
        shopHuajia.setDay(DateUtil.getWeek(createDate));
        //设置总收入（当日营业额）
        shopHuajia.setGetAllMoney(allMoney);
        //设置当日净利润
        BigDecimal putHouseMoney = shopHuajia.getPutHouseMoney() == null ? BigDecimal.valueOf(0) : shopHuajia.getPutHouseMoney();
        BigDecimal putMoney = shopHuajia.getPutMoney() == null ? BigDecimal.valueOf(0) : shopHuajia.getPutMoney();
        //净利润 总收入-房租-进货
        BigDecimal actualMoney = allMoney.subtract(putHouseMoney).subtract(putMoney);
        shopHuajia.setActualMoney(actualMoney);
        //shuo_Salary 净利润 * 1/3 向下取整
//        double a = 1;
//        double b = 3;
//        double c = a/b;
        BigDecimal shuoSalary = actualMoney.divide( BigDecimal.valueOf(3),0,BigDecimal.ROUND_DOWN).setScale(0,BigDecimal.ROUND_DOWN);
        //guo_salary 净利润 * 2/3 向下取整
//        double d = 2;
//        double e = 3;
//        double f = d/e;
        BigDecimal guoSalary = actualMoney.divide( BigDecimal.valueOf(3),0,BigDecimal.ROUND_DOWN).multiply(BigDecimal.valueOf(2)).setScale(0,BigDecimal.ROUND_DOWN);
        shopHuajia.setShuoSalary(shuoSalary);
        shopHuajia.setGuoSalary(guoSalary);
        return shopHuajiaMapper.updateShopHuajia(shopHuajia);
    }

    /**
     * 批量删除huajia
     * 
     * @param ids 需要删除的huajia主键
     * @return 结果
     */
    @Override
    public int deleteShopHuajiaByIds(Long[] ids)
    {
        return shopHuajiaMapper.deleteShopHuajiaByIds(ids);
    }

    /**
     * 删除huajia信息
     * 
     * @param id huajia主键
     * @return 结果
     */
    @Override
    public int deleteShopHuajiaById(Long id)
    {
        return shopHuajiaMapper.deleteShopHuajiaById(id);
    }

    @Override
    public HuaJiaTongJiVo selectTongJi(ShopHuajia shopHuajia) {
        return shopHuajiaMapper.selectTongJi(shopHuajia);
    }

    @Override
    public ZheXianTongJiVo selectZheXianTongJi(ShopHuajia shopHuajia) {
        ZheXianTongJiVo vo = new ZheXianTongJiVo();
        List<Map> huaJiaList = shopHuajiaMapper.selectZheXianTongJi(shopHuajia);
        List<String> dataList = new ArrayList<>();
        List<String> getAllList = new ArrayList<>();
        List<String> getActualList = new ArrayList<>();
        List<String> shuoList = new ArrayList<>();
        List<String> guoList = new ArrayList<>();
        for(Map data:huaJiaList){
            String createDatedata= data.get("create_date")+"";
            dataList.add(createDatedata);
            String getAllMoney= data.get("get_all_money")+"";
            getAllList.add(getAllMoney);
            String actualMoney= data.get("actual_money")+"";
            getActualList.add(actualMoney);
            String guoSalary= data.get("guo_salary")+"";
            guoList.add(guoSalary);
            String shuoSalary= data.get("shuo_salary")+"";
            shuoList.add(shuoSalary);
        }
        vo.setDataList(dataList);
        vo.setGetActualList(getActualList);
        vo.setGetAllList(getAllList);
        vo.setShuoList(shuoList);
        vo.setGuoList(guoList);

        HuaJiaTongJiVo huaJiaTongJiVo = shopHuajiaMapper.selectTongJi(shopHuajia);
        vo.setGetAllMoney(huaJiaTongJiVo.getCurGetAllMoney());
        vo.setGetActualMoney(huaJiaTongJiVo.getCurActualMoney());
        vo.setPutAllMoney(huaJiaTongJiVo.getCurPutAllMoney());
        vo.setGuoSalary(huaJiaTongJiVo.getCurFengMoney());
        vo.setShuoSalary(huaJiaTongJiVo.getCurGengMoney());
        vo.setAllSalary(huaJiaTongJiVo.getCurFengaAndShuoMoney());
        vo.setPutHouseMoney(huaJiaTongJiVo.getCurPutHouseMoney());
        return vo;
    }

}
