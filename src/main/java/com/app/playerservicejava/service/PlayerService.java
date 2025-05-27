package com.app.playerservicejava.service;

import com.app.playerservicejava.exception.PlayersException;
import com.app.playerservicejava.model.Player;
import com.app.playerservicejava.model.Players;
import com.app.playerservicejava.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlayerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerService.class);

    @Autowired
    private PlayerRepository playerRepository;

    public Players getPlayers(Pageable pageable) {
        Players players = new Players();
        players.setPlayers(playerRepository.findAll(pageable).toList());
        return players;
    }

    public Players getAllPlayers(){
        Players players = new Players();
        players.setPlayers(playerRepository.findAll());
        return players;
    }

    public Player getPlayerById(String playerId) {
//        Optional<Player> player = null;

        /* simulated network delay */
//        try {
//            player = playerRepository.findById(playerId);
//            Thread.sleep((long)(Math.random() * 2000));
//        } catch (Exception e) {
//            LOGGER.error("message=Exception in getPlayerById; exception={}", e.toString());
//            return Optional.empty();
//        }
        return playerRepository.findById(playerId)
                .orElseThrow(()-> new PlayersException(HttpStatus.NOT_FOUND, "Player Not found with Id: " + playerId));
    }

    public Player addNewPlayer(Player player){
        if(player.getPlayerId() == null || player.getPlayerId().isBlank()){
            throw new IllegalArgumentException("Player Id must not be blank or null");
        }
        if(playerRepository.existsById(player.getPlayerId())){
            throw new PlayersException(HttpStatus.BAD_REQUEST, "Player Already Exists");
        }
        return playerRepository.save(player);
    }

    public boolean updatePlayer(String playerId, Player player){
        if(playerRepository.existsById(playerId)){
            player.setPlayerId(playerId);
            playerRepository.save(player);
            return true;
        }
        throw new PlayersException(HttpStatus.NOT_FOUND, "Player Not Found");
    }

    public boolean deletePlayer(String playerId){
        if(playerRepository.existsById(playerId)){
            playerRepository.deleteById(playerId);
            return true;
        }
        throw new PlayersException(HttpStatus.NOT_FOUND, "Player Not Found");
    }

//    public Player addNewPlayer(Player player){
//        player.setPlayerId(UUID.randomUUID().toString().replace("-", "").substring(0, 9));
//        return playerRepository.save(player);
//    }

}
