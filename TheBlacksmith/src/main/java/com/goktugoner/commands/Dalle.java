package com.goktugoner.commands;

import com.goktugoner.DallEGenerator;
import com.goktugoner.ICommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
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
        String url = generator.returnImageUrl(prompt);
        if(!url.equals("failure")){
            MessageEmbed embed = createImageEmbed(url);
            event.getInteraction().getHook().editOriginal(String.valueOf(embed)).complete();

        }else{
            event.getInteraction().getHook().editOriginal("Failed to generate image.").complete();
        }
    }

    private MessageEmbed createImageEmbed(String url) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Image image = ImageIO.read(new File(url));
            ImageIO.write((RenderedImage) image, "jpg", baos);
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] imageBytes = baos.toByteArray();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("DALL-E image");
        builder.setImage("data:image/jpeg;base64," + base64Image);
        return builder.build();
    }
}
