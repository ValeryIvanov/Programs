package com.walts.programs.skype;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.skype.ChatMessage;
import com.skype.ChatMessageAdapter;
import com.skype.Skype;
import com.skype.SkypeException;
import com.walts.programs.skype.data.Response;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ChatListener {

    //static String lastMessage;

    public static void main(String[] args) throws Exception {
        ChatListener chatListener = new ChatListener();
        chatListener.test();
    }

    private void test() throws SkypeException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectMapper objectMapper = new ObjectMapper();
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        ClassLoader classLoader = getClass().getClassLoader();
        List<Response> responses = null;
        try {
            responses = mapper.readValue(new File(classLoader.getResource("data.json").getFile()), typeFactory.constructCollectionType(List.class, Response.class));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (responses == null || responses.size() == 0) {
            System.out.println("No responses defined, exiting...");
        } else {
            System.out.println("Here is the data.json content");
            for (Response response : responses) {
                System.out.println(response);
            }

            System.out.println("Starting Skype chat listener...");

            Skype.setDaemon(false);
            final List<Response> finalResponses = responses;
            Skype.addChatMessageListener(new ChatMessageAdapter() {
                public void chatMessageReceived(ChatMessage received) throws SkypeException {
                    //if (received.getContent().equals(lastMessage)) return;
                    //System.out.println(lastMessage);
                    if (received.getType().equals(ChatMessage.Type.SAID)) {
                        for (Response response : finalResponses) {
                            if (received.getSenderId().equals(response.senderId)) {
                                for (String message : response.messages) {
                                    if (received.getContent().contains(message)) {
                                        received.getSender().send(response.response);
                                        System.out.println("Sending message " + response.response + " to " + received.getSenderId());
                                    }
                                }
                            }
                        }
                    }
                    //lastMessage = received.getContent();
                    System.out.println("Message received from " + received.getSenderId() + " - " + received.getSenderDisplayName() + ", content --> " + received.getContent());
                }
            });
        }


    }

}
