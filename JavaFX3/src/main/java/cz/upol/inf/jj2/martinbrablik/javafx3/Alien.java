package cz.upol.inf.jj2.martinbrablik.javafx3;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Alien {
	
	private final BooleanProperty active;
	private final IntegerProperty row;
	private final IntegerProperty column;
	private int direction;
	
	public Alien(int row, int column) {
		this.row = new SimpleIntegerProperty(row);
		this.column = new SimpleIntegerProperty(column);
		this.active = new SimpleBooleanProperty(true);
		direction = 1;
	}
	
	public boolean updateCol() {
		setColumn(getColumn() + direction);
		return getColumn() == 0 || getColumn() == (GameState.COLUMNS - 1) / GameState.ALIEN_WIDTH;
	}
	
	public boolean updateRow() {
		setRow(getRow() + 1);
		return getRow() == GameState.BARRIER_ROW - 1;
	}
	
	public int getDirection() {
		return direction;
	}
	
	public void setDirection() {
		direction = direction * -1;
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
	
	public final IntegerProperty rowProperty() {
		return this.row;
	}

	public final int getRow() {
		return this.rowProperty().get();
	}	

	public final void setRow(final int row) {
		this.rowProperty().set(row);
	}
	
	public final IntegerProperty columnProperty() {
		return this.column;
	}

	public final int getColumn() {
		return this.columnProperty().get();
	}

	public final void setColumn(final int column) {
		this.columnProperty().set(column);
	}
}
