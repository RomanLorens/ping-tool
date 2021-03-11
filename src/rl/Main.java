package rl;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

public class Main {

	private static class Server {
		String host;
		String env;

		Server(String host, String env) {
			this.host = host;
			this.env = env;
		}
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			throw new IllegalArgumentException("needs path to cfg file");
		}
		Properties props = new Properties();
		if (args.length >= 2) {
			try (FileInputStream stream =  new FileInputStream(args[1])) {
				props.load(stream);
			}
		}
		
		List<Server> servers = new ArrayList<>();
		String cfg = args[0];
		try (Stream<String> lines = Files.lines(Paths.get(cfg))) {
			lines.forEach(l -> {
				String[] split = l.split("\\|");
				if (split.length == 2) {
					servers.add(new Server(split[0], split[1]));
				}
			});
		}
		List<Server> failed = new ArrayList<>();
		servers.forEach(s -> {
			if (!ping(s)) {
				failed.add(s);
			}
		});

		if (!failed.isEmpty()) {
			Mail mail = new Mail(props);
			StringBuilder msg = new StringBuilder();
			failed.forEach(f -> {
				msg.append(f.host).append("|").append(f.env).append(" HOST not reachable").append("\n");
			});
			mail.send(msg.toString());
		}

	}

	private static boolean ping(Server s) {
		InetAddress address;
		boolean reachable = false;
		try {
			address = InetAddress.getByName(s.host);
			reachable = address.isReachable(10000);
			System.out.println(s.host + " [" + s.env + "] is " + (reachable ? "reachable" : "NOT reachable!"));
		} catch (IOException e) {
			System.out.println(s.host + " [" + s.env + "] is " + (reachable ? "reachable" : "NOT reachable!"));
		}
		return reachable;
	}

}
