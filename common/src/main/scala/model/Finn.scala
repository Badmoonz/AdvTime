package my.game.pkg.model;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

final object State extends Enumeration {
  type State = Value;
  val IDLE, WALKING, JUMPING, DYING = Value;
}

object Finn {
  val SPEED = 2f;
  val JUMP_VELOCITY = 1f;
  val SIZE = 0.5f; // half a unit
}


class Finn(position_ : Vector2) {
  import  Finn._;
  var position : Vector2 = position_;
  var bounds : Rectangle = new Rectangle(0, 0, SIZE, SIZE);
  var acceleration : Vector2 = new Vector2();
  var velocity : Vector2 = new Vector2();
  var state : State.State  = State.IDLE;
  var stateTime : Float = 0;
  var facingLeft : Boolean = true;

  def update(delta : Float) {
    stateTime += delta;
    position.add(velocity.mul(delta));
  }
}
