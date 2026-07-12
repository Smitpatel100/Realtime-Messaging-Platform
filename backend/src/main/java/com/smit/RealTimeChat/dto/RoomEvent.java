package com.smit.RealTimeChat.dto;

public class RoomEvent {

    private String type;
    private Long roomId;
    private String actorEmail;

    public RoomEvent() {
    }

    public RoomEvent(String type, Long roomId, String actorEmail) {
        this.type = type;
        this.roomId = roomId;
        this.actorEmail = actorEmail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getActorEmail() {
        return actorEmail;
    }

    public void setActorEmail(String actorEmail) {
        this.actorEmail = actorEmail;
    }
}