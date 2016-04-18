package com.whackamole.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.whackamole.game.WhackAMole;
import com.whackamole.game.model.*;
import com.whackamole.game.utils.Prefs;
import com.whackamole.game.views.BoardRenderer;
import com.whackamole.game.controller.BoardController;

/**
 * Created by AnneSofie on 04.04.2016.
 */
public class GameScreen implements Screen, InputProcessor{


    // Screens inneholder først og fremst disse: Game, Model, View og Kontroller
    final WhackAMole game;
    private Board board;
    private BoardRenderer boardRenderer;
    private BoardController controller;
    private Preferences prefs;

    // Helt greit at denne starter musikken
    private Music backgroundmusic;


    /**

        GameScreen kan sees på som en del av kontrollerne.
        Den håndterer input fra bruker, initialiserer modellen og viewene. I dette tilfelle Board og BoardRenderer.
        Den delegerer også oppgaver videre til andre kontrollere. I dette tilfelle BoardController først og fremst.
        Ikke så alt for mye bør skje her. Den bør delegere mesteparten av arbeidet videre til andre klasser.

     **/

    public GameScreen(final WhackAMole game) {

        // game kan nå brukes til å endre screens, f.eks. game.goToMainMenuScreen();
        this.game = game;

        // Preferences
        this.prefs = Gdx.app.getPreferences(Prefs.PREFS.key());

        // Initialiserer brettet basert på theme, num of moles osv. som alltid er definert i Preferences
        this.board = new Board();

        // Gir boardRenderer modellen å jobbe med
        this.boardRenderer = new BoardRenderer(board, game.getMatch());

        // Gir kontrolleren modellen å jobbe med. Legg merke til at kun kontroller
        controller = new BoardController(board, game.getMatch());

        // TEMORARILY MOVED HERE FOR PERFORMANCE
        loadBoard();
    }


    @Override
    public void show() {
        // Setter denne til å lytte på input fra brukeren
        Gdx.input.setInputProcessor(this);

        // Load controller
        controller.loadController();

        boardRenderer.loadRenderer();

        // Starter musikken
        loadSoundtracks();
    }


    @Override
    public void render(float delta) {
        controller.update(delta);
        boardRenderer.render();
    }


    public void loadBoard() {
        // Load board
        board.loadBoard();

        // Load renderer
        boardRenderer.loadRenderer();
    }


    public void loadView() {
         boardRenderer.loadRenderer();
    }



    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        int xhit = screenX;
        int yhit = Gdx.graphics.getHeight() - screenY;

        return this.controller.touchDown(xhit, yhit, pointer, button);
    }


    public void loadSoundtracks() {
        boolean isSound = prefs.getBoolean(Prefs.ISSOUND.key());
        if(isSound) {
            Theme theme = Theme.getThemeOnThemeId(prefs.getInteger(Prefs.THEME.key()));
            backgroundmusic = Gdx.audio.newMusic(Gdx.files.internal(theme.path() + FileName.BACKGROUNDMUSIC.filename()));
            backgroundmusic.setLooping(true);
            backgroundmusic.setVolume(0.5f);
            backgroundmusic.play();
        }
    }


    // THE REST OF SCREEN METHODS

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }



    // REST OF THE INPUTPROCESSOR METHODS

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }



}
