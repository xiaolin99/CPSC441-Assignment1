/**
 * @author Xiao
 *
 */
import java.io.*;
import java.net.*;

public class WebServer {

	private static void serverListener(int port) {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				Socket connection = serverSocket.accept();
				BufferedReader request = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String requestLine = request.readLine();
				
				connection.close();
			}
		}
		catch (Exception e) {
			System.out.println("Unable to start server");
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int port = 2025;
		try {port = Integer.parseInt(args[0]);}
		catch (Exception e) {
			System.out.println("Invalid port, using default 2025");
		}
		serverListener(port);

	}

}
