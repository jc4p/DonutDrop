package com.kasra.donutdrop.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.kasra.donutdrop.DonutDrop;
import com.kasra.donutdrop.screens.MainMenuScreen;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "DonutDrop";
		config.width = 432;
		config.height = 768;
		new LwjglApplication(new DonutDrop(), config);
	}
}
