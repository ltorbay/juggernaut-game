package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;


public class MyGdxGame extends ApplicationAdapter {

	private static final int SCREEN_WIDTH = 1366;
	private static final int SCREEN_HEIGHT = 768;
	public static final int DEFAULT_VERTICAL_POSITION = -SCREEN_HEIGHT / 2 + 100;

	private static final int RUNNER_HORIZONTAL_POSITION = -200;

	// Instantiated items
	Rectangle hitbox;
	Juggernaut juggernaut;

	// Rendering tools
	SpriteBatch spriteBatch;
	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;

	// Status variables
	float stateTime;
	int verticalPosition;
	boolean jumping = false;


	@Override
	public void create() {

		stateTime = 0f;
		verticalPosition = DEFAULT_VERTICAL_POSITION;

		shapeRenderer = new ShapeRenderer();
		spriteBatch = new SpriteBatch();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

		juggernaut = new Juggernaut();
		hitbox = new Rectangle(juggernaut.getHitboxCoordinates(RUNNER_HORIZONTAL_POSITION),
				juggernaut.getHitboxCoordinates(verticalPosition),
				Juggernaut.HITBOX,
				Juggernaut.HITBOX);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen
		stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time

		// Update camera once per frame
		camera.update();

		// User inputs
		if (Gdx.input.isKeyPressed(Input.Keys.UP) && !jumping) {
			jumping = true;
			stateTime = 0f;
		}
		
		if(jumping) {
			verticalPosition = DEFAULT_VERTICAL_POSITION + juggernaut.getJumpHeight(stateTime);
		}

		// Get current frame of animation for the current stateTime
		if (jumping && stateTime >= juggernaut.getJumpingTime()) {
			jumping = false;
			stateTime = 0f + juggernaut.getAnimation(false).getFrameDuration() * 13;
			verticalPosition = DEFAULT_VERTICAL_POSITION;
		}
		final Animation<TextureRegion> animation = juggernaut.getAnimation(jumping);
		TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);

		// Colored hitbox...
		hitbox.y = juggernaut.getHitboxCoordinates(verticalPosition);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
		shapeRenderer.end();

		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		spriteBatch.draw(currentFrame, RUNNER_HORIZONTAL_POSITION, verticalPosition);

		spriteBatch.end();
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		juggernaut.dispose();
	}
}
