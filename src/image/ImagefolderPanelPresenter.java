package image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;

import data.LeFile;
import gui.Gui;
import interfaces.IGuiEventHandler;
import io.CustomFile;

@SuppressWarnings("unused")
public class ImagefolderPanelPresenter {

    private final JPanel pnlNorth;
    private final JPanel pnlSouth;
    private final IGuiEventHandler eventHandler;
    private List<CustomFile> otherFiles = new ArrayList<>();
    private List<CustomFile> duplicates = new ArrayList<>();
    private final JScrollPane scrollPaneSouth;
    private final JSplitPane container;
    private final ImagePanelController imagePanelController = new ImagePanelController();
    private final Gui gui;

    public ImagefolderPanelPresenter(Gui gui, IGuiEventHandler eventHandler) {
        this.pnlNorth = gui.getPnlNorth();
        this.pnlSouth = gui.getPnlSouth();
        this.scrollPaneSouth = gui.getScrollPaneSouth();
        this.container = gui.getSpImages();
        this.gui = gui;
        this.eventHandler = eventHandler;
    }

    public void reset() {
        otherFiles = new ArrayList<>();
        duplicates = new ArrayList<>();
        int duplicatesCount = 0;
        folderImagesColumnCount = folderImagesRowCount = 0;
        imagePanelController.reset();
    }

    public void setOtherFiles(List<CustomFile> files) {
        this.otherFiles = files;
    }

    private int folderImagesColumnCount = 0;
    private int folderImagesRowCount = 0;
    private final int PICS_PER_ROW = 5;
    private final int IMG_PNL_INSET = 5;

    private Dimension calcImageDimension() {
        int width = scrollPaneSouth.getWidth() / PICS_PER_ROW;
        width = width - PICS_PER_ROW * IMG_PNL_INSET;
        return new Dimension(width, width);
    }

    private Dimension calcImageDimension(int picsPerRow) {
        int width = scrollPaneSouth.getWidth() / picsPerRow;
        int max = container.getHeight() / 2;
        width = (width > max) ? max : width;
        return new Dimension(width, width);
    }

    public void setDuplicates(List<LeFile> duplicates) {
        folderImagesColumnCount =0;
        folderImagesRowCount =0;
        pnlNorth.removeAll();
        this.duplicates = new ArrayList<>();
        if (duplicates != null) {
            int picsPerRow = duplicates.size();
            duplicates.forEach(dup -> {
                ImagePanel imagePanel = addImage(pnlNorth, dup.getCustomFile());
                imagePanel.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == 3) {
                            JPopupMenu menu = new JPopupMenu("bla");
                            JMenuItem menuItemDelFile = new JMenuItem("delete this copy");
                            JMenuItem menuItemDelOther = new JMenuItem("delete other copies");
                            menuItemDelFile.addActionListener(ev -> eventHandler.onFileDeleteFile(dup));
                            menuItemDelOther.addActionListener(ev -> eventHandler.onFileDeleteFilesEverywhereElse(dup));
                            menu.add(menuItemDelFile);
                            menu.add(menuItemDelOther);
                            menu.show(imagePanel, e.getX(), e.getY());
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });
            });
        }
        pnlNorth.validate();
        pnlNorth.repaint();
    }

    public void setFolderImages(File[] images) {
        folderImagesColumnCount =0;
        folderImagesRowCount =0;
        pnlSouth.removeAll();
        Arrays.stream(images).forEach(f -> addImage(pnlSouth, f));
        gui.getSpImages().validate();
        pnlSouth.validate();
        pnlSouth.repaint();
    }

    private ImagePanel addImage(JPanel panel, File file) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = folderImagesColumnCount;
        gbc.gridy = folderImagesRowCount;
        gbc.insets = new Insets(IMG_PNL_INSET, IMG_PNL_INSET, IMG_PNL_INSET, IMG_PNL_INSET);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        ImagePanel imagePanel = imagePanelController.createImagePanel(file);
        imagePanel.setBackground(Color.darkGray);
        imagePanel.setPreferredSize(calcImageDimension());

        panel.add(imagePanel, gbc);
        folderImagesColumnCount++;
        if (folderImagesColumnCount >= PICS_PER_ROW) {
            folderImagesColumnCount = 0;
            folderImagesRowCount++;
        }
        imagePanel.validate();
        return imagePanel;
    }
}
