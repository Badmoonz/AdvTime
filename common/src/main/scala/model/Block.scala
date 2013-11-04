package my.game.pkg.model

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2

object  Block {
  val SIZE = 1f;
}

class Block(position_ : Vector2) {
  import Block._;
  var position : Vector2 = position_;
  var bounds : Rectangle = new Rectangle(0, 0, SIZE, SIZE);
}
