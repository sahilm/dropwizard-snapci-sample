package com.snapci.microblog;

import com.snapci.microblog.jdbi.MicroBlogDAO;
import com.snapci.microblog.jdbi.UserDAO;
import com.snapci.microblog.resources.MicroBlogResource;
import com.snapci.microblog.resources.UserResource;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.jdbi.DBIFactory;
import com.yammer.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import com.yammer.dropwizard.migrations.MigrationsBundle;
import org.skife.jdbi.v2.DBI;

public class MicroBlogService extends Service<MicroBlogConfiguration> {
    public static void main(String... args) throws Exception {
        new MicroBlogService().run(args);
    }

    @Override
    public void initialize(Bootstrap bootstrap) {
        bootstrap.setName("microblog");
        bootstrap.addBundle(new MigrationsBundle<MicroBlogConfiguration>() {
            @Override
            public DatabaseConfiguration getDatabaseConfiguration(MicroBlogConfiguration config) {
                return config.getDatabaseConfiguration();
            }
        });
        bootstrap.addBundle(new DBIExceptionsBundle());
    }


    @Override
    public void run(MicroBlogConfiguration config, Environment environment) throws Exception {
        DBIFactory factory = new DBIFactory();
        DBI jdbi = factory.build(environment, config.getDatabaseConfiguration(), "postgresql");
        UserDAO userDAO = jdbi.onDemand(UserDAO.class);
        environment.addResource(new UserResource(userDAO));
        MicroBlogDAO microBlogDAO = jdbi.onDemand(MicroBlogDAO.class);
        environment.addResource(new MicroBlogResource(microBlogDAO, userDAO));
    }
}
