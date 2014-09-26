/**
 * @author Xiao
 *
 */
import java.io.*;
import java.net.*;

public class WebServer {
	
	class connectionThread implements Runnable {
		private Socket con;
		public connectionThread(Socket con) {
			this.con = con;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	private static void serverListener(int port) {
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				Socket connection = serverSocket.accept();
				BufferedReader request = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				DataOutputStream response = new DataOutputStream(connection.getOutputStream());
				String requestLine = request.readLine();
				if (requestLine == null) continue;
				// will only process "GET" request
				if (requestLine.contains("GET")) {
					// parse file path
					String path = requestLine.substring(4, requestLine.indexOf(' ', 4));
					if (path == "/") path = "/index.html";
					path = path.substring(1);
					String header = path + ".header";
					File file = new File(path);
					File fileHeader = new File(header);
					// check if file exist
					if (file.isFile()) {
						// I have HTTrack save the original header file as .header
						// so this will send the original header if available
						if (fileHeader.exists()) {
							BufferedReader headerReader = new BufferedReader(new FileReader(fileHeader));
							String line = headerReader.readLine();
							while (line != null) {
								line = line + "\r\n";
								response.writeBytes(line);								line = headerReader.readLine();
							}
							headerReader.close();
						}
						else {
							response.writeBytes("HTTP/1.1 200 OK\r\n");
							response.writeBytes("\r\n");
						}
						// send message body
						InputStream in = new FileInputStream(file);
						int count;
						byte[] buffer = new byte[2048];
						while ((count = in.read(buffer)) != -1)
						{
						  response.write(buffer, 0, count);
						  response.flush();
						}
						in.close();
						
					}
					else {
						// send 404 if file not found
						response.writeBytes("HTTP/1.1 404 Not Found\r\n");
						response.writeBytes("\r\n");
						response.writeBytes("404 Not Found\r\n");
					}
				}
				else {
					// send 400 if request is not GET
					response.writeBytes("HTTP/1.1 400 Bad Request\r\n");
					response.writeBytes("\r\n");
				}
				request.close();
				response.close();
				connection.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println("Unable to start server");
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// if user didn't enter port, default to 2025
		int port = 2025;
		try {port = Integer.parseInt(args[0]);}
		catch (Exception e) {
			System.out.println("Invalid port, using default 2025");
		}
		serverListener(port);

	}

}


