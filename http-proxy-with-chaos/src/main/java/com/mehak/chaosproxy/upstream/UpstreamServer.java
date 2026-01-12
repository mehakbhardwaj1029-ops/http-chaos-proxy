package com.mehak.chaosproxy.upstream;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UpstreamServer {

    public static void main(String[] args) throws IOException {

//        “Open port 9000 and start listening for HTTP connections.”
        HttpServer server = HttpServer.create(new InetSocketAddress(9090),0); //os decides backlog size

        //createContext means that when client hits this path do something
        server.createContext("/health",exchange -> {
            respond(exchange,200,"ok");
        });

        server.createContext("/echo",exchange -> {
            byte[] body = exchange.getRequestBody().readAllBytes();
            respond(exchange,200,new String(body, StandardCharsets.UTF_8));
        });

        server.createContext("/headers",exchange -> {
            StringBuilder sb = new StringBuilder();
            exchange.getRequestHeaders()
                    .forEach((k,v)->
                            sb.append(k).append(":").append(v).append("\n")
                    );
            respond(exchange,200,sb.toString());
        });

        server.createContext("/slow",exchange -> {
            try{
                Thread.sleep(500);
            }catch (InterruptedException e){}

            respond(exchange,200,"SLOW RESPONSE");

        });

        server.setExecutor(Executors.newFixedThreadPool(10)); //server can take max 10 concurrent requests
        server.start();

        System.out.println("Upstream server started on port http://localhost:9090");

    }

    private static void respond(HttpExchange exchange, int status, String body) throws IOException{

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status,bytes.length);
        try(OutputStream os =exchange.getResponseBody()){   //ensures stream is closed
            os.write(bytes);
        }
    }
}

//http server -> creates and runs server
//http exchange -> represents 1 request+response
//Inet socket address -> define IP + port

//standard charsets avoids platform dependent bugs
//executors -> controls concurrency

//health purpose:
//Health checks
//Monitoring
//Sanity verification

//echo purpose
//It lets you verify:
//Request forwarding
//Body integrity
//Proxy does not corrupt payloads

//headers purpose
//Later, corrupted headers, effect will be seen here.
//Inspect incoming headers
//Verify proxy header behavior

//slow purpose
//Simulate slow backend
//this delay is intentional to analyze backend slowness vs proxy-injected slowness