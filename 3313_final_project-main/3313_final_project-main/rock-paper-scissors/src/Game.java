import javax.swing.JFrame;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Color;
import java.io.*;
import java.net.ConnectException;
import java.net.Socket;



class Game extends JFrame implements ActionListener {

	//Variables
    JButton rock, paper, scissors, done;
    String choice = "";
    JTextField output;
    ImageIcon image_rock, image_paper, image_scissor;

    private static String host = "192.168.1.171";
    private static Integer port = 3000;
    private Thread thread1, thread2, thread;

    Socket clientSocket;
	DataOutputStream outToServer;
	BufferedReader inFromServer;
	
	static Game g;
    
    public void setup() throws IOException {
    	//Set Javax swing objects
        output = new JTextField(50);
        output.setEditable(false);
        done = new JButton("Done");
        rock = new JButton("Rock");
        paper = new JButton("Paper");
        scissors = new JButton("Scissors");
        

    	//Server Connection 
        
        try {
        	clientSocket = new Socket(Game.host, Game.port);
         	outToServer = new DataOutputStream(
         	clientSocket.getOutputStream());
         	inFromServer = new BufferedReader(new InputStreamReader(
         	clientSocket.getInputStream()));
      
            //Import images
            BufferedImage rockPic = ImageIO.read(this.getClass().getResource("/rock.png"));
            BufferedImage paperPic = ImageIO.read(this.getClass().getResource("/paper.png"));
            BufferedImage scissorPic = ImageIO.read(this.getClass().getResource("/scissor.png"));
            
            //Set images
            image_rock = new ImageIcon(rockPic.getScaledInstance(80, 80, Image.SCALE_DEFAULT));
            image_paper = new ImageIcon(paperPic.getScaledInstance(80, 80, Image.SCALE_DEFAULT));
            image_scissor = new ImageIcon(scissorPic.getScaledInstance(80, 80, Image.SCALE_DEFAULT));
            
            //Set output text and position
            output.setText("Please select your choice");
            output.setBounds(115, 150, 350, 25);
            output.setHorizontalAlignment(JTextField.CENTER);
            
         

            done.addActionListener(this);
            done.setVerticalAlignment(JButton.CENTER);
            done.setBounds(240, 200, 100, 25);

            //Rock listeners, Labels, positioning
            JLabel label_rock = new JLabel();
            label_rock.setIcon(image_rock);
            label_rock.setHorizontalTextPosition(JLabel.CENTER);
            label_rock.setVerticalTextPosition(JLabel.BOTTOM);
            label_rock.setBounds(150, 300, 100, 100);
            rock.addActionListener(this);
            rock.setVerticalAlignment(JButton.CENTER);
            rock.setVerticalTextPosition(JButton.BOTTOM);
            rock.setBounds(155, 400, 70, 25);

            
            //Paper listeners, Labels, positioning
            JLabel label_paper = new JLabel();
            label_paper.setIcon(image_paper);
            label_paper.setHorizontalTextPosition(JLabel.CENTER);
            label_paper.setVerticalTextPosition(JLabel.BOTTOM);
            label_paper.setBounds(250, 300, 100, 100);
            paper.addActionListener(this);
            paper.setVerticalAlignment(JButton.CENTER);
            paper.setVerticalTextPosition(JButton.BOTTOM);
            paper.setBounds(255, 400, 70, 25);

            //Scissor listeners, Labels, positioning
            JLabel label_scissor = new JLabel();
            label_scissor.setIcon(image_scissor);
            label_scissor.setHorizontalTextPosition(JLabel.CENTER);
            label_scissor.setVerticalTextPosition(JLabel.BOTTOM);
            label_scissor.setBounds(350, 300, 100, 100);
            scissors.addActionListener(this);
            scissors.setVerticalAlignment(JButton.CENTER);
            scissors.setVerticalTextPosition(JButton.BOTTOM);
            scissors.setBounds(350, 400, 85, 25);

            //Add elements to frame
            add(output);
            add(label_paper);
            add(label_rock);
            add(label_scissor);
            add(done);
            add(rock);
            add(paper);
            add(scissors);

            setLayout(null);
            //Assign threads
            thread1 = new Thread(new Runnable() {
                @Override
                public void run() {
                	try {
                        output.setText(calculate());
                    } catch (IOException e) {
                       performShutdown();
                    }
                }
           });
            thread2= new Thread(new Runnable() {
            	
                @Override
                public void run(){ 
                	try {
                		while(output.getText().equals("Waiting")) {
                    		//Do nothing	
                    	}		
                	}catch(NullPointerException E) {
                		//do nothing
                	}
                	closeWindow();
                }
           });
        }catch(ConnectException err){
          	//Catch condition that server isnt running
        	performShutdown();
        }
 
    }

    private void performShutdown() {
    	output.setText("No Server Running.. exiting");
        output.setBounds(115, 150, 350, 25);
        output.setHorizontalAlignment(JTextField.CENTER);
        add(output);
        thread = new Thread(new Runnable() {
            @Override
            public void run(){ 
            	try {
    				Thread.sleep(4000);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
                g.dispatchEvent(new WindowEvent(g, WindowEvent.WINDOW_CLOSING));
            }
       });
        thread.start();
		
	}
    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
        if (JOptionPane.showConfirmDialog(g, 
            "Are you sure you want to close this window?", "Close Window?", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
        	try {
				clientSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(e);
			}
        }
    }

	public void actionPerformed(java.awt.event.ActionEvent evt) { 
    	
    	//Event listeners for rock, paper,  scissor buttons set choice variable
    	 if (rock.equals(evt.getSource())){
    		 choice = "R";
    	 }else if(paper.equals(evt.getSource())) {
    		 choice ="P";
    	 }else if(scissors.equals(evt.getSource())) {
    		 choice ="S";
    	 }
    	 
    	 
    	//Event if done button is pressed and choice isn't empty
        if (done.equals(evt.getSource()) && !choice.equals("")){  
        	try {
				outToServer.writeBytes(choice + "\n");
                output.setText("Waiting for Opponent");
                remove(done);
                repaint();
                //start threads
                thread1.start();
              	thread2.start();
           
            } catch (IOException e) {
            	//Catch condition that server isnt running
            	performShutdown();
			} 
        }
      

    }

    //Close socket, and game window after 6 seconds
    private void closeWindow() {
    	 try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	try {
			clientSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	g.dispatchEvent(new WindowEvent(g, WindowEvent.WINDOW_CLOSING));
		
	}
    
    //return server response
	public String calculate() throws IOException {
    	return inFromServer.readLine();
    }

    public static void main(String[] args) throws IOException {
        g = new Game();
        g.setTitle("Rock, Paper, Scissors Game");
        g.setBounds(200, 200, 600, 600);
        g.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        g.getContentPane().setBackground(Color.WHITE);
        g.setup();
        g.setResizable(false);
        g.setVisible(true);
    }


}