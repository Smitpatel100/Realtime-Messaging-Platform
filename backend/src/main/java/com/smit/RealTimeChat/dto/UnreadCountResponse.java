package com.smit.RealTimeChat.dto;

public class UnreadCountResponse {

    private Long roomId;
    private long count;

    public UnreadCountResponse() {
    }

    public UnreadCountResponse(Long roomId, long count) {
        this.roomId = roomId;
        this.count = count;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}