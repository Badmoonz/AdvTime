package my.game.pkg.utils;

import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.Gdx;
;

object TextureSetup extends App{
  def main(args : String*) {
    TexturePacker2.process("/home/alex/scala/testgame/common/assets/images",  "/home/alex/scala/testgame/common/assets/images/textures", "textures.pack");
  }
}