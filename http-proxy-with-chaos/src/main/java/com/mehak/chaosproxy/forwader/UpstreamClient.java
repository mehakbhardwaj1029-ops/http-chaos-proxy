package com.mehak.chaosproxy.forwader;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;

public class UpstreamClient {

    private final String upStreamBaseUrl;
    private final HttpClient client;
    private static final Set<String> HOP_BY_HOP_HEADERS = Set.of(
            "connection",
            "host",
            "content-length",
            "transfer-encoding",
            "expect",
            "upgrade"
    );


    public UpstreamClient(String upStreamBaseUrl, HttpClient client) {
        this.upStreamBaseUrl = upStreamBaseUrl;
        this.client = client;
    }
    //accept request -> forwards it upstream -> write response back
    public void forward(HttpExchange exchange)throws IOException ,InterruptedException {

        URI targetUri = URI.create(upStreamBaseUrl + exchange.getRequestURI().toString());

        HttpRequest.Builder requestBuilder =
                HttpRequest.newBuilder()
                        .uri(targetUri)
                        .method(
                                exchange.getRequestMethod(),
                                HttpRequest.BodyPublishers
                                        .ofInputStream(
                                                exchange::getRequestBody
                                        )
                        );
        //copy headers
        exchange.getRequestHeaders()
                .forEach((key,values)-> {
                            if (HOP_BY_HOP_HEADERS.contains(key.toLowerCase())) {
                                return; // skip forbidden headers
                            }
                            for (String value : values) {
                                requestBuilder.header(key, value);
                            }
                        });
        HttpResponse<byte[]> response =
                client.send(
                        requestBuilder.build(),
                        HttpResponse.BodyHandlers.ofByteArray()
                );

        //write response back to client
        exchange.getResponseHeaders()
                .putAll(response.headers().map());

        exchange.sendResponseHeaders(
                response.statusCode(),
                response.body().length
        );

        exchange.getResponseBody()
                .write(response.body());

        exchange.close();
    }
}
