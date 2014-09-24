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
			
			
			Socket socket = new Socket(host, port);
			PrintWriter request = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
			request.println("GET "+path+ " HTTP/1.0");
			request.println();
			request.flush();
			
			BufferedReader download = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = download.readLine();
			if (!line.contains("200")) {
				// expect return code 200 OK
				download.close();
				socket.close();
				return;
			}
			
			// skip the header
			while (line != null) {
				line = download.readLine();
				if (line.length() == 0) break;
			}
			
			File downloadFile = new File("./"+host+path);
			downloadFile.getParentFile().mkdirs();
			PrintWriter fileWriter = new PrintWriter(downloadFile);
			int charI = download.read();
			while (charI != -1) {
				// cannot write header to file


				System.out.print(Character.toChars(charI));
				fileWriter.print(Character.toChars(charI));

				if (line.toLowerCase().contains("<a href=")) {
					// recursively download more URL
					String refLink = "";
					try {
						URL refURL = new URL(refLink);
						// ref includes hostname, so check if it's the same host
						String refHost = refURL.getHost();
							if (refHost.equalsIgnoreCase(host)) {
								String refPath = refURL.getPath();
								int refPort = refURL.getPort();
								if (refPort == -1) refPort = refURL.getDefaultPort();
								if (refPort == -1) refPort = 80;
								downloadFromURL(refHost, refPath, refPort);
						}
						
					}
					catch (MalformedURLException e) {
						// ref is not a URL, so it's a relative link
						
					}
				}
				
				charI = download.read();
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
