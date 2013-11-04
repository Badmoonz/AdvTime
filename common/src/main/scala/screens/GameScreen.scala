package my.game.pkg.screens

import my.game.pkg.model.World;
import my.game.pkg.view.WorldRenderer;
import my.game.pkg.controller.FinnController;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;

class GameScreen extends Screen with InputProcessor{

  private var world : World = _;
  private var renderer : WorldRenderer = _;
  private var controller :  FinnController = _;

  private var width : Int =_;
  private var height : Int = _;

  override def show() {
    world = new World();
    world.createDemoWorld();
    renderer = new WorldRenderer(world, false);
    controller = new FinnController(world);
    Gdx.input.setInputProcessor(this);
  }

  override def resize(width_ : Int, height_ : Int) {
    width = width_;
    height = height_;
    renderer.setSize(width_, height_);
  }

  override def render(delta : Float) {
    Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
    Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    controller.update(delta);
    renderer.render();
  }

  override def dispose() {
    Gdx.input.setInputProcessor(null);
  }
  override def hide() {
    Gdx.input.setInputProcessor(null);
  }
  override def pause() {}
  override def resume() {}


  // * InputProcessor methods ***************************//

  override def keyDown(keycode : Int) : Boolean = {
    if (keycode == Keys.LEFT)
      controller.leftPressed();
    if (keycode == Keys.RIGHT)
      controller.rightPressed();
    if (keycode == Keys.SPACE)
      controller.jumpPressed();
    if (keycode == Keys.X)
      controller.firePressed();
    return true;
  }

  override def keyUp(keycode : Int) : Boolean = {
    if (keycode == Keys.LEFT)
      controller.leftReleased();
    if (keycode == Keys.RIGHT)
      controller.rightReleased();
    if (keycode == Keys.SPACE)
      controller.jumpReleased();
    if (keycode == Keys.X)
      controller.fireReleased();
    return true;
  }

   override def keyTyped(character : Char) : Boolean = {
    // TODO Auto-generated method stub
    return false;
  }

  override def touchDown(x : Int, y : Int, pointer : Int, button : Int) : Boolean = {
    if (x < width / 2 && y > height / 2) {
      controller.leftPressed();
    }
    if (x > width / 2 && y > height / 2) {
      controller.rightPressed();
    }
    return true;
  }

  override def touchUp(x : Int, y : Int, pointer : Int, button : Int) : Boolean = {
    if (x < width / 2 && y > height / 2) {
      controller.leftReleased();
    }
    if (x > width / 2 && y > height / 2) {
      controller.rightReleased();
    }
    return true;
  }

  override def touchDragged(x : Int,y : Int, pointer : Int) : Boolean = {
    // TODO Auto-generated method stub
    return false;
  }

  override def mouseMoved(x : Int, y : Int) : Boolean = {
    // TODO Auto-generated method stub
    return false;
  }

  override def scrolled(amount : Int) : Boolean = {
    // TODO Auto-generated method stub
    return false;
  }

}
