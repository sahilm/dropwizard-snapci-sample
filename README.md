# Setting up a complete deployment pipeline for your Dropwizard services

[Dropwizard](http://dropwizard.codahale.com/) is fast becoming the weapon of choice for deploying performant RESTful web services on the JVM platform. Snap makes it a breeze to fast track your Dropwizard services to Heroku with complete [deployment pipelines](http://martinfowler.com/bliki/DeploymentPipeline.html).

## Meet the sample Dropwizard service

For the purpose of this blog I have created a sample dropwizard service, appropriately titled [dropwizard-snapci-sample](https://github.com/sahilm/dropwizard-snapci-sample). It's a simple microblogging service API with two resources, User and MicroBlog. It speaks to Postgresql using the light [jdbi](http://jdbi.org/) library with [Liquibase](http://www.liquibase.org/) database migrations. The sample service is covered by unit tests and one end to end integration test. To get started, fork this sample service to your GitHub account.

## The first stage of our deployment pipeline - running unit tests and packaging the service

When you setup this repository in Snap for the first time, it will detect that it's a Maven project and create a single stage pipeline for you with an empty `mvn` command. Simply replace `mvn` with `mvn package` to run the unit tests and create our deployable microblog jar.

We're almost done with our first stage. Only exporting the build artifacts remain. Propagating build artifacts through the pipeline is an essential component of a deployment pipeline. It ensures that the deployable artifact remains unchanged throughout the pipeline. Essentially we ensure that what we test and build against is what we deploy.

Exporting an artifact is easy. Simply click on the artifacts tab and add a new artifact with the `target` directory. Snap will ensure that the `target` directory is available in all subsequent stages.

## The second stage - end to end integration tests

Ensuring that the service works with a real database and web server is vital to a reliable deployment pipeline. Up until now, our unit tests were mocking out database calls and we had no need to connect to a database server. However our integration tests require a database. Thus we need to tweak our Dropwizard service to connect to a Snap's Postgresql server.

Snap follows Heroku's convention of injecting a `DATABASE_URL` environment variable into each stage's execution environment. If you've not read it already it's highly recommended to check out the Heroku dev center article, [Connecting to Relational Databases on Heroku with Java](https://devcenter.heroku.com/articles/connecting-to-relational-databases-on-heroku-with-java). Briefly the `DATABASE_URL` is formatted like this `[database type]://[username]:[password]@[host]:[port]/[database name]`.

Our sample service is already setup to leverage the `DATABASE_URL` environment variable. The code to do this is located in [MicroBlogDatabaseConfiguration.java](https://github.com/sahilm/dropwizard-snapci-sample/blob/master/src/main/java/com/snapci/microblog/MicroBlogDatabaseConfiguration.java). Here's the meat of it:

```java
public class MicroBlogDatabaseConfiguration {
    final static Logger logger = LoggerFactory.getLogger(MicroBlogDatabaseConfiguration.class);

    public static DatabaseConfiguration create(String databaseUrl) {
        if (databaseUrl == null) {
            throw new IllegalArgumentException("The DATABASE_URL environment variable must be set before running the app " +
                    "example: DATABASE_URL=\"postgres://sahilm:sahilm@localhost:5432/microblog\"");
        }
        DatabaseConfiguration databaseConfiguration = null;
        try {
            URI dbUri = new URI(databaseUrl);
            String user = dbUri.getUserInfo().split(":")[0];
            String password = dbUri.getUserInfo().split(":")[1];
            String url = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
            databaseConfiguration = new DatabaseConfiguration();
            databaseConfiguration.setUser(user);
            databaseConfiguration.setPassword(password);
            databaseConfiguration.setUrl(url);
            databaseConfiguration.setDriverClass("org.postgresql.Driver");
        } catch (URISyntaxException e) {
            logger.info(e.getMessage());
        }
        return databaseConfiguration;
    }
}
```
Next, setting up the actual stage is easy. Here's what you do:
- Select Postgresql from the database dropdown.
- Create a new stage by clicking on Add new stage.
- Select custom stage.
- `java -jar target/microblog-0.0.1-SNAPSHOT.jar db migrate microblog.yml` runs the database migrations.
- `java -jar target/microblog-0.0.1-SNAPSHOT.jar server microblog.yml &>log &` starts Jetty.
- `mvn failsafe:integration-test failsafe:verify` runs our integration tests.

The `target` directory already contains our build artifact which was propagated by Snap from the previous stage.


## The final stage - deploying to Heroku

Deploying to Heroku is the easiest part. All we need to ensure is that the `web` task in our [Procfile](https://github.com/sahilm/dropwizard-snapci-sample/blob/master/Procfile) runs our database migrations and starts up Jetty. Here's how it looks:
`web: java $JAVA_OPTS -jar target/microblog*.jar db migrate microblog.yml && java $JAVA_OPTS -Ddw.http.port=$PORT -Ddw.http.adminPort=$PORT -jar target/microblog*.jar server microblog.yml`

To setup the stage:
- Create a new stage by click on Add new stage.
- Select Heroku deployment. If this your first time deploying to Heroku using Snap you will be asked to login via Heroku.
- Fill in your desired app name.
- Select auto deploy if you wish to deploy each commit to Heroku.
- And you're done!

That's all there's to it. We've setup a complete deployment pipeline for our Dropwizard service. Snap will track all subsequent commits to this repository and deploy all green builds to Heroku.
