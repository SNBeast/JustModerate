package com.github.SNBeast.JustModerate;

import discord4j.core.*;
import discord4j.core.event.domain.message.*;
import discord4j.core.object.entity.*;
import discord4j.core.object.entity.channel.*;

public class Main {
    private static boolean updaterLaunch = false;
    public static void main(final String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar JustModerate.jar token");
            System.exit(0);
        }

        final GatewayDiscordClient gateway = DiscordClient.create(args[0]).login().block();

        if (args.length > 1) {
            if (args[1].equals("--updater-launch")) {
                updaterLaunch = true;
            }
        }

        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            Message message = event.getMessage();
            if ("!exit".equals(message.getContent())) {
                MessageChannel channel = message.getChannel().block();
                channel.createMessage("Exiting with code 0.").block();
                System.exit(0);
            }
            if ("!update".equals(message.getContent())) {
                MessageChannel channel = message.getChannel().block();
                if (updaterLaunch) {
                    channel.createMessage("Exiting with code 1 (restart signal for updater).").block();
                    System.exit(1);
                }
                else {
                    channel.createMessage("Update operation not supported: not launched from updater.").block();
                }
            }
            if ("!ping".equals(message.getContent())) {
                MessageChannel channel = message.getChannel().block();
                channel.createMessage("Pong!").block();
            }
        });

        gateway.onDisconnect().block();
    }
}
