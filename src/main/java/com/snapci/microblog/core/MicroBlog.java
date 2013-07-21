package com.snapci.microblog.core;

import javax.validation.constraints.NotNull;

public class MicroBlog {
    private final Integer id;
    private final int userId;
    @NotNull
    private final String content;

    public MicroBlog(Integer id, int userId, String content) {
        this.id = id;
        this.userId = userId;
        this.content = content;
    }

    public MicroBlog(int userId, String content) {
        this(null, userId, content);
    }

    public int getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MicroBlog microBlog = (MicroBlog) o;

        if (userId != microBlog.userId) return false;
        if (!content.equals(microBlog.content)) return false;
        if (id != null ? !id.equals(microBlog.id) : microBlog.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + userId;
        result = 31 * result + content.hashCode();
        return result;
    }
}
