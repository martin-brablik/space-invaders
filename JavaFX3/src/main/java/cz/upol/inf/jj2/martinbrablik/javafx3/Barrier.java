package cz.upol.inf.jj2.martinbrablik.javafx3;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Barrier {
	
	private BooleanProperty active;
	private IntegerProperty row;
	private IntegerProperty column;
	private DoubleProperty hp;
	
	public Barrier(int row, int column, int hp) {
		this.active = new SimpleBooleanProperty(true);
		this.row = new SimpleIntegerProperty(row);
		this.column = new SimpleIntegerProperty(column);
		this.hp = new SimpleDoubleProperty(hp);
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
	
	public final DoubleProperty hpProperty() {
		return this.hp;
	}

	public final double getHp() {
		return this.hpProperty().get();
	}

	public final void setHp(final double hp) {
		this.hpProperty().set(hp);
	}
	
}
