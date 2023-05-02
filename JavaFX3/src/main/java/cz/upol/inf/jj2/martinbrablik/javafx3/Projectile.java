package cz.upol.inf.jj2.martinbrablik.javafx3;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Projectile {

	private BooleanProperty active;
	private IntegerProperty row;
	private IntegerProperty column;
	private boolean direction;
	
	public Projectile(int row, int column, boolean direction) {
		this.row = new SimpleIntegerProperty(row);
		this.column = new SimpleIntegerProperty(column);
		this.active = new SimpleBooleanProperty(true);
		this.direction = direction;
	}
	
	public void update() {
		setRow(direction ? getRow() - 1 : getRow() + 1);
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

	public boolean isDirection() {
		return direction;
	}

	public void setDirection(boolean direction) {
		this.direction = direction;
	}
}
