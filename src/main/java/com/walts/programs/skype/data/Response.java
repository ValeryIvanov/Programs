package com.walts.programs.skype.data;

import java.util.Arrays;

public class Response {

    public String senderId;
    public String[] messages;
    public String response;

    public Response() {

    }

    public Response(String senderId, String[] messages, String response) {
        this.senderId = senderId;
        this.messages = messages;
        this.response = response;
    }

    @Override
    public String toString() {
        return "Response{" +
                "senderId='" + senderId + '\'' +
                ", messages=" + Arrays.toString(messages) +
                ", response='" + response + '\'' +
                '}';
    }
}
