package com.axellience.resiist.client.acquisitiontointegration.integration.authentication;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Oauth2HttpClient extends HttpClient
{
    private AccessToken accessToken;

    public Oauth2HttpClient()
    {
        super();
    }

    public Oauth2HttpClient(SslContextFactory sslContextFactory)
    {
        super(sslContextFactory);
    }

    public void login(String username, String password, String apiUrl)
            throws InterruptedException, TimeoutException, ExecutionException, IOException
    {
        Request tokenRequest = this.newRequest(apiUrl + "/oauth/token");
        tokenRequest.method(HttpMethod.POST);
        String encodeToString =
                Base64.getEncoder()
                      .encodeToString("indicator:indicator".getBytes(Charset.forName("UTF-8")));
        tokenRequest.getHeaders().add(HttpHeader.AUTHORIZATION, "Basic " + encodeToString);
        tokenRequest.getHeaders().add(HttpHeader.CONTENT_TYPE, "application/json");

        tokenRequest.param("username", username)
                    .param("password", password)
                    .param("grant_type", "password");
        ContentResponse response = tokenRequest.send();

        ObjectMapper objectMapper = new ObjectMapper();
        accessToken = objectMapper.readValue(response.getContentAsString(), AccessToken.class);
    }

    public boolean isAuthenticated()
    {
        return accessToken != null;
    }

    @Override
    public Request newRequest(URI uri)
    {
        Request newRequest = super.newRequest(uri);
        if (isAuthenticated())
            newRequest.getHeaders()
                      .add(HttpHeader.AUTHORIZATION, "Bearer " + accessToken.getValue());
        return newRequest;
    }
}
