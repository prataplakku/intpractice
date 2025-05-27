package com.app.playerservicejava.controller;

import com.app.playerservicejava.model.Player;
import com.app.playerservicejava.model.Players;
import com.app.playerservicejava.service.PlayerService;
import com.app.playerservicejava.service.chat.ChatClientService;
import jakarta.annotation.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping(value = "v1/players", produces = { MediaType.APPLICATION_JSON_VALUE })
public class PlayerController {
    @Resource
    private PlayerService playerService;
    @Resource
    private  ChatClientService chatClientService;


    //Get All Players - Paginated
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Players> getPlayers(@PageableDefault(size = 20, page = 0)Pageable pageable) {
        return new ResponseEntity<>(playerService.getPlayers(pageable), HttpStatus.OK);
    }

    //Get All Players(without pagination)
    @GetMapping("/all")
    public ResponseEntity<Players> getAllPlayers(){
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

    //View Players with isAdmin Parameter
    @GetMapping("/view")
    public ResponseEntity<List<Map<String, String>>> viewPlayers(@RequestParam boolean isAdmin) {
        List<Player> allPlayers = playerService.getAllPlayers().getPlayers();

        List<Map<String, String>> response = allPlayers.stream()
                .map(player -> {
                    Map<String, String> playerMap = new HashMap<>();
                    playerMap.put("firstName", player.getFirstName());
                    if (isAdmin) {
                        playerMap.put("lastName", player.getLastName());
                    }
                    return playerMap;
                })
                .toList();

        return ResponseEntity.ok(response);
    }

    //View Players with Authorization Header
    @GetMapping("/view1")
    public ResponseEntity<List<Map<String, String>>> viewPlayersWithHeader(@RequestHeader(value = "X-Admin", defaultValue = "false") boolean isAdmin) {
        List<Player> allPlayers = playerService.getAllPlayers().getPlayers();

        List<Map<String, String>> response = allPlayers.stream()
                .map(player -> {
                    Map<String, String> playerMap = new HashMap<>();
                    playerMap.put("firstName", player.getFirstName());
                    if (isAdmin) {
                        playerMap.put("lastName", player.getLastName());
                    }
                    return playerMap;
                })
                .toList();

        return ResponseEntity.ok(response);
    }



    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable("id") String id) {
        return ResponseEntity.ok(playerService.getPlayerById(id));
    }

//    @GetMapping
//    ResponseEntity<Page<Player>> getPlayer(@PageableDefault(size = 20, page = 0)Pageable pageable){
//        return new ResponseEntity<>(playerService.getPlayers(pageable), HttpStatus.OK);
//    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<String> getPlayerSummary(@PathVariable String id){
        Player player = playerService.getPlayerById(id);
        return ResponseEntity.ok(chatClientService.generatePlayerSummary(player));
    }

    @PostMapping
    public ResponseEntity<Player> addNewPlayer(@RequestBody Player player){
        Player addedPlayer = playerService.addNewPlayer(player);
        return  new ResponseEntity<>(addedPlayer, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updatePlayer(@RequestBody Player player, @PathVariable String id){
        playerService.updatePlayer(id, player);
        return ResponseEntity.ok("Player Details Updated");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePlayer(@PathVariable String id){
        playerService.deletePlayer(id);
        return ResponseEntity.ok("Player deleted");
    }


}
