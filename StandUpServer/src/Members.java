import java.io.PrintWriter;

public class Members {
	String name;
	PrintWriter writer;
	boolean ready;
	
	Members(String name, PrintWriter writer, boolean ready) {
		this.name = name;
		this.writer = writer;
		this.ready = ready;
	}
}
