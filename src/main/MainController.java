package main;

import java.io.File;

import javax.swing.UIManager;

import data.ConcurrentResultMap;
import data.DuplicationStructureBuilder;
import filefinder.FileFinder;
import gui.GuiController;
import interfaces.IDuplicateSearchListener;
import interfaces.IGuiController;
import io.FileSystem;
import tools.OTimer;

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
