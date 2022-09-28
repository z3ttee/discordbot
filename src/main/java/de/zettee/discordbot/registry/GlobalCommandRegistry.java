package de.zettee.discordbot.registry;

import de.zettee.discordbot.config.ClientConfigRef;
import discord4j.common.JacksonResources;
import discord4j.discordjson.json.ApplicationCommandData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.rest.RestClient;
import discord4j.rest.service.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
public class GlobalCommandRegistry implements ApplicationRunner {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final RestClient client;
    private final ClientConfigRef clientConfig;

    public GlobalCommandRegistry(RestClient client, ClientConfigRef config) {
        this.client = client;
        this.clientConfig = config;
    }

    //This method will run only once on each start up and is automatically called with Spring so blocking is okay.
    @Override
    public void run(ApplicationArguments args) {
        //Create an ObjectMapper that supported Discord4J classes
        final JacksonResources d4jMapper = JacksonResources.create();

        // Convenience variables for the sake of easier to read code below.
        PathMatchingResourcePatternResolver matcher = new PathMatchingResourcePatternResolver();
        final ApplicationService applicationService = client.getApplicationService();

        try {
            final Mono<Long> applicationIdMono = client.getApplicationId();
            final long applicationId = applicationIdMono.block();

            //Get our commands json from resources as command data
            List<ApplicationCommandRequest> commands = new ArrayList<>();
            for (Resource resource : matcher.getResources("commands/*.json")) {
                ApplicationCommandRequest request = d4jMapper.getObjectMapper().readValue(resource.getInputStream(), ApplicationCommandRequest.class);
                commands.add(request);
            }

            // Bulk overwrite commands. This is now idempotent, so it is safe
            // to use this even when only 1 command is changed/added/removed
            Flux<ApplicationCommandData> bulkOverwriteData;
            if(this.clientConfig.isProduction()) {
                final long guildId = this.clientConfig.getGuildId();
                bulkOverwriteData = applicationService.bulkOverwriteGuildApplicationCommand(applicationId, guildId, commands);
            } else {
                bulkOverwriteData = applicationService.bulkOverwriteGlobalApplicationCommand(applicationId, commands);
            }

            // Execute request and subscribe to events
            final String scope = this.clientConfig.isProduction() ? "GLOBAL" : "GUILD";
            bulkOverwriteData.doOnNext(ignore -> LOGGER.info("Successfully registered commands with scope " + scope))
                    .doOnError(e -> LOGGER.error("Failed to register commands with scope " + scope, e))
                    .subscribe();

        } catch (Exception exception) {
            this.LOGGER.error("Error occured while overwriting commands", exception);
        }
    }
}
