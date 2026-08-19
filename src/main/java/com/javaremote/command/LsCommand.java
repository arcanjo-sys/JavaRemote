package com.javaremote.command;

import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 *   Command to list files on the remote server
 */
public class LsCommand implements Command {
    @Override
    public String getName() {
        return "ls";
    }

    @Override
    public String execute(String[] args) {
        try {
            String command = "ls";

            for (int i=0; i < args.length; i++) {
                command += " " + args[i];
            }
            Process exec = Runtime.getRuntime().exec( command );

            OutputStream out = exec.getOutputStream();
            InputStream in = exec.getInputStream();

            String resultado = new String(in.readAllBytes());

            System.out.println("DEBUG:");
            System.out.println(resultado);

            return resultado;

        } catch (IOException e) {
            e.printStackTrace();

            return "";
        }
    }
}