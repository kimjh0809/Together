package com.example.login;

public class RoomDTO {
    private String roomName;
    private String member;

    public RoomDTO() {}
    public RoomDTO(String roomName, String admin) {
        this.roomName = roomName;
        this.member = member;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setMember(String member) {
        this.member = member;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getMember() {
        return member;
    }
}