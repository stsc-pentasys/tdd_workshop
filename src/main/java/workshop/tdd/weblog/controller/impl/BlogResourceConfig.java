package workshop.tdd.weblog.controller.impl;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

/**
 * Integrates Jersey with Spring.
 */
@Component
public class BlogResourceConfig extends ResourceConfig {
    /**
     * Add all Jersey managed resources.
     */
    public BlogResourceConfig() {
    }
}
