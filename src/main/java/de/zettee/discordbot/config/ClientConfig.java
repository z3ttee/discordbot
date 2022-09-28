package de.zettee.discordbot.config;

public class ClientConfig extends ClientConfigRef {

    public ClientConfig() {
        super();
    }

    public void setProduction(boolean production) {
        this.isProduction = production;
    }

    public void setGuildId(long guildId) {
        this.guildId = guildId;
    }

    public ClientConfigRef toRef() {
        return this;
    }
}
