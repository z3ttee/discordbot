package de.zettee.discordbot.commands;

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.entity.Member;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * A simple interface defining our slash command class contract.
 *  a getName() method to provide the case-sensitive name of the command.
 *  and a handle() method which will house all the logic for processing each command.
 */
public interface SlashCommand extends Command {
    Mono<Void> handleSlashCommand(Member sender, String command, List<ApplicationCommandInteractionOption> options, ApplicationCommandInteractionEvent event);
}
