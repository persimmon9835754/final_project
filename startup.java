import javax.swing.*;
import javax.xml.xpath.XPathEvaluationResult;
import battle.battle;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.event.*;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.IOException;
import javax.swing.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.lang.reflect.Array;
import java.io.File;
import java.io.FilenameFilter;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class startup extends JFrame implements KeyListener, ActionListener {
    private Graphics2D buffer;
    private Image offscreen;
    final int FRAME_WIDTH = 1000;
    final int FRAME_HEIGHT = 800;
    ArrayList<Image> arrayImages = new ArrayList<Image>();
    String imageFolderLocation = "/mnt/chromeos/SMB/ed26bc8a3151bf71c2aa30ee422e94bb9a1b21408c3a0eb6cb55c1c06c4f14c4/_Andrew/GITA/img/";

    public startup() {
        // starts timer on load, and adds key listener
        super("Button Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        addKeyListener(this);
        if (battle.sharedFolder) {
            if (new File(imageFolderLocation).exists()) {
                System.out.println("file exists");
            }
        } else {
            imageFolderLocation = "img/";
        }
        loadImages();
        //repaint();
    }

    public static void main(String[] args) {
        // Place components on the applet panel
        startup frame = new startup();
        // frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        // frame.setUndecorated(true);
        frame.pack();
        frame.setVisible(true);
    }

    // when you push the button it comes this method
    public void actionPerformed(ActionEvent event) {
        Object objSource = event.getSource();
        
    }

    public void launchBattleClassicMode() {
        battle battle = new battle();
        battle.factionFolder = "01_classic_rps";
        // battle.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        // battle.setVisible(true);
        battle.setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        battle.pack();
        // battle.setUndecorated(true);
        battle.setVisible(true);
        setVisible(false);
        dispose();
    }

    public void launchBattleHealthMode() {
        battle battle_health = new battle();
        battle_health.factionFolder = "01_classic_rps";
        // battle_health.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        // battle_health.setVisible(true);
        battle_health.setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        battle_health.pack();
        // battle_health.setUndecorated(true);
        battle_health.setVisible(true);
        setVisible(false);
        dispose();
    }

    public void keyPressed(KeyEvent e) {
        // captures keypress
        switch (e.getKeyCode()) {
            case 49:
                launchBattleClassicMode();
                break;
            case 50:
                launchBattleHealthMode();
                break;
        }
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void loadImages() {

        File fileFolder = new File(imageFolderLocation);
        File[] subFolders = (fileFolder.listFiles());
        Arrays.sort(subFolders);
        for (File imageFolder : subFolders) {
            //System.out.println(imageFolder.getName());
            getSprites(imageFolder.getName());
        }
    }

    public void getSprites(String folder) {
        int width = 40;
        int height = width;
        String imageFolder;
        
            imageFolder = "img/" + folder;
        if (new File(imageFolder).exists()) {
            System.out.println("file exists");
            File fileFolder = new File(imageFolder);
            if ((fileFolder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    if (name.startsWith("a_") || name.startsWith("b_") || name.startsWith("c_"))
                        return true;
                    return false;
                }
            })).length > 0) {
                Image tempImage = new ImageIcon(imageFolder + "/" + fileFolder.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.startsWith("a_");

                    }
                })[0].getName()).getImage();
                tempImage = tempImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                arrayImages.add(arrayImages.size(), tempImage);
                
                /* tempImage = new ImageIcon(imageFolder + "/" + fileFolder.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.startsWith("b_");
                    }
                })[0].getName()).getImage();
                tempImage = tempImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                arrayImages.add(arrayImages.size(), tempImage);

                tempImage = new ImageIcon(imageFolder + "/" + fileFolder.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        if(name.startsWith("c_"))System.out.println(imageFolder + "/" + name);
                        return name.startsWith("c_");
                    }
                })[0].getName()).getImage();
                tempImage = tempImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                arrayImages.add(arrayImages.size(), tempImage); */
            }
        }
    }

    public void paint(Graphics g) {
        offscreen = createImage(getSize().width, getSize().height);
        buffer = (Graphics2D) offscreen.getGraphics();
        buffer.setColor(new Color(0, 0, 0));
        buffer.fillRect(0, 0, getWidth(), getHeight());
        int x = 50;
        int y = 100;
        System.out.println(arrayImages.size());
        for (Image i : arrayImages) {
            buffer.drawImage(i, x, y, null);
            x += 50;
            if (x > getWidth() - 50) {
                x = 50;
                y += 50;
            }
        }

        buffer.drawRect(x, y, x, y);
        g.drawImage(offscreen, 0, 0, this);
    }

    public void Update(Graphics gr) {
        // call the paint method
        paint(gr);
    }
}
