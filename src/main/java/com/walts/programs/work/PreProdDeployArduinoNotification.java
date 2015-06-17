package com.walts.programs.work;

import java.io.*;

import com.skype.ChatMessage;
import com.skype.ChatMessageAdapter;
import com.skype.Skype;
import com.skype.SkypeException;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.nio.charset.Charset;
import java.util.Enumeration;

public class PreProdDeployArduinoNotification implements SerialPortEventListener {

    SerialPort serialPort;

    private static final String PORT_NAMES[] = { "COM4" };

    private BufferedReader input;
    private OutputStream output;
    private static final int TIME_OUT = 2000;
    private static final int DATA_RATE = 9600;

    public void initialize() {

        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            System.out.println("Found port " + currPortId.getName());
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        if (portId == null) {
            System.out.println("Could not find COM port.");
            return;
        }

        try {
            serialPort = (SerialPort) portId.open(this.getClass().getName(), TIME_OUT);

            serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            //serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN | SerialPort.FLOWCONTROL_RTSCTS_OUT);
            //serialPort.setRTS(false);

            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();

            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    /**
     * This should be called when you stop using the port.
     * This will prevent port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    @Override
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                if (input.ready()) {
                    String inputLine = input.readLine();
                    System.out.println(inputLine);
                }
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        final PreProdDeployArduinoNotification main = new PreProdDeployArduinoNotification();
        main.initialize();
        System.out.println("Arduino serial port started");

        ChatListener chatListener = new ChatListener();
        chatListener.test(main.output);
    }

    private static class ChatListener {
        final String senderId = ""/*"#rababarber/$kurbkala;"*/;
        final String[] messages = {"teen", "Robot is deploying"};

        private void test(final OutputStream outputStream) throws SkypeException {
            System.out.println("Starting Skype chat listener...");
            Skype.setDaemon(false);
            Skype.addChatMessageListener(new ChatMessageAdapter() {
                @Override
                public void chatMessageReceived(ChatMessage receivedChatMessage) throws SkypeException {
                    handleMessages(receivedChatMessage, outputStream);
                }
                @Override
                public void chatMessageSent(ChatMessage sentChatMessage) throws SkypeException {
                    handleMessages(sentChatMessage, outputStream);
                }
            });
            System.out.println("Listening Skype messages...");
        }

        private void handleMessages(ChatMessage chatMessage, OutputStream outputStream) throws SkypeException {
            System.out.println("Message received from " + chatMessage.getChat().getId() + " - " + chatMessage.getSenderId() + " - " + chatMessage.getSenderDisplayName() + ", content --> " + chatMessage.getContent());
            if (chatMessage.getType().equals(ChatMessage.Type.SAID)) {
                if (chatMessage.getSenderId().contains(senderId) || chatMessage.getChat().getId().contains(senderId)) {
                    for (String message : messages) {
                        if (chatMessage.getContent().contains(message)) {
                            System.out.println("Message " + chatMessage.getContent() + " contains " + message + ". Sending HIGH to Arduino");
                            writeToOutputStream(outputStream, "HIGH");
                            return;
                        }
                    }
                    System.out.println("Message "+ chatMessage.getContent() + " did not contain deploy starting message. Sending LOW to Arduino");
                    writeToOutputStream(outputStream, "LOW");
                }
            }
        }

        private void writeToOutputStream(OutputStream outputStream, String message) {
            try {
                outputStream.write(message.getBytes(Charset.forName("UTF-8")));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
