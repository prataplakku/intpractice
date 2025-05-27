package com.app.playerservicejava.exception;

import org.springframework.http.HttpStatus;

public class PlayersException extends RuntimeException{
    private final HttpStatus status;

    public PlayersException(HttpStatus status, String message){
        super(message);
        this.status = status;
    }
    public HttpStatus getStatus(){
        return this.status;
    }

}