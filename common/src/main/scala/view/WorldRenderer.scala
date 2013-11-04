package my.game.pkg.view

import my.game.pkg.model.{Block, Finn, State, World};
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;


import com.badlogic.gdx.utils.{Array => GdxArray}
import com.badlogic.gdx.graphics.g2d.{SpriteBatch, TextureAtlas, TextureRegion, Animation};
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;




object WorldRenderer {
  val CAMERA_WIDTH = 5f;
  val CAMERA_HEIGHT = 3.5f;
  val RUNNING_FRAME_DURATION = 0.15f;
}

class WorldRenderer {
  import WorldRenderer._;

  private var world :World = _;
  private var debug : Boolean = _;
  private lazy val cam : OrthographicCamera =  new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
  private lazy val debugRenderer : ShapeRenderer  = new ShapeRenderer();

  def this(world_ : World, debug_ : Boolean) {
    this();
    this.world = world_;
    this.debug = debug_;
    this.cam.position.set(CAMERA_WIDTH / 2f, CAMERA_HEIGHT / 2f, 0);
    this.cam.update();
  }
  def this(world : World) = this(world, true);

  /** Textures **/

  lazy val atlas = new TextureAtlas(Gdx.files.internal("images/textures/textures.pack"));
  lazy val finnIdle  = atlas.findRegion("finn-idle");
  lazy val blockTexture = atlas.findRegion("block");
  lazy val finnJumpRight = atlas.findRegion("finn-jump-01");
  lazy val finnJumpLeft = new TextureRegion(finnJumpRight);
  finnJumpLeft.flip(true, false);

  lazy val finnFallRight = atlas.findRegion("finn-fall-01");
  lazy val finnFallLeft =  new TextureRegion(finnFallRight);
  finnFallLeft.flip(true, false);

  /** Animations **/
  val walkLeftFrames = new GdxArray[TextureRegion](4);
  val walkRightFrames = new GdxArray[TextureRegion](4);
  for (i <- 1 to 4) {
    val rFrame = atlas.findRegion("finn-run-0" + i);
    walkRightFrames.add(rFrame);
    val lFrame = new TextureRegion(rFrame);
    lFrame.flip(true, false);
    walkLeftFrames.add(lFrame);
  }

//  lazy val walkRightFrames = walkLeftFrames map (frame : TextureRegion => frame.flip(true, false));

  lazy val walkLeftAnimation = new Animation(RUNNING_FRAME_DURATION, walkLeftFrames);
  lazy val walkRightAnimation = new Animation(RUNNING_FRAME_DURATION, walkRightFrames);

  private var spriteBatch : SpriteBatch = new SpriteBatch();
  private var width : Int = 0;
  private var height : Int = 0;
  private var ppuX : Float = 1f; // pixels per unit on the X axis
  private var ppuY : Float = 1f; // pixels per unit on the Y axis

  def setSize (w : Int, h : Int) {
    width = w;
    height = h;
    ppuX = width / CAMERA_WIDTH;
    ppuY = height / CAMERA_HEIGHT;
  }

  def render() {
    spriteBatch.begin();
    drawBlocks();
    drawFinn();
    spriteBatch.end();
    if (debug)
      drawDebug();
  }

  private def drawBlocks() {
    for (block <- world.blocks.toArray()) {
      spriteBatch.draw(blockTexture, block.position.x * ppuX, block.position.y * ppuY, Block.SIZE * ppuX, Block.SIZE * ppuY);
    }
  }

  private def drawFinn() {
    val finn = world.finn;
    val finnTexture = finn.state match  {
      case State.WALKING => {if (finn.facingLeft) walkLeftAnimation else walkRightAnimation}.getKeyFrame(finn.stateTime, true);
      case State.JUMPING =>
        if (finn.velocity.y > 0)
          if (finn.facingLeft) finnJumpLeft else finnJumpRight;
        else
         if (finn.facingLeft) finnFallLeft else finnFallRight;
      case _ => finnIdle;
    }
     spriteBatch.draw(finnTexture, finn.position.x * ppuX, finn.position.y * ppuY, Finn.SIZE * ppuX, Finn.SIZE * ppuY);
  }

  private def drawDebug() {
    // render blocks
    debugRenderer.setProjectionMatrix(cam.combined);
    debugRenderer.begin(ShapeType.values().apply(1));
    for (block <- world.blocks.toArray()) {
      val rect = block.bounds;
      val x1 = block.position.x + rect.x;
      val y1 = block.position.y + rect.y;
      debugRenderer.setColor(new Color(1, 0, 0, 1));
      debugRenderer.rect(x1, y1, rect.width, rect.height);
    }

    val finn = world.finn;
    val rect = finn.bounds;
    val x1 = finn.position.x + rect.x;
    val y1 = finn.position.y + rect.y;
    debugRenderer.setColor(new Color(0, 1, 0, 1));
    debugRenderer.rect(x1, y1, rect.width, rect.height);
    debugRenderer.end();
  }
}
