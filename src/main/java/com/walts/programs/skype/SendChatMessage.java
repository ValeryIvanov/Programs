package com.walts.programs.skype;

import com.skype.Chat;
import com.skype.Skype;

import java.io.BufferedReader;
import java.io.FileReader;

public class SendChatMessage {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: java com.skype.sample.SendChatMessage 'skype_id' 'chat_message'");
            return;
        }
        System.out.println("Started working!");
        Chat group = null;
        System.out.println("Looking for chat which id contains string = " + args[0]);
        for (Chat c : Skype.getAllChats()){
            System.out.println("Found chat with id = " + c.getId());
            if (c.getId().contains(args[0])) {
                System.out.println("Chat id = " + c.getId() + " contains = " + args[0]);
                group = c;
                break;
            }
        }
        if (group != null) {
            if (args[1].endsWith(".txt")) {
                BufferedReader br = new BufferedReader(new FileReader(args[1]));
                try {
                    String line = br.readLine();
                    while (line != null) {
                        System.out.println("Sending message = " + line + " to chat id = " + group.getId());
                        group.send(line);
                        line = br.readLine();
                    }
                } finally {
                    br.close();
                }
            } else {
                System.out.println("Sending message = " + args[1] + " to chat id = " + group.getId());
                group.send(args[1]);
            }
            System.out.println("Message sent!");
        } else {
            System.out.println("Could not find group to send message to!");
        }
        System.out.println("Finished working!");
    }
}
