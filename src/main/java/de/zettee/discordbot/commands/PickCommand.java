package de.zettee.discordbot.commands;

import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ButtonInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.presence.Presence;
import discord4j.core.object.presence.Status;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

@Component
public class PickCommand implements SlashCommand {

    private final GatewayDiscordClient client;

    public PickCommand(GatewayDiscordClient client) {
        this.client = client;
    }

    @Override
    public String getName() {
        return "pick";
    }

    @Override
    public Mono<Void> handleSlashCommand(Member sender, String command, List<ApplicationCommandInteractionOption> options, ApplicationCommandInteractionEvent event) {
        UUID onlineBtnId = UUID.randomUUID();
        UUID offlineBtnId = UUID.randomUUID();

        Button offlineBtn = Button.primary(offlineBtnId.toString(), "Alle");
        Button onlineBtn = Button.secondary(onlineBtnId.toString(), "Nur Online");

        AtomicBoolean wasTriggered = new AtomicBoolean(false);

        Mono<Void> tmpButtonListener = this.client.on(ButtonInteractionEvent.class, (btnEvent) -> {
            if(wasTriggered.get()) return Mono.empty();

            wasTriggered.set(true);
            final boolean onlineOnly = btnEvent.getCustomId().equals(onlineBtnId.toString());
            return pickRandomMember(btnEvent, onlineOnly).then();
        })
        .timeout(Duration.ofSeconds(15))
        .onErrorResume(TimeoutException.class, ignore -> {
            if(!wasTriggered.get()) {
                return event.createFollowup("Auswahl abgelaufen. Du musst den Befehl erneut starten.").withEphemeral(true).then();
            } else {
                return Mono.empty();
            }
        })
        .then();

        return event.reply()
                .withEphemeral(true)
                .withContent("Welcher Online-Status soll beim aufbauen des Pools berücksichtigt werden? (Auswahl läuft in 15s ab)")
                .withComponents(ActionRow.of(Arrays.asList(offlineBtn, onlineBtn)))
                .then(tmpButtonListener);
    }

    private Mono<Void> pickRandomMember(ButtonInteractionEvent event, boolean onlineOnly) {
        return event.deferReply().then(pick(event, onlineOnly)).then();
    }

    private Mono<Void> pick(ButtonInteractionEvent event, boolean onlineOnly) {
        final Guild guild = event.getInteraction().getGuild().block(Duration.ofSeconds(4));
        if(guild == null) return event.createFollowup("Derzeit gibt es Probleme bei der Verbindung zu Discord").then();

        final List<Member> members = guild.getMembers().collectList().block(Duration.ofSeconds(10));
        if(members == null) return event.createFollowup("Derzeit gibt es Probleme bei der Verbindung zu Discord").then();

        Stream<Member> poolStream = members.stream().filter((member) -> !member.isBot());
        if(onlineOnly) {
            poolStream = poolStream.filter((member -> {
                final Presence presence = member.getPresence().block(Duration.ofSeconds(1));

                return presence != null && presence.getStatus() != Status.OFFLINE;
            }));
        }

        Member member = poolStream.findAny().orElse(null);
        if(member == null) {
            return event.createFollowup("Es konnte kein Mitglied ausgewählt werden, da keiner auf die Kriterien zutrifft.").then();
        }

        return event.createFollowup(member.getMention() + " wurde ausgewählt!").then();
    }
}
