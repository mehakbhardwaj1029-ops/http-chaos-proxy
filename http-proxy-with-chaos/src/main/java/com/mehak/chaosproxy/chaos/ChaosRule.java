package com.mehak.chaosproxy.chaos;

public class ChaosRule {

    private final String path;
    private final int delaysMs;
    private final double failureRate;

    public ChaosRule(String path, int delaysMs, double failureRate) {
        this.path = path;
        this.delaysMs = delaysMs;
        this.failureRate = failureRate;
    }

    public String getPath() {
        return path;
    }

    public int getDelaysMs() {
        return delaysMs;
    }

    public double getFailureRate() {
        return failureRate;
    }
}
