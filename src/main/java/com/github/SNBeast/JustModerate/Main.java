package com.github.SNBeast.JustModerate;

import discord4j.core.*;
import discord4j.core.event.domain.message.*;
import discord4j.core.object.entity.*;
import discord4j.core.object.entity.channel.*;

import java.util.*;

public class Main {
    private static boolean updaterLaunch = false;
    private static List<String> hostAuthorizedUsers;
    private static void sendMessage (String message, MessageChannel channel) {
        if (channel == null) {
            System.out.println("Warning: sendMessage with null channel.");
        }
        else {
            channel.createMessage(message).block();
        }
    }
    private static boolean checkIfAuthorizedByHost(Message message, MessageChannel channel) {
        if (message.getAuthor().isPresent()) {
            if (hostAuthorizedUsers.contains(message.getAuthor().get().getId().asString())) {
                return true;
            }
            else {
                sendMessage("Request denied: request author is not authorized by the bot host to perform the action.", channel);
            }
        }
        else {
            sendMessage("Request denied: request message has no author.", channel);
        }
        return false;
    }
    public static void main(final String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java -jar JustModerate.jar token");
            System.exit(0);
        }

        if (args.length > 1) {
            int hostAuthorizedStart = 1;
            if (args[1].equals("--updater-launch")) {
                updaterLaunch = true;
                hostAuthorizedStart++;
            }
            hostAuthorizedUsers = List.of(Arrays.copyOfRange(args, hostAuthorizedStart, args.length));
        }
        else {
            hostAuthorizedUsers = new ArrayList<>();
        }

        if (hostAuthorizedUsers.size() == 0) {
            System.out.println("Warning: There are no host-authorized users.");
        }

        final GatewayDiscordClient gateway = DiscordClient.create(args[0]).login().block();

        gateway.on(MessageCreateEvent.class).subscribe(event -> {
            Message message = event.getMessage();
            MessageChannel channel = message.getChannel().block();
            if ("!exit".equals(message.getContent())) {
                if (checkIfAuthorizedByHost(message, channel)) {
                    sendMessage("Exiting with code 0.", channel);
                    System.exit(0);
                }
            }
            else if ("!update".equals(message.getContent())) {
                if (checkIfAuthorizedByHost(message, channel)) {
                    if (updaterLaunch) {
                        sendMessage("Exiting with code 1 (restart signal for updater).", channel);
                        System.exit(1);
                    } else {
                        sendMessage("Update operation not supported: not launched from updater.", channel);
                    }
                }
            }
            else if ("!ping".equals(message.getContent())) {
                sendMessage("Pong!", channel);
            }
        });

        gateway.onDisconnect().block();
    }
}
