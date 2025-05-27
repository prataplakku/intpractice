package com.app.playerservicejava.service.chat;

import com.app.playerservicejava.model.Player;
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.models.Model;
import io.github.ollama4j.models.OllamaResult;
import io.github.ollama4j.types.OllamaModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.github.ollama4j.utils.OptionsBuilder;
import io.github.ollama4j.utils.PromptBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@Service
public class ChatClientService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatClientService.class);

    @Autowired
    private OllamaAPI ollamaAPI;

    public List<Model> listModels() throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        List<Model> models = ollamaAPI.listModels();
        return models;
    }

    public String chat() throws OllamaBaseException, IOException, InterruptedException {
        String model = OllamaModelType.TINYLLAMA;

        // https://ollama4j.github.io/ollama4j/intro
        PromptBuilder promptBuilder =
                new PromptBuilder()
                        .addLine("Recite a haiku about recursion.");

        boolean raw = false;
        OllamaResult response = ollamaAPI.generate(model, promptBuilder.build(), raw, new OptionsBuilder().build());
        return response.getResponse();
    }

    public String generatePlayerSummary(Player player){
        String model = OllamaModelType.TINYLLAMA;

        String prompt = String.format("""
                Create a short 3 line bio summary for a player with the following details:
                Name: %s %s
                Birth: %s, %s, %s
                Debut: %s
                Final Game: %s,
                Bats: %s | Throws: %s
                """,player.getFirstName(), player.getLastName(), player.getBirthMonth(), player.getBirthDay(),player.getBirthYear(), player.getDebut() , player.getFinalGame(), player.getBats(), player.getThrowStats());

        try{
            OllamaResult result = ollamaAPI.generate(model, new PromptBuilder().addLine(prompt).build(), false, new OptionsBuilder().build());
            return result.getResponse();
        } catch (Exception e){
            LOGGER.error("Error generating player summary", e);
            return "Error generating summary: " + e.getMessage();
        }

    };


}
