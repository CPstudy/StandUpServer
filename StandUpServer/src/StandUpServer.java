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
	ServerSocket listener = null;	// ServerSocket: ���� ��û�� ��ٸ��� ����
	Socket socket = null;
	
	HashMap<String, PrintWriter> hashMap;
	ArrayList<String> players;
	
	public StandUpServer() {
		try {
			StandUpServerThread svrThread;	// ���� ������
			Thread thread;
			
			listener = new ServerSocket(port);	// ���� ���� ����
			
			System.out.println("*************************************************");
			System.out.println("                   ä�� ����                                         ");
			System.out.println("*************************************************");
			System.out.println("Ŭ���̾�Ʈ�� ������ ��ٸ��ϴ�...");
			
			hashMap = new HashMap<>();
			players = new ArrayList<>();
			
			while(true) {
				socket = listener.accept();		// Ŭ���̾�Ʈ�κ��� ���� ��û ���� - ��ſ� ���� ����
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
