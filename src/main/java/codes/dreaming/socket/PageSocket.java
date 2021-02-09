package codes.dreaming.socket;

import codes.dreaming.classes.Game;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.vertx.http.runtime.devmode.Json;
import net.bytebuddy.utility.RandomString;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

@ServerEndpoint("/pagesocket")
@ApplicationScoped
public class PageSocket {
    Session currentSession = null;

    @OnOpen
    public void onOpen(Session session) throws IOException {
        System.out.println("Tentativo di connessione");
        //Se nessuno è connesso attualmente
        if (currentSession == null){
            System.out.println("Iniziata nuova sessione");
            currentSession = session;
            SecureRandom random = new SecureRandom();
            long longToken = Math.abs( random.nextLong() );
            String token = Long.toString( longToken, 16 );

            ObjectMapper mapper = new ObjectMapper();
            ObjectNode data = mapper.createObjectNode();
            data.put("operation", "presentator");
            data.put("arg", token);
            System.out.println(data);
            session.getAsyncRemote().sendText(data.toString());
        } else { //Altrimenti riporta errore perché in uso
            System.out.println("Connessione rifiutata");
            session.getAsyncRemote().sendObject("inUse");
            session.close(new CloseReason(CloseReason.CloseCodes.RESERVED, "Già in uso"));
        }
    }

    @OnClose
    public void onClose() { //Quando viene chiusa la connessione rimette la sessione su null
        currentSession = null;
        System.out.println("Connessione chiusa");
    }

    @OnError
    public void onError(Session session, Throwable throwable) throws IOException { //Quando c'è un errore chiude la sessione e imposta la sessione corrente su null
        session.close(new CloseReason(CloseReason.CloseCodes.PROTOCOL_ERROR, "Già in uso"));
        currentSession = null;
        //Loggo l'errore
        System.out.println("Errore: " + throwable);
    }

    @OnMessage
    public void onMessage(String message) { //Scrivo in console il messaggio
        System.out.println(message);
    }

    //region StepManager

    public void nextStep() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();
        data.put("operation", "step");
        data.put("arg", "next");
        System.out.println(data);
        currentSession.getAsyncRemote().sendText(data.toString());
    }
    public void previousStep() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();
        data.put("operation", "step");
        data.put("arg", "back");
        System.out.println(data);
        currentSession.getAsyncRemote().sendText(data.toString());
    }
    //endregion
    public void startGame(Game game) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();
        data.put("operation", "game");


        switch (game){
            case Dice:
                data.put("arg", "Dice");
                System.out.println("Dice");
                break;
            case Draw:
                data.put("arg", "Draw");
                System.out.println("Draw");
                break;
            default:
                return;
        }
        System.out.println(data);
        currentSession.getAsyncRemote().sendText(data.toString());
    }

}