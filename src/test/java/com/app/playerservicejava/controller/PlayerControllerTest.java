package com.app.playerservicejava.controller;

import com.app.playerservicejava.exception.PlayersException;
import com.app.playerservicejava.model.Player;
import com.app.playerservicejava.model.Players;
import com.app.playerservicejava.service.PlayerService;
import com.app.playerservicejava.service.chat.ChatClientService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;


import java.lang.reflect.Array;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlayerController.class)
class PlayerControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    private PlayerService playerService;

    @MockBean
    private ChatClientService chatClientService;

    @Test
    void getAllPLayersShouldReturnListOfPlayers() throws Exception{
        //Given
        Player p1 = new Player(); p1.setPlayerId("p1");
        Player p2 = new Player(); p2.setPlayerId("p2");
        Players players = new Players();
        players.setPlayers(Arrays.asList(p1, p2));

        //When
        Mockito.when(playerService.getAllPlayers()).thenReturn(players);

        mockMvc.perform(get("/v1/players/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.players").isArray())
                .andExpect(jsonPath("$.players.length()").value(2))
                .andExpect(jsonPath("$.players[0].playerId").value("p1"));
    }

    @Test
    void getALlPlayersShoudlRetrunEmptyList() throws Exception{
        Players players = new Players();
        Mockito.when(playerService.getAllPlayers()).thenReturn(players);

        mockMvc.perform(get("/v1/players/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.players").isArray())
                .andExpect(jsonPath("$.players.length()").value(0));
    }

    @Test
    void getPlayerByIdShouldReturnPlayer() throws Exception{
        Player player = new Player();
        player.setPlayerId("p1");
        player.setFirstName("Frank");

        Mockito.when(playerService.getPlayerById("p1")).thenReturn(player);

        mockMvc.perform(get("/v1/players/p1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.playerId").value("p1"))
                .andExpect(jsonPath("$.firstName").value("Frank"));
    }

    @Test
    void getPlayerByIdShouldReturnNotFound() throws Exception{
        Mockito.when(playerService.getPlayerById("not_exist"))
                .thenThrow(new PlayersException(HttpStatus.NOT_FOUND, "Player Not found with Id: not_exist"));

        mockMvc.perform(get("/v1/players/not_exist"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Player Not found with Id: not_exist"))
                .andExpect(jsonPath("$.status").value(404));
    }


    @Test
    void addNewPlayerShouldReturnCreated() throws Exception {
        Player player = new Player();
        player.setPlayerId("p123");
        player.setFirstName("Frank");

        Mockito.when(playerService.addNewPlayer(any(Player.class))).thenReturn(player);

        mockMvc.perform(post("/v1/players")
                        .contentType("application/json")
                        .content("""
                {
                  "playerId": "p123",
                  "firstName": "Frank"
                }
            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.playerId").value("p123"))
                .andExpect(jsonPath("$.firstName").value("Frank"));
    }

    @Test
    void addNewPlayerShouldReturnBadRequestIfPlayerExists() throws Exception {
        Mockito.when(playerService.addNewPlayer(any(Player.class)))
                .thenThrow(new PlayersException(HttpStatus.BAD_REQUEST, "Player Already Exists"));

        mockMvc.perform(post("/v1/players")
                        .contentType("application/json")
                        .content("""
                {
                  "playerId": "p123",
                  "firstName": "Frank"
                }
            """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Player Already Exists"));
    }


    @Test
    void updatePlayerShouldReturnSuccess() throws Exception {
        Mockito.when(playerService.updatePlayer(eq("p123"), any(Player.class))).thenReturn(true);

        mockMvc.perform(put("/v1/players/p123")
                        .contentType("application/json")
                        .content("""
                {
                  "firstName": "Updated"
                }
            """))
                .andExpect(status().isOk())
                .andExpect(content().string("Player Details Updated"));
    }

    @Test
    void updatePlayerShouldReturnNotFoundIfPlayerMissing() throws Exception {
        Mockito.doThrow(new PlayersException(HttpStatus.NOT_FOUND, "Player Not Found"))
                .when(playerService).updatePlayer(eq("p123"), any(Player.class));

        mockMvc.perform(put("/v1/players/p123")
                        .contentType("application/json")
                        .content("""
                {
                  "firstName": "Updated"
                }
            """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Player Not Found"));
    }



    @Test
    void deletePlayerShouldReturnSuccess() throws Exception {
        Mockito.when(playerService.deletePlayer("p123")).thenReturn(true);

        mockMvc.perform(delete("/v1/players/p123"))
                .andExpect(status().isOk())
                .andExpect(content().string("Player deleted"));
    }


    @Test
    void deletePlayerShouldReturnNotFoundIfMissing() throws Exception {
        Mockito.doThrow(new PlayersException(HttpStatus.NOT_FOUND, "Player Not Found"))
                .when(playerService).deletePlayer("p123");

        mockMvc.perform(delete("/v1/players/p123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Player Not Found"));
    }




}