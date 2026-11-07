package main;

import gui.GuiController;
import tools.OTimer;

import javax.swing.*;

class MainController {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			OTimer timerStartUp = new OTimer("startup").start();
			GuiController guiController = new GuiController();
			SearchController searchController = new SearchController();
			searchController.addSearchListener(guiController);
			guiController.setSearchController(searchController);
			guiController.show();
			timerStartUp.stop().print();
		});
	}
}
