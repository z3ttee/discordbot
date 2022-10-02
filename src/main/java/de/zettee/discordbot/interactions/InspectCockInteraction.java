package de.zettee.discordbot.interactions;

import de.zettee.discordbot.commands.UserCommand;
import de.zettee.discordbot.entities.Cock;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class InspectCockInteraction implements UserCommand {

    @Override
    public String getName() {
        return "Inspect Cock";
    }

    @Override
    public Mono<Void> handleUserCommand(User sender, Mono<User> target, String command, ApplicationCommandInteractionEvent event) {
        final User resolvedTarget = target.block(Duration.ofSeconds(2));
        if(resolvedTarget == null) {
            return null;
        }

        final Cock cock = new Cock(resolvedTarget);

        EmbedCreateSpec embedSpec = EmbedCreateSpec.builder()
                .color(Color.MAGENTA)
                .title(resolvedTarget.getUsername() + "'s Cock (" + cock.getSize() + "cm)")
                .description(cock.toString())
                .build();

        if(resolvedTarget.getId().toString().equals(sender.getId().toString())) {
            return event.reply(sender.getMention() + " hat seinen Cock geleaked:")
                    .withEmbeds(embedSpec);
        } else {
            return event.reply(sender.getMention() + " hat den Cock von " + resolvedTarget.getMention() + " untersucht. Ergebnis:")
                    .withEmbeds(embedSpec);
        }
    }
}
