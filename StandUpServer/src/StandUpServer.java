import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class StandUpServer {
	
	public static int turn = 0;
	public static int next = 0;
	public static boolean boolFirst = true;
	public static int money1 = 100000000;
	public static int money2 = 100000000;
	public static int betting = 0;
	public static int total = 0;
	public static int lose = 0;
	
	int port = 9999;
	ServerSocket listener = null;	// ServerSocket: 연결 요청을 기다리는 소켓
	Socket socket = null;
	
	HashMap<String, PrintWriter> hashMap;
	ArrayList<String> players;
	
	public StandUpServer() {
		try {
			StandUpServerThread svrThread;	// 서버 스레드
			Thread thread;
			
			listener = new ServerSocket(port);	// 서버 소켓 생성
			
			System.out.println("*************************************************");
			System.out.println("                   채팅 서버                                         ");
			System.out.println("*************************************************");
			System.out.println("클라이언트의 접속을 기다립니다...");
			
			hashMap = new HashMap<>();
			players = new ArrayList<>();
			
			while(true) {
				socket = listener.accept();		// 클라이언트로부터 연결 요청 수신 - 통신용 소켓 생성
				if(socket != null) {
					svrThread = new StandUpServerThread(socket, hashMap, players);
					thread = new Thread(svrThread);
					thread.start();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new StandUpServer();
	}
}
