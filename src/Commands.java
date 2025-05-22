import com.github.philippheuer.events4j.api.domain.IDisposable;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.enums.CommandPermission;
import com.github.twitch4j.helix.domain.*;

import java.io.*;
import java.time.*;
import java.util.*;

public class Commands {

    public Chatbot chatbot;

    public Commands(Chatbot chatbot){
        this.chatbot = chatbot;
    }


    public void start(){

        IDisposable handlerReg = chatbot.twitchClient.getEventManager().onEvent(ChannelMessageEvent.class, event -> {
            String user = event.getUser().getName();
            String userId = event.getUser().getId();
            System.out.println("[" + event.getChannel().getName() + "][" + event.getPermissions().toString() + "] " + event.getUser().getName() + ": " + event.getMessage());

            String msg = event.getMessage();

            if (msg.equals("!ping")) {
                nachicht(" pong!");
            }
            if (msg.equals("!lurk")) {
                nachicht("In der Stille des Waldes Beobachtet " + event.getUser().getName() + " den Fuchs. Vielen dank für deinen Lurk");
            }

            if (msg.equals("!unlurk")) {
                nachicht(event.getUser().getName() + " kehrt zurück aus dem Wald");
            }

            if (msg.equals("!clip")) {
                List<String> users = new ArrayList<>();
                users.add("Ayuuki_Chan");

                StreamList resultList = chatbot.twitchClient.getHelix().getStreams(null, null, null, 1, null, null, null, users).execute();

                resultList.getStreams().forEach(stream -> {
                    System.out.println("ID: " + stream.getId() + " - Title: " + stream.getTitle());
                    CreateClipList clipData = chatbot.twitchClient.getHelix().createClip(chatbot.finalDeinToken, stream.getUserId(), false).execute();

                    clipData.getData().forEach(clip -> {
                        System.out.println("Created Clip with ID: " + clip.getId() + "\n" + clip.getEditUrl());

                        nachicht("/w " + user + " Hier der code zum bearbeite des clips" + clip.getEditUrl());
                        nachicht("Clip wurde erfolgereich erstellt ");
                    });

                });
            }

            if (msg.equals("!lost")) {

                InputStream input = null;
                try {
                    ausgebenVonConfig();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                chatbot.config.getProperty("Lost", chatbot.config.getProperty("Lost"));
                chatbot.lostCounter = Integer.parseInt(chatbot.config.getProperty("Lost"));
                chatbot.lostCounter++;
                chatbot.config.setProperty("Lost", String.valueOf(chatbot.lostCounter));


                nachicht("Ayuu war bereits " + chatbot.config.getProperty("Lost") + " mal Lost im stream");

                try {
                    OutputStream output = new FileOutputStream("config.properties"); // ausgeben
                    chatbot.config.store(output, null); //schreiben in die dartei
                    output.close();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            if (msg.startsWith("!newGame")) {

                if (event.getPermissions().contains(CommandPermission.MODERATOR)) {
                    String gametitel = msg.substring(9);
                    String foundGameId = null;
                    GameList resultList2 = chatbot.twitchClient.getHelix().getGames(chatbot.finalToken, null, Arrays.asList(gametitel)).execute();
                    for (Game game : resultList2.getGames()) {
                        foundGameId = game.getId();
                        System.out.println("Game ID: " + game.getId() + " is " + game.getName());
                    }

                    chatbot.twitchClient.getHelix().updateChannelInformation(
                            chatbot.finalDeinToken,
                            chatbot.broadcastID,
                            ChannelInformation.builder().gameId(foundGameId).build()
                    ).execute();
                }
            }

            if (msg.startsWith("!titel")) {

                if (event.getPermissions().contains(CommandPermission.MODERATOR)) {
                    String neuername = msg.substring(7);

                    chatbot.twitchClient.getHelix().updateChannelInformation(
                            chatbot.finalDeinToken,
                            chatbot.broadcastID,
                            ChannelInformation.builder().title(neuername).build()
                    ).execute();
                }
            }

            if (msg.equals("!followage")) {
                InboundFollowers inboundFollowers = chatbot.twitchClient.getHelix().getChannelFollowers(chatbot.finalToken, "id", userId, null, null).execute();
                try {
                    InboundFollow follow = inboundFollowers.getFollows().getFirst();
                    String followingSince = follow.getFollowedAt().toString();

                    LocalDateTime parsedDate = LocalDateTime.parse(followingSince.substring(0, followingSince.length() - 1));

                    LocalDateTime aktuellertag = LocalDateTime.now();

                    Period result2 = Period.between(LocalDate.from(parsedDate), LocalDate.from(aktuellertag));

                    System.out.printf("Jahre: %d Monate: %d Tage: %d\n", result2.getYears(), result2.getMonths(), result2.getDays());
                    nachicht(user + "Du folgst schon Jahre: " + result2.getYears() + " Monate: " + result2.getMonths() + " Tage: " + result2.getDays());

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            if (!event.getPermissions().contains(CommandPermission.MODERATOR)) {
                if (msg.contains("https://www.twitch.tv/ayuuki_chan/clip")) {
                    System.out.println("passt");

                } else if (msg.contains("https://")) {
                    chatbot.twitchClient.getHelix().deleteChatMessages(chatbot.finalToken, chatbot.config.getProperty("broadcastID"), chatbot.config.getProperty("broadcastID"), event.getMessageEvent().getMessageId().get()).execute();
                    System.out.println("nachicht wurde gelöcht");
                }
            }

            if (msg.startsWith("!magische miesmuschel")) {
                int random = (int) (Math.random() * 3);
                System.out.println(random);

                String[] jaNeinVlt = {"@" + user + " die magische miesmuschel sagt ja", "@" + user + " hmm vieleicht", "@" + user + "die magische miesmuschel sagt definitiv nein"};
                nachicht(jaNeinVlt[random]);
            }

            if (msg.startsWith("!add")) {
                String neuesZitat = msg.substring(5).trim();

                Properties properties = new Properties();


                try {
                    FileInputStream in = new FileInputStream("config.propertiesZitate");
                    properties.load(in);
                    in.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                boolean gefunden = false;

                for (String key : properties.stringPropertyNames()) {
                    if (properties.getProperty(key).equals(neuesZitat)) {
                        gefunden = true;
                        break;
                    }
                }

                if (gefunden) {
                    nachicht("Zitat existiert schon.");
                } else {
                    int randomN = (int) (Math.random() * 100000);
                    properties.setProperty("zitat_" + randomN, neuesZitat);

                    try {

                        OutputStream out = new FileOutputStream("config.propertiesZitate");
                        properties.store(out, null); //schreiben in die dartei
                        out.close();
                        nachicht("Zitat hinzugefügt.");
                    } catch (IOException e) {
                        nachicht("Zitat konnte nicht hinzugefügt werden");
                        throw new RuntimeException(e);

                    }
                }
            }

            if (msg.startsWith("!delete")) {

                String zitat = msg.substring(8);

                Properties properties = new Properties();

                try (FileInputStream input = new FileInputStream("config.propertiesZitate")) {
                    properties.load(input);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                List<String> keysRemove = new ArrayList<>();
                for (String key : properties.stringPropertyNames()) {
                    if (zitat.equals(properties.getProperty(key))) {
                        keysRemove.add(key);
                    }
                }
                for (String key : keysRemove) {
                    properties.remove(key);
                    nachicht("Zitat gelöscht");
                }


                try (FileOutputStream outputStream = new FileOutputStream("config.propertiesZitate")) {
                    properties.store(outputStream, null);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }

            if (msg.startsWith("!bonk")) {

                ModeratorList mods = chatbot.twitchClient.getHelix().getModerators(chatbot.finalDeinToken, "133625281", null, null, null).execute();
                List<String> modnamen = mods.getModerators().stream().map(Moderator::getUserName).toList();

                String username = msg.substring(6);

                List<User> foundUsers = chatbot.twitchClient.getHelix().getUsers(chatbot.finalToken, null, Arrays.asList(username)).execute().getUsers();

                boolean isMod = modnamen.contains(username);

                if (foundUsers.isEmpty()) {
                    nachicht("Gibts nicht");
                } else if (username.equalsIgnoreCase("Ayuuki_Chan")) {
                    nachicht("OHA, das Merk ich mir");
                } else if (!isMod) {
                    chatbot.twitchClient.getHelix().banUser(chatbot.finalToken, chatbot.config.getProperty("broadcastID"), "1310069803", new BanUserInput(foundUsers.getFirst().getId(), "Du wurdest gebonkt", 60)).execute();
                    nachicht(username + " wurde gebonkt");
                } else {

                    nachicht("Die mods besitzen die macht deinen Bonk abzuwehren");
                }
            }

            if (msg.equals("!dc")) {
                nachicht("hier der Link zum Discord server " + chatbot.discordLink);
            }

            if (msg.equals("!socials")) {
                nachicht("Twitter: " + chatbot.twitterLink + " instagram: " + chatbot.InstaLink);
            }

            if (msg.equals("!code")) {
                nachicht("der code für die Lobby ist " + chatbot.codefuerLobby);
            }

            if (msg.startsWith("!codeChange")) {
                String neuercode = msg.substring(12);

                chatbot.codefuerLobby = (String) chatbot.config.setProperty("lobbyCode", neuercode); // macht das überhautp sinn ?
                chatbot.codefuerLobby = neuercode;
                chatbot.config.getProperty("lobbyCode", neuercode);

                try {
                    speichernConfig();
                    speichernConfig();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            if (msg.equals("!yt")) {
                nachicht("Youtube: " + chatbot.YtLink);
            }

            if (msg.equals("!fc")) {
                nachicht(chatbot.friendcode);
            }

            if (msg.equals("!musik")) {
                nachicht("die hintergrundmusik vom stream " + chatbot.musikPlaylist);
            }

            if (msg.equals("!donation")) {
                nachicht(chatbot.spendenLink);
            }

            if (msg.equals("!podcast")) {
                nachicht("hier gehts zum podcast " + chatbot.podcastLink);
            }

            if (msg.equals("!uptime")) {
                StreamList streams = (StreamList) chatbot.twitchClient.getHelix().getStreams(
                        chatbot.finalDeinToken,
                        null,
                        null,
                        null,
                        null,
                        null,
                        List.of(chatbot.broadcastID),
                        null
                ).execute();
                List<Stream> streamListe = streams.getStreams();

                if (!streamListe.isEmpty()) {
                    Stream streaminhalte = streamListe.getFirst();
                    Instant startedAt = streaminhalte.getStartedAtInstant();

                    Duration laufzeit = Duration.between(startedAt, Instant.now());

                    long stunden = laufzeit.toHours();
                    long minuten = laufzeit.toMinutes() % 60;
                    long sekunden = laufzeit.getSeconds() % 60;

                    nachicht("Stream läuft seit: " + laufzeit.toHours() + " Stunden, " + laufzeit.toMinutes() + " Minuten");
                } else {
                    nachicht("stream ist Offline");
                }
            }

            if (msg.startsWith("!hug")) {
                String person = msg.substring(5);

                nachicht("@" + event.getUser().getName() + " umarmt " + "@" + person);

            }

            if (msg.startsWith("!slap")) {

                String person = msg.substring(6);

                nachicht("@" + event.getUser().getName() + " slapt " + "@" + person);
            }

            if (msg.equals("!alleZitate")) {

                Properties zitatepropeties = new Properties();

                try (FileInputStream in = new FileInputStream("config.propertiesZitate")) {
                    zitatepropeties.load(in);

                    for (Object value : zitatepropeties.values()) {
                        nachicht((String) value);
                    }

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            }

            if (msg.equals("!commands")) {
                nachicht("Folgende Commands sind möglich: !lurk , !unlurk, " +
                        "!clip, !lost, !followage, !magische miesmuschel, !add, !delete, !hug, !slap, !uptime, !podcast, !donation, !musik, !fc, !code, !codeChange ,!socials, !yt, !dc, " +
                        "!musik, !ping , !alleZitate" + "Mod commands: !newGame + cathecorie name, !titel + titeltext");
            }
        });
    }

    public void nachicht(String text) { // kein return ist void
        chatbot.twitchClient.getChat().sendMessage(chatbot.channelName, text);
    }

    public void ausgebenVonConfig() throws IOException {

        FileInputStream in = new FileInputStream("config.properties");
        chatbot.config.load(in);
        in.close();
    }

    public void speichernConfig() throws IOException {

        OutputStream output = new FileOutputStream("config.properties"); // ausgeben
        chatbot.config.store(output, null); //schreiben in die dartei
        output.close();
    }


}
