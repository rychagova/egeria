/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.serverchassis.springboot;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.odpi.openmetadata.adminservices.OMAGServerOperationalServices;
import org.odpi.openmetadata.adminservices.rest.SuccessMessageResponse;
import org.odpi.openmetadata.http.HttpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;


@SpringBootApplication
@ComponentScan(basePackages = {"${scan.packages}"})
@OpenAPIDefinition(
        info = @Info(
                title = "Egeria's Open Metadata and Governance (OMAG) Server Platform",
                version = "2.11-SNAPSHOT",
                description = "The OMAG Server Platform provides a runtime process and platform for Open Metadata and Governance (OMAG) Services.\n" +
                        "\n" +
                        "The OMAG services are configured and activated in OMAG Servers using the Administration Services.\n" +
                        "The configuration operations of the admin services create configuration documents, one for each OMAG Server.  " +
                        "Inside a configuration document is the definition of which OMAG services to activate in the server. " +
                        "These include the repository services (any type of server), the access services (for metadata access points " +
                        "and metadata servers), governance services (for governance servers) and view services (for view servers).  " +
                        "Once a configuration document is defined, the OMAG Server can be started and stopped multiple times by " +
                        "the admin services server instance operations.  \n" +
                        "\n" +
                        "The OMAG Server Platform also supports platform services to query details of the servers running on the platform.\n" +
                        "\n" +
                        "The OMAG Server Platform can host multiple OMAG servers at any one time. " +
                        "Each OMAG server is isolated within the server platform and so the OMAG server platform can be used to support multi-tenant " +
                        "operation for a cloud service, " +
                        "or host a variety of different OMAG Servers needed at a particular location.\n" +
                        "\n" +
                        "Click on the documentation link to find out more ...",
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0"),
                contact = @Contact(url = "https://egeria.odpi.org", name = "Egeria Project", email = "egeria-technical-discuss@lists.lfaidata.foundation")
        ),

        externalDocs = @ExternalDocumentation(description = "OMAG Server Platform documentation",
                url="https://egeria.odpi.org/open-metadata-implementation/admin-services/docs/user")
        )


@Configuration
public class OMAGServerPlatform
{
    @Value("${strict.ssl}")
    Boolean strictSSL;

    @Value("${startup.user}")
    String sysUser;

    @Value("${startup.server.list}")
    String startupServers;

    @Autowired
    private Environment env;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    private boolean triggeredRuntimeHalt = false;
    private String startupMessage = "";
    private OMAGServerOperationalServices operationalServices = new OMAGServerOperationalServices();

    private static final Logger log = LoggerFactory.getLogger(OMAGServerPlatform.class);

    public static void main(String[] args) {
        SpringApplication.run(OMAGServerPlatform.class, args);
    }

    @Bean
    public InitializingBean getInitialize()
    {
        return () -> {
            if (!strictSSL)
            {
                log.warn("strict.ssl is set to false! Invalid certificates will be accepted for connection!");
                HttpHelper.noStrictSSL();
            }
            autoStartConfig();
        };
    }

    /**
     * Extract the list of servers to auto start along with the administration userId.
     * The userId is in property "sysUser" and the list of server names are in property
     * "startupServers".  If either are null then no servers are auto started.
     */
    List<String>  getAutoStartList()
    {
        if (!startupServers.trim().isEmpty())
        {
            String[] splits = startupServers.split(",");
            //remove eventual duplicates
            HashSet<String> serverSet = new HashSet<>(Arrays.asList(splits));

            if (! serverSet.isEmpty())
            {
                return new ArrayList<>(serverSet);
            }
        }

        return null;
    }

    /**
     * Starts the servers specified in the startup.server.list property
     */
    private void autoStartConfig()
    {
        List<String>  servers = getAutoStartList();

        if (servers != null)
        {
            log.info("Startup detected for servers: {}", startupServers);
        }

        SuccessMessageResponse response = operationalServices.activateServerListWithStoredConfig(sysUser.trim(), servers);

        if (response.getRelatedHTTPCode() == 200)
        {
            startupMessage = response.getSuccessMessage();
        }
        else
        {
            startupMessage = response.getExceptionErrorMessage();

            StartupFailEvent customSpringEvent = new StartupFailEvent(this, startupMessage);
            applicationEventPublisher.publishEvent(customSpringEvent);
            triggeredRuntimeHalt = true;
        }
    }


    /**
     *  Deactivate all servers that were started automatically
     */
    private void temporaryDeactivateServers()
    {
        List<String>  servers = getAutoStartList();

        if (servers != null)
        {
            log.info("Temporarily deactivating the auto-started servers '{}'", servers.toString());

            System.out.println(new Date().toString() + " OMag Server Platform shutdown requested. Temporarily deactivating the following " +
                                       "auto-started servers: " + servers.toString());

            operationalServices.deactivateTemporarilyServerList(sysUser, servers);
        }
    }

    @Component
    public class ApplicationContextListener
    {

        @EventListener
        public void onApplicationEvent(ContextRefreshedEvent event)
        {
            System.out.println();
            System.out.println(OMAGServerPlatform.this.startupMessage);
            if(triggeredRuntimeHalt){
                Runtime.getRuntime().halt(43);
            }
            System.out.println(new Date().toString() + " OMAG server platform ready for more configuration");
        }

        @EventListener
        public void onApplicationEvent(ContextClosedEvent event)
        {
            temporaryDeactivateServers();
        }
    }

    @Component
    public class CustomSpringEventListener implements ApplicationListener<StartupFailEvent>
    {
        @Override
        public void onApplicationEvent(StartupFailEvent event) {
            log.info("Received startup fail event with message: {} " + event.getMessage());
            temporaryDeactivateServers();
        }

    }

    @PostConstruct
    private void configureTrustStore() {

        //making sure truststore was not set using JVM options
        // and strict.ssl is true ( if false, truststore will ignored anyway )
        if(strictSSL && System.getProperty("javax.net.ssl.trustStore")==null) {
            //load the 'javax.net.ssl.trustStore' and
            //'javax.net.ssl.trustStorePassword' from application.properties
            System.setProperty("javax.net.ssl.trustStore", env.getProperty("server.ssl.trust-store"));
            System.setProperty("javax.net.ssl.trustStorePassword", env.getProperty("server.ssl.trust-store-password"));
        }
    }

}
