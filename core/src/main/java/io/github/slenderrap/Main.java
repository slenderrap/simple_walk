package io.github.slenderrap;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Logger;

import java.util.ArrayList;


/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Texture imageMoviment, imatgeFons;
    private float stateTime, speed;
    private float backgroundX, backgroundY;
    private ArrayList<Animation<TextureRegion>> animations;
    private int currentAnimationIndex;

    @Override
    public void create() {
        batch = new SpriteBatch();
        imageMoviment = new Texture("spritesheet_jugador.png");
        imatgeFons = new Texture("background.png");
        imatgeFons.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);

        backgroundX = 0;
        backgroundY = 0;

        speed = 200;



        animations = new ArrayList<>();
        int frameCols = 4;
        int frameRows = 4;
        TextureRegion[][] tmp = TextureRegion.split(imageMoviment,
            imageMoviment.getWidth() / frameCols,
            imageMoviment.getHeight() / frameRows);

        for (int i = 0; i < frameRows; i++) {
            TextureRegion[] rowFrames = new TextureRegion[frameCols];
            System.arraycopy(tmp[i], 0, rowFrames, 0, frameCols);
            animations.add(new Animation<>(0.25f, rowFrames));
        }
        animations.add(new Animation<>(0.25f, tmp[0][0]));
        stateTime = 0;
        currentAnimationIndex = 4;
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float delta = Gdx.graphics.getDeltaTime();
        stateTime += delta;

        if (Gdx.input.isTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.graphics.getHeight() - Gdx.input.getY();

            float diffX = touchX - (Gdx.graphics.getWidth() / 2f);
            float diffY = touchY - (Gdx.graphics.getHeight() / 2f);

            float distance = (float) Math.sqrt(diffX * diffX + diffY * diffY);
            if (distance > 1) {
                float dirX = diffX / distance;
                float dirY = diffY / distance;

                backgroundX -= dirX * speed *2* delta;
                backgroundY -= dirY * speed *2* delta;

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    currentAnimationIndex = (dirX > 0) ? 2 : 1;
                } else {
                    currentAnimationIndex = (dirY > 0) ? 3 : 0;
                }
            }
        }else {
            currentAnimationIndex=4;
        }

        batch.begin();

        float bgWidth = imatgeFons.getWidth();
        float bgHeight = imatgeFons.getHeight();

        for (float x = backgroundX % bgWidth - bgWidth; x < Gdx.graphics.getWidth(); x += bgWidth) {
            for (float y = backgroundY % bgHeight - bgHeight; y < Gdx.graphics.getHeight(); y += bgHeight) {
                batch.draw(imatgeFons, x, y);
            }
        }

        TextureRegion frame = animations.get(currentAnimationIndex).getKeyFrame(stateTime, true);
        batch.draw(frame, Gdx.graphics.getWidth() / 2f - (frame.getRegionWidth()  * 2.5f),
            Gdx.graphics.getHeight() / 2f - (frame.getRegionHeight() * 2.5f),
            frame.getRegionWidth() * 5, frame.getRegionHeight() * 5);

        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        imageMoviment.dispose();
        imatgeFons.dispose();
    }
}
