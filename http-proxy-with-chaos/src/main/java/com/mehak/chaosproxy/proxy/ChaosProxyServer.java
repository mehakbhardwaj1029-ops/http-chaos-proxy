package com.mehak.chaosproxy.proxy;

import com.mehak.chaosproxy.chaos.ChaosEngine;
import com.mehak.chaosproxy.chaos.ChaosRule;
import com.mehak.chaosproxy.forwader.UpstreamClient;
import com.mehak.chaosproxy.upstream.UpstreamServer;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChaosProxyServer{

    public static void main(String[] args) throws IOException {

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpServer server = HttpServer.create(new InetSocketAddress(1234),0);

        UpstreamClient upstreamClient = new UpstreamClient("http://localhost:9090",httpClient);

        //introducing chaos
        List<ChaosRule> rules = List.of(
                new ChaosRule("/slow",1000,0.0),
                new ChaosRule("/headers",0,0.3)
        );

        ChaosEngine chaosEngine = new ChaosEngine(rules);

        server.createContext("/",exchange -> {
            try{
                boolean chaosApplied = chaosEngine.applyChaos(exchange);
                if(!chaosApplied){
                    upstreamClient.forward(exchange);
                }
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
