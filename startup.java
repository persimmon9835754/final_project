import javax.swing.*;
import javax.swing.text.DefaultEditorKit.CopyAction;
//import javax.xml.xpath.XPathEvaluationResult;
import battle.battle;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.event.*;
import java.awt.Image;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.awt.FontMetrics;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;

public class startup extends JFrame implements KeyListener, ActionListener, MouseListener {
    private Graphics2D buffer;
    private Image offscreen;
    final static int FRAME_WIDTH = 1000;
    final static int FRAME_HEIGHT = 750;
    public static boolean sharedFolder = true;
    ArrayList<Image> arrayImages = new ArrayList<Image>();
    ArrayList<Integer> displayImages = new ArrayList<Integer>();
    ArrayList<String> arrayFilePaths = new ArrayList<String>();
    String imageFolderLocation;
    int btnIncreaseSize_x = 500;
    int btnIncreaseSize_y = 500;
    boolean btnIncreaseSize = false;
    boolean btnDecreaseSize = false;
    Timer animateTimer = new Timer(16, this);
    int mouseX;
    int mouseY;
    Boolean customMode = false;
    int unitCount = 10;
    double unitSpeed = 5.0;
    int teamA_Avatar = -4;
    int teamB_Avatar = -3;
    int teamC_Avatar = -5;
    ArrayList<String> teamNames = new ArrayList<String>();
    ArrayList<String> teamDescriptions = new ArrayList<String>();
    String selectedTeam = "";

