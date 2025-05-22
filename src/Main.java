import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Chatbot chatbot = new Chatbot();
        Commands commands = new Commands(chatbot);
        chatbot.start();
        commands.start();
    }

}


