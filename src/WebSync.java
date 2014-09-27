/**
 * CPSC 441 Assignment 1 Part3
 * @author Xiao Lin
 */
import java.net.*;
import java.io.*;

public class WebSync {

	/**
	 * This function will check for stored Last-Modified date
	 * and use a conditional GET to download the webpage
	 * @param aURL URL of the webpage you are trying to sync
	 */
	private static void sync(URL aURL) {
		String host = aURL.getHost();
		String path = aURL.getPath();
		// try guessing the port number
		int port = aURL.getPort();
		if (port == -1) port = aURL.getDefaultPort();
		if (port == -1) port = 80;
		
		// Try to get last-motified date of saved webpages
		// pick an arbitrary early default date
		String modifiedDate = "Sun, 1 Jan 1995 13:00:00 GMT";
		try {
			BufferedReader header = new BufferedReader(new FileReader("./"+host+path+".header"));
			String field = header.readLine();
			while (field != null) {
				if (field.startsWith("Last-Modified")) modifiedDate = field.substring(field.indexOf(' ')+1);
				field = header.readLine();
			}
			header.close();
		} catch (Exception e) {}
		System.out.println("Modified Date: " + modifiedDate);
		
		try
		{
			Socket socket = new Socket(host, port);
			PrintWriter request = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
			// send the conditional get
			request.print("GET "+path+ " HTTP/1.0\r\nIf-Modified-Since: "+modifiedDate+"\r\n");
			request.println();
			request.flush();
			BufferedReader download = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String line = download.readLine();
			
			// is the response 200 OK?
			if (!line.contains("200")) {
				System.out.println(line);
				download.close();
				socket.close();
				return;
			}
			
			System.out.println("Webpage modified. Updating ...");
			boolean isText = false;
			
			// write out header to a separate file in case we need it later
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
				int count = 0;
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
	 * Main
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			BufferedReader metadata = new BufferedReader(new FileReader("downloadedWebpage.txt"));
			String link = metadata.readLine();
			while (link != null) {
				System.out.println("Checking: " +link);
				if (link.length() == 0) continue;
				sync(new URL(link));
				link = metadata.readLine();					
			}
			metadata.close();
		} catch (Exception e) {
			System.out.println("Metadata not found or corrupt");
		}

	}

}
