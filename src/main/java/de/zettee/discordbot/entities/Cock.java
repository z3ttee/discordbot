package de.zettee.discordbot.entities;

import discord4j.core.object.entity.User;

public class Cock {

    private final int size;
    private User owner = null;
    private String segments = "";

    public Cock() {
        this.size = (int) ((Math.random() * 20) + 1);

        for (int i = 0; i < this.size; ++i) {
            this.segments = segments.concat("=");
        }
    }
    public Cock(User owner) {
        this.size = (int) ((Math.random() * 20) + 1);
        this.owner = owner;

        for(int i = 0; i < this.size; ++i) {
            this.segments = segments.concat("=");
        }
    }

    public int getSize() {
        return size;
    }

    public User getOwner() {
        return owner;
    }

    public String toString() {
        return "8" + this.segments + "D";
    }
}
