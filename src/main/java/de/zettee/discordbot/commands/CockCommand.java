package de.zettee.discordbot.commands;

import de.zettee.discordbot.entities.Cock;
import de.zettee.discordbot.utils.CommandValue;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionReplyEditSpec;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class CockCommand implements SlashCommand {

    @Override
    public String getName() {
        return "cock";
    }

    @Override
    public Mono<Void> handleSlashCommand(Member sender, String command, List<ApplicationCommandInteractionOption> options, ApplicationCommandInteractionEvent event) {
        // Handle command for sender
        if(options.size() == 0) {
            final Cock cock = new Cock();

            EmbedCreateSpec embedSpec = EmbedCreateSpec.builder()
                    .color(Color.MAGENTA)
                    .title(sender.getUsername() + "'s Cock (" + cock.getSize() + "cm)")
                    .description(cock.toString())
                    .build();

            return event.reply(sender.getMention() + " hat seinen Cock geleaked:")
                    .withEmbeds(embedSpec);
        }

        // Send first reply
        event.reply("Cock-Vergleich wird aufgestellt...").block(Duration.ofSeconds(2));

        // Create embed builder
        EmbedCreateSpec.Builder embedBuilder = EmbedCreateSpec.builder()
                .color(Color.MAGENTA)
                .title("Ultimativer Cock-Vergleich");

        // Create cocks
        AtomicReference<Cock> largest = new AtomicReference<>(null);
        options.forEach((option) -> {
            final User user = CommandValue.getUser(option);
            if(user == null) return;

            final Cock cock = new Cock(user);
            if(largest.get() == null || largest.get().getSize() < cock.getSize()) {
                largest.set(cock);
            }

            embedBuilder.addField(cock.getOwner().getUsername() + "(" + cock.getSize() + "cm): ", cock.toString(), false);
        });

        // Construct reply
        return event.editReply(
            InteractionReplyEditSpec
                .builder()
                .contentOrNull("Gewonnen hat " + largest.get().getOwner().getMention() + " mit unglaublichen " + largest.get().getSize() + "cm")
                .addEmbed(embedBuilder.build())
                .build()
        ).then();
    }

}
