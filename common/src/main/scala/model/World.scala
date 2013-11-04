package my.game.pkg.model

import com.badlogic.gdx.math.{Vector2, Rectangle};
import com.badlogic.gdx.utils.Array
import my.game.pkg.model.{Finn, Block};

object World {
  val LEVEL_WIDTH  = 10f;
  val LEVEL_HEIGHT = 7f;
}

class World {
  import World._;
  /** The blocks making up the world **/
  lazy val blocks : Array[Array[Block]] = new Array[Array[Block]]();
  /** Our player controlled hero **/
  lazy val finn : Finn = new Finn(new Vector2(1, 0));

  var collisionRects : List[Rectangle] = Nil;


  /** Return only the blocks that need to be drawn **/
  def getDrawableBlocks(width : Int, height : Int) : Array[Block] = {
    import scala.math.{floor};
    val x =  {
      val _x = math.floor(finn.position.x - width).toInt;
      if (_x < 0) 0 else _x
    };
    val y =  {
      val _y = math.floor(finn.position.y - height).toInt;
      if (_y < 0) 0 else _y
    };
    val x2 = {
      val _x = (x + 2 * width).toInt
      if (_x > World.LEVEL_WIDTH) (World.LEVEL_WIDTH - 1).toInt else _x
    };

    val y2 = {
      val _y = (y + 2 * height).toInt
      if (_y > World.LEVEL_HEIGHT)  (World.LEVEL_HEIGHT - 1).toInt else _y
    };
    blocks.clear();
    for (col <- x to x2; row <- y to y2) {
      val block = blocks.apply(col).apply(row);
      if (block != None)
        blocks.add(block);
    }
    return blocks;
  }


  def createDemoWorld() {
//    for (i <- 0 until 10) {
//      blocks.add(new Block(new Vector2(i, -1)));
////      blocks.add(new Block(new Vector2(i, 7)));
////      if (i > 2)
////        blocks.add(new Block(new Vector2(i, 1)));
//    }
//
//    for (i <- 0 to 6)   {
//      blocks.add(new Block(new Vector2(-1, i)));
//      blocks.add(new Block(new Vector2(11, i)));
//    }

//    for (i <- 3 to 6)   {
//      blocks.add(new Block(new Vector2(6, i)));
//    }
  }
}
