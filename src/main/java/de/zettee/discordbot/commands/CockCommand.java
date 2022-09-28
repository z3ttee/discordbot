package de.zettee.discordbot.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.entity.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CockCommand implements SlashCommand {

    @Override
    public String getName() {
        return "cock";
    }

    @Override
    public Mono<Void> handle(ChatInputInteractionEvent event) {

        if(event.getOption("name").isEmpty()) {
            return event.reply().withEphemeral(true).withContent("Please see command usage");
        }

        final ApplicationCommandInteractionOption targetOption = event.getOption("name").get();
        User user = targetOption.getValue().get().asUser().block();

        return event.reply().withContent("Cum cock" + user.getUsername());
    }
}
