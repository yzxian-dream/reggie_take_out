package com.yzx.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yzx.reggie.common.R;
import com.yzx.reggie.dto.DishDto;
import com.yzx.reggie.entity.Category;
import com.yzx.reggie.entity.Dish;
import com.yzx.reggie.entity.DishFlavor;
import com.yzx.reggie.service.CategoryService;
import com.yzx.reggie.service.DishFlavorService;
import com.yzx.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //这里page对象的范型是dish,是不满足页面展示要求的，因为菜品还要关联具体的categoryName，而这个entity只有categoryid是不行的
        //构建分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝,records字段就是从dish数据库分页查询出来的每一条数据的集合
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        //遍历集合，创建新的dishDto对象集合
        List<DishDto> list =  records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            //将查到的dish属性值通过对象拷贝转到dishDto类属性上
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            //根据id去category表查询分类对象
            Category category = categoryService.getById(categoryId);

            if (category != null){
                //将查到的categoryName值赋到dishDto上
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);

    }

    /**
     * 根据id查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getbyIdWithFlavor(id);
        return R.success(dishDto);
    }


    /**
     * 修改菜品
     * @param dishDto
     * @return
     */

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        //当更新菜品数据时，要清理菜品的缓存数据，以保证数据相对一致性，避免用户读到脏数据
        //清理所有相关缓存
//        Set keys = redisTemplate.keys("dish_*");
//        redisTemplate.delete(keys);
        //清理某个分类下的菜品缓存
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);

        return R.success("修改菜品成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        //构造查询条件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
//        //只查询状态为1的（起售状态）的菜品
//        queryWrapper.eq(Dish::getStatus,1);
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList = null;
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dishDtoList != null){
            return R.success(dishDtoList);
        }
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //只查询状态为1的（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        dishDtoList = list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }
}
