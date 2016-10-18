/*******************************************************************************
 * Copyright (c) 2015, 2016 Automation RockStars Ltd.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License v2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Contributors:
 *     Automation RockStars - initial API and implementation
 *******************************************************************************/
package com.automationrockstars.gir.rest;

import com.automationrockstars.design.gir.StorySteps;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.HttpClientConfig;
import com.jayway.restassured.config.RedirectConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.config.SSLConfig;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import java.nio.file.Paths;
import java.util.List;

import static com.automationrockstars.base.ConfigLoader.config;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

@StorySteps
public class GenericRestSteps {

    private void createToolFromProperties(){
        String baseUrl = config().getString("rest.base.uri");
        boolean followRedirects = config().getBoolean("rest.follow.redirects",true);
        boolean ignoreCerts = config().getBoolean("rest.ignore.certs",true);
        given.baseUri(baseUrl);
        given.config(RestAssuredConfig.config().redirect(RedirectConfig.redirectConfig().followRedirects(followRedirects)));
        if (ignoreCerts) given.config(RestAssuredConfig.config().sslConfig(new SSLConfig().allowAllHostnames()));
    }

    @Given("base uri %uri")
    public void setUri(String uri){
        given.baseUri(uri);
    }
RequestSpecification given = RestAssured.given();

    @Given("request header %name %value")
    public void setHeader(String name, String value){
        given.header(new Header(name, value));
    }

    @Given("request body %body")
    public void setBody(String bodyContent){
        given.body(bodyContent);
    }

    @Given("request content type %contentType")
    public void setContentType(String type){
        given.contentType(type);
    }

    @Given("request basic authentication user %user and pass %pass")
    public void setAuthentication(String user, String pass){
        given.authentication().basic(user, pass);
    }

    @Given("request get parameter %name %value")
    public void setParameter(String name, String value){
        given.parameter(name, value);
    }

    @Given("request file %filePath")
    public void setFile(String filePath){
        given.body(Paths.get(filePath).toFile());
    }
    @Given("request file %filePath with name %fileName")
    public void setFile(String filePath, String fileName){
        given.parameter(fileName, Paths.get(filePath).toFile());
    }

    @Given("request timeout %timeout")
    public void setTimeout(int timeout){
        given.config(RestAssuredConfig.config().httpClient(new HttpClientConfig().setParam("CONNECTION_MANAGER_TIMEOUT", timeout)));
    }

    protected Response response;

    @When("execute GET to %url")
    public void executeGet(String url){
        response = RestAssured.get(url);
    }

    @When("execute POST to %url")
    public void executePost(String url){
        response = RestAssured.post(url);
    }

    @When("execute HEAD to %url")
    public void executeHead(String url){
        response = RestAssured.head(url);
    }

    @When("execute PUT to %url")
    public void executePut(String url){
        response = RestAssured.put(url);
    }

    @When("execute DELETE to %url")
    public void executeDelete(String url){
        response = RestAssured.delete(url);
    }

    @Then("response code is %code")
    public void verifyResponseCode(String code){
        response.then().statusCode(Integer.parseInt(code));
    }

    @Then("response body contains %values")
    public void verifyResponseBody(List<String> values){
        assertThat(response.andReturn().body().toString(), containsString(values.get(0)));
    }
}
