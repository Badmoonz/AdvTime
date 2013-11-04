package my.game.pkg.controller

import java.util.HashMap;
import java.util.Map;
import my.game.pkg.model.{Finn, Block, State, World};
import com.badlogic.gdx.math.{Vector2, Rectangle};


object KEYS extends Enumeration {
  type KEYS= Value;
  val LEFT, RIGHT, JUMP, FIRE = Value;
}

object FinnController {
  import KEYS._
  val keys = new HashMap[KEYS, Boolean]();
  keys.put(KEYS.LEFT, false);
  keys.put(KEYS.RIGHT, false);
  keys.put(KEYS.JUMP, false);
  keys.put(KEYS.FIRE, false);

  val LONG_JUMP_PRESS 	= 350l;
  val ACCELERATION 	    = 100f;
  val GRAVITY 			    = -200f;
  val MAX_JUMP_SPEED	  = 7f;
  val DAMP 			        = 0.90f;
  val MAX_VEL 			    = 200f;

}

class FinnController(private val world : World) {
  import FinnController._;
  // ** Key presses and touches **************** //


  private var collidable : List[Block] = Nil;
  private var	jumpPressedTime : Long = _;
  private var jumpingPressed : Boolean =_;


 def leftPressed() {
    keys.get(keys.put(KEYS.LEFT, true));
  }

  def rightPressed() {
    keys.get(keys.put(KEYS.RIGHT, true));
  }

  def jumpPressed() {
    keys.get(keys.put(KEYS.JUMP, true));
  }

  def firePressed() {
    keys.get(keys.put(KEYS.FIRE, false));
  }

  def leftReleased() {
    keys.get(keys.put(KEYS.LEFT, false));
  }

  def rightReleased() {
    keys.get(keys.put(KEYS.RIGHT, false));
  }

  def jumpReleased() {
    keys.get(keys.put(KEYS.JUMP, false));
    jumpingPressed = false;
  }

  def fireReleased() {
    keys.get(keys.put(KEYS.FIRE, false));
  }


  // ... code omitted ... //


  /** The main update method **/
  def update(delta : Float){
    processInput();
    val finn  = world.finn;

    finn.acceleration.y = GRAVITY;
    finn.velocity.add(finn.acceleration.mul(delta));
    if (finn.acceleration.x == 0)
      finn.velocity.x *= DAMP;
    if (finn.velocity.x > MAX_VEL)
      finn.velocity.x = MAX_VEL;
    if (finn.velocity.x < -MAX_VEL)
      finn.velocity.x = -MAX_VEL;

    checkCollisionWithBlocks(delta);
    finn.update(delta);
    if (finn.position.y < 0) {
      finn.position.y = 0f;
      if (finn.state == State.JUMPING)
        finn.state = State.IDLE;
    }
    if (finn.position.x < 0) {
      finn.position.x = 0;
      if (finn.state != State.JUMPING)
        finn.state = State.IDLE;
    }
    if (finn.position.x > World.LEVEL_WIDTH - finn.bounds.width ) {
      finn.position.x = World.LEVEL_WIDTH - finn.bounds.width;
      if (finn.state != State.JUMPING)
        finn.state = State.IDLE;
    }
  }

  /** Change Bob's state and parameters based on input controls **/
  def processInput() {
    val finn = world.finn;
    if (keys.get(KEYS.JUMP)) {
      if (finn.state != State.JUMPING) {
        jumpingPressed = true;
        jumpPressedTime = System.currentTimeMillis();
        finn.state = State.JUMPING;
        finn.velocity.y = MAX_JUMP_SPEED;
      } else {
        if (jumpingPressed && ((System.currentTimeMillis() - jumpPressedTime) >= LONG_JUMP_PRESS)) {
          jumpingPressed = false;
        } else {
          if (jumpingPressed)
            finn.velocity.y = MAX_JUMP_SPEED;
        }
      }
    }
    if (keys.get(KEYS.LEFT)) {
      // left is pressed
      finn.facingLeft = true;
      if (finn.state != State.JUMPING)
        finn.state = State.WALKING;
      finn.acceleration.x = -ACCELERATION;
    } else if (keys.get(KEYS.RIGHT)) {
      // left is pressed
      finn.facingLeft = false;
      if (finn.state != State.JUMPING)
        finn.state = State.WALKING;
      finn.acceleration.x = ACCELERATION;
    } else {
      if (finn.state != State.JUMPING)
        finn.state = State.IDLE;
      finn.acceleration.x = 0;
    }
  }

  private def checkCollisionWithBlocks(delta : Float) {
    import scala.math;
    val finn  = world.finn;
    finn.velocity.mul(delta);
    var finnRect = finn.bounds;
    var startY = math.floor(finn.bounds.y).toInt;
    var endY = math.floor(finn.bounds.y + finn.bounds.height).toInt;
    var startX =  {
      if (finn.velocity.x < 0)
        math.floor(finn.bounds.x + finn.velocity.x);
      else
        math.floor(finn.bounds.x + finn.velocity.x + finn.bounds.width);
    }.toInt
    var endX = startX;
    populateCollidableBlocks(startX, startY, endX, endY);
    finnRect.x += finn.velocity.x;
    world.collisionRects = Nil;
    for (block <- collidable;
         if block != None;
         if finnRect.overlaps(block.bounds)
    ) {
      finn.velocity.x = 0;
      world.collisionRects :+= block.bounds;
    }

    finnRect.x = finn.position.x;
    startX = math.floor(finn.bounds.x).toInt;
    endX = math.floor(finn.bounds.x + finn.bounds.width).toInt;
    startY =  {
      if (finn.velocity.y < 0)
        math.floor(finn.bounds.y + finn.velocity.y);
      else
        math.floor(finn.bounds.y + finn.velocity.y + finn.bounds.height);
    }.toInt
    endY = startY;
    populateCollidableBlocks(startX, startY, endX, endY);
    finnRect.y += finn.velocity.y;
    for (block <- collidable;
         if block != None;
         if finnRect.overlaps(block.bounds)
         ) {
//      if (finn.velocity.y < 0)
//        val grounded = true;
      finn.velocity.y = 0;
      world.collisionRects :+= block.bounds;
    }
    finnRect.y = finn.position.y;
    finn.position.add(finn.velocity.mul(delta));
    finn.bounds.x = finn.position.x;
    finn.position.y = finn.position.y;
    finn.velocity.mul(1 / delta);
  }

  private def populateCollidableBlocks(startX : Int, startY : Int, endX : Int, endY : Int) {
    collidable = Nil;
    for (x <- startX to endX; y <- startY to endY) {
        if (x >= 0 && x < World.LEVEL_WIDTH && y >=0 && y < World.LEVEL_HEIGHT)
          collidable :+= world.blocks(x)(y);
    }
  }




}
