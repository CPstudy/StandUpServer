import java.io.PrintWriter;

public class Members {
	String id;
	String name;
	PrintWriter writer;
	boolean ready;
	long money;
	int index;
	int card1;
	int card2;
	
	Members(String id, String name, PrintWriter writer, boolean ready, long money, int index) {
		this.id = id;
		this.name = name;
		this.writer = writer;
		this.ready = ready;
		this.money = money;
		this.index = index;
	}
}
