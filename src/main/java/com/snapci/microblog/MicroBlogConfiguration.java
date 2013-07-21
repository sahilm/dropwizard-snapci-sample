package com.snapci.microblog;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;
import com.yammer.dropwizard.db.DatabaseConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class MicroBlogConfiguration extends Configuration {

    @JsonProperty
    private String dummy;

    @Valid
    @NotNull
    private DatabaseConfiguration database = MicroBlogDatabaseConfiguration.create(System.getenv("DATABASE_URL"));

    public DatabaseConfiguration getDatabaseConfiguration() {
        return database;
    }
}
