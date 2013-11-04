package my.game.pkg

import com.badlogic.gdx.backends.lwjgl._

object Main extends App {
  new LwjglApplication(new MyGame(), "Star Assault", 1040, 700, true);
}
