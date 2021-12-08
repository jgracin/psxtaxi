package psxtaxi;

import java.util.function.Consumer;

public class EventProcessor extends Thread {
    private boolean shouldExit = false;
    private final PsxConnector connector;
    private final Consumer<Message> qs121Handler;
    private final Consumer<Message> qh426Handler;

    public EventProcessor(PsxConnector connector, Consumer<Message> qs121Handler, Consumer<Message> qh426Handler) {
        super();
        this.connector = connector;
        this.qs121Handler = qs121Handler;
        this.qh426Handler = qh426Handler;
    }

    public void run() {
        String received;
        Exception previousException = null;
        while (!shouldExit) {
            try {
                if ((received = connector.readLine()) != null) {
                    if ("exit".equals(received)) {
                        exit();
                    } else {
                        Message message = parseMessage(received);
                        switch (message.qcode) {
                            case "Qs121": this.qs121Handler.accept(message); break;
                            case "Qh426": this.qh426Handler.accept(message); break;
                        }
                    }
                }
            } catch (Exception e) {
                previousException = printExceptionUnlessSameAsPrevious(e, previousException);
            }
        }
    }

    private Exception printExceptionUnlessSameAsPrevious(Exception e, Exception previousException) {
        if (previousException == null) {
            e.printStackTrace(System.err);
        } else if (!previousException.getMessage().equals(e.getMessage())) {
            e.printStackTrace(System.err);
        }
        return e;
    }

    public void exit() {
        this.connector.writeLine("exit");
        this.shouldExit = true;
        try { Thread.sleep(200); } catch (InterruptedException e) { /* ignore */ }
    }

    private static Message parseMessage(String psxMessage) {
        String[] m = psxMessage.split("=");
        if (m.length == 2) {
            return new Message(m[0], m[1].split(";"));
        } else {
            return new Message(m[0], new String[]{});
        }
    }
}
