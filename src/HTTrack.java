/**
 * CPSC 441 Assignment 1 Part1
 * @author Xiao Lin
 * Reference: 
 * http://www.java-samples.com/java/geturl-using-SOCKET-connection-freejavasample.htm
 */
import java.net.*;
import java.io.*;

public class HTTrack {

	/**
	 * Function to download from a given URL
	 * @param aURL - URL
	 * @param firstPage - String indicating the initial HTML path
	 */
	private static void downloadFromURL(URL aURL, String firstPage){
		String host = aURL.getHost();
		String path = aURL.getPath();
		// try guessing the port number
		int port = aURL.getPort();
		if (port == -1) port = aURL.getDefaultPort();
		if (port == -1) port = 80;
		try
		{
			Socket socket = new Socket(host, port);
			PrintWriter request = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
			request.println("GET "+path+ " HTTP/1.0");
			request.println();
			request.flush();
			BufferedReader download = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = download.readLine();
			// is the response 200 OK?
			if (!line.contains("200")) {
				download.close();
				socket.close();
				return;
			}
			
			// keeping a record of downloaded websites
			File metadata = new File("downloadedWebpage.txt");
			PrintWriter metadataWriter = new PrintWriter(new FileWriter(metadata, true));
			metadataWriter.println(aURL.toString());
			metadataWriter.close();
	
			boolean isText = false;
			
			// write out header to a separate file
			File downloadHeader = new File("./"+host+path+".header");
			downloadHeader.getParentFile().mkdirs();
			PrintWriter headerWriter = new PrintWriter(downloadHeader);
			while (line != null) {
				headerWriter.println(line);
				if (line.contains("Content-Type: text")) isText = true;
				if (line.length() == 0) break;
				line = download.readLine();
			}
			headerWriter.close();
			
			// if response is not text (i.e. image), write byte directly to file
			if (!isText) {
				OutputStream os = new FileOutputStream("./"+host+path);
				DataInputStream in = new DataInputStream(socket.getInputStream());
				int count;
				byte[] buffer = new byte[2048];
				count = in.read(buffer);
				while (count != -1)
				{
				  os.write(buffer, 0, count);
				  os.flush();
				  count = in.read(buffer);
				}
				in.close();
				os.close();
				socket.close();
				download.close();
				return;
			}
			
			// if response if text, write text file
			File downloadFile = new File("./"+host+path);
			PrintWriter fileWriter = new PrintWriter(downloadFile);
			line = download.readLine();

			while (line != null) {
				fileWriter.println(line);
				if (line.toLowerCase().contains("href=") && firstPage == aURL.getPath()) {
					// recursively download more from referenced link "href"
					int startIndex = line.toLowerCase().indexOf("href=")+6;
					int endIndex = line.indexOf('\"', startIndex);
					String refLink = line.substring(startIndex, endIndex);
					if (refLink.startsWith("http://")) {
						URL refURL = new URL(refLink);
						if (refURL.getHost() == aURL.getHost()) {
							downloadFromURL(refURL, firstPage);
						}
					}
					else {
						downloadFromURL(new URL(aURL.getProtocol(), host, port, path.substring(0, path.lastIndexOf('/'))+"/"+refLink), firstPage);
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
		if (args.length != 1) {
			System.out.println("Please enter a URL");
			System.exit(1);
		}
		URL aURL = new URL(args[0]);
		
		try {
			new File("downloadedWebpage.txt").delete();
		}
		catch (Exception e){
			// do nothing
		}
		downloadFromURL(aURL, aURL.getPath());
		
		
	}
}
