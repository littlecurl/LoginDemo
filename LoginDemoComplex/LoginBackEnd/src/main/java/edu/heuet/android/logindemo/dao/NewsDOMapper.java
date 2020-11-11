package edu.heuet.android.logindemo.dao;

import edu.heuet.android.logindemo.dataobject.NewsDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface NewsDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table news_info
     *
     * @mbg.generated Wed Jun 17 23:26:31 GMT+08:00 2020
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table news_info
     *
     * @mbg.generated Wed Jun 17 23:26:31 GMT+08:00 2020
     */
    int insert(NewsDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table news_info
     *
     * @mbg.generated Wed Jun 17 23:26:31 GMT+08:00 2020
     */
    int insertSelective(NewsDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table news_info
     *
     * @mbg.generated Wed Jun 17 23:26:31 GMT+08:00 2020
     */
    NewsDO selectByPrimaryKey(Long id);

    List<NewsDO> selectByTitle(@Param("title") String title);

    List<NewsDO> selectAll();
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table news_info
     *
     * @mbg.generated Wed Jun 17 23:26:31 GMT+08:00 2020
     */
    int updateByPrimaryKeySelective(NewsDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table news_info
     *
     * @mbg.generated Wed Jun 17 23:26:31 GMT+08:00 2020
     */
    int updateByPrimaryKey(NewsDO record);
}