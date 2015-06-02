package com.walts.programs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

import static java.lang.Thread.sleep;

public class GetURLContent {

    public static void main(String[] args) {
        if (args.length != 2) {
            args = new String[]{"", ""};
        }
        boolean versionReceived = false;
        while (!versionReceived) {
            versionReceived = getURLContent(args[0], args[1]);
            if (!versionReceived) {
                try {
                    sleep(1000);
                    System.out.println("Sleeping one second...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean getURLContent(String spec, String filePath) {
        boolean written = false;
        try {
            URL url = new URL(spec);
            System.out.println("Opening connection");
            URLConnection conn = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("File does not exist creating new file...");
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            System.out.println("Starting reading URL content and writing to file");
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                System.out.println(inputLine);
                bw.write(inputLine );
                bw.newLine();
            }
            written = true;
            bw.close();
            br.close();
            System.out.println("All done");
        } catch (MalformedURLException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return written;
    }

}
