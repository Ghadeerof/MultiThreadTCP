/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package multithreadtcp;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ghadeer
 */
public class ClientHandler implements Runnable{

    final Socket socket;
    final Scanner scan;
    String name;
    boolean isLosggedIn;
    
    private DataInputStream input;
    private DataOutputStream output;
    
    public ClientHandler(Socket socket, String name){
        this.socket = socket;
        scan = new Scanner(System.in);
        this.name = name;
        isLosggedIn = true;
        
        try{
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            
        }catch(IOException ex){
            log("ClientHander : " + ex.getMessage());
        }
    }
    @Override
    public void run() {
        String received;
        write(output, "Your name : " + name);
        
        while(true){
            received = read();
            if(received.equalsIgnoreCase(Constants.LOGOUT)){
                this.isLosggedIn = false;
                closeSocket();
                closeStreams();
                break;
            }
            
            forwardToClient(received);
        }
        closeStreams();
    }
    
    private void forwardToClient(String received){
        // username # message
        StringTokenizer tokenizer = new StringTokenizer(received, "#");
        String recipient = tokenizer.nextToken().trim();
        String message = tokenizer.nextToken().trim();
        
        for(ClientHandler c : Server.getClients()){
            if(c.isLosggedIn && c.name.equals(recipient)){
                write(c.output,  recipient + " : " + message);
                log(name + " --> " + recipient + " : " + message);
                break;
            }
        }
        
    }
    
    private String read(){
        String line = "";
        try {
            line = input.readUTF();
        } catch (IOException ex) {
            log("read : " + ex.getMessage());
        }
        return line;
    }
    
    private void write(DataOutputStream output , String message){
        try {
            output.writeUTF(message);
        } catch (IOException ex) {
            log("write : " + ex.getMessage());
        }
    }
    
    private void closeStreams() {
        try {
            this.input.close();
            this.output.close();
        } catch (IOException ex) {
            log("closeStreams : " + ex.getMessage());
        }
    }
  
    private void closeSocket(){
        try{
            socket.close();
        }catch(IOException ex){
            log("closeSocket : " + ex.getMessage());
        }
    }
   
    private void log(String msg){
        System.out.println(msg);
    }
}
