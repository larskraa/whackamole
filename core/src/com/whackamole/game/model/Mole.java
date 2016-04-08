package com.whackamole.game.model;

/**
 * Created by AnneSofie on 04.04.2016.
 */

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;
<<<<<<< HEAD
import com.badlogic.gdx.math.Vector2;

public class Mole {
=======
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Vector2;

public class Mole {


>>>>>>> master


    /**
     *  The view:
     *  Moles should be rendered on the screen when a signal is given
     *
     *  The controller:
     *  Should maybe accept requests from the server and put a mole in the GameState?
     *
     *  The model:
     *  Should now contain a Mole on a specified location and notify the view that
     *  a mole should be rendered at that location.
     *  Some people say that the model should not be aware of the view, but other people
     *  say that it is perfectly fine to use the Observer-pattern between the model and the view. Let's check this.
     *
     *
     */


<<<<<<< HEAD
    public enum Theme {
        Kardashian, President
    }

=======
>>>>>>> master

    private Theme theme;
    private Texture moleImage;
    private Sound moleSound;  //A different sound when user hits different types of moles
<<<<<<< HEAD
    private Vector2 position = new Vector2();
    private Boolean hit;


    public Mole(Vector2 pos, Theme th) {

        position = pos;
=======
    private Vector2 position, velocity;
    private int height = Gdx.graphics.getHeight(), width = Gdx.graphics.getWidth(), location;
    private float dt = 0, hiddenposition;
    private float hidespeed = 1000.0f;


    public Mole(Vector2 pos, Theme th, int location) {

        this.position = pos;
        this.location = location;
        hiddenposition = this.position.y - height*33/160;
>>>>>>> master
        theme = th;
        if (theme == Theme.KARDASHIAN) {
            moleImage = new Texture(Gdx.files.internal("trump.png"));

<<<<<<< HEAD
        if (theme == Theme.Kardashian) {
            moleImage = new Texture(Gdx.files.internal("kardashian.png"));
            moleSound = Gdx.audio.newSound(Gdx.files.internal("kar.mp3"));
        } else if (theme == Theme.President) {
            moleImage = new Texture(Gdx.files.internal("DonaldTrump.png"));
            moleSound = Gdx.audio.newSound(Gdx.files.internal("trump.mp3"));
=======
            //moleSound = Gdx.audio.newSound(Gdx.files.internal("kar.mp3"));
        } else if (theme == Theme.PRESIDENTIAL) {
            moleImage = new Texture(Gdx.files.internal("trump.png"));
            //moleSound = Gdx.audio.newSound(Gdx.files.internal("trump.mp3"));
>>>>>>> master
        }
        hit = false;
    }

<<<<<<< HEAD
=======
    public void setPos(float x, float y) {
        position.set(x,y);
    }
>>>>>>> master

    public Texture getMoleImage(){
        return moleImage;
    }
    public void setMoleImg(Texture img) {
        this.moleImage=img;
    }
    public Sound getMoleSound(){
        return moleSound;
    }
    public void setMoleSound(Sound msc) {
        this.moleSound = msc;
    }
<<<<<<< HEAD
    public void hitMole() {
        this.hit = true;
=======

    public Vector2 getPosition(){
        return position;
    }

    public int getLocation(){
        return location;
    }

    public void hide(float dt){
        if(this.position.y > hiddenposition) {
            this.position.set(this.position.x, this.position.y - (height/100)*dt);
        }
    }

    public void show(){

>>>>>>> master
    }

}