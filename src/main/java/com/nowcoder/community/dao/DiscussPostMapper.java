package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 不白而痴
 * @version 1.0
 * @date 2020/11/17 15:58
 */
@Mapper
public interface DiscussPostMapper {
    /**
     * 查询帖子列表
     * @param userId 默认为0
     * @param offset 起始行
     * @param limit  每页包含数据数
     * @param orderMode 排序方法
     * @return
     */
    List<DiscussPost> selectDiscussPosts(
            @Param("userId") Integer userId,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit,
            @Param("orderMode") Integer orderMode);

    /**
     * 总行数
     * @param userId
     * @return
     */
    int selectDiscussPostRows(@Param("userId") Integer userId);

    /**
     * 插入帖子
     * @param discussPost
     * @return
     */
    int insertDiscussPost(DiscussPost discussPost);

    /**
     * 按id查帖子
     * @param id
     * @return
     */
    DiscussPost selectDiscussPostById(@Param("id") Integer id);

    /**
     * 更新贴子的评论数量
     * @param id
     * @return
     */
    int updateCommentCount(@Param("id") Integer id,
                           @Param("commentCount") Integer commentCount);


    /**
     * 更改帖子类型
     * @param id
     * @param type
     * @return
     */
    int updateType(@Param("id") Integer id, @Param("type") Integer type);

    /**
     * 修改帖子状态
     * @param id
     * @param status
     * @return
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);

    int updateScore(@Param("id") int id, @Param("score") double score);
}
