package kim.jungbin.gwbackend.model;

import java.io.Serializable;

public class RoutingProperty implements Serializable {
    private String targetUrl;

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }
}
