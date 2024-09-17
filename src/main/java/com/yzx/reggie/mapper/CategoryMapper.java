package com.yzx.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yzx.reggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    /**
     * delete by primary key
     *
     * @param id primaryKey
     * @return deleteCount
     */
    int deleteByPrimaryKey(Long id);

    /**
     * insert record to table
     *
     * @param record the record
     * @return insert count
     */
    int insert(Category record);

    /**
     * insert record to table selective
     *
     * @param record the record
     * @return insert count
     */
    int insertSelective(Category record);

    /**
     * select by primary key
     *
     * @param id primary key
     * @return object by primary key
     */
    Category selectByPrimaryKey(Long id);

    /**
     * update record selective
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKeySelective(Category record);

    /**
     * update record
     *
     * @param record the updated record
     * @return update count
     */
    int updateByPrimaryKey(Category record);

    List<Category> findByAll(Category category);

    List<Category> findByNameAndType(@Param("name") String name, @Param("type") Integer type);

    List<Category> findByIdBetweenAndNameIn(@Param("minId") Long minId, @Param("maxId") Long maxId, @Param("nameCollection") Collection<String> nameCollection);
    int updateById(@Param("updated")Category updated,@Param("id")Long id);










}