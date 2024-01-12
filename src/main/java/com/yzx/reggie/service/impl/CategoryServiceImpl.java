package com.yzx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yzx.reggie.common.CustomerException;
import com.yzx.reggie.entity.Category;
import com.yzx.reggie.entity.Dish;
import com.yzx.reggie.entity.Setmeal;
import com.yzx.reggie.mapper.CategoryMapper;
import com.yzx.reggie.service.CategoryService;
import com.yzx.reggie.service.DishService;
import com.yzx.reggie.service.SetmealService;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除分类，删前进行判断。这里是实现CategoryService接口里自己加的方法，里面也可以借助mybatisplus写好的方法作为辅助
     * @param id
     */

    @Override
    public void remove(Long id) {
        //添加查询条件
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        //这里时调用mybatisplus提供的方法
        int count1 = dishService.count(dishLambdaQueryWrapper);
        //查询当前分类是否关联了菜品，如果关联，抛出一个业务异常
        if (count1 > 0){
            throw new CustomerException("当前分类下关联了菜品，不能删除");
        }
        //查询当前分类是否关联了套餐，如果关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2 > 0){
            //抛出异常，使用自定义的异常类，检测到异常会进入全局异常处理器GlobalExceptionHandler
            throw new CustomerException("当前分类下关联了套餐，不能删除");
        }

        //正常删除分类,目前时处于category的service层的实现类，直接通过super调用mybatisplus的原生方法；
        super.removeById(id);



    }
}
