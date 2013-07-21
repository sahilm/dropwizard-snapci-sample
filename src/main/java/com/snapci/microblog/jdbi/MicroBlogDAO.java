package com.snapci.microblog.jdbi;

import com.snapci.microblog.core.MicroBlog;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(MicroBlogMapper.class)
public interface MicroBlogDAO {
    @SqlUpdate("insert into \"microBlogs\" (\"userId\", content) values (:userId, :content)")
    @GetGeneratedKeys
    int create(@BindBean MicroBlog microBlog);

    @SqlQuery("select * from \"microBlogs\" where \"userId\" = :userId order by id desc")
    List<MicroBlog> findAllByUserId(@Bind("userId") Integer userId);

    @SqlQuery("select * from \"microBlogs\" where id = :id")
    MicroBlog findById(@Bind("id") Integer id);

}
