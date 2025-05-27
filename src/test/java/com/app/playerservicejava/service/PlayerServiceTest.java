package com.app.playerservicejava.service;

import com.app.playerservicejava.exception.PlayersException;
import com.app.playerservicejava.model.Player;
import com.app.playerservicejava.model.Players;
import com.app.playerservicejava.repository.PlayerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class PlayerServiceTest {
    @InjectMocks
    private PlayerService playerService;

    @Mock
    private PlayerRepository playerRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllPlayersShouldReturnAllPlayers() {
        //Given
            Player p1 = new Player();
            Player p2 = new Player();
            p1.setPlayerId("p1");
            p2.setPlayerId("p2");

        List<Player> mockPlayers = Arrays.asList(p1, p2);

        Mockito.when(playerRepository.findAll()).thenReturn(mockPlayers);

        Players players = playerService.getAllPlayers();

        assertNotNull(players);
        assertEquals(2, players.getPlayers().size());
        assertEquals("p1", players.getPlayers().get(0).getPlayerId());
        assertEquals("p2", players.getPlayers().get(1).getPlayerId());
        Mockito.verify(playerRepository, Mockito.times(1)).findAll();

    }

    @Test
    void getAllPlayersShouldReturnEmptyListWhenNoPlayersExist(){
        Mockito.when(playerRepository.findAll()).thenReturn(Collections.emptyList());

        Players result = playerService.getAllPlayers();

        assertNotNull(result);
        assertTrue(result.getPlayers().isEmpty());
        Mockito.verify(playerRepository, Mockito.times(1)).findAll();
    }

    @Test
    void getAllPlayersShouldThrowExceptionIfRepositoryFails() {
        Mockito.when(playerRepository.findAll()).thenThrow(new RuntimeException("DB Failure"));

        RuntimeException ex = assertThrows(RuntimeException.class, ()->playerService.getAllPlayers());
        assertEquals("DB Failure", ex.getMessage());
        Mockito.verify(playerRepository, Mockito.times(1)).findAll();
    }




    @Test
    void getPlayerByIdShouldReturnPlayer() {
        //Given
        Player player = new Player();
        player.setPlayerId("abercda");
        player.setFirstName("Prathap");

        //Mock
        Mockito.when(playerRepository.findById("abercda")).thenReturn(Optional.of(player));

        //When
        Player foundplayer = playerService.getPlayerById("abercda");

        //Then
        assertEquals(player.getPlayerId(), foundplayer.getPlayerId());
        Mockito.verify(playerRepository, Mockito.times(1)).findById("abercda");
        assertEquals(player.getFirstName(), foundplayer.getFirstName());
    }

    @Test
    void getPlayerByIdThrowsExceptionWhenPlayerDoesNotExists(){
        //Given
        String playerId = "abercda";

        //Mock
        Mockito.when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        //when
        PlayersException exception = assertThrows(PlayersException.class, ()->playerService.getPlayerById("abercda"));
        assertEquals("Player Not found with Id: " + playerId, exception.getMessage());
        assertEquals(404, exception.getStatus().value());
        Mockito.verify(playerRepository, Mockito.times(1)).findById(playerId);
    }

    @Test
    void addNewPlayerShouldAddNewPlayer() {
        //Given
        Player player = new Player();
        player.setPlayerId("abercda");
        player.setBirthYear("1850");
        player.setBirthMonth("1");
        player.setBirthDay("2");
        player.setBirthCountry("USA");
        player.setBirthState("OK");
        player.setWeight("124");
        player.setFinalGame("1871-10-21");

        //Mock The Calls
        Mockito.when(playerRepository.save(player)).thenReturn(player);

        //When
        Player savedPLayer = playerService.addNewPlayer(player);

        //Then
        assertEquals(player.getPlayerId(), savedPLayer.getPlayerId());
        assertEquals(player.getBirthState(), savedPLayer.getBirthState());
        Mockito.verify(playerRepository, Mockito.times(1)).save(player);
    }

    @Test
    void addNewPlayerShouldThrowExceptionWhenIdIsBlank(){
        //Given
        Player player = new Player();
        player.setPlayerId(" ");

        //When + Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, ()->playerService.addNewPlayer(player));

        assertEquals("Player Id must not be blank or null", exception.getMessage());
        Mockito.verify(playerRepository, Mockito.never()).save(any());
    }
    @Test
    void addNewplayerShouldThowExceptionWhenPlayerAlreadyExists(){
        //given
        Player player = new Player();
        player.setPlayerId("abercda01");

        //Mocking
        Mockito.when(playerRepository.existsById("abercda01")).thenReturn(true);

        //When
        PlayersException exception = assertThrows(PlayersException.class, ()-> playerService.addNewPlayer(player));

        assertEquals("Player Already Exists", exception.getMessage());
        assertEquals(400, exception.getStatus().value());
        Mockito.verify(playerRepository, Mockito.times(1)).existsById("abercda01");
        Mockito.verify(playerRepository, Mockito.never()).save(any());
    }


    @Test
    void updatePlayer() {
    }

    @Test
    void deletePlayer() {
    }
}