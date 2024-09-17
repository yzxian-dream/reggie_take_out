package com.yzx.reggie.entity;

import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 菜品管理
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Dish {
        private static final long serialVersionUID = 1L;
        /**
         * 主键
         */
        private Long id;

        /**
         * 菜品名称
         */
        private String name;

        /**
         * 菜品分类id
         */
        private Long categoryId;

        /**
         * 菜品价格
         */
        private BigDecimal price;

        /**
         * 商品码
         */
        private String code;

        /**
         * 图片
         */
        private String image;

        /**
         * 描述信息
         */
        private String description;

        /**
         * 0 停售 1 起售
         */
        private Integer status;

        /**
         * 顺序
         */
        private Integer sort;

        /**
         * 创建时间
         */
        private Date createTime;

        /**
         * 更新时间
         */
        private Date updateTime;

        /**
         * 创建人
         */
        private Long createUser;

        /**
         * 修改人
         */
        private Long updateUser;

        /**
         * 是否删除
         */
        private Integer isDeleted;
}