/**
 * The server class waits for the connection of the two clients client_1
 * and client_2 (in either order) on port 1337. Receives one character
 * from client_1 and one character from client_2 (in either order) and
 * calculates the winner of the game based on a rule set. After sending a
 * correspondent massage to each client the server waits again for two
 * clients to connect.
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;

public class Server {
	private static Integer port = 3000;
	static ArrayList<Thread> threads = new ArrayList<>();
	public static void main(String args[]) throws Exception {

		try {

			// Create new server socket and display message
			ServerSocket server = new ServerSocket(Server.port);
		
			System.out.println("\nServer running on port " + Server.port + " ...");
			
			ShutdownThread shutdownHandler = new ShutdownThread(server);
			new Thread(shutdownHandler).start();
			
			while (true) {	
				Socket player_1 = server.accept();
				
				//Display message on client 1 connection
				if (player_1.isConnected()) {
					System.out.println("\nPlayer one (" + (player_1.getLocalAddress().toString()).substring(1) + ":"
							+ player_1.getLocalPort() + ") has joined ... waiting for player two ...");
					
				}
				Socket player_2 = server.accept();	
				
				//Display message on client 2 connection
		    	if (player_2.isConnected() && player_1.isConnected()) {
					System.out.println("Player two (" + (player_2.getLocalAddress().toString()).substring(1) + ":"
							+ player_1.getLocalPort() + ") has joined ... lets start ...");
				}
		    	if(player_2.isConnected() && player_1.isConnected()) {
		    		ServerThread clientSocket  = new ServerThread(player_1,player_2);
		    		Thread thread = new Thread(clientSocket);
		    		thread.start();
				    threads.add(thread);
		    	}
				
			    
				System.out.println("\nWaiting for new players ...\n");
				

			}
		
		}catch(IOException o){
			for(Thread x : threads) {
				x.interrupt();
			}
			System.out.println(o);
		}
		
		
}
}

class ShutdownThread implements Runnable{
	
	private ServerSocket server;
	
	public ShutdownThread(ServerSocket server) {
		this.server = server;
	}

	@Override
	public void run() {
		Scanner scn = new Scanner(System.in);
		String s;
		while(true) {
			System.out.println("Type 'done' to close the server\n");
			s = scn.next();
			if(s.equals("done")) {
				try {
					server.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				scn.close();
				System.out.println("Server closing...");
				break;
			}
		}
		
		
	}
	
}

class ServerThread implements Runnable {
	
	//declare variables
    private Socket player_1;
    private Socket player_2;
	String player_1_response = "";
	String player_2_response = "";
	String player_1_input;
	String player_2_input;
	DataOutputStream out_player_1;
	BufferedReader in_player_1;
	DataOutputStream out_player_2;
	BufferedReader in_player_2;

    // Constructor
    public ServerThread(Socket client1, Socket client2)
    {
        this.player_1 = client1;
        this.player_2 = client2;
    }

    public void run()
    {
		try {
			
			 out_player_1 = new DataOutputStream(player_1.getOutputStream());
			 in_player_1 =new BufferedReader(new InputStreamReader(player_1.getInputStream()));
			 out_player_2 = new DataOutputStream(player_2.getOutputStream());
			 in_player_2 =new BufferedReader(new InputStreamReader(player_2.getInputStream()));
	
			 //get client inputs
			 player_1_input = in_player_1.readLine();
			 player_2_input = in_player_2.readLine();

			 	//Draw if both inputs are the same
					if (player_1_input.equals(player_2_input)) {
						player_1_response = "Draw";
						player_2_response = "Draw";
						System.out.println("It's a draw.");
					}
				
				//Player 1 Wins Conditions
					else if (player_1_input.equals("R") && player_2_input.equals("S")) {
						player_1_response = "You win";
						player_2_response = "You lose";
						System.out.println("Player one wins.");
	
					}
					else if (player_1_input.equals("P") && player_2_input.equals("R")) {
						player_1_response = "You win";
						player_2_response = "You lose";
						System.out.println("Player one wins.");
					}
					
					else if (player_1_input.equals("S") && player_2_input.equals("P")) {
						player_1_response = "You win";
						player_2_response = "You lose";
						System.out.println("Player one wins.");
					}
				
				//Player two win conditions
					else if (player_1_input.equals("S") && player_2_input.equals("R")) {
						player_1_response = "You lose";
						player_2_response = "You win";
						System.out.println("Player two wins.");
					}
					
					else if (player_1_input.equals("R") && player_2_input.equals("P")) {
						player_1_response = "You lose";
						player_2_response = "You win";
						System.out.println("Player two wins.");
					}
					else if (player_1_input.equals("P") && player_2_input.equals("S")) {
						player_1_response = "You lose";
						player_2_response = "You win";
						System.out.println("Player two wins.");
					}

				// Send responses and close sockets
				out_player_1.writeBytes(player_1_response.toUpperCase());
				out_player_2.writeBytes(player_2_response.toUpperCase());
				player_1.close();
				player_2.close();
		} catch (IOException e) {
			//a client closed, handle
			try {
				
				out_player_1.writeBytes("A player disconnected from the game... disconnecting");
				player_1.close();
				System.out.println("hi");
			}catch(IOException x){
				
			}
			try {
				
				out_player_2.writeBytes("A player disconnected from the game... disconnecting");
				player_2.close();
				System.out.println("hi2");
			}catch(IOException x){
			}
			return;
		}
		
    }
}
