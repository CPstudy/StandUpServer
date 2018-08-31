import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class StandUpServer {
	
	public static int turnCnt = 0;
	public static int callCnt = 0;
	public static int turn = 0;
	public static int next = 0;
	public static int money1 = 100000000;
	public static int money2 = 100000000;
	public static int betting = 0;
	public static int total = 0;
	public static int lose = 0;
	public static boolean boolFirst = true;
	public static boolean boolStart = false;
	public static boolean boolPick = false;
	public static String nextPlayer = "";
	public static int[] cards = new int[10];
	
	int port = 9999;
	ServerSocket listener = null;	// ServerSocket: ���� ��û�� ��ٸ��� ����
	Socket socket = null;
	
	ArrayList<Members> players;
	
	public StandUpServer() {
		try {
			StandUpServerThread svrThread;	// ���� ������
			Thread thread;
			
			listener = new ServerSocket(port);	// ���� ���� ����
			
			System.out.println("*************************************************");
			System.out.println("                   ä�� ����                                         ");
			System.out.println("*************************************************");
			System.out.println("Ŭ���̾�Ʈ�� ������ ��ٸ��ϴ�...");
			
			players = new ArrayList<>();
			
			while(true) {
				socket = listener.accept();		// Ŭ���̾�Ʈ�κ��� ���� ��û ���� - ��ſ� ���� ����
				if(socket != null) {
					svrThread = new StandUpServerThread(socket, players);
					thread = new Thread(svrThread);
					thread.start();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void pickCard() {
		if(boolPick) return;
		
		boolPick = true;
		for (int i = 0; i < 10; i++) {
			int number = (int) (Math.random() * 20);
			System.out.println("number = " + number);
			StandUpServer.cards[i] = number;
			for (int j = 0; j < i; j++) {
				if (StandUpServer.cards[j] == number) {
					i--;
				}
			}
		}
		
		System.out.print("���� = ");
		for(int i = 0; i < 10; i++) {
			System.out.print(StandUpServer.cards[i] + " ");
		}
		System.out.println();
	}
	
	public static void main(String[] args) {
		new StandUpServer();
	}
}
