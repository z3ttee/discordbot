# Alliance Bot
This is the official repository for the Alliance Discord Bot

## Obtain Discord Client Token
In order for the bot to function, you have to register a new application on Discord's Developer Portal.
There you can also generate the invite url for the bot. Please note, that you have to tick the `bot` and
``applications.commands`` scopes.
 
## Installation
There are two options for installing and running the discord bot:

### Docker

```bash
docker run -e DISCORD_CLIENT_TOKEN=<YOUR_DISCORD_CLIENT_TOKEN> --name=discordbot --restart=always zettee/discordbot
```

### Run via Java
...