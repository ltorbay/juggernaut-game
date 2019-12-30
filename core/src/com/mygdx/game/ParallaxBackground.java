package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.List;

public class ParallaxBackground extends Actor {

	private static final int LAYER_SPEED_DIFFERENCE = 4;

	private float scroll;
	private List<Texture> layers;
	private List<Texture> backgrounds;
	private List<Texture> asyncLayers;

	float heigth;
	int srcY;
	boolean flipX;
	boolean flipY;

	private float speed;

	public ParallaxBackground(List<Texture> textures) {
		layers = textures;
		for (int i = 0; i < textures.size(); i++) {
			layers.get(i).setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
		}
		scroll = 0;
		speed = 0;

		setX(0);
		setY(0);
		setOriginX(0);
		setOriginY(0);
		setRotation(0);
		srcY = 0;
		setWidth(Gdx.graphics.getWidth());
		heigth = Gdx.graphics.getHeight();
		setScaleX(1);
		setScaleY(1);
		flipX = flipY = false;
	}

	public void setBackgrounds(List<Texture> textures) {
		backgrounds = textures;
	}

	public void setAsyncLayers(List<Texture> textures) {
		asyncLayers = textures;
	}


	public void setSpeed(float newSpeed) {
		this.speed = newSpeed;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);

		scroll += speed;

		if (backgrounds != null) {
			backgrounds.forEach(background -> drawLayer(batch, background, 0));
		}

		if (asyncLayers != null) {
			asyncLayers.forEach(background -> drawLayer(batch, background, (int) -scroll/10));
		}

		for (int i = 0; i < layers.size(); i++) {
			drawLayer(batch, layers.get(i), (int) (scroll + i * LAYER_SPEED_DIFFERENCE * scroll));
		}
	}

	private void drawLayer(final Batch batch, final Texture layer, final int srcX) {
		batch.draw(layer, getX(), getY(), getOriginX(), getOriginY(), getWidth(), heigth, getScaleX(), getScaleY(), getRotation(), srcX, srcY, layer.getWidth(), layer.getHeight(), flipX, flipY);
	}
}
