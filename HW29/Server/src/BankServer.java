

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.io.PrintWriter;
import java.io.IOException;

public class BankServer{
	private static final int BANKPORT = 8888;
	private BankAccount account;
//	private ServerSocket serverSocket;
	
	public BankServer(){
		account = new BankAccount();
	}
	
	public void init(){
		try{
			try(ServerSocket serverSocket = new ServerSocket(BANKPORT)){
				System.out.println("Socket created.");
			
				while(true){	
					System.out.println( "Listening for a connection on the local port " +
										serverSocket.getLocalPort() + "..." );
					Socket socket = serverSocket.accept();
					System.out.println( "\nA connection established with the remote port " + 
										socket.getPort() + " at " +
										socket.getInetAddress().toString() );
					new Thread(new executeCommandRunnable(socket)).start();
				}
			}
		}
		catch(IOException exception){}
	}
	
	private class executeCommandRunnable implements Runnable{
		private Socket socket;

		public executeCommandRunnable(Socket socket) {
			this.socket = socket;
		}
		@Override
		public void run() {
			try{
				try(Scanner in = new Scanner( socket.getInputStream() )){
					PrintWriter out = new PrintWriter( socket.getOutputStream() );
					System.out.println( "I/O setup done" );
					
					while(true){
						if( in.hasNext() ){
							String command = in.next();
							if( command.equals("QUIT") ){
								System.out.println( "QUIT: Connection being closed." );
								out.println( "QUIT accepted. Connection being closed." );
								out.close();
								return;
							}
							accessAccount( command, in, out );
						}
					}
				}	
			}
			catch(Exception exception){
				exception.printStackTrace();
			}
		}
	}
	private void accessAccount( String command, Scanner in, PrintWriter out ){
		double amount;
		if( command.equals("DEPOSIT") ){
			amount = in.nextDouble();
			account.deposit( amount );
			System.out.println( "DEPOSIT: Current balance: " + account.getBalance() );
			out.println( "DEPOSIT Done. Current balance: " + account.getBalance() );
		}
		else if( command.equals("WITHDRAW") ){
			amount = in.nextDouble();
			account.withdraw( amount );
			System.out.println( "WITHDRAW: Current balance: " + account.getBalance() );
			out.println( "WITHDRAW Done. Current balance: " + account.getBalance() );
		}
		else if( command.equals("BALANCE") )
		{
			System.out.println( "BALANCE: Current balance: " + account.getBalance() );
			out.println( "BALANCE accepted. Current balance: " + account.getBalance() );
		}
		else{
			System.out.println( "Invalid Command" );
			out.println( "Invalid Command. Try another command." );
		}
		out.flush();
	}
	
	public static void main(String[] args){
		BankServer server = new BankServer();
		server.init();
	}
}
