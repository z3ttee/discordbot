package de.zettee.discordbot.commands;

import de.zettee.discordbot.entities.Cock;
import de.zettee.discordbot.utils.CommandValue;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CockCommand implements SlashCommand, UserCommand {

    @Override
    public String getName() {
        return "cock";
    }

    @Override
    public Mono<Void> handleSlashCommand(Member sender, String command, List<ApplicationCommandInteractionOption> options, ApplicationCommandInteractionEvent event) {
        // Handle command for sender
        if(options.size() == 0) {
            final Cock cock = new Cock();
            return event.reply().withContent("Your cock size is: " + cock.getSize() + "cm\n\n" + cock);
        }

        event.reply("Calculating...").subscribe();

        List<Cock> cocks = new ArrayList<>();
        options.forEach((option) -> {
            final User user = CommandValue.getUser(option);
            if(user == null) return;

            cocks.add(new Cock(user));
        });

        return event.editReply(cocks.stream().map((cock) -> {
            final User owner = cock.getOwner();
            return owner.getUsername() + " [ " + cock.getSize() + "cm ]: " + cock;
        }).collect(Collectors.joining("\n"))).then();
    }

    @Override
    public Mono<Void> handleUserCommand(User sender, Mono<User> target, String command, ApplicationCommandInteractionEvent event) {
        return event.getInteraction().getGuild().map(guild -> {
            return sender.asMember(guild.getId()).map(member -> {
                return event.getInteraction().getCommandInteraction().map(interaction -> {
                    return this.handleSlashCommand(member, command, interaction.getOptions(), event);
                });
            });
        }).then();
    }
}
