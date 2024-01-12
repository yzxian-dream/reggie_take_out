package com.yzx.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yzx.reggie.entity.Category;
//这个接口里，如果mybatisplus里的IService里的方法不适用，可以在这里自定义合适的方法，然后在impl类里自行实现
public interface CategoryService extends IService<Category> {
    public void remove (Long id);
}
