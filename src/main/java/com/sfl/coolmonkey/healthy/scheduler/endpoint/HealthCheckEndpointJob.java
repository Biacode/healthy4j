package com.sfl.coolmonkey.healthy.scheduler.endpoint;

import com.sfl.coolmonkey.healthy.service.endpoint.HealthCheckEndpointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;

/**
 * User: Arthur Asatryan
 * Company: SFL LLC
 * Date: 1/24/17
 * Time: 7:30 PM
 */
@Lazy(false)
@Component
public class HealthCheckEndpointJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(HealthCheckEndpointJob.class);

    //region Constants
    private static final String CRON_EVERY_MINUTE = "* 10 * * * ?";
    //endregion

    //region Dependencies
    @Autowired
    private Client jerseyClient;

    @Autowired
    private HealthCheckEndpointService healthCheckEndpointService;
    //endregion

    //region Constructors
    public HealthCheckEndpointJob() {
        LOGGER.debug("Initializing");
    }
    //endregion

    //region Public methods
    @Scheduled(cron = CRON_EVERY_MINUTE, zone = "Europe/Amsterdam")
    public void checkEndpoints() {
        healthCheckEndpointService.getAll().forEach(healthCheckEndpoint -> {
            try {
                jerseyClient
                        .target(healthCheckEndpoint.getUrl())
                        .request(MediaType.APPLICATION_JSON_TYPE)
                        .get();
            } catch (final RuntimeException ex) {
            }
        });
    }
    //endregion

}
