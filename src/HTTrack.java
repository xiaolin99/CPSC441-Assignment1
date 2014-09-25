/**
 * 
 */
import java.net.*;
import java.io.*;
/**
 * @author Xiao
 * Reference: 
 * http://www.java-samples.com/java/geturl-using-SOCKET-connection-freejavasample.htm
 */
public class HTTrack {
	

	private static void downloadFromURL(String host, String path, int port){
	
		try
		{
			File downloadFile = new File("./"+host+path);
			downloadFile.getParentFile().mkdirs();
			PrintWriter fileWriter = new PrintWriter(downloadFile);
			
			Socket socket = new Socket(host, port);
			PrintWriter request = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
			request.println("GET "+path+ " HTTP/1.0");
			request.println();
			request.flush();
			
			BufferedReader download = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = download.readLine();
			while (line != null) {
				System.out.println(line);
				fileWriter.println(line);
				if (line.toLowerCase().contains("<a href=")) {
					// recursively download more URL
					String refLink = "";
					try {
						URL refURL = new URL(refLink);
					}
					catch (MalformedURLException e) {
						
					}
				}
				line = download.readLine();
			}
			
			download.close();
			fileWriter.close();
			socket.close();

		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	/**
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException {
		URL aURL = new URL(args[0]);
		String host = aURL.getHost();
		String path = aURL.getPath();
		
		// try guessing the port number
		int port = aURL.getPort();
		if (port == -1) port = aURL.getDefaultPort();
		if (port == -1) port = 80;
		
		downloadFromURL(host, path, port);
		
	}

}
