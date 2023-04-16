package com.goktugoner.commands;

import com.goktugoner.ChatGPTGenerator;
import com.goktugoner.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatGPT implements ICommand {
    @Override
    public String getName() {
        return "gpt";
    }

    @Override
    public String getDescription() {
        return "ask chatgpt";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> ChatGPTOptionsList = new ArrayList<>();
        ChatGPTOptionsList.add(new OptionData(OptionType.STRING, "prompt", "question to ask", true));
        return ChatGPTOptionsList;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        //write the option filter
        String prompt = Objects.requireNonNull(event.getOption("prompt")).getAsString();
        event.deferReply().queue();
        ChatGPTGenerator generator = new ChatGPTGenerator();
        String response = generator.generateResponse(prompt);
        if(!response.equals("failure")){
            event.getInteraction().getHook().editOriginal(response).complete();
        }else{
            event.getInteraction().getHook().editOriginal("I'm sorry, I was unable to generate a response for you.").complete();
        }
    }
}
