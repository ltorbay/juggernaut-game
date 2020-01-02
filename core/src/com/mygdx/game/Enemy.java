package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.HashMap;
import java.util.Map;

public class Enemy extends Actor {

	private static final Map<String, Texture> texturesCache = new HashMap<>();

	private int scroll;

	private final ShapeRenderer shapeRenderer;

	private final Texture texture;
	private final Rectangle hitbox;
	private final int verticalPosition;
	private final float textureXOffset;
	private final float textureYOffset;
	private final float speed;

	public Enemy(final String texturePath, final float speed, final int verticalPosition) {
		if (!texturesCache.containsKey(texturePath)) {
			texturesCache.put(texturePath, new Texture(Gdx.files.internal(texturePath)));
		}
		texture = texturesCache.get(texturePath);
		scroll = 1400;
		this.speed = speed;
		this.verticalPosition = verticalPosition + (texture.getHeight() / 2);
		this.shapeRenderer = new ShapeRenderer();
		this.textureXOffset = (float) texture.getWidth() / 2;
		this.textureYOffset = (float) texture.getHeight() / 2;
		this.hitbox = new Rectangle(scroll  - textureXOffset,
				this.verticalPosition,
				20,
				texture.getHeight());
	}

	@Override
	public void draw(final Batch batch, final float parentAlpha) {
		scroll -= (int) speed;
		hitbox.setX(scroll);

		batch.draw(texture,
				scroll - textureXOffset,
				verticalPosition,
				texture.getWidth(),
				texture.getHeight());
	}

	public Rectangle getHitbox() {
		return hitbox;
	}
}
