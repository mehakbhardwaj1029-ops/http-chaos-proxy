package com.mehak.chaosproxy.proxy;

import com.mehak.chaosproxy.forwader.UpstreamClient;
import com.mehak.chaosproxy.upstream.UpstreamServer;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChaosProxyServer{

    public static void main(String[] args) throws IOException {

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpServer server = HttpServer.create(new InetSocketAddress(1234),0);

        UpstreamClient upstreamClient = new UpstreamClient("http://localhost:9090",httpClient);

        server.createContext("/",exchange -> {
            try{
                upstreamClient.forward(exchange);
            }catch (Exception e){
                e.printStackTrace();
                exchange.sendResponseHeaders(500,-1);  //upstream crash
            }
        });

        server.setExecutor(Executors.newFixedThreadPool(50));
        server.start();
        System.out.println("Chaos proxy running on http://localhost:1234");
    }
}
