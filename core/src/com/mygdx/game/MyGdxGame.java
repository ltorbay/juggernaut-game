package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class MyGdxGame extends ApplicationAdapter {

	private static final int SCREEN_WIDTH = 1366;
	private static final int SCREEN_HEIGHT = 768;
	private static final float JUGGERNAUT_SCALE = 0.5f;

	// TODO get this default from the sprite height (method)
	public static final int DEFAULT_VERTICAL_POSITION = (int) ((-SCREEN_HEIGHT / 2. + 100) * JUGGERNAUT_SCALE);

	private static final int RUNNER_HORIZONTAL_POSITION = (int) (-200 * JUGGERNAUT_SCALE);

	// Instantiated items
	Rectangle hitbox;
	Juggernaut juggernaut;
	ParallaxBackground parallaxBackground;
	final List<Enemy> enemies = new ArrayList<>();

	// Rendering tools
	SpriteBatch spriteBatch;
	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	private Stage stage;

	// Status variables
	float stateTime;
	float totalTime;
	float lastSpawnTime = 0;
	int verticalPosition;
	boolean jumping = false;


	@Override
	public void create() {

		stateTime = 0f;
		verticalPosition = DEFAULT_VERTICAL_POSITION;

		shapeRenderer = new ShapeRenderer();
		spriteBatch = new SpriteBatch();

		stage = new Stage();
		camera = (OrthographicCamera) stage.getCamera();
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);


		juggernaut = new Juggernaut(JUGGERNAUT_SCALE);
		hitbox = new Rectangle(juggernaut.getHitboxCoordinates(RUNNER_HORIZONTAL_POSITION),
				juggernaut.getHitboxCoordinates(verticalPosition),
				juggernaut.getHitboxSize(),
				juggernaut.getHitboxSize());

		parallaxBackground = new ParallaxBackground(getTextures(4, "parallax/desert/layer"));
		parallaxBackground.setBackgrounds(getTextures(3, "parallax/desert/bg"));
		parallaxBackground.setAsyncLayers(getTextures(0, "parallax/desert/async"));
		parallaxBackground.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		parallaxBackground.setSpeed(1f);
		stage.addActor(parallaxBackground);
	}

	private List<Texture> getTextures(final int i2, final String s) {
		List<Texture> backgrounds = new ArrayList<>();
		for (int i = i2; i >= 0; i--) {
			backgrounds.add(new Texture(Gdx.files.internal(s + i + ".png")));
			backgrounds.get(backgrounds.size() - 1).setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
		}
		return backgrounds;
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); // Clear screen
		totalTime += Gdx.graphics.getDeltaTime();
		stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time

		/*
		User inputs
		 */
		if (Gdx.input.isKeyPressed(Input.Keys.UP) && !jumping) {
			jumping = true;
			stateTime = 0f;
		}
		if (totalTime > (lastSpawnTime + 3)) {
			lastSpawnTime = totalTime;
//			final var newEnemy = new Enemy("enemies/cactus2.png", 1f * 16, DEFAULT_VERTICAL_POSITION + 100);
			final var newEnemy = new Enemy("enemies/cactus1.png", 1f * 16, DEFAULT_VERTICAL_POSITION);
			enemies.add(newEnemy);
			stage.addActor(newEnemy);
		}

		stage.act();
		stage.draw();

		// Update camera once per frame
		camera.update();

		if (jumping) {
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
		final Optional<Rectangle> enemyHitboxOptional = enemies.stream().findFirst().map(Enemy::getHitbox);
		enemyHitboxOptional.ifPresent(enemyHitbox -> {

			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.setColor(Color.RED);
			shapeRenderer.rect(enemyHitbox.x, enemyHitbox.y, enemyHitbox.width, enemyHitbox.height);
//		shapeRenderer.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
			shapeRenderer.end();
		});

		spriteBatch.setProjectionMatrix(camera.combined);
		spriteBatch.begin();
		spriteBatch.draw(currentFrame,
				RUNNER_HORIZONTAL_POSITION,
				verticalPosition,
				juggernaut.getWidth(),
				juggernaut.getHeight());

		spriteBatch.end();
		enemies.stream()
				.filter(enemy -> !enemy.isVisible())
				.forEach(enemy -> stage.getActors().removeValue(enemy, true));
		enemies.removeIf(enemy -> !enemy.isVisible());
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		juggernaut.dispose();
	}
}
