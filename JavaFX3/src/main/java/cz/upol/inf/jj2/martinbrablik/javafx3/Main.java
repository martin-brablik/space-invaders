package cz.upol.inf.jj2.martinbrablik.javafx3;

import java.util.Comparator;
import java.util.Optional;
import java.util.Random;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
	
	private static final int DELAY = 100;
	private static final int ALEIN_MOVE_DELAY = 2000;
	private static final int ALEIN_FIRE_DELAY = 2000;
	private static final int UNIT_SIZE = 15;

	public static void main(String[] args) {
		launch(args);
	}

	private Pane gamePane;
	private Text lbScore;
	private Text lbHp;
	private Button btnStart;
	private Timeline mainTimeline;
	private Timeline projectileTimeline;
	private GameState gameState = new GameState();

	@Override
	public void start(Stage primaryStage) throws Exception {
		createGamePane();
		
		lbScore = new Text();
		lbScore.textProperty().bind(gameState.scoreProperty().asString());
		
		lbHp = new Text();
		lbHp.textProperty().bind(gameState.hpProperty().asString());
		
		mainTimeline = new Timeline();
		KeyFrame move = new KeyFrame(
		        Duration.millis(ALEIN_MOVE_DELAY),
		        e -> { if(!gameState.updateAliens()) gameOver(); }
		        );
		KeyFrame fire = new KeyFrame(
		        Duration.millis(ALEIN_FIRE_DELAY),
		        e -> {
		        	if(!gameState.getAliens().isEmpty()) {
		        		Random r = new Random();
		        		int pos = r.nextInt(GameState.COLUMNS);
		        		Optional<Alien> alien = Optional.of(gameState.getAliens().get(0));
		        		alien = gameState.getAliens().stream().filter(a -> a.isActive()).max(Comparator.comparing(Alien::getRow));
		        		if(alien.isPresent())
		        			fire(alien.get().getRow() + 1, pos, false);
		        	}
		        });
		mainTimeline.getKeyFrames().add(move);
		mainTimeline.getKeyFrames().add(fire);
		mainTimeline.setCycleCount(Animation.INDEFINITE);
		
		btnStart = new Button("Start");
		btnStart.visibleProperty().bind(gameState.activeProperty().not());
		btnStart.setOnAction(e -> {
			gameState.reinitialize();
			gameState.setActive(true);
			gamePane.setOpacity(1);
			mainTimeline.play();});
		
		BorderPane root = new BorderPane();
		
		StackPane gameStack = new StackPane();
		gameStack.getChildren().addAll(gamePane, btnStart);
		
		HBox scorePane = new HBox(10, new Text("Score: "), lbScore, new Text("Lifes: "), lbHp);
		
		root.setCenter(gameStack);
		root.setTop(scorePane);
		
		Scene scene = new Scene(root, GameState.COLUMNS * UNIT_SIZE, (GameState.ROWS + 6) * UNIT_SIZE);
		scene.setOnKeyPressed(this::dispatchKeyEvents);
		primaryStage.setTitle("Space Invaders");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private void createGamePane() {
		gamePane = new Pane();
		gamePane.setMaxWidth(UNIT_SIZE * GameState.COLUMNS);
		gamePane.setMaxHeight(UNIT_SIZE * GameState.ROWS);
		
		for(Alien alien : gameState.getAliens()) {
			gamePane.getChildren().add(createAlien(alien));
		}
		
		for(Barrier barrier : gameState.getBarriers()) {
			gamePane.getChildren().add(createBarrier(barrier));
		}
		
		gamePane.getChildren().add(createPlayer());
	}
	
	private Rectangle createAlien(Alien alien) {
		Rectangle alienShape = new Rectangle(UNIT_SIZE  * GameState.ALIEN_WIDTH - 1, UNIT_SIZE - 1);
		
		if ((alien.getRow() + alien.getColumn()) % 2 == 0) alienShape.setFill(Color.DARKRED);
		else alienShape.setFill(Color.RED);
		
		alienShape.xProperty().bind(alien.columnProperty().multiply(UNIT_SIZE * GameState.ALIEN_WIDTH));
		alienShape.yProperty().bind(alien.rowProperty().multiply(UNIT_SIZE));
		alienShape.visibleProperty().bind(alien.activeProperty());
		
		InnerShadow is = new InnerShadow();
		is.setOffsetY(1.0);
		is.setOffsetX(1.0);
		is.setColor(Color.GRAY);
		alienShape.setEffect(is);
		
		return alienShape;
	}
	
	private Rectangle createPlayer() {
		Rectangle playerShape = new Rectangle(UNIT_SIZE * GameState.PLAYER_WIDTH, UNIT_SIZE);
		playerShape.xProperty().bind(gameState.playerPosProperty().multiply(UNIT_SIZE));
		playerShape.setY(UNIT_SIZE * GameState.PLAYER_ROW);
		
		DropShadow ds = new DropShadow();
		ds.setOffsetY(3.0);
		ds.setOffsetX(3.0);
		ds.setColor(Color.GRAY);
				
		playerShape.setEffect(ds);
		return playerShape;
	}
	
	private Rectangle createProjectile(Projectile projectile) {
		Rectangle projectileShape = new Rectangle(UNIT_SIZE / 2, UNIT_SIZE / 2);
		projectileShape.xProperty().bind(projectile.columnProperty().multiply(UNIT_SIZE).add(GameState.PLAYER_WIDTH / 2));
		projectileShape.yProperty().bind(projectile.rowProperty().multiply(UNIT_SIZE));
		projectileShape.visibleProperty().bind(projectile.activeProperty());
		return projectileShape;
	}
	
	private Rectangle createBarrier(Barrier barrier) {
		Rectangle barrierShape = new Rectangle(UNIT_SIZE * GameState.BARRIER_WIDTH - 1, UNIT_SIZE - 1);
		barrierShape.setFill(Color.ROYALBLUE);
		barrierShape.xProperty().bind(barrier.columnProperty().multiply(UNIT_SIZE * GameState.ALIEN_WIDTH));
		barrierShape.yProperty().bind(barrier.rowProperty().multiply(UNIT_SIZE));
		barrierShape.opacityProperty().bind(barrier.hpProperty().divide(3));
		barrierShape.visibleProperty().bind(barrier.activeProperty());
		
		DropShadow ds = new DropShadow();
		ds.setOffsetY(3.0);
		ds.setOffsetX(3.0);
		ds.setColor(Color.ROYALBLUE);
		barrierShape.setEffect(ds);
		
		return barrierShape;
	}
	
	private void gameOver() {
		gameState.setActive(false);
		mainTimeline.stop();
		projectileTimeline.stop();
		gamePane.setOpacity(0.2);
	}
	
	private void dispatchKeyEvents(KeyEvent e) {
		if(gameState.isActive()) {
			switch (e.getCode()) {
			case LEFT: gameState.moveLeft(); break;
			case RIGHT: gameState.moveRight(); break;
			case UP: fire(GameState.PLAYER_ROW, gameState.playerPosProperty().get() + GameState.PLAYER_WIDTH / 2, true); break;
			default:
			}
		}
	}
	
	private void fire(int posY, int posX, boolean direction) {
		Projectile p = new Projectile(posY, posX, direction);
		Rectangle projectile = createProjectile(p);
		gameState.getProjectiles().add(p);
		gamePane.getChildren().add(projectile);
		projectileTimeline = new Timeline();
		KeyFrame updates = new KeyFrame(
				Duration.millis(DELAY),
				e -> {
					if(!gameState.updateProjectile(p))
						gameOver();
				});
		projectileTimeline.getKeyFrames().add(updates);
		projectileTimeline.setCycleCount(50);
		projectileTimeline.play();
	}
}
