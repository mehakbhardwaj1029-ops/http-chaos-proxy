package com.mehak.chaosproxy.chaos;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class ChaosEngine {

    private final List<ChaosRule> rules;
    private final Random random = new Random();

    public ChaosEngine(List<ChaosRule> rules) {
        this.rules = rules;
    }

    public boolean applyChaos(HttpExchange exchange) throws IOException{
        String path = exchange.getRequestURI().getPath();

        for(ChaosRule rule: rules){
            if(path.startsWith(rule.getPath())){
                //delay
                if(rule.getDelaysMs()>0){
                    try{
                        Thread.sleep(rule.getDelaysMs());
                    }catch (InterruptedException ignored){}
                }

                //failure injection
                if(random.nextDouble() < rule.getFailureRate()){
                    exchange.sendResponseHeaders(500,-1);
                    exchange.close();
                    return true;
                }

            }
        }
        return false;
    }
}
