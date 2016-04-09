package userInterface;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class StarPane {
	static StackPane createStar(double size) {
		StackPane stackPane = new StackPane();
		stackPane.setMinHeight(size);
		stackPane.setMaxHeight(size);
		stackPane.setMinWidth(size);
		stackPane.setMaxWidth(size);
		stackPane.setAlignment(Pos.CENTER);
		stackPane.getChildren().add(buildStar(0.5 * (size / 2)));
		return stackPane;
	}

	static Polygon buildStar(double size) {
		int arms = 5;
		double rOuter = 1 * size;
		double rInner = 0.5 * size;
		double angle = Math.PI / arms;
		int c = 0;
		Double[] starCoor = new Double[20];
		for (int i = 0; i < arms * 2; i++) {
			double r = (i & 1) == 0 ? rOuter : rInner;
			double x = Math.cos(i * angle) * r;
			double y = Math.sin(i * angle) * r;
			starCoor[c++] = x;
			starCoor[c++] = y;
		}
		Polygon polygon = new Polygon();
		polygon.getPoints().addAll(starCoor);
		polygon.setFill(Color.WHITE);
		polygon.setStroke(Color.BLACK);
		return polygon;
	}
}
