package com.goktugoner.commands;

import com.goktugoner.DallEGenerator;
import com.goktugoner.ICommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.EmbedBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dalle implements ICommand {
    @Override
    public String getName() {
        return "dalle";
    }

    @Override
    public String getDescription() {
        return "DALL-E image generator";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> DallEOptionsList = new ArrayList<>();
        DallEOptionsList.add(new OptionData(OptionType.STRING, "prompt", "image info to generate", true));
        return DallEOptionsList;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        //write the option filter
        String prompt = Objects.requireNonNull(event.getOption("prompt")).getAsString();
        event.deferReply().queue();
        DallEGenerator generator = new DallEGenerator();
        String imageUrl = generator.returnImageUrl(prompt);
        //make it in embed type
        if (!imageUrl.equals("failure")) {
            EmbedBuilder imageBuilder = new EmbedBuilder();
            imageBuilder.setImage(imageUrl);
            imageBuilder.setTitle("Image generation - " + prompt);
            event.getInteraction().getHook().editOriginalEmbeds(imageBuilder.build()).complete();
        } else {
            event.getInteraction().getHook().editOriginal("I'm sorry, I was unable to generate an image for you.").queue();
        }
    }
}
