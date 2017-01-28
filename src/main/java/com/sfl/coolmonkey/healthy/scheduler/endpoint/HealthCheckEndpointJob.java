package com.sfl.coolmonkey.healthy.scheduler.endpoint;

import com.sfl.coolmonkey.healthy.service.endpoint.HealthCheckEndpointService;
import com.sfl.coolmonkey.notifications.api.client.rest.email.EmailResourceClient;
import com.sfl.coolmonkey.notifications.api.model.email.EmailModel;
import com.sfl.coolmonkey.notifications.api.model.email.request.EmailRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Client;
import javax.ws.rs.core.MediaType;
import java.util.Arrays;

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
    private EmailResourceClient emailResourceClient;

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
                performHealthCheckExceptionHandling(ex);
            }
        });
    }
    //endregion

    //region Utility methods
    private void performHealthCheckExceptionHandling(final RuntimeException ex) {
        LOGGER.error("{}", ex);
        final EmailModel emailModel = new EmailModel();
        emailModel.setTo("arthur.asatryan@sflpro.com");
        emailModel.setCc(Arrays.asList(
                "email1@mailinator.com",
                "email2@mailinator.com",
                "email3@mailinator.com"
        ));
        emailModel.setFrom("healthy@callmonkey.com");
        emailModel.setSubject("CoolMonkey - Healthy Microservice");
        emailModel.setContent(
                "<b>Message</b> - " + ex.getMessage() + "<br><b>Stack trace</b><br>" + Arrays.toString(ex.getStackTrace())
        );
        final EmailRequest request = new EmailRequest(emailModel);
        emailResourceClient.send(request);
    }
    //endregion

}
