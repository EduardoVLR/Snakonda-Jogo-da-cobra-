package application;

import application.Queue;

import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

public class Main extends Application {
	
	//variaveis
	static int score = 0;
	static int speed = 3;
	static int foodcolor = 0;
	static int snakecolor = 0;
	static int dmgcolor = 0;
	static int width = 20;
	static int height = 20;
	static int foodX = 0;
	static int foodY = 0;
	static int dmgX = 0;
	static int dmgY= 0;
	static int cornersize = 25;
	static Dir direction = Dir.up;
	static boolean gameOver = false;
	static Random rand = new Random();
	
	static Queue<Corner> snake = new Queue<>();
	
	 
	public enum Dir{
		left, right, up, down
	}
	
	public static class Corner{
		int x;
		int y;
		
		public Corner(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
	
	public void start(Stage primaryStage){
		try {
			newFood();
			damageFood();
			
			VBox root = new VBox();
			Canvas c = new Canvas(width*cornersize, height*cornersize);
			GraphicsContext gc = c.getGraphicsContext2D();
			root.getChildren().add(c);
			
			new AnimationTimer() {
				long lastTick = 0;
				
				public void handle(long now) {
					if(lastTick == 0) {
						lastTick = now;
						tick(gc);
						
						return;
					}
					
					if(now - lastTick > 1000000000/speed) {
						lastTick = now;
						tick(gc);
					}
				}
			
			}.start();
			
			
			Scene scene = new Scene(root, width*cornersize, height*cornersize);
			
			//Controles
			scene.addEventFilter(KeyEvent.KEY_PRESSED,key ->{
				if(key.getCode() == KeyCode.W) {
					direction = Dir.up;
				}
				if(key.getCode() == KeyCode.A) {
					direction = Dir.left;
				}
				if(key.getCode() == KeyCode.S) {
					direction = Dir.down;
				}
				if(key.getCode() == KeyCode.D) {
					direction = Dir.right;
				}
			});
			
			//add start snake parts
			snake.enfileira(new Corner(width/2, height/2));
			
			
			primaryStage.setScene(scene);
			primaryStage.setTitle("Snakonda Ultimate");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//
	public static void tick(GraphicsContext gc) {
		if(gameOver) {
			gc.setFill(Color.RED);
			gc.setFont(new Font("", 50));
			gc.fillText("YOU DIE :(", 140, 250);
			return;
		}
		
		for(int i = snake.size() -1;i >= 1; i--) {
			snake.get(i).x = snake.get(i - 1).x;
			snake.get(i).y = snake.get(i - 1).y;
			
		}
		
		switch(direction) {
		case up:
			snake.get(0).y--;
			if(snake.get(0).y < 0) {
				gameOver = true;
			}
			break;
		case down:
			snake.get(0).y++;
			if(snake.get(0).y > height) {
				gameOver = true;
			}
			break;
		case left:
			snake.get(0).x--;
			if(snake.get(0).x < 0) {
				gameOver = true;
			}
			break;
		case right:
			snake.get(0).x++;
			if(snake.get(0).x > width) {
				gameOver = true;
			}
			break;
		
		}
		
		//comendo
		if(foodX == snake.get(0).x && foodY == snake.get(0).y) {
			snake.enfileira(new Corner(-1, -1));
			newFood();
		}
		
		//dano na cobra
		if(dmgX == snake.get(0).x && dmgY == snake.get(0).y) {
			snake.desenfileira();
			damageFood();
		}
		
		//se a fila estiver vazia, ela mostra o game over na tela.
		if(snake.estaVazia()) {
			gameOver = true;
		}
			
		//auto destruição
		for(int i = 1; i<snake.size(); i++) {
			if(snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
				gameOver = true;
			}
		}
		
		//fundo de tela
		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, width*cornersize, height*cornersize);
		
		//Pontuação
		gc.setFill(Color.WHITE);
		gc.setFont(new Font("",30));
		gc.fillText("Score: " + (snake.size() - 1), 10, 30);
		
		//Cor aleatória para a comida.
		Color cc = Color.WHITE;
		
		switch(foodcolor) {
		case 0: cc = Color.PURPLE;
		break;
		case 1: cc = Color.LIGHTBLUE;
		break;
		case 2: cc = Color.YELLOW;
		break;
		case 3: cc = Color.PINK;
		break;
		case 4: cc = Color.ORANGE;
		break;
		}
		gc.setFill(cc);
		gc.fillOval(foodX*cornersize, foodY*cornersize, cornersize, cornersize);
		gc.setFill(Color.GRAY);
		gc.fillOval(dmgX*cornersize, dmgY*cornersize, cornersize, cornersize);
		
		//Snake
			for(int i = 0;i <snake.size();i++){
				
				gc.setFill(Color.GREEN);
				gc.fillRect(snake.get(i).x*cornersize, snake.get(i).y*cornersize, cornersize-1, cornersize-1);
				
			
			}
		
	}
	
	//food
	public static void newFood() {
		start:while(true) {
			foodX = rand.nextInt(width);
			foodY = rand.nextInt(height);
			
			 
			for(int i = 0;i <snake.size();i++){
				if(snake.get(i).x == foodX && snake.get(i).y == foodY) {
					continue start;
				}
			}
			foodcolor = rand.nextInt(5);
			speed++;
			break;
		}
	}
	
	public static void damageFood() {
		start:while(true) {
			dmgX = rand.nextInt(width);
			dmgY = rand.nextInt(height);
			
			 
			for(int i = 0;i <snake.size();i++){
				if(snake.get(i).x == dmgX && snake.get(i).y == dmgY) {
					continue start;
				}
			}
			speed++;
			break;
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}