package psxtaxi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class PsxConnector implements AutoCloseable {
    private final String host;
    private final int port;
    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter ou = null;

    public PsxConnector(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void connect() {
        try {
            this.socket = new Socket(this.host, this.port);
            this.socket.setTcpNoDelay(true);
            this.ou = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            this.socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readLine() {
        try {
            return in.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void writeLine(String line) {
        this.ou.println(line);
        this.ou.flush();
    }
}
