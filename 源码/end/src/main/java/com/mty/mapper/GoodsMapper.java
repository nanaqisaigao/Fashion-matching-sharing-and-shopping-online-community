package com.mty.mapper;

import com.mty.entity.Goods;
import com.mty.util.MyMapper;
import java.util.List;
import java.util.Map;

/**
 * 操作goodsDAO层接口
 */
public interface GoodsMapper extends MyMapper<Goods> {

    /**
     * 分页查询数据
     */
    List<Goods> queryAllByLimit(Map mp);

    /**
     * 通过实体作为筛选条件查询
     */
    List<Goods> queryCondition(Goods goods);

    /**
     * 通过ID查询单条数据
     */
    Goods queryById(Integer id);

    /**
     * 根据ID列表查询商品详情
     */
    List<Goods> queryByIds(List<Integer> ids);

    /**
     * 新增
     */
    int insert(Goods goods);

    /**
     * 修改
     */
    boolean update(Goods goods);

    /**
     * 删除
     */
    int deleteById(Integer id);

}