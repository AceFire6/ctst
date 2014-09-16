package za.co.jethromuller.ctst.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import za.co.jethromuller.ctst.CtstMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.resizable = false;
        config.title = "Climbing Towers, Stealing Treasure";
        config.width = 600;
        config.height = 600;
		new LwjglApplication(new CtstMain(), config);
	}
}
