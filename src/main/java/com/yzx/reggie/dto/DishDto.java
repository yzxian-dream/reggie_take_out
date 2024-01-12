package com.yzx.reggie.dto;

import com.yzx.reggie.entity.Dish;
import com.yzx.reggie.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * dto:data transfer object，用于封装页面提交的数据，用于展示层和服务层之间的数据传输
 * 前面要传输的对象（从前端传来的参数）都是和实体类一一对应的，那么直接用实体类接收即可，现在这里flavors不在实体类dish里，所以要设计专门的dto传输
 * 在dish的基础之上又扩展出来的属性
 */
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
