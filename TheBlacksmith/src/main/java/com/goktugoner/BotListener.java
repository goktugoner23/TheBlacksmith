package com.goktugoner;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class BotListener extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) { //ignore bot for not entering into infinite message loops
            return;
        }
        System.out.println("I received a message from " + event.getAuthor().getName() + ":" + event.getMessage().getContentDisplay());
        String messageReceived = event.getMessage().getContentRaw();
        //standart message tryout
        if (messageReceived.equals("!ney")) {
            event.getChannel().sendMessage("Babana gotten mi gireyim(yatay)?").queue();
        }
        //purge the channel, will go into interactioneventlistener - U GOT CANCELED
        if (messageReceived.equals("!purge")) {
            List<Message> messageList = event.getChannel().getHistory().retrievePast(10).complete();
            event.getChannel().asTextChannel().deleteMessages(messageList).queue();
        }
    }
}
