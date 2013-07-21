package com.snapci.microblog;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class SmokeIT {
    private final String APP_URL = getAppUrl(System.getenv("APP_URL"));

    @Test
    public void smokeIT() throws Exception {
        Request createTestUser = Request.Post(APP_URL + "users").bodyForm(Form.form().add("name", "tester").build());
        assertThat(
                createTestUser.execute().returnResponse().getStatusLine().getStatusCode(),
                equalTo(Response.Status.CREATED.getStatusCode()));

        Request createTestBlog = Request.Post(APP_URL + "tester/blog").bodyForm(Form.form().add("content", "First Post!").build());
        HttpResponse createTestBlogResponse = createTestBlog.execute().returnResponse();
        assertThat(
                createTestBlogResponse.getHeaders("Location")[0].getValue(),
                equalTo(APP_URL + "tester/blog/1"));


        Request getTestBlog = Request.Get(APP_URL + "tester/blog/1");
        assertThat(getTestBlog.execute().returnContent().asString(), equalTo("{\"id\":1,\"userId\":1,\"content\":\"First Post!\"}"));

        Request createAnotherTestBlog = Request.Post(APP_URL + "tester/blog").bodyForm(Form.form().add("content", "another one").build());
        createAnotherTestBlog.execute();

        Request getAllBlogs = Request.Get(APP_URL + "tester/blog");
        assertThat(getAllBlogs.execute().returnContent().asString(), equalTo("[{\"id\":2,\"userId\":1,\"content\":\"another one\"},{\"id\":1,\"userId\":1,\"content\":\"First Post!\"}]"));

    }

    private String getAppUrl(String appUrl) {
        if (appUrl == null) {
            return "http://localhost:8080/";
        }
        if (appUrl.endsWith("/")) {
            return appUrl;
        } else {
            return appUrl + "/";
        }
    }
}
