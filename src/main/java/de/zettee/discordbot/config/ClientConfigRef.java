package de.zettee.discordbot.config;

public class ClientConfigRef {

    protected boolean isProduction = false;
    protected long guildId = 0L;

    public ClientConfigRef() {}

    public boolean isProduction() {
        return isProduction;
    }

    public long getGuildId() {
        return guildId;
    }

}
