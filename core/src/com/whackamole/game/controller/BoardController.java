package com.whackamole.game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.whackamole.game.model.*;
import com.whackamole.game.utils.Prefs;
import com.whackamole.game.utils.SocketRetreiver;
import com.whackamole.game.views.Assets;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONException;
import org.json.JSONObject;


public class BoardController implements Disposable {


    private int touch_x, touch_y, counter = 4;
    private Board board;
    private Sound hitsound;
    private Sound speech;
    private Socket socket;
    private String gameName;
    private String nickName;
    private Mole mole;
    private Match match;
    private Preferences prefs;
    private ScreenController screenController;
    private boolean isSound;
    private boolean gameFinished;

    public BoardController(Board board, ScreenController screenController) {

        this.board = board;
        this.match = Match.getCurrentMatch();
        this.prefs = Gdx.app.getPreferences(Prefs.PREFS.key());
        this.screenController = screenController;
        this.gameFinished = false;

    }

    public void loadController() {

        isSound = this.prefs.getBoolean(Prefs.ISSOUND.key());

        SocketRetreiver retreiver = SocketRetreiver.getInstance();
        socket = retreiver.getSocket();

        System.out.println("In BoardController loadController(). Socket is connected: " + socket.connected());

        String themePath = Theme.getThemeOnThemeId(prefs.getInteger(Prefs.THEME.key())).path();

        this.gameName = match.getGameName();
        this.nickName = match.getThisPlayerNickName();

        this.hitsound = Gdx.audio.newSound(Gdx.files.internal(Assets.HITSOUND));
        this.speech = Gdx.audio.newSound(Gdx.files.internal(themePath + Assets.SPEECH));

        SocketRetreiver socketRetreiver = SocketRetreiver.getInstance();
        socket = socketRetreiver.getSocket();


        socket.on(Socket.EVENT_DISCONNECT, disconnected);
        socket.on(Socket.EVENT_RECONNECT_ATTEMPT, onReconnecting);
        socket.on(Socket.EVENT_RECONNECT, onReconnected);
        socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.on(Socket.EVENT_CONNECT, onConnect);
        socket.on("player hit", playerHit);
        socket.on("new mole", onNewMole);
        socket.on("game finished", onGameFinished);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("Connected during the game.");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("Connection error during the game.");
        }
    };

    private Emitter.Listener onReconnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("Reconnected during the game.");
        }
    };

    private Emitter.Listener onReconnecting = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            System.out.println("Attempting to reconnect during the game.");
        }
    };

    private Emitter.Listener disconnected = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if(!gameFinished) {
                System.out.println("Disconnected and was redirected to MainMenuScreen from ReadyController disconnected().");
                screenController.goToMainMenuScreen();
                match.setIsOnGoingMatch(false);
            }
        }
    };

    private Emitter.Listener onGameFinished = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JsonValue attendersJson = new JsonReader().parse((String) args[0]);

            // Update the scoreboard just to make sure all scores were correct and updated
            for(JsonValue attender : attendersJson.iterator()) {
                match.setScoreToUser(attender.getString("nickName"), attender.getInt("points"));
            }

            gameFinished = true;
            screenController.goToGameOverScreen();
            socket.emit("left game", "");
        }
    };


    private Emitter.Listener playerHit = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject obj = (JSONObject) args[0];
            try {
                String nickName = obj.getString("nickName");
                int points = obj.getInt("points");
                int totalScore = obj.getInt("totalScore");
                if(match.getThisPlayerNickName().equals(nickName)) {
                    System.out.print("You hit the last mole for " + points + " points!");
                    board.setHitTheLastMole(true, points);
                    match.setScoreToUser(nickName, totalScore);
                }
                else {
                    Mole mole = board.getCurrentMole();
                    if(mole != null) {
                        mole.reset();
                    }
                    board.setHitTheLastMole(false, 0);
                    match.setScoreToUser(nickName, totalScore);
                }
                board.setNotFirst();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    // NEW MOLES FROM SERVER ARRIVES HERE
    private Emitter.Listener onNewMole = new Emitter.Listener(){
        @Override
        public void call(Object... args) {
            System.out.println("New mole.");
            Mole mole = board.getCurrentMole();
            if(mole != null) {
                mole.reset();
            }
            JSONObject obj = (JSONObject) args[0];
            try {
                moleFromSocket(obj.getInt("pos"), obj.getInt("pic"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touch_x = screenX;
        touch_y = screenY;
        mole = board.getCurrentMole();
        if(mole != null && mole.getBoundingRectangle().contains(touch_x, touch_y)){
            if(isSound) {
                hitsound.play(1);
            }
            mole.reset();
            JSONObject json = new JSONObject();
            try {
                json.put("gameName", gameName);
                json.put("nickName", nickName);
                json.put("mole", mole.getMoleImageId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            socket.emit("mole hit", json);
        }
        return true;
    }

    public void moleFromSocket(int mole, int img){
        board.setMole(mole, img);
        if(this.prefs.getBoolean(Prefs.ISSOUND.key()) && img == 0){
            if(counter == 4){
                speech.play();
                counter = 0;
            } else counter++;
        }
    }

    @Override
    public void dispose() {
        this.hitsound.dispose();
        this.speech.dispose();

        socket.off(Socket.EVENT_DISCONNECT, disconnected);
        socket.off(Socket.EVENT_RECONNECT_ATTEMPT, onReconnecting);
        socket.off(Socket.EVENT_RECONNECT, onReconnected);
        socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        socket.off(Socket.EVENT_CONNECT, onConnect);
        socket.off("player hit", playerHit);
        socket.off("new mole", onNewMole);
        socket.off("game finished", onGameFinished);
    }

    public boolean touchUp(int screenX, int screenY, int pointer, int button) {return false;}
    public boolean keyDown(int keycode) {return false;}
    public boolean keyUp(int keycode) {return false;}
    public boolean keyTyped(char character) {return false;}
    public boolean touchDragged(int screenX, int screenY, int pointer) {return false;}
    public boolean mouseMoved(int screenX, int screenY) {return false;}
    public boolean scrolled(int amount) {return false;}
}
