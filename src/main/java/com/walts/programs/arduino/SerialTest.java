package com.walts.programs.arduino;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.nio.charset.Charset;
import java.util.Enumeration;

//http://playground.arduino.cc/Interfacing/Java
public class SerialTest implements SerialPortEventListener {

    SerialPort serialPort;

    private static final String PORT_NAMES[] = {
            "/dev/tty.usbserial-A9007UX1", // Mac OS X
            "/dev/ttyACM0", // Raspberry Pi
            "/dev/ttyUSB0", // Linux
            "COM4", // Windows
    };

    private BufferedReader input;
    private OutputStream output;
    private static final int TIME_OUT = 2000;
    private static final int DATA_RATE = 9600;

    public void initialize() {
        // the next line is for Raspberry Pi and
        // gets us into the while loop and was suggested here was suggested
        // http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
        //System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");

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
        final SerialTest main = new SerialTest();
        main.initialize();
        Thread t = new Thread() {
            public void run() {
                //the following line will keep this app alive for 1000 seconds,
                //waiting for events to occur and responding to them (printing incoming messages to console).
                try {Thread.sleep(1000000);} catch (InterruptedException ie) {}
            }
        };
        t.start();
        System.out.println("Started");


        Thread r = new Thread() {
            public void run() {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String s;
                while (true) {
                    try {
                        System.out.print("Enter String");
                        try {
                            s = br.readLine();
                            if (s != null) {
                                main.output.write(s.getBytes(Charset.forName("UTF-8")));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {}
                }
            }
        };
        r.start();

    }
}
