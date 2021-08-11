package com.hyp.service;

import com.hyp.dao.CommentRepository;
import com.hyp.po.Comment;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService{

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public List<Comment> listCommentByBlogId(Long blogId) {
        Sort sort = Sort.by("createTime");
        List<Comment> comments = commentRepository.findByBlogIdAndParentCommentNull(blogId,sort);
        return eachComment(comments);
    }

    @Transactional
    @Override
    public Comment saveComment(Comment comment) {
        Long parentCommentId = comment.getParentComment().getId();
        if(parentCommentId != -1){
            comment.setParentComment(commentRepository.findById(parentCommentId).get());
        }else{
            comment.setParentComment(null);
        }
        comment.setCreateTime(new Date());
        return commentRepository.save(comment);
    }


    /**
     * 循环每个根评论
     */
    private List<Comment> eachComment(List<Comment> comments){
        List<Comment> commentsView = new ArrayList<>();
        for(Comment comment:comments){
            Comment c = new Comment();
            BeanUtils.copyProperties(comment,c);
            commentsView.add(c);
        }
        combineChildren(commentsView);
        return commentsView;
    }
    /**
     * 合并根节点相同的所有子节点
     */
    private void combineChildren(List<Comment> comments){

        for(Comment comment:comments){
            List<Comment> replys1 = comment.getReplayComment();
            for(Comment reply1:replys1){
                recursively(reply1);
            }
            comment.setReplayComment(tempReplys);
            tempReplys = new ArrayList<>();
        }
    }
    private List<Comment> tempReplys = new ArrayList<>();
    /**
     *递归迭代下级子回复
     */
    private void recursively(Comment comment){
        tempReplys.add(comment);
        if(comment.getReplayComment().size()>0){
            List<Comment> replys = comment.getReplayComment();
            for(Comment reply:replys){
                tempReplys.add(reply);
                if(reply.getReplayComment().size()>0){
                    recursively(reply);
                }
            }
        }
    }
}
