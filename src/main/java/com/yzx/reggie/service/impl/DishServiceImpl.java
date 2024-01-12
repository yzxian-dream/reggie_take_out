package com.yzx.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yzx.reggie.dto.DishDto;
import com.yzx.reggie.entity.Dish;
import com.yzx.reggie.entity.DishFlavor;
import com.yzx.reggie.mapper.DishMapper;
import com.yzx.reggie.service.DishFlavorService;
import com.yzx.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    /**
     * 新增菜品，同时保存对应的口味数据，设计两张表
     * @param dishDto
     */
    @Autowired
    private DishFlavorService dishFlavorService;
    @Override
    //由于涉及到多张表的操作，需要加上事物控制注解,启动类也要开启事务支持
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //新增菜品，保存菜品的基本信息到菜品表dish
        this.save(dishDto);//这里由于dishDto继承自dish类，所以可以传进去

        //dishDto里的flavors字段子集合里前端只给了name和value值，但是没有dishId，这个需要我们自己包装
        //通过stream流来赋值，也可以for循环
        //上面保存进数据库后就会有mybatisplus自动通过雪花算法生成主键Id也就是dishId
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 查询菜品和口味
     * @param id
     * @return
     */
    @Override
    public DishDto getbyIdWithFlavor(Long id) {
        //查询菜品基本信息，dish表
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //查询当前菜品对应的口味信息，从dish_flavor表查
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> dishFlavor = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(dishFlavor);
        return dishDto;

    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);
        //清理当前菜品对应口味数据---dish——flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //添加当前提交过来的口味数据---dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

}
