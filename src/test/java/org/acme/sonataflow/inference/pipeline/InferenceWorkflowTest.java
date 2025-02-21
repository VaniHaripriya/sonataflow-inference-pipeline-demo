package org.acme.sonataflow.inference.pipeline;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class InferenceWorkflowTest {

    private static final int REQUEST_TIMEOUT = 90_000;

    @Test
    void test() throws IOException {
        given().config(configRequest())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"image\" : \"src/main/resources/images/coco_image.jpg\"}").when()
                .when().post("/pipeline")
                .then()
                .statusCode(201)
                .body("workflowdata.output_image", is("src/main/resources/images/output_image.jpg"));

        Path overlaidImage = Paths.get("src/main/resources/images/output_image.jpg");
        assertThat(overlaidImage).exists();
        Files.delete(overlaidImage);
    }

    private static RestAssuredConfig configRequest() {
        return RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.socket.timeout", REQUEST_TIMEOUT)
                        .setParam("http.connection.timeout", REQUEST_TIMEOUT));
    }
}
