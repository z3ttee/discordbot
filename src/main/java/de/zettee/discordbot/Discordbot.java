package de.zettee.discordbot;

import de.zettee.discordbot.config.ClientConfig;
import de.zettee.discordbot.config.ClientConfigRef;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.rest.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Discordbot {
    private final Logger logger = LoggerFactory.getLogger(Discordbot.class);

    public static final String TOKEN_ENV_KEY = "DISCORD_CLIENT_TOKEN";
    public static final String GUILDID_ENV_KEY = "GUILD_ID";
    public static final String ENVIRONMENT_ENV_KEY = "ENVIRONMENT";
    public static final int MAX_COCK_SIZE = 20;

    public static void main(String[] args) {
        // Start Spring application
        new SpringApplicationBuilder(Discordbot.class).build().run(args);
    }

    @Bean
    public GatewayDiscordClient gatewayDiscordClient(ApplicationContext context) {
        String discordClientToken = "";

        if(!System.getenv().containsKey(TOKEN_ENV_KEY)) {
            SpringApplication.exit(context, () -> 1);
            this.logger.error("No discord client token found: " + TOKEN_ENV_KEY + " environment variable not set.");
            this.logger.error("Please set the required environment variable " + TOKEN_ENV_KEY + " for the application to start.");
        } else {
            discordClientToken = System.getenv(TOKEN_ENV_KEY);
        }

        return DiscordClientBuilder.create(discordClientToken).build()
                .gateway()
                .setInitialPresence(ignore -> ClientPresence.online())
                .login()
                .block();
    }

    @Bean
    public RestClient discordRestClient(GatewayDiscordClient client) {
        return client.getRestClient();
    }

    @Bean
    public ClientConfigRef clientConfigRef() {
        final ClientConfig config = new ClientConfig();

        if(System.getenv().containsKey(GUILDID_ENV_KEY)) {
            config.setProduction(!(System.getenv(ENVIRONMENT_ENV_KEY).equalsIgnoreCase("development")));
        }

        if(System.getenv().containsKey(GUILDID_ENV_KEY)) {
            config.setGuildId(Long.parseLong(System.getenv(GUILDID_ENV_KEY)));
        }

        return config;
    }

}
