import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class WebServer {
	private final static Logger log = Logger.getLogger(WebServer.class.getName());
	//  Logger 클래스는 심각한 레벨부터 나열하면 다음과 같다. (SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST)
	//	먼저, 진행순서는 다음과 같다. 먼저 서버에서 생성한 후에, 포트에 대한 감시가 시작되고 클아이언트의 접속을 기다린다. 
	//	소켓 생성시에 인자 값으로 클라리언트가 아이피, 포트를 지정하고 서버에 접속을 요구한다. 그러면 서버는 클라이언트의 요구를 받아 
	//	소켓 객체를 생성한다. 생성된 소켓 객체를 이용해 클라이언트에게 데이터를 보낸다. 클라이언트는 소켓 객체로 데이터를 받고 필요한
	//	데이터를 다시 서버로 전송함.
	
	public static void main(String argv[]) throws Exception{
		ServerSocket listenSocket = new ServerSocket(8081, 3);
		//서버 소켓 생성
		log.log(Level.INFO, "WebServer Socket Created.");
		//서버 소켓이 만들어졌다는 로그 생성.
		Socket connection;
		ServerThread serverThread;
		//소켓과 서버 쓰레드에 대한 객체를 생성.
		while ((connection = listenSocket.accept())!= null){
			//클라이언트에게 전송할 때 필요한 소켓 객체가 있을때 루프가 돌아간다.
			serverThread = new ServerThread(connection);
			serverThread.start();
			//서버 쓰레드를 생성하고 거기에 소켓을 부여한다. 그리고 시작한다.
		}
		
	}
}