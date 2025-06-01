// JavaFX 버전의 PieceCircle.java
package main;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import javafx.geometry.*;

public class PieceCircle extends Region {
	private boolean blinking = false;
	private Timeline blinkTimeline;
	private Color originalColor;
	private Color currentColor;
	private final Circle circle;

	public PieceCircle(Color color) {
		this.originalColor = color;
		this.currentColor = color;
		this.circle = new Circle(20); // 반지름 20px

		circle.setFill(color);
		circle.setStroke(Color.BLACK);
		circle.setStrokeWidth(2);

		setPrefSize(40, 40);
		setMinSize(40, 40);
		setMaxSize(40, 40);

		getChildren().add(circle);
	}

	@Override
	protected void layoutChildren() {
		double centerX = getWidth() / 2;
		double centerY = getHeight() / 2;
		circle.setCenterX(centerX);
		circle.setCenterY(centerY);
	}

	public void setColor(Color color) {
		circle.setFill(color);
	}

	public void resetColor() {
		circle.setFill(originalColor);
	}

	public void update() {
		this.setVisible(false); // 강제로 다시 그리게 하는 방식
		this.setVisible(true);
	}

	public void startBlinking() {
		if (blinking)
			return;
		blinking = true;

		blinkTimeline = new Timeline(new KeyFrame(Duration.millis(300), e -> {
			circle.setVisible(!circle.isVisible());
		}));
		blinkTimeline.setCycleCount(Timeline.INDEFINITE);
		blinkTimeline.play();
	}

	public void stopBlinking() {
		if (blinkTimeline != null) {
			blinkTimeline.stop();
		}
		circle.setVisible(true);
		blinking = false;
	}
}
