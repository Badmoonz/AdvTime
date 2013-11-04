package my.game.pkg

import my.game.pkg.screens.GameScreen;

import com.badlogic.gdx.{Game, Gdx};

class MyGame extends Game {
  override def create() {
    setScreen(new GameScreen());
  }
}