    public startup() {
        // starts timer on load, and adds key listener
        super("Button Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        addKeyListener(this);
        addMouseListener(this);
        makeMouseOverListener();
        // startup.addMouseMotionListener();
        imageFolderLocation = "img/";
        loadImages();
        animateTimer.start();
        repaint();
        System.out.println(getWidth() + ", " + getHeight());
        getNewdisplayImages();
        // Transparent 16 x 16 pixel cursor image.
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        // Create a new blank cursor.
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");

        // Set the blank cursor to the JFrame.
        this.getContentPane().setCursor(blankCursor);
    }

    public void makeMouseOverListener() {
        System.out.println("Inside make mouseover...");
        MouseMotionListener ret = new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }

            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        };
        this.addMouseMotionListener(ret);
    }

    public static void main(String[] args) {
        // Place components on the applet panel
        startup frame = new startup();
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setVisible(true);
    }

    public void getNewdisplayImages() {
        displayImages.clear();
        ArrayList<Integer> tempImages = new ArrayList<Integer>();
        for (int i = 0; i < arrayImages.size(); i++) {
            tempImages.add(i);
        }
        for (int i = 0; i < 12; i++) {
            int randomImage = (int) (Math.random() * tempImages.size());
            displayImages.add(tempImages.get(randomImage));
            tempImages.remove(tempImages.get(randomImage));
        }
        teamA_Avatar = -1;
        teamB_Avatar = -1;
        teamC_Avatar = -1;
    }

    // when you push the button it comes this method
    public void actionPerformed(ActionEvent event) {
        Object objSource = event.getSource();
        repaint();
    }

    public void launchBattleClassicMode() {
        setModes("01_classic_rps", 25, 1);
        battle battle = new battle();
        battle.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        battle.setVisible(true);
        setVisible(false);
        dispose();
    }

    public static void setModes(String mode, int numUnits, double speedUnit) {
        battle.factionFolder = mode;
        battle.unitCount = numUnits;
    }

    public void launchBattleHealthMode() {
        setModes("12_college_clash", 25, 1);
        battle battle_health = new battle();
        battle_health.setSize(FRAME_WIDTH, FRAME_HEIGHT);
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
            case 32:
                getNewdisplayImages();
                repaint();
                break;
        }
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        // increase unit count
        if (mouseX > 760 - (mouseY - 130) && mouseX < 760 + 60 + (mouseY - 130) && mouseY > 130 - 30 && mouseY < 130
                && unitCount < 40)
            unitCount++;
        // decrease unit count
        if (mouseX > 760 + (mouseY - 190) && mouseX < 760 + 60 - (mouseY - 190) && mouseY < 190 + 30 && mouseY > 190
                && unitCount > 1)
            unitCount--;
        // increase unit speed
        if (mouseX > 760 - (mouseY - 280) && mouseX < 760 + 60 + (mouseY - 280) && mouseY > 280 - 30 && mouseY < 280
                && unitSpeed < 10.0)
            unitSpeed += 0.5;
        // decrease unit speed
        if (mouseX > 760 + (mouseY - 340) && mouseX < 760 + 60 - (mouseY - 340) && mouseY < 340 + 30 && mouseY > 340
                && unitSpeed > 1)
            unitSpeed -= 0.5;
        // avatar tables
        // 1,1
        if (mouseX > 205 && mouseX < 295 && mouseY < 590 && mouseY > 500)
            populateWith(0);
        if (mouseX > 305 && mouseX < 395 && mouseY < 590 && mouseY > 500)
            populateWith(1);
        if (mouseX > 405 && mouseX < 495 && mouseY < 590 && mouseY > 500)
            populateWith(2);
        if (mouseX > 505 && mouseX < 595 && mouseY < 590 && mouseY > 500)
            populateWith(3);
        if (mouseX > 605 && mouseX < 695 && mouseY < 590 && mouseY > 500)
            populateWith(4);
        if (mouseX > 705 && mouseX < 795 && mouseY < 590 && mouseY > 500)
            populateWith(5);
        if (mouseX > 205 && mouseX < 295 && mouseY < 690 && mouseY > 600)
            populateWith(6);
        if (mouseX > 305 && mouseX < 395 && mouseY < 690 && mouseY > 600)
            populateWith(7);
        if (mouseX > 405 && mouseX < 495 && mouseY < 690 && mouseY > 600)
            populateWith(8);
        if (mouseX > 505 && mouseX < 595 && mouseY < 690 && mouseY > 600)
            populateWith(9);
        if (mouseX > 605 && mouseX < 695 && mouseY < 690 && mouseY > 600)
            populateWith(10);
        if (mouseX > 705 && mouseX < 795 && mouseY < 690 && mouseY > 600)
            populateWith(11);
        if (mouseX > 0 && mouseX < 160 && mouseY > 70 && mouseY < 105)
            getNewdisplayImages();
        if (mouseX > 0 && mouseX < 160 && mouseY > 110 && mouseY < 145)
            generatePreset(1);
        if (mouseX > 0 && mouseX < 160 && mouseY > 150 && mouseY < 195)
            generatePreset(2);
        if (mouseX > 0 && mouseX < 160 && mouseY > 190 && mouseY < 235)
            generatePreset(3);
        if (mouseX > 0 && mouseX < 160 && mouseY > 230 && mouseY < 275)
            generatePreset(4);
        if (mouseX > 0 && mouseX < 160 && mouseY > 270 && mouseY < 315)
            generatePreset(5);
        if (mouseX > 0 && mouseX < 160 && mouseY > 310 && mouseY < 355)
            generatePreset(6);
        if (mouseX > 0 && mouseX < 160 && mouseY > 350 && mouseY < 395)
            generatePreset(7);
        if (mouseX > 0 && mouseX < 160 && mouseY > 390 && mouseY < 445)
            generatePreset(8);
        if (mouseX > 0 && mouseX < 160 && mouseY > 430 && mouseY < 485)
            generatePreset(9);
        if (mouseX > 0 && mouseX < 160 && mouseY > 470 && mouseY < 525)
            generatePreset(10);
        if (mouseX > 0 && mouseX < 160 && mouseY > 510 && mouseY < 565)
            generatePreset(11);
        if (mouseX > 0 && mouseX < 160 && mouseY > 550 && mouseY < 605)
            generatePreset(12);
        if (mouseX > 0 && mouseX < 160 && mouseY > 590 && mouseY < 645)
            generatePreset(13);
        if (mouseX > 0 && mouseX < 160 && mouseY > 630 && mouseY < 685)
            generatePreset(14);
        if (mouseX > 0 && mouseX < 160 && mouseY > 670 && mouseY < 725)
            generatePreset(15);
    }

    public void populateWith(int index) {
        if (teamA_Avatar < 0) {
            teamA_Avatar = displayImages.get(index);
        } else if (teamB_Avatar < 0 && displayImages.get(index) != teamA_Avatar) {
            teamB_Avatar = displayImages.get(index);
        } else if (teamC_Avatar < 0 && displayImages.get(index) != teamA_Avatar
                && displayImages.get(index) != teamB_Avatar) {
            teamC_Avatar = displayImages.get(index);
        } else if (teamA_Avatar >= 0 && teamB_Avatar >= 0 && teamC_Avatar >= 0) {
            teamA_Avatar = -1;
            teamB_Avatar = -1;
            teamC_Avatar = -1;
        }
    }

    public void generatePreset(int index) {
        System.out.println(index);
        int imagePos = (index - 1) * 3;
        teamA_Avatar = imagePos;
        teamB_Avatar = imagePos + 1;
        teamC_Avatar = imagePos + 2;
        selectedTeam = teamDescriptions.get(index - 1);
        System.out.println(selectedTeam);
        
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void loadImages() {
        File fileFolder = new File(imageFolderLocation);
        File[] subFolders = (fileFolder.listFiles());
        Arrays.sort(subFolders);
        for (File imageFolder : subFolders) {
            // System.out.println(imageFolder.getName());
            // System.out.println(imageFolder.getAbsolutePath());
            if (String.valueOf(imageFolder.getName()) != ".SynologyWorkingDirectory") {

                getSprites(imageFolder.getName());
            }
        }
        for (int i = 0; i < arrayFilePaths.size(); i++) {
            arrayImages.add(new ImageIcon(arrayFilePaths.get(i)).getImage());
            arrayImages.set(i, arrayImages.get(i).getScaledInstance(90, 90, Image.SCALE_SMOOTH));
        }

    }

    public void getSprites(String folder) {
        if (!folder.equals(".SynologyWorkingDirectory")) {
            String imageFolder;
            imageFolder = "img/" + folder;
            if (new File(imageFolder).exists()) {
                try {
                    File initializationFile = new File(imageFolder + "/config.ini");
                    Scanner initilizationScanner = new Scanner(initializationFile);
                    int lineCount = 0;
                    while (initilizationScanner.hasNextLine()) {
                        lineCount++;
                        String data = initilizationScanner.nextLine();
                        if (lineCount == 1) {
                            teamNames.add(data);
                        }
                        if (lineCount == 2) {
                            teamDescriptions.add(data);
                        }

                    }
                    initilizationScanner.close();
                    File fileFolder = new File(imageFolder);
                    if ((fileFolder.listFiles(new FilenameFilter() {
                        public boolean accept(File dir, String name) {
                            if (name.startsWith("a_") || name.startsWith("b_") || name.startsWith("c_"))
                                return true;
                            return false;
                        }
                    })).length > 0) {
                        arrayFilePaths.add(imageFolder + "/" + fileFolder.listFiles(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.startsWith("a_");
                            }
                        })[0].getName());
                        arrayFilePaths.add(imageFolder + "/" + fileFolder.listFiles(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.startsWith("b_");
                            }
                        })[0].getName());
                        arrayFilePaths.add(imageFolder + "/" + fileFolder.listFiles(new FilenameFilter() {
                            public boolean accept(File dir, String name) {
                                return name.startsWith("c_");
                            }
                        })[0].getName());
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }
        }
    }

    public void paint(Graphics g) {
        offscreen = createImage(1000, 750);
        buffer = (Graphics2D) offscreen.getGraphics();
        buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        buffer.setColor(new Color(0, 0, 0));
        buffer.fillRect(0, 0, 1000, 750);
        int x = 50;
        int y = 100;
        // System.out.println(arrayImages.size());
        for (String i : arrayFilePaths) {
            // System.out.println(i);
        }

        // start button
        buffer.setColor(Color.red);
        buffer.fillRect(500 - 110, 440 - 30, 220, 60);
        buffer.setColor(Color.black);
        buffer.setFont(new Font("Comic Sans MS", Font.BOLD, 38));
        buffer.drawString("START", 435, 455);
        if (mouseX > 390 && mouseX < 610 && mouseY > 410 && mouseY < 470) {
            buffer.setColor(Color.white);
            buffer.setStroke(new BasicStroke(3));
            buffer.drawRect(500 - 112, 440 - 30, 224, 60);
        }
        buffer.setColor(Color.gray);
        buffer.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        int stringLength = (int) (buffer.getFontMetrics().getStringBounds(selectedTeam, buffer)
                .getWidth());
        int start = 500 - stringLength / 2;
        buffer.drawString(selectedTeam, start, 390);
        // background gradient
        buffer.setColor(Color.lightGray);
        int displayImageX = 250;
        int displayImageY = 500;
        buffer.setStroke(new BasicStroke(5));
        Color endColor = new Color(42, 82, 162);
        Color startColor = new Color(0, 179, 179);
        int startX = displayImageX - 50, startY = displayImageY + 50, endX = displayImageX,
                endY = displayImageY + 200;
        GradientPaint gradient = new GradientPaint(startX, startY, startColor, endX, endY, endColor);
        buffer.setPaint(gradient);
        buffer.fillRect(500 - 102 * 3, 595 - 105, 102 * 6, 105 * 2);
        // draw 12 random images
        for (int i = 0; i < 12; i++) {
            buffer.setColor(new Color(148, 148, 184));
            buffer.setColor(Color.lightGray);
            buffer.fillRect(displayImageX - 45, displayImageY, 90, 90);
            if (mouseX > 205 && mouseX < 795 && mouseY > 500 && mouseY < 690) {
                int xpos = (mouseX - 205) / 100;
                int ypos = (mouseY - 500) / 100;
                int id = ypos * 6 + xpos;
                if (id == i) {
                    buffer.setColor(Color.white);
                    buffer.drawRect(displayImageX - 45, displayImageY, 90, 90);
                }
            }
            BufferedImage resized = new BufferedImage(70, 70, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = resized.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(arrayImages.get(displayImages.get(i)), 0, 0, 70, 70, 0, 0, 90,
                    90, null);
            g2.dispose();
            buffer.drawImage(resized, displayImageX - 35, displayImageY + 10,
                    null);
            displayImageX += 100;
            if (i == 5) {
                displayImageX = 250;
                displayImageY = 600;
            }
        }
        // preset modes
        buffer.setColor(Color.gray);
        x = 0;
        y = 70;
        buffer.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        for (int i = 0; i < 16; i++) {
            buffer.fillRect(x, y, 160, 35);
            buffer.setColor(Color.white);
            if (i == 0) {
                stringLength = (int) (buffer.getFontMetrics().getStringBounds("Custom", buffer)
                        .getWidth());
                start = 160 / 2 - stringLength / 2;
                buffer.drawString("Custom", x + start, y + 25);
            } else {
                stringLength = (int) (buffer.getFontMetrics().getStringBounds(teamNames.get(i - 1), buffer)
                        .getWidth());
                start = 160 / 2 - stringLength / 2;
                buffer.drawString(teamNames.get(i - 1), x + start, y + 25);

            }
            buffer.setColor(Color.gray);
            y += 40;
        }
        if (mouseX > 0 && mouseX < 160 && mouseY > 70 && mouseY < 710) {
            int ypos = (mouseY - 70) / 40;
            buffer.setColor(Color.white);
            buffer.drawRect(0 - 1, 70 + ypos * 40 - 1, 162, 37);
        }
        // unit count button
        buffer.setStroke(new BasicStroke(2));
        x = 760;
        y = 130;
        buffer.setColor(new Color(204, 51, 0));
        Path2D.Double path = new Path2D.Double();
        path.moveTo(x, y);
        path.lineTo(x + 60, y);
        path.lineTo(x + 30, y - 30);
        path.lineTo(x, y);
        path.closePath();
        if (mouseX > x - (mouseY - y) && mouseX < x + 60 + (mouseY - y) && mouseY > y - 30 && mouseY < y)
            buffer.fill(path);
        else
            buffer.draw(path);
        buffer.setColor(Color.lightGray);
        y += 10;
        buffer.fillRect(x - 2, y - 2, 64, 44);
        buffer.setColor(Color.black);
        buffer.fillRect(x + 3, y + 3, 54, 34);
        buffer.setColor(new Color(204, 51, 0));
        y += 50;
        path.reset();
        path.moveTo(x, y);
        path.lineTo(x + 60, y);
        path.lineTo(x + 30, y + 30);
        path.lineTo(x, y);
        path.closePath();
        if (mouseX > x + (mouseY - y) && mouseX < x + 60 - (mouseY - y) && mouseY < y + 30 && mouseY > y)
            buffer.fill(path);
        else
            buffer.draw(path);
        buffer.setColor(Color.white);
        buffer.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
        if (unitCount < 10)
            buffer.drawString(String.valueOf(unitCount), x + 25, y - 22);
        else
            buffer.drawString(String.valueOf(unitCount), x + 15, y - 22);
        buffer.drawString("Unit Count", x + 70, y - 22);
        x = 760;
        y = 280;
        buffer.setColor(Color.lightGray);
        path.reset();
        path.moveTo(x, y);
        path.lineTo(x + 60, y);
        path.lineTo(x + 30, y - 30);
        path.lineTo(x, y);
        path.closePath();
        if (mouseX > x - (mouseY - y) && mouseX < x + 60 + (mouseY - y) && mouseY > y - 30 && mouseY < y)
            buffer.fill(path);
        else
            buffer.draw(path);
        // unit speed buttons
        buffer.setColor(Color.lightGray);
        y += 10;
        buffer.fillRect(x - 2, y - 2, 64, 44);
        buffer.setColor(Color.black);
        buffer.fillRect(x + 3, y + 3, 54, 34);
        buffer.setColor(Color.lightGray);
        y += 50;
        path.reset();
        path.moveTo(x, y);
        path.lineTo(x + 60, y);
        path.lineTo(x + 30, y + 30);
        path.lineTo(x, y);
        path.closePath();
        if (mouseX > x + (mouseY - y) && mouseX < x + 60 - (mouseY - y) && mouseY < y + 30 && mouseY > y)
            buffer.fill(path);
        else
            buffer.draw(path);
        buffer.setColor(Color.white);
        buffer.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
        if (unitSpeed < 10)
            buffer.drawString(String.valueOf(unitSpeed), x + 12, y - 22);
        else
            buffer.drawString(String.valueOf(unitSpeed), x + 5, y - 22);
        buffer.drawString("Unit Speed", x + 70, y - 22);
        // main 3 teams
        buffer.setColor(Color.white);
        buffer.setStroke(new BasicStroke(7));
        buffer.drawOval(500 - 60, 104 - 60, 120, 120);
        buffer.drawOval(380 - 60, 290 - 60, 120, 120);
        buffer.drawOval(620 - 60, 290 - 60, 120, 120);
        // arrows
        x = 460;
        y = 300;
        path.reset();
        path.moveTo(x, y);
        path.lineTo(x, y - 6);
        path.lineTo(x + 50, y - 6);
        path.lineTo(x + 50, y - 15);
        path.lineTo(x + 80, y);
        path.lineTo(x + 50, y + 15);
        path.lineTo(x + 50, y + 6);
        path.lineTo(x, y + 6);
        path.lineTo(x, y);
        path.closePath();
        buffer.fill(path);

        x = 565;
        y = 215;
        path.reset();
        path.moveTo(x, y);
        path.lineTo(x, y - 6);
        path.lineTo(x + 50, y - 6);
        path.lineTo(x + 50, y - 15);
        path.lineTo(x + 80, y);
        path.lineTo(x + 50, y + 15);
        path.lineTo(x + 50, y + 6);
        path.lineTo(x, y + 6);
        path.lineTo(x, y);
        AffineTransform rotation = AffineTransform.getRotateInstance(Math.toRadians(230), 580, 215);
        path.transform(rotation);
        path.closePath();
        buffer.fill(path);
        // arrow 2
        x = 430;
        y = 170;
        path.reset();
        path.moveTo(x, y);
        path.lineTo(x, y - 6);
        path.lineTo(x + 50, y - 6);
        path.lineTo(x + 50, y - 15);
        path.lineTo(x + 80, y);
        path.lineTo(x + 50, y + 15);
        path.lineTo(x + 50, y + 6);
        path.lineTo(x, y + 6);
        path.lineTo(x, y);
        rotation = AffineTransform.getRotateInstance(Math.toRadians(125), 440, 170);
        path.transform(rotation);
        path.closePath();
        buffer.fill(path);

        // buffer.drawImage(tempImage, 560, 180, null);

        if (teamA_Avatar >= 0)
            buffer.drawImage(arrayImages.get(teamA_Avatar), 500 - 45, 104 - 45, 90, 90, null);
        if (teamB_Avatar >= 0)
            buffer.drawImage(arrayImages.get(teamB_Avatar), 380 - 45, 290 - 45, 90, 90, null);
        if (teamC_Avatar >= 0)
            buffer.drawImage(arrayImages.get(teamC_Avatar), 620 - 45, 290 - 45, 90, 90, null);
        buffer.setColor(Color.green);
        buffer.drawOval(mouseX, mouseY, 5, 5);
        g.drawImage(offscreen, 0, 0, this);
    }

    public void Update(Graphics gr) {
        // call the paint method
        paint(gr);
    }
}
