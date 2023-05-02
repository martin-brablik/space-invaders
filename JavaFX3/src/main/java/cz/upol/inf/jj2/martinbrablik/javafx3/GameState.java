package cz.upol.inf.jj2.martinbrablik.javafx3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class GameState {

	public static final int ALIEN_WIDTH = 4;
	public static final int BARRIER_WIDTH = 4;
	public static final int ALIEN_COLS = 9;
	public static final int ALIEN_ROWS = 7;;
	public static final int COLUMNS = 11 * ALIEN_WIDTH;
	public static final int ROWS = 24;
	public static final int PLAYER_WIDTH = 4;
	public static final int PLAYER_ROW = 24;
	private static final int MAX_HP = 3;
	public static final int BARRIER_COUNT = 3;
	public static final int BARRIER_ROW = 20;
	
	private final IntegerProperty hp;
	private final IntegerProperty playerPos;
	private final IntegerProperty score;
	private final BooleanProperty active;
	private final List<Alien> aliens;
	private final List<Barrier> barriers;
	private final List<Projectile> projectiles = new ArrayList<Projectile>();
	
	
	public GameState() {
		this.playerPos = new SimpleIntegerProperty((COLUMNS - PLAYER_WIDTH) / 2);
		this.score = new SimpleIntegerProperty(0);
		this.active = new SimpleBooleanProperty(false);
		this.hp = new SimpleIntegerProperty(MAX_HP);
		
		aliens = new ArrayList<Alien>();
		for (int r = 0; r < ALIEN_ROWS; r++)
			for (int c = 1; c < ALIEN_COLS + 1; c++)
				aliens.add(new Alien(r, c));

		barriers = new ArrayList<Barrier>();
		for(int i = 0; i < BARRIER_COUNT; i++) {
			barriers.add(new Barrier(BARRIER_ROW, i * ALIEN_COLS / BARRIER_COUNT + i + 1, MAX_HP));
		}
		
		setDefaultValues();
	}
	
	public void reinitialize() {
		aliens.forEach(a -> a.setActive(true));
		barriers.forEach(b -> {b.setActive(true); b.setHp(MAX_HP);});
		projectiles.forEach(p -> p.setActive(false));
		projectiles.clear();
		setDefaultValues();
	}
	
	private void setDefaultValues() {
		int alienRow = 0;
		int alienCol = 0;
		for(int i = 0; i < ALIEN_COLS * ALIEN_ROWS; i++) {
			aliens.get(i).setRow(alienRow);
			aliens.get(i).setColumn(++alienCol);
			if(i % ALIEN_COLS == ALIEN_COLS - 1)
				alienRow++;
			if(alienCol == ALIEN_COLS)
				alienCol = 0;
		}
		
		playerPos.setValue((COLUMNS - PLAYER_WIDTH) / 2);
		score.setValue(0);
		active.setValue(false);
		hp.setValue(MAX_HP);
	}
	
	public List<Alien> getAliens() {
		return Collections.unmodifiableList(aliens);
	}
	
	public List<Barrier> getBarriers() {
		return Collections.unmodifiableList(barriers);
	}
	
	public boolean updateProjectile(Projectile p) {
		p.update();
		return checkProjectile(p);
	}
	
	public boolean updateAliens() {
		boolean setDirection = false;
		for(Alien a : aliens) {
			boolean flag = a.updateCol();
			if(!setDirection && flag && a.isActive())
				setDirection = true;
		}
		if(setDirection) {
			setDirection = false;
			for(Alien a : aliens) {
				a.setDirection();
				boolean flag = a.updateRow() && a.isActive();
				if(flag) return false;
			}
		}
		return true;
	}	
	
	public boolean checkProjectile(Projectile projectile) {
		int c = projectile.getColumn();
		int r = projectile.getRow();
		Optional<Alien> collisionAlien = aliens.stream()
				.filter(a -> a.getColumn() == c / ALIEN_WIDTH)
				.filter(a -> a.getRow() == r)
				.filter(a -> a.isActive())
				.findFirst();
		
		Optional<Barrier> collisionBarrier = barriers.stream()
				.filter(b -> b.getColumn() == c / ALIEN_WIDTH)
				.filter(b -> b.getRow() == r)
				.filter(b -> b.isActive())
				.findFirst();
		
		if (collisionAlien.isPresent() && projectile.isActive()) {
			projectile.setActive(false);
			collisionAlien.get().setActive(false);
			score.setValue(score.getValue() + 1);
			return aliens.stream().filter(a -> a.isActive()).count() > 0;
		}
		if (collisionBarrier.isPresent() && projectile.isActive()) {
			projectile.setActive(false);
			collisionBarrier.get().setHp(collisionBarrier.get().getHp() - 1);
			if(collisionBarrier.get().getHp() == 0)
				collisionBarrier.get().setActive(false);
			return true;
		}
		if(c >= playerPos.getValue() && c < playerPos.getValue() + PLAYER_WIDTH && r == PLAYER_ROW) {
			hp.setValue(hp.getValue() - 1);
			projectile.setActive(false);  
			return hp.getValue() >= 0;
		}
		return true;
	}
	
	public void moveLeft() {
		int playerCol = playerPos.getValue();
		if (playerCol > 0) playerPos.setValue(playerCol - 1);
	}
	
	public void moveRight() {
		int playerCol = playerPos.getValue();
		if (playerCol + PLAYER_WIDTH < COLUMNS) playerPos.setValue(playerCol + 1);
	}
	
	public final IntegerProperty playerPosProperty() {
		return this.playerPos;
	}

	public final IntegerProperty scoreProperty() {
		return this.score;
	}
	
	public final int getScore() {
		return this.scoreProperty().get();
	}
	
	public final void setScore(final int score) {
		this.scoreProperty().set(score);
	}

	public final BooleanProperty activeProperty() {
		return this.active;
	}
	
	public final boolean isActive() {
		return this.activeProperty().get();
	}
	
	public final void setActive(final boolean active) {
		this.activeProperty().set(active);
	}

	public final List<Projectile> getProjectiles() {
		return this.projectiles;
	}

	public final IntegerProperty hpProperty() {
		return this.hp;
	}
	
	public final int getHp() {
		return this.hpProperty().get();
	}
	
	public final void setHp(final int hp) {
		this.scoreProperty().set(hp);
	}
}
