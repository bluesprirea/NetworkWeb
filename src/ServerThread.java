import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

import javax.activation.MimetypesFileTypeMap;


public class ServerThread extends Thread {
	//맨 처음에 뜨는 HTML 주소 설정하는 스트링을 설정.
	  private static final String DEFAULT_FILE_PATH = "./webapps/index.html";
	  private Socket connectionSocket;
	  
	  public ServerThread(Socket connectionSocket)
	  {
	    this.connectionSocket = connectionSocket;
	  }
	  
	  @Override
	  public void run()
	  {
	    System.out.println("Thread Created");
	    BufferedReader inClient = null;
	    //bufferedReader클래스는 바이트 단위가 아니라 문자 단위로 문자를 읽어드리는 데, 버퍼링을 함으로써 문자, 문자배열 문자열 라인 들을 효율적이로 편하게 사용할 수 있
	    DataOutputStream outClient = null;
	    //DataOutputStream은 boolean, byte, char, short, int, long, float, double들과 같은 자료의 ;기본형을 직접 읽고 쓸 수 있게 해준다.
	    try
	    {
	      inClient = new BufferedReader(
	            new InputStreamReader(connectionSocket.getInputStream()));
	      outClient = new DataOutputStream(
	            connectionSocket.getOutputStream());
	      // 클라이언트 통신에 대한 I/O Stream 생성.
	      // 클라이언트로의 메시지중 첫번째 줄을 읽은 후에, 토큰을 생성한다. 
	      // StringTokenizer은 문자열을 분해할 때 특정 요소의 값이 없을 경우 처리하는 것에 대한 질문을 받아 문자열을 특정
	      //구분자를 기준으로 분해하는 것.
	      String requestMessageLine = inClient.readLine();

	      StringTokenizer tokenizedLine = new StringTokenizer(
	            requestMessageLine);

	      if(tokenizedLine.nextToken().equals("GET"))
	      {
	        // 토큰이 겟이라고 되어 있으면 그 다음 토큰이 파일이름이라고 지정해주는 것. 또한 불필요한 path(/)를 생략한다.
	        String fileName = tokenizedLine.nextToken();

	        
	        if(fileName.startsWith("/") == true)
	        {
	          if(fileName.length() > 1)
	          {
	            fileName = fileName.substring(1);
	          }
	          else
	          {
	            fileName = DEFAULT_FILE_PATH;
	          }
	        }

	        File file = new File(fileName);
	        
	        // 요청한 파일이 있을 경우,
	        // MIME이란, 브라우저에게 지금 서버가 보내려고 하는 데이터의 형식을 보내려고 하니 준비하라는 의미로 할 수 잇음.
	        // html의 경우, text/html, 이미지의 경우, images/jpg 등의 예시를 들 수 있다.
	        // 먼저 MimetypesFileTypeMap으로 형식을 파악한다. 그리고 파일의 바이트 수를 찾는다.
	        // 그 다음, 해당 파일을 읽는 작업을 한다. 그리고 헤더에 입력할 내용을 입력한다. 
	        // 이미지의 경우 현재, 크롬에서 이미지를 text/html로 둬도 알아서 해석이 되기 때문에 
	        // 이미지를 따로 설정하는 것은 생략하였음.. (사실 어떻게 해야할지 잘 모르겟음.)
	        if(file.exists())
	        {
	          String mimeType = new MimetypesFileTypeMap()
	            .getContentType(file);
	          mimeType = "text/html";
	          
	          int numOfBytes = (int) file.length();

	          FileInputStream inFile = new FileInputStream(fileName);
	          byte[] fileInBytes = new byte[numOfBytes];
	          inFile.read(fileInBytes);

	          outClient.writeBytes("HTTP/1.0 200 Document Follows \r\n");
	          outClient.writeBytes("Content-Type: " + mimeType + "\r\n");

	          outClient.writeBytes("Content-Length: " + numOfBytes + "\r\n");
	          outClient.writeBytes("\r\n");
	          
	          outClient.write(fileInBytes, 0, numOfBytes);
	        }
	        else
	        {
	          // 맞는 파일이 존재하지 않으면 종료시킴.
	          System.out.println("Requested File Not Found : " + fileName);
	          
	          outClient.writeBytes("HTTP/1.0 404 Not Found \r\n");
	          outClient.writeBytes("Connection: close\r\n");
	          outClient.writeBytes("\r\n");
	        }
	      }
	      else
	      {
	        // 토큰이 get이 아니라면, 내보낼 에러 메시지에 대한 내용.
	        System.out.println("Bad Request");
	        
	        outClient.writeBytes("HTTP/1.0 400 Bad Request Message \r\n");
	        outClient.writeBytes("Connection: close\r\n");
	        outClient.writeBytes("\r\n");
	      }
	      //연결을 끊고, 연결이 끊겼다는 메시지 주기.
	      connectionSocket.close();
	      System.out.println("Connection Closed");
	      System.out.println(outClient);
	    }
	    catch(IOException ioe)
	    {
	      ioe.printStackTrace();
	    }
	  }
}
