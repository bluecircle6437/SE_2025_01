package ui;

public class GameConfig {
	public int playerCount;
	public int pieceCount;
	public String boardShape;
	
	public GameConfig(int playerCount, int pieceCount, String boardShape) {
		this.playerCount = playerCount;
		this.pieceCount = pieceCount;
		this.boardShape = boardShape;
	}
}
