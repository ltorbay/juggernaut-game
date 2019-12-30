package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Juggernaut implements Disposable {
	/*
	Dev fields
	 */
	private static final int SPEED_MULTIPLIER = 1;

	/*
	Private fields
	 */
	private static final int RUN_SHEET_ROWS = 4;
	private static final int RUN_SHEET_COLUMNS = 4;
	private static final int JUMP_SHEET_ROWS = 2;
	private static final int JUMP_SHEET_COLUMNS = 5;
	private static final int HITBOX = 290;

	private Texture runSheet;
	private Texture jumpSheet;
	private TextureRegion jumpAirTexture;
	private TextureRegion jumpFallTexture;
	private TextureRegion jumpStopTexture;

	private final float scale;
	final float totalJumpFrames;


	/*
	Externalized fields
	 */
	Animation<TextureRegion> runAnimation;
	public final int runFrameHeight;
	public final int runFrameWidth;

	Animation<TextureRegion> jumpAnimation;
	public final int jumpFrameHeight;
	public final int jumpFrameWidth;

	public Juggernaut(final float scale) {
		this.scale = scale;
		
		// Building run animation...
		runSheet = new Texture(Gdx.files.internal("run.png"));
		runFrameHeight = runSheet.getHeight() / RUN_SHEET_ROWS;
		runFrameWidth = runSheet.getWidth() / RUN_SHEET_COLUMNS;
		runAnimation = new Animation<>(0.04f * SPEED_MULTIPLIER,
				getTextureRegions(runSheet, RUN_SHEET_COLUMNS, RUN_SHEET_ROWS).toArray(new TextureRegion[0]));

		// Building jump animation...
		jumpSheet = new Texture(Gdx.files.internal("jump_start.png"));
		jumpFrameHeight = jumpSheet.getHeight() / JUMP_SHEET_ROWS;
		jumpFrameWidth = jumpSheet.getWidth() / JUMP_SHEET_COLUMNS;
		final List<TextureRegion> jumpTextures = getTextureRegions(jumpSheet, JUMP_SHEET_COLUMNS, JUMP_SHEET_ROWS);
		jumpAirTexture = new TextureRegion(new Texture(Gdx.files.internal("jump_on_air.png")));
		jumpTextures.add(jumpAirTexture);

		jumpFallTexture = new TextureRegion(new Texture(Gdx.files.internal("jump_fall.png")));
		jumpTextures.add(jumpFallTexture);

		jumpStopTexture = new TextureRegion(new Texture(Gdx.files.internal("jump_stop.png")));
		jumpTextures.add(jumpStopTexture);

		jumpAnimation = new Animation<>(0.04f * SPEED_MULTIPLIER, jumpTextures.toArray(new TextureRegion[0]));
		totalJumpFrames = jumpAnimation.getAnimationDuration() / jumpAnimation.getFrameDuration();
	}

	private List<TextureRegion> getTextureRegions(final Texture sheet, final int columns, final int rows) {
		TextureRegion[][] textureRegions = TextureRegion.split(sheet,
				sheet.getWidth() / columns,
				sheet.getHeight() / rows);
		final List<TextureRegion> regions = new ArrayList<>();
		for (int i = 0; i < rows; i++) {
			regions.addAll(Arrays.asList(textureRegions[i]).subList(0, columns));
		}
		return regions;
	}

	public int getJumpHeight(final float statetime) {
		final float elapsedFrames = statetime / jumpAnimation.getFrameDuration();
		final float ratio = Math.max(0, (elapsedFrames - 1) / (totalJumpFrames - 2));

		final float initialSpeed = 1200;
		return (int) Math.max(0, (((-initialSpeed * ratio) + initialSpeed) * ratio)); // -st² + st -> Movement equations
	}

	public int getHitboxCoordinates(final int x) {
		return (int) (x + (((runFrameWidth - HITBOX + 32) * scale) / 2));
	}

	Animation<TextureRegion> getAnimation(final boolean jumping) {
		return jumping ? jumpAnimation : runAnimation;
	}

	public float getJumpingTime() {
		return jumpAnimation.getAnimationDuration();
	}

	public void dispose() {
		runSheet.dispose();
		jumpSheet.dispose();
		jumpAirTexture.getTexture().dispose();
		jumpFallTexture.getTexture().dispose();
		jumpStopTexture.getTexture().dispose();
	}
	
	public float getWidth() {
		return runFrameWidth * scale;
	}
	
	public float getHeight() {
		return runFrameHeight * scale;
	}
	
	public int getHitboxSize() {
		return (int) (HITBOX * scale);
	}
}
