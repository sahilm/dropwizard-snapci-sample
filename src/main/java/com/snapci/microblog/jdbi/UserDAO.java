package com.snapci.microblog.jdbi;

import com.snapci.microblog.core.User;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.sql.SQLException;

@RegisterMapper(UserMapper.class)
public interface UserDAO {
    @SqlUpdate("insert into users (name) values (:name)")
    Integer create(@BindBean User user);

    @SqlQuery("select * from users where name = :name")
    User findByName(@Bind("name") String name);
}

