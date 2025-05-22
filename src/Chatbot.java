import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.api.domain.IDisposable;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.enums.CommandPermission;
import com.github.twitch4j.helix.domain.*;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.time.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Chatbot {

    public TwitchClient twitchClient;
    public Properties config = new Properties();
    public String channelName = null;
    public int lostCounter;

   public String discordLink;
   public String twitterLink;
   public String YtLink;
   public String InstaLink;
   public String podcastLink;
   public String musikPlaylist;
   public String friendcode;
   public String spendenLink;
   public String codefuerLobby;

    public String finalDeinToken;
    public String broadcastID;
    public String deintoken;
    public String finalToken;

    Scanner scanner = new Scanner(System.in);

    public InputStream input;
    public Properties properties = new Properties();

    public void start() throws IOException {

        File configDatei = new File("config.properties");
        File zitateDatei = new File("config.propertiesZitate");

        if (!configDatei.exists()) {
            boolean erstellen = configDatei.createNewFile();
        }

        if (!zitateDatei.exists()) {
            boolean erstellen = zitateDatei.createNewFile();
        }
        input = new FileInputStream(configDatei);
        config.load(input);
        properties.load(input);

        String clientID = null;
        String clientSecret = null;

       config.load(input);

        if (config.getProperty("clientID") == null || config.getProperty("clientID").isEmpty()) { //
            System.out.println("Gib deine clientID ein:");
            String clientIDEingabe = scanner.nextLine();

            config.setProperty("clientID", clientIDEingabe); // key + value die gespeichert werden soll

            clientID = clientIDEingabe;
        } else {
            clientID = config.getProperty("clientSecret");
        }

        if (config.getProperty("clientSecret") == null || config.getProperty("clientSecret").isEmpty()) { //
            System.out.println("Gib deine Client Secret ein:");
            String clientSecretEingabe = scanner.nextLine();

            config.setProperty("clientSecret", clientSecretEingabe);
            clientSecret = clientSecretEingabe;
        } else {
            clientID = config.getProperty("clientSecret");
        }

        if (config.getProperty("deinToken") == null || config.getProperty("deinToken").isEmpty()) { //
            System.out.println("Gib deinen Token ein:");
            String deintokenEingabe = scanner.nextLine();

            config.setProperty("deinToken", deintokenEingabe);
            deintoken = deintokenEingabe;


        } else {
            deintoken = config.getProperty("deinToken");
        }

        if (config.getProperty("DiscordLink") == null || config.getProperty("DiscordLink").isEmpty()) {

            System.out.println("Gib deinen discord Link ein");
            String discordLinkEingabe = scanner.nextLine();

            config.setProperty("DiscordLink", discordLinkEingabe);
            discordLink = discordLinkEingabe;

        } else {
            discordLink = config.getProperty("DiscordLink");
        }

        if (config.getProperty("twitterLink") == null || config.getProperty("twitterLink").isEmpty()) {

            System.out.println("Gib deinen Twitter Link ein");
            String twitterLinkEingabe = scanner.nextLine();

            config.setProperty("twitterLink", twitterLinkEingabe);
            twitterLink = twitterLinkEingabe;

        } else {
            twitterLink = config.getProperty("twitterLink");
        }

        if (config.getProperty("InstaLink") == null || config.getProperty("InstaLink").isEmpty()) {

            System.out.println("Gib deinen Instagram Link ein");
            String instagramEingabe = scanner.nextLine();

            config.setProperty("InstaLink", instagramEingabe);
            InstaLink = instagramEingabe;

        } else {
            InstaLink = config.getProperty("InstaLink");
        }

        if (config.getProperty("YtLink") == null || config.getProperty("YtLink").isEmpty()) {

            System.out.println("Gib deinen youtube Link ein");
            String ytEingabe = scanner.nextLine();

            config.setProperty("YtLink", ytEingabe);
            YtLink = ytEingabe;

        } else {
            YtLink = config.getProperty("YtLink");
        }

        if (config.getProperty("podcastLink") == null || config.getProperty("podcastLink").isEmpty()) {

            System.out.println("Gib deinen podcastLink ein");
            String podcastEingabe = scanner.nextLine();

            config.setProperty("podcastLink", podcastEingabe);
            podcastLink = podcastEingabe;

        } else {
            podcastLink = config.getProperty("");
        }

        if (config.getProperty("lobbyCode") == null || config.getProperty("lobbyCode").isEmpty()) {

            System.out.println("Gib deinen lobby code ein");
            String codeEingabe = scanner.nextLine();

            config.setProperty("lobbyCode", codeEingabe);
            codefuerLobby = codeEingabe;

        } else {
            codefuerLobby = config.getProperty("lobbyCode");
        }

        if (config.getProperty("fiendCode") == null || config.getProperty("fiendCode").isEmpty()) {
            System.out.println("Gib deinen Friendcode ein:");
            String fcEingabe = scanner.nextLine();

            config.setProperty("fiendCode", fcEingabe);
            friendcode = fcEingabe;

        } else {
            friendcode = config.getProperty("fiendCode");
        }

        if (config.getProperty("playlist") == null || config.getProperty("playlist").isEmpty()) {

            System.out.println("Gib deinen Link für die musik ein:");
            String playlistEingabe = scanner.nextLine();

            config.setProperty("playlist", playlistEingabe);
            musikPlaylist = playlistEingabe;

        } else {
            musikPlaylist = config.getProperty("playlist");
        }

        if (config.getProperty("ChannelName") == null || config.getProperty("ChannelName").isEmpty()) {
            System.out.println("Gib deinen Channel Namen ein:");
            String channelNameEingabe = scanner.nextLine();


            config.setProperty("ChannelName", channelNameEingabe);
            channelName = channelNameEingabe;

        } else {
            channelName = config.getProperty("ChannelName");
        }

        if (config.getProperty("spendenLink") == null || config.getProperty("spendenLink").isEmpty()) {
            System.out.println("Gib deine SpendenLink ein:");
            String spendenlinkEingabe = scanner.nextLine();

            config.setProperty("spendenLink", spendenlinkEingabe);
            spendenLink = spendenlinkEingabe;
        } else {
            spendenLink = config.getProperty("spendenLink");
        }

        if (config.getProperty("broadcastID") == null || config.getProperty("broadcastID").isEmpty()) {
            System.out.println("Gib deine Broadcast Id ein (findest du im internet) ein:");
            String broadcastIDEingabe = scanner.nextLine();
            scanner.close();

            config.setProperty("broadcastID", broadcastIDEingabe);
            broadcastID = broadcastIDEingabe;
        } else {
            broadcastID = config.getProperty("broadcastID");
        }

        String bottoken = "xkxku1asl8gzgjmps2qh366ldzzcvj";
        finalDeinToken = deintoken;

        if (bottoken == null) {
            String tokenURL = "https://id.twitch.tv/oauth2/authorize?response_type=token&client_id=" + clientID + "&redirect_uri=http://localhost&scope=chat:read+chat:edit+clips:edit+user:edit:broadcast+user:manage:whispers+moderator:read:followers+moderator:manage:chat_messages+moderation:read+moderator:manage:banned_users&force_verify=true";
            try {
                Desktop.getDesktop().browse(new URI(tokenURL));
            } catch (Exception e) {
                System.out.printf("Please open URL: %s", tokenURL);
            }

            System.out.println("Gib den Token ein");
            Scanner scanner = new Scanner(System.in);
            bottoken = scanner.nextLine();
            System.out.println(bottoken);
        }
        finalToken = bottoken;


        if (config.getProperty("Lost") == null || config.getProperty("Lost").isEmpty()) {
            config.setProperty("Lost", String.valueOf(lostCounter));
        } else {
            lostCounter = Integer.parseInt((config.getProperty("Lost")));
        }

        try {
            speichernConfig();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // chat credential
        OAuth2Credential credential = new OAuth2Credential("twitch", bottoken);

        // twitch client
        twitchClient = TwitchClientBuilder.builder()
                .withEnableChat(true)
                .withChatAccount(credential)
                .withEnableHelix(true)
                .withClientId(clientID)
                .withClientSecret(clientSecret)
                .build();
        twitchClient.getChat().joinChannel(channelName);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> {
            zitatAusgabe();
        };

        LocalTime jetzt = LocalTime.now();
        int secZurNaechstenStunde = (60 - jetzt.getMinute()) * 60 - jetzt.getSecond();

        scheduler.scheduleAtFixedRate(task, secZurNaechstenStunde, 3600, TimeUnit.SECONDS);
    }

    public void zitatAusgabe() {

        Properties properties = new Properties();

        try (InputStream input = new FileInputStream("config.propertiesZitate")) {
            ladenvonPropeties(String.valueOf(properties));
            input.close();

            if (!properties.isEmpty()) {
                Object[] werte = properties.values().toArray();
                String zufall = werte[(int) (Math.random() * werte.length)].toString();
                twitchClient.getChat().sendMessage("Ayuuki_Chan", zufall);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void ladenvonPropeties(String name) throws IOException {
        config.load(Reader.of(name));
    }

    public void speichernConfig() throws IOException {

        OutputStream output = new FileOutputStream("config.properties"); // ausgeben
        config.store(output, null); //schreiben in die dartei
        output.close();
    }
}