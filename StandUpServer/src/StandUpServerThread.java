import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

public class StandUpServerThread implements Runnable {

	private static final int MAX_PLAYER = 5;
	private static final long MONEY = 100000000;

	Socket socket;

	BufferedReader in; // ������ ���ſ�
	PrintWriter out; // ������ ���ۿ�

	String userID; // ��ȭ��
	ArrayList<Members> players;

	InetAddress ip;
	String msg;
	String notice;
	String users = "";

	int lose = 0;

	boolean boolReset = true;

	Boolean player2 = false;

	int xPos = 0;
	int yPos = 0;

	public StandUpServerThread(Socket socket, ArrayList<Members> players) {
		this.socket = socket;
		this.players = players;

		try {
			// ������ ����
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			// ������ �۽�
			out = new PrintWriter(socket.getOutputStream());

			userID = in.readLine();
			String id = createID();

			ip = socket.getInetAddress();

			Thread thread = new Thread(new Runnable() {

				boolean b = true;
				int c = 0;

				@Override
				public void run() {
					while (b) {
						c = 0;
						try {
							for (Members player : players) {
								if (player.ready) {
									c++;
								}

							}

							if (c == players.size() && players.size() >= 2) {
								System.out.println("play game");
								users = users + players.get(0).name + " " + players.get(1).name;
								b = false;
							}
						} catch (Exception e) {

						}
					}
					broadcast("/ready true");
					startGame();
				}
			});
			thread.start();

			if (players.size() < MAX_PLAYER) {
				System.out.println(ip + "�κ��� " + userID + "���� �����Ͽ����ϴ�.");

				// ��� Ŭ���̾�Ʈ���� ��ε�ĳ��Ʈ
				broadcast(userID + "(" + id + ") ���� �����ϼ̽��ϴ�.");
				playerVisit();

				synchronized (players) {
					int[] indexes = new int[MAX_PLAYER];
					Members member;

					for (int i = 0; i < players.size(); i++) {
						member = players.get(i);
						indexes[member.index - 1] = member.index;
					}

					for (int i = 0; i < MAX_PLAYER; i++) {
						if (indexes[i] == 0) {
							players.add(new Members(id, userID, out, false, MONEY, i + 1));
							System.out.println("index = " + (i + 1));
							break;
						}
					}
				}

				if (players.size() >= MAX_PLAYER) {

					users = users + players.get(0).name + " " + players.get(1).name;

					System.out.println(players.size());

					if (players.size() >= MAX_PLAYER) {
						System.out.println("/member " + players.get(0).name + " " + players.get(1).name + " size = "
								+ players.size());
					}
				}

			} else {
				synchronized (players) {
					players.add(new Members(id, userID, out, false, MONEY, 6));
					sendNotice("/to " + userID + " �ο����� �ʰ��Ǿ����ϴ�.", userID);
					players.remove(players.size() - 1);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String createID() {
		Calendar cal = Calendar.getInstance();
		int h = cal.get(Calendar.HOUR_OF_DAY);
		int m = cal.get(Calendar.MINUTE);
		int s = cal.get(Calendar.MILLISECOND);
		int ran = (int) (Math.random() * 9999) + 1000;

		return String.format(Locale.KOREA, "%02d%02d%02d%04d", h, m, s, ran);
	}

	public void pickCard() {
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

		if (boolReset) {
			StandUpServer.total = 0;
			StandUpServer.betting = 0;
		} else {
			StandUpServer.betting += StandUpServer.total;
			StandUpServer.total = 0;
		}
	}
	
	public void playerVisit() {
		String strName = "";
		String strIndex = "";
		Members player;
		
		for(int i = 0; i < players.size(); i++) {
			player = players.get(i);
			
			strName += player.name + " ";
			strIndex += player.index + " ";
		}
		
		strName = strName.trim();
		strIndex = strIndex.trim();
		
		broadcast("/player " + strName);
		broadcast("/index " + strIndex);
	}

	public void startGame() {

		try {
			Thread.sleep(3000);
		} catch (Exception e) {
			
		}
		StandUpServer.pickCard();
		broadcast("/play");
		
		String card = "";
		Members player;
		
		for(int i = 0; i < players.size(); i++) {
			player = players.get(i);
			player.card1 = StandUpServer.cards[2 * i];
			player.card2 = StandUpServer.cards[2 * i + 1];
			
			card += player.card1 + " " + player.card2 + " ";
		}

		System.out.println("server = " + card);
		broadcast("/card " + card.trim());

		broadcast("/money " + StandUpServer.money1 + " " + StandUpServer.money2);

		try {
			Thread.sleep(3000);
			StandUpServer.turn = 1;
			broadcast("/button true");
			broadcast("/turn " + players.get(StandUpServer.turnCnt).name);
			//broadcast("/turn " + StandUpServer.turn + " " + "true " + StandUpServer.betting + " " + StandUpServer.total + " " + StandUpServer.money1 + " " + StandUpServer.money2);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		String rcvData;
		String tag;

		try {
			while ((rcvData = in.readLine()) != null) {
				tag = rcvData.split(" ")[0];
				if (rcvData.equals("/quit")) { // /quit�� �Է��ϸ� ä�� ����
					synchronized (players) {
						for (int i = 0; i < players.size(); i++) {
							if (players.get(i).name.equals(userID)) {
								players.remove(i);
							}
						}
					}

					break;
				} else if (rcvData.indexOf("/to") >= 0) { // �ӼӸ�
					sendMsg(rcvData);
				} else {
					if (!rcvData.split(" ")[0].equals("/mouse")) {
						System.out.println("[" + userID + "] " + rcvData);
					}
					// ��� Ŭ���̾�Ʈ���� ��ε�ĳ��Ʈ
					broadcast("[" + userID + "] " + rcvData);
				}

				if (tag.equals("/ready")) {
					String name = rcvData.split(" ")[1];
					boolean b = Boolean.parseBoolean(rcvData.split(" ")[2]);

					for (int i = 0; i < players.size(); i++) {
						if (players.get(i).name.equals(name)) {
							players.get(i).ready = b;
						}
					}
				}

				if (tag.equals("/turn")) {
					if (rcvData.split(" ")[2].equals("call")) {
						broadcast("/button true");
						StandUpServer.callCnt++;
						StandUpServer.turnCnt++;
						
						if(StandUpServer.turnCnt == players.size()) {
							StandUpServer.turnCnt = 0;
						}
						
						broadcast("/turn " + players.get(StandUpServer.turnCnt).name);
						
						/*StandUpServer.turn = 0;
						StandUpServer.betting = Integer.parseInt(rcvData.split(" ")[3]);
						StandUpServer.total += StandUpServer.betting;
						System.out.println(">>>>>>>>>>>>> call = " + StandUpServer.total);

						if (rcvData.split(" ")[1].equals("1")) {
							StandUpServer.money1 -= StandUpServer.betting;
							StandUpServer.next = 2;
						} else if (rcvData.split(" ")[1].equals("2")) {
							StandUpServer.money2 -= StandUpServer.betting;
							StandUpServer.next = 1;
						}

						if (StandUpServer.lose == 1) {
							StandUpServer.money2 += StandUpServer.total;
						} else if (StandUpServer.lose == 2) {
							StandUpServer.money1 += StandUpServer.total;
						}

						broadcast("/money " + StandUpServer.money1 + " " + StandUpServer.money2);
						broadcast("/button false");
						broadcast("/turn " + StandUpServer.next + " " + "false " + StandUpServer.betting + " "
								+ StandUpServer.total + " " + StandUpServer.money1 + " " + StandUpServer.money2);
						broadcast("/result " + StandUpServer.lose);
						broadcast("/open");
						Thread.sleep(5000);
						broadcast("/close");
						startGame();*/

					} else if (rcvData.split(" ")[2].equals("die")) {

						if (StandUpServer.lose == 1) {
							StandUpServer.money2 += StandUpServer.total;
						} else if (StandUpServer.lose == 2) {
							StandUpServer.money1 += StandUpServer.total;
						}
						broadcast("/money " + StandUpServer.money1 + " " + StandUpServer.money2);
						broadcast("/button false");
						broadcast("/result " + rcvData.split(" ")[1]);

						Thread.sleep(5000);
						broadcast("/close");
						startGame();
					} else if (rcvData.split(" ")[2].equals("plus")) {
						broadcast("/button true");
						StandUpServer.turn = 0;
						StandUpServer.betting = Integer.parseInt(rcvData.split(" ")[3]);
						StandUpServer.total += StandUpServer.betting;
						System.out.println(">>>>>>>>>>>>> plus = " + StandUpServer.total);

						if (rcvData.split(" ")[1].equals("1")) {
							StandUpServer.money1 -= StandUpServer.betting;
							StandUpServer.next = 2;
						} else if (rcvData.split(" ")[1].equals("2")) {
							StandUpServer.money2 -= StandUpServer.betting;
							StandUpServer.next = 1;
						}

						broadcast("/turn " + StandUpServer.next + " " + "false " + StandUpServer.betting + " "
								+ StandUpServer.total + " " + StandUpServer.money1 + " " + StandUpServer.money2);

					}

					broadcast("/money " + StandUpServer.money1 + " " + StandUpServer.money2);
				}

				if (tag.equals("/lose")) {
					StandUpServer.lose = Integer.parseInt(rcvData.split(" ")[1]);
					System.out.println(">>>>>>>>>>>>> lose = " + StandUpServer.total);

					System.out.println("server lose = " + StandUpServer.lose);
					if (StandUpServer.lose == 0) {
						boolReset = false;
					} else {
						boolReset = true;
					}
				}

				if (tag.equals("/mouse")) {
					xPos = Integer.parseInt(rcvData.split(" ")[1]);
					yPos = Integer.parseInt(rcvData.split(" ")[2]);
					broadcast(rcvData);
				}
				
				if(tag.equals("/visitors")) {
					playerVisit();
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			synchronized (players) {
				for (int i = 0; i < players.size(); i++) {
					if (players.get(i).name.equals(userID)) {
						players.remove(i);
					}
				}
			}
			broadcast(userID + "���� �����߽��ϴ�.");
			System.out.println(userID + "�� ����");

			try {
				if (socket != null) {
					in.close();
					out.close();
					socket.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	} // run() ��

	// ��� Ŭ���̾�Ʈ���� ��ε�ĳ��Ʈ �޽��� ����
	public void broadcast(String message) {
		synchronized (players) {
			PrintWriter out = null;
			for (int i = 0; i < players.size(); i++) {
				out = players.get(i).writer;
				out.println(message);
				out.flush();
			}
		}
	}

	@SuppressWarnings("null")
	public void broadcastObject(Object obj) {
		synchronized (players) {
			PrintWriter out = null;
			for (int i = 0; i < players.size(); i++) {
				out.println(obj);
				out.flush();
			}
		}
	}

	/*
	 * �ӼӸ� ó�� �ӼӸ� �Է� ����: /to ���ID �޽���
	 */
	public void sendMsg(String message) {
		// �����̽��� �����̽� ���̿� ���̵� �����ϱ� ����
		int begin = message.indexOf(" ") + 1;
		int end = message.indexOf(" ", begin);

		if (end != -1) {
			String id = message.substring(begin, end);
			String msg = message.substring(end + 1);

			PrintWriter out = null;

			for (int i = 0; i < players.size(); i++) {
				if (players.get(i).name.equals(id)) {
					out = players.get(i).writer;
				}
			}

			try {
				if (out != null) {
					out.println(userID + "���� �ӼӸ� >> " + msg);
					out.flush();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void sendNotice(String message, String userID) {
		int begin = message.indexOf(" ") + 1;
		int end = message.indexOf(" ", begin);

		if (end != -1) {
			String id = message.substring(begin, end);
			String msg = message.substring(end + 1);

			PrintWriter out = null;

			for (int i = 0; i < players.size(); i++) {
				if (players.get(i).name.equals(id)) {
					out = players.get(i).writer;
				}
			}

			try {
				if (out != null) {
					out.println("/quit");
					out.flush();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
