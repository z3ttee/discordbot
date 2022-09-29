package de.zettee.discordbot.commands;

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.entity.Member;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class PingCommand implements SlashCommand {
    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public Mono<Void> handleSlashCommand(Member sender, String command, List<ApplicationCommandInteractionOption> options, ApplicationCommandInteractionEvent event) {
        return event.reply().withEphemeral(true).withContent("Pong!");
    }
}
