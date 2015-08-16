package main;

import gui.GuiController;
import tools.OTimer;

import javax.swing.*;

class MainController {

	public static void main(String[] args) {
		OTimer timerStartUp = new OTimer("startup").start();
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");

		} catch (Exception e) {
			e.printStackTrace();
		}
		GuiController guiController = new GuiController();
		SearchController searchController = new SearchController();
		searchController.addSearchListener(guiController);
		guiController.setSearchController(searchController);
		guiController.show();
		timerStartUp.stop().print();
	}

}
