package com.daniebker.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
    final Drop game;

    Texture dropImage;
    Texture bucketImage;
    Sound dropSound;
    Music rainMusic;
    OrthographicCamera camera;
    Array<RainDrop> raindrops;
    Player player;
    long lastDropTime;
    int dropsGathered;

    private final Pool<RainDrop> rainDropPool = new Pool<RainDrop>() {
        @Override
        protected RainDrop newObject() {
            return new RainDrop();
        }
    };

    public GameScreen(final Drop game) {
        this.game = game;

        // load the images for the droplet and the bucket, 64x64 pixels each
        dropImage = new Texture(Gdx.files.internal("droplet.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));

        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        player = new Player();
        player.init();

        raindrops = new Array<>();
        spawnRaindrop();

    }

    private void spawnRaindrop() {
        RainDrop item = rainDropPool.obtain();
        item.init();
        raindrops.add(item);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw();
        processInput(delta);
        updateGame();
        processPhysics();
    }

    private void updateGame() {
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) {
            spawnRaindrop();
        }
    }

    private void processPhysics() {
        // move the raindrops, remove any that are beneath the bottom edge of
        // the screen or that hit the bucket. In the later case we increase the
        // value our drops counter and add a sound effect.
        Iterator<RainDrop> iter = raindrops.iterator();
        while (iter.hasNext()) {
            RainDrop raindrop = iter.next();
            if (raindrop.position.y + 64 < 0) {
                iter.remove();
                rainDropPool.free(raindrop);
            }
            if (raindrop.boundingBox.overlaps(player.getBoundingBox())) {
                dropsGathered++;
                dropSound.play();
                iter.remove();
                rainDropPool.free((raindrop));
            }
        }
    }

    private void processInput(float delta) {
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            player.setPosition(touchPos.x, player.getPosition().y);
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            player.setPosition(player.getPosition().x -= 200 * delta, player.getPosition().y);
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)){
            player.setPosition(player.getPosition().x += 200 * delta, player.getPosition().y);
        }
    }

    private void draw() {
        game.batch.begin();
        game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 480);
        game.batch.draw(bucketImage, player.getPosition().x, player.getPosition().y, player.getWidth(), player.getHeight());
        for (RainDrop raindrop : raindrops) {
            game.batch.draw(dropImage, raindrop.position.x, raindrop.position.y);
        }
        game.batch.end();
    }

    private void update(float delta) {
        // clear the screen with a dark blue color. The
        // arguments to clear are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        ScreenUtils.clear(0, 0, 0.2f, 1);

        // tell the camera to update its matrices.
        camera.update();
        player.update(delta);
        Iterator<RainDrop> iter = raindrops.iterator();
        while (iter.hasNext()) {
            RainDrop raindrop = iter.next();
            raindrop.update(delta);
        }
        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
        rainMusic.play();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }

}
