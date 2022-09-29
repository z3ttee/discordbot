package de.zettee.discordbot.commands;

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

public interface UserCommand extends Command {
    Mono<Void> handleUserCommand(User sender, Mono<User> target, String command, ApplicationCommandInteractionEvent event);
}
