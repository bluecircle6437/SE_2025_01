package main;

import junit.framework.*;
import org.junit.Test;

public class GameTest extends TestCase {

    @Test public void test1() {
        Game game = new Game();
        GameConfig config = new GameConfig(2, 2, "사각형");
        game.initialize(config);
        assertEquals(2, game.players.size());
        assertEquals(2, game.maxPieceCount);
        assertEquals(29, game.map.length);

        game.throwYutSelect(5);
        game.throwYutSelect(2);
        game.checkZeroBack();
        game.makeTurn(0, 0);
        game.makeTurn(1, 0);
        assertEquals(5, game.players.get(0).pieces.get(0).getLocation());
        assertEquals(2, game.players.get(0).pieces.get(1).getLocation());

        game.throwYutSelect(2);
        game.checkZeroBack();
        game.makeTurn(0, 0);
        game.throwYutSelect(2);
        game.checkZeroBack();
        game.makeTurn(0, 0);
        assertEquals(-1, game.players.get(0).pieces.get(1).getLocation());
        assertEquals(4, game.players.get(1).pieces.get(0).getLocation());

        game.throwYutSelect(5);
        game.throwYutSelect(3);
        game.checkZeroBack();
        game.makeTurn(1, 0);
        game.makeTurn(0, 0);
        assertEquals(24, game.players.get(0).pieces.get(0).getLocation());
        assertEquals(24, game.players.get(0).pieces.get(1).getLocation());

        game.throwYutSelect(4);
        game.throwYutSelect(2);
        game.checkZeroBack();
        game.makeTurn(0, 0);
        game.makeTurn(0, 0);
        assertEquals(10, game.players.get(1).pieces.get(0).getLocation());

        game.throwYutSelect(1);
        game.checkZeroBack();
        game.makeTurn(0, 0);
        game.throwYutSelect(3);
        game.checkZeroBack();
        game.makeTurn(0, 0);
        game.throwYutSelect(-1);
        game.checkZeroBack();
        game.makeTurn(0, 0);
        game.throwYutSelect(3);
        game.checkZeroBack();
        game.makeTurn(0, 0);
        assertEquals(0, game.players.get(0).pieces.get(0).getLocation());
        assertEquals(0, game.players.get(0).pieces.get(1).getLocation());
        assertEquals(-1, game.players.get(1).pieces.get(0).getLocation());

        game.throwYutSelect(-1);
        game.checkZeroBack();
        assertEquals(-1, game.players.get(1).pieces.get(0).getLocation());

        game.throwYutSelect(1);
        game.checkZeroBack();
        game.makeTurn(0, 0);
        assertEquals(1, game.finishPlayers.size());
    }
}
