package com.walts.programs.work;

import com.skype.ChatMessage;
import com.skype.ChatMessageAdapter;
import com.skype.Skype;
import com.skype.SkypeException;

import java.io.IOException;

public class RemoteDeployer {


    public static void main(String[] args) throws Exception {
        ChatListener chatListener = new ChatListener();
        chatListener.test();
    }

    private static class ChatListener {
        final String senderId = ""/*"#rababarber/$kurbkala;"*/;
        final String[] messages = {"Robot, please deploy! :)"};

        private void test() throws SkypeException {
            System.out.println("Starting Skype chat listener...");
            Skype.setDaemon(false);
            Skype.addChatMessageListener(new ChatMessageAdapter() {
                @Override
                public void chatMessageReceived(ChatMessage receivedChatMessage) throws SkypeException {
                    handleMessages(receivedChatMessage);
                }
                @Override
                public void chatMessageSent(ChatMessage sentChatMessage) throws SkypeException {
                    handleMessages(sentChatMessage);
                }
            });
            System.out.println("Listening Skype messages...");
        }

        private void handleMessages(ChatMessage chatMessage) throws SkypeException {
            System.out.println("Message received from " + chatMessage.getChat().getId() + " - " + chatMessage.getSenderId() + " - " + chatMessage.getSenderDisplayName() + ", content --> " + chatMessage.getContent());
            if (chatMessage.getType().equals(ChatMessage.Type.SAID)) {
                if (chatMessage.getSenderId().contains(senderId) || chatMessage.getChat().getId().contains(senderId)) {
                    for (String message : messages) {
                        if (chatMessage.getContent().contains(message)) {
                            System.out.println("Message " + chatMessage.getContent() + " contains " + message + ". Starting deploy script.");
                            try {
                                Runtime.getRuntime().exec("cmd /c start C:\\Users\\valeryi\\Desktop\\GOD.bat");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

    }
}
