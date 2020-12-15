package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @author 不白而痴
 * @version 1.0
 * @date 2020/12/3 14:12
 * @Description 帖子控制器
 */
@Controller
@RequestMapping("/discuss")
public class DicussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 添加帖子
     * @param title
     * @param content
     * @return
     */
    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = hostHolder.getUser();
        if(user==null){
            return CommunityUtil.getJSONString(403, "没有登录!");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        Map<String, Object> map=new HashMap<>();
        map.put("row",discussPostService.addDiscussPost(post));
        // 报错的情况,将来统一处理.
        map.get("row");
        return CommunityUtil.getJSONString(0, "发布成功!",map);
    }

    /**
     *
     * @param id
     * @param model
     * @param page
     * @return
     */
    @RequestMapping(path = "/detail/{id}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("id") Integer id, Model model, Page page){
        //帖子
        DiscussPost discussPostById = discussPostService.findDiscussPostById(id);
        model.addAttribute("post",discussPostById);
        //作者
        User userById = userService.findUserById(discussPostById.getUserId());
        model.addAttribute("user",userById);
        //评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/"+id);
        page.setRows(discussPostById.getCommentCount());
        //评论：帖子的评论
        //回复：评论的评论
        List<Comment> commentsByEntity =
                commentService.findCommentsByEntity(ENTITY_TYPE_POST, discussPostById.getId(), page.offsetget(), page.getLimit());
        //评论VO列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if(commentsByEntity!=null){
            for (Comment comment : commentsByEntity) {
                //评论VO
                Map<String, Object> commentVo = new HashMap<>();
                commentVo.put("comment",comment);
                commentVo.put("user",userService.findUserById(comment.getUserId()));
                //回复列表
                List<Comment> replyList =
                        commentService.findCommentsByEntity(ENTITY_TYPE_Comment, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList=new ArrayList<>();
                if(replyList!=null){
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo=new HashMap<>();
                        //回复
                        replyVo.put("reply",reply);
                        //回复作者
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        //回复目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target",target);
                        replyVoList.add(replyVo);
                    }
                    commentVo.put("replys",replyVoList);
                    //回复数量(这里我认为可以直接获取replyList的长度)
                    int replyCount = commentService.findCommentsCountByEntity(ENTITY_TYPE_Comment, comment.getId());
                    commentVo.put("replysCount",replyCount);
                }
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments",commentVoList);
        return "site/discuss-detail";
    }

}
