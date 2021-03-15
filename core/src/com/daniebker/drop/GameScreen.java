package com.daniebker.drop;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.daniebker.drop.Drop;

public class GameScreen implements Screen {
    final Drop game;

    Texture dropImage;
    Texture bucketImage;
    Sound dropSound;
    Music rainMusic;
    OrthographicCamera camera;
    Rectangle bucket;
    Array<RainDrop> raindrops;
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

        // create a Rectangle to logically represent the bucket
        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2; // center the bucket horizontally
        bucket.y = 20; // bottom left corner of the bucket is 20 pixels above
        // the bottom screen edge
        bucket.width = 64;
        bucket.height = 64;

        // create the raindrops array and spawn the first raindrop
        raindrops = new Array<RainDrop>();
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
        update();
        draw();
        processInput(delta);
        updateGame();
        processPhysics(delta);
    }

    private void updateGame() {
        // check if we need to create a new raindrop
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) {
            spawnRaindrop();
        }
    }

    private void processPhysics(float delta) {
        // move the raindrops, remove any that are beneath the bottom edge of
        // the screen or that hit the bucket. In the later case we increase the
        // value our drops counter and add a sound effect.
        Iterator<RainDrop> iter = raindrops.iterator();
        while (iter.hasNext()) {
            RainDrop raindrop = iter.next();
            raindrop.update(delta);
            if (raindrop.position.y + 64 < 0) {
                iter.remove();
                rainDropPool.free(raindrop);
            }
            if (raindrop.boundingBox.overlaps(bucket)) {
                dropsGathered++;
                dropSound.play();
                iter.remove();
                rainDropPool.free((raindrop));
            }
        }
    }

    private void processInput(float delta) {
        // process user input
        if (Gdx.input.isTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64 / 2;
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT))
            bucket.x -= 200 * delta;
        if (Gdx.input.isKeyPressed(Keys.RIGHT))
            bucket.x += 200 * delta;

        // make sure the bucket stays within the screen bounds
        if (bucket.x < 0)
            bucket.x = 0;
        if (bucket.x > 800 - 64)
            bucket.x = 800 - 64;
    }

    private void draw() {
        // begin a new batch and draw the bucket and
        // all drops
        game.batch.begin();
        game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 480);
        game.batch.draw(bucketImage, bucket.x, bucket.y, bucket.width, bucket.height);
        for (RainDrop raindrop : raindrops) {
            game.batch.draw(dropImage, raindrop.position.x, raindrop.position.y);
        }
        game.batch.end();
    }

    private void update() {
        // clear the screen with a dark blue color. The
        // arguments to clear are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        ScreenUtils.clear(0, 0, 0.2f, 1);

        // tell the camera to update its matrices.
        camera.update();

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
