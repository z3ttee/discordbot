package de.zettee.discordbot.utils;

import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.entity.User;

public class CommandValue {

    public static String getString(ApplicationCommandInteractionOption option) {
        if(option.getValue().isEmpty()) return null;
        return option.getValue().get().asString();
    }

    public static User getUser(ApplicationCommandInteractionOption option) {
        if(option.getValue().isEmpty()) return null;
        return option.getValue().get().asUser().block();
    }

}
