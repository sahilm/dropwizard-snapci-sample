package com.snapci.microblog.jdbi;

import com.snapci.microblog.core.MicroBlog;
import com.snapci.microblog.core.User;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


public class MicroBlogMapper implements ResultSetMapper<MicroBlog> {
    public MicroBlog map(int index, ResultSet r, StatementContext ctx) throws SQLException {
        return new MicroBlog(r.getInt("id"), r.getInt("userId"), r.getString("content"));
    }
}

