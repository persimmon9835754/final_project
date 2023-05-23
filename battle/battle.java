package battle;

import javax.swing.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.Image;
/* import javax.imageio.ImageIO;
import java.io.IOException;
import java.lang.reflect.Array; */
import java.io.File;
import java.io.FilenameFilter;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import java.util.Scanner;
import java.io.FileNotFoundException;

// img file path: /mnt/chromeos/SMB/ed26bc8a3151bf71c2aa30ee422e94bb9a1b21408c3a0eb6cb55c1c06c4f14c4/_Andrew/GITA/
public class battle extends JFrame implements KeyListener, ActionListener {
    ArrayList<factions> teamA = new ArrayList<factions>();
    ArrayList<factions> teamB = new ArrayList<factions>();
    ArrayList<factions> teamC = new ArrayList<factions>();
    ArrayList<animation> animationImages = new ArrayList<animation>();
    public int frameRate = 12;
    Timer myTimer = new Timer(frameRate, this);
    private Graphics2D buffer;
    private Image offscreen;
    private boolean inputLeft;
    private boolean inputUp;
    private boolean inputDown;
    private boolean inputRight;
    private int unitCount = 20;
    private int heroX = 0;
    private int heroY = 0;
    Image imageTeamA;
    Image imageTeamB;
    Image imageTeamC;
    private int collisionRadius;
    private int defaultCooldown = 30;
    private double maximumSpeed = 2;
    private final int screenWidth = 1000;
    private final int screenHeight = 750;
    private int imageBoundsWidth;
    private int imageBoundsHeight;
    private int threeSecondStart = (int) (1000 / frameRate) * 3;
    public static int imageWidth;
    public static int imageHeight;
    String[][] teamStats = new String[3][9];
    public static String factionFolder = "01_classic_rps";
    public static String rps_battle_mode = "";

    public battle() {
        // starts timer on load, and adds key listener
        super("Button Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        addKeyListener(this);
        myTimer.start();
        java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font[] fonts = ge.getAllFonts();
        for (Font i : fonts) {
            // System.out.println(i.getName());
        }
        // your directory

        try {
            String imageFolder;

            imageFolder = "img/" + factionFolder;
            File fileFolder = new File(imageFolder);
            String filePathA = imageFolder + "/" + fileFolder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith("a_");
                }
            })[0].getName();
            String filePathB = imageFolder + "/" + fileFolder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith("b_");
                }
            })[0].getName();
            String filePathC = imageFolder + "/" + fileFolder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith("c_");
                }
            })[0].getName();
            imageTeamA = new ImageIcon(filePathA).getImage();
            imageTeamB = new ImageIcon(filePathB).getImage();
            imageTeamC = new ImageIcon(filePathC).getImage();

            /*
             * imageTeamB = ImageIO.read(fileFolder.listFiles(new FilenameFilter() {
             * public boolean accept(File dir, String name) {
             * return name.startsWith("b_");
             * }
             * })[0]);
             */

            File initializationFile = new File(imageFolder + "/config.ini");
            Scanner initilizationScanner = new Scanner(initializationFile);
            int lineCount = 0;
            String gameMode = "";
            while (initilizationScanner.hasNextLine()) {
                lineCount++;
                String data = initilizationScanner.nextLine();
                data = data.replaceAll("\s", "");
                String[] stringsOfInitValues = data.split(",");
                if (lineCount == 1) {
                    gameMode = data;
                } else if (lineCount <= 5 && lineCount >= 3) {
                    for (int i = 0; i < stringsOfInitValues.length; i++)
                        teamStats[lineCount - 3][i] = stringsOfInitValues[i];
                    teamStats[lineCount - 3][8] = gameMode;
                }
            }
            initilizationScanner.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        setup();
    }


    public void setup() {
        imageWidth = 40;
        imageHeight = 40;
        collisionRadius = 40;
        imageBoundsHeight = screenHeight- (imageHeight * 4);
        imageBoundsWidth = screenWidth - (imageWidth * 4);
        createTeams(unitCount, unitCount, unitCount);
        // imageTeamA = convertToBufferedImage(imageTeamA);
        imageTeamA = imageTeamA.getScaledInstance(imageWidth, imageHeight, Image.SCALE_DEFAULT);
        imageTeamB = imageTeamB.getScaledInstance(imageWidth, imageHeight, Image.SCALE_DEFAULT);
        imageTeamC = imageTeamC.getScaledInstance(imageWidth, imageHeight, Image.SCALE_DEFAULT);
    }

    // when you push the button it comes this method
    public void actionPerformed(ActionEvent event) {
        Object objSource = event.getSource();
        moveObjects();
        repaint();
        threeSecondStart--;
    }

    public static BufferedImage convertToBufferedImage(Image image) {
        BufferedImage newImage = new BufferedImage(40, 40,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }

    public BufferedImage dye(BufferedImage image, Color color) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage dyed = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dyed.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.setComposite(AlphaComposite.SrcAtop);
        g.setColor(color);
        g.fillRect(0, 0, w, h);
        g.dispose();
        return dyed;
    }

    public static synchronized void playSound(final String url) {
        
        // new Thread(new Runnable() {
        //     // The wrapper thread is unnecessary, unless it blocks on the
        //     // Clip finishing; see comments.
        //     public void run() {
        //         try {
        //             Clip clip = AudioSystem.getClip();
        //             AudioInputStream inputStream = AudioSystem.getAudioInputStream(
        //                     battle.class.getResourceAsStream("/sounds/" + url));
        //             clip.addLineListener(event -> {
        //                 if (LineEvent.Type.STOP.equals(event.getType())) {
        //                     clip.close();
        //                 }
        //             });
        //             clip.open(inputStream);
        //             clip.start();
        //         } catch (Exception e) {
        //             System.err.println(e.getMessage());
        //         }
        //     }
        // }).start();
    }

    public factions generateUnit(int health, int maxHealth, int attack_strong, int attack_weak, int shield,
            int maxShield, boolean regen, double maxSpeed) {
        // this generates a new unit, the faction is determined by which arraylist the
        // unit is stored in
        factions newUnit = new factions();
        newUnit.xCoord = (int) (Math.random() * imageBoundsWidth) + imageWidth * 2;
        newUnit.yCoord = (int) (Math.random() * imageBoundsHeight) + imageHeight/2;
        double x = Math.random() * 100 - 50;
        double y = Math.random() * 100 - 50;
        newUnit.maxSpeed = maxSpeed;
        newUnit.velX = getRandomDirection(x, 0, y, 0, newUnit.maxSpeed)[0];
        newUnit.velY = getRandomDirection(x, 0, y, 0, newUnit.maxSpeed)[1];
        newUnit.health = health;
        newUnit.maxHealth = maxHealth;
        newUnit.attack_strong = attack_strong;
        newUnit.attack_weak = attack_weak;
        newUnit.shield = shield;
        newUnit.maxShield = maxShield;
        newUnit.regen = regen;
        return newUnit;
    }

    public factions getUnit(int teamNumber) {
        return generateUnit(Integer.parseInt(teamStats[teamNumber][1]), Integer.parseInt(teamStats[teamNumber][1]),
                Integer.parseInt(teamStats[teamNumber][5]), Integer.parseInt(teamStats[teamNumber][4]),
                Integer.parseInt(teamStats[teamNumber][3]), Integer.parseInt(teamStats[teamNumber][3]),
                (Integer.parseInt(teamStats[teamNumber][2]) == 0) ? false : true,
                Double.parseDouble(teamStats[teamNumber][6]));
    }

    public void createTeams(int size1, int size2, int size3) {
        // health, maxHealth, attack_strong, attack_weak, shield, maxShield
        for (int i = 0; i < size1; i++) {
            teamA.add(teamA.size(), getUnit(0));
        }
        for (int i = 0; i < size2; i++) {
            teamB.add(teamB.size(), getUnit(1));
        }
        for (int i = 0; i < size3; i++) {
            teamC.add(teamC.size(), getUnit(2));
        }
        if (unitCount <= 25) {
            try {
                checkCollisions();
                while (teamA.size() != unitCount || teamB.size() != unitCount || teamC.size() != unitCount) {
                    while (teamA.size() > unitCount) {
                        teamA.remove(teamA.size() - 1);
                    }
                    while (teamB.size() > unitCount) {
                        teamB.remove(teamB.size() - 1);
                    }
                    while (teamC.size() > unitCount) {
                        teamC.remove(teamC.size() - 1);
                    }
                    while (teamA.size() < unitCount) {
                        teamA.add(teamA.size(), getUnit(0));
                    }
                    while (teamB.size() < unitCount) {
                        teamB.add(teamB.size(), getUnit(1));
                    }
                    while (teamC.size() < unitCount) {
                        teamC.add(teamC.size(), getUnit(2));
                    }
                    checkCollisions();
                }
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
    }

    public double[] getRandomDirection(double x1, double x2, double y1, double y2, double maxSpeed) {
        double newVelocity[] = new double[10];
        double xDist = (x1 - x2) * (Math.random() * .4 + 0.8);
        double yDist = (y1 - y2) * (Math.random() * .4 + 0.8);
        double slope = Math.sqrt(xDist * xDist + yDist * yDist);
        while (Math.abs(slope) > maxSpeed) {
            xDist *= 0.99;
            yDist *= 0.99;
            slope = Math.sqrt(xDist * xDist + yDist * yDist);
        }
        newVelocity[0] = xDist;
        newVelocity[1] = yDist;
        return newVelocity;
    }

    public void moveObjects() {
        heroX -= (inputLeft) ? 5 : 0;
        heroX += (inputRight) ? 5 : 0;
        heroY -= (inputUp) ? 5 : 0;
        heroY += (inputDown) ? 5 : 0;
        for (factions i : teamA) {
            i.xCoord += i.velX;
            i.yCoord += i.velY;
        }
        for (factions i : teamB) {
            i.xCoord += i.velX;
            i.yCoord += i.velY;
        }
        for (factions i : teamC) {
            i.xCoord += i.velX;
            i.yCoord += i.velY;
        }
        if (teamA.size() != (unitCount * 3) && teamB.size() != unitCount * 3 && teamC.size() != unitCount * 3) {
            checkCollisions();
        }
        for (int i = 0; i < animationImages.size(); i++) {
            animationImages.get(i).timeLeft--;
            if (animationImages.get(i).timeLeft <= 0) {
                animationImages.remove(i);
            }
        }
    }

    public factions transferProperties(factions tempFaction, factions oldFactions, factions newFactions) {
        tempFaction.xCoord = oldFactions.xCoord;
        tempFaction.yCoord = oldFactions.yCoord;
        tempFaction.attack_strong = newFactions.attack_strong;
        tempFaction.attack_weak = newFactions.attack_weak;
        tempFaction.shield = newFactions.shield;
        tempFaction.maxShield = newFactions.maxShield;
        tempFaction.health = newFactions.health;
        tempFaction.maxHealth = newFactions.maxHealth;
        tempFaction.regen = newFactions.regen;
        tempFaction.maxSpeed = newFactions.maxSpeed;
        return tempFaction;
    }

    public String randomSound() {
        int randomPopSound = (int) (Math.random() * 5) + 1;
        String soundFile = "pop" + randomPopSound + ".wav";
        return soundFile;
    }

    public void addWhiteImage(factions imageFactions, Image imageToDisplay) {
        animation tempAnimation = new animation();
        imageToDisplay = (Image) (dye(convertToBufferedImage(imageToDisplay), new Color(179, 179, 179, 255)));
        // - GRAY imageToDisplay = (Image) (dye(convertToBufferedImage(imageToDisplay),
        // new Color(77, 77, 77, 255)));
        // tempAnimation.createAnimation(imageFactions.xCoord, imageFactions.yCoord, 60,
        // imageToDisplay,
        // imageFactions.velX, imageFactions.velY, 1);
        tempAnimation.createAnimation(imageFactions.xCoord, imageFactions.yCoord, 10, imageToDisplay,
                0, 0, 1);
        animationImages.add(animationImages.size(), tempAnimation);
    }

    public void checkCollisions() {
        // bounces off the walls
        for (animation i : animationImages) {
            i.xCoord += i.velX;
            i.yCoord += i.velY;
            if (i.timeLeft == 5) {
                i.imageIcon = (Image) (dye(convertToBufferedImage(i.imageIcon), new Color(77, 77, 77, 255)));
            }
            /*
             * if (i.timeLeft < 30) {
             * i.opacity -= 1 / (0.15 * 1000 / frameRate);
             * if (i.opacity <= 0) {
             * i.opacity = 0;
             * }
             * }
             */
        }
        for (factions i : teamA) {
            // left and right wall collisions
            if (i.regen && i.health < i.maxHealth) {
                i.health += 1;
            }
            if (i.xCoord > screenWidth - (imageWidth / 2) || i.xCoord < (imageWidth / 2)) {
                i.velX *= -1;
                i.xCoord += i.velX;
            }
            if (i.yCoord > screenHeight - (imageHeight) || i.yCoord < (imageHeight / 2)) {
                i.velY *= -1;
                i.yCoord += i.velY;
            }
        }
        for (factions i : teamB) {
            if (i.regen && i.health < i.maxHealth) {
                i.health += 1;
            }
            if (i.xCoord > screenWidth - (imageWidth / 2) || i.xCoord < (imageWidth / 2)) {
                i.velX *= -1;
                i.xCoord += i.velX;
            }
            if (i.yCoord > screenHeight - (imageHeight) || i.yCoord < (imageHeight / 2)) {
                i.velY *= -1;
                i.yCoord += i.velY;
            }
        }
        for (factions i : teamC) {
            if (i.regen && i.health < i.maxHealth) {
                i.health += 1;
            }
            if (i.xCoord > screenWidth - (imageWidth / 2) || i.xCoord < (imageWidth / 2)) {
                i.velX *= -1;
                i.xCoord += i.velX;
            }
            if (i.yCoord > screenHeight - (imageHeight) || i.yCoord < (imageHeight / 2)) {
                i.velY *= -1;
                i.yCoord += i.velY;
            }
            
        }
        if (threeSecondStart < 0) {
            for (factions a : teamA) {
                for (factions b : teamB) {
                    if (Math.abs(a.xCoord - b.xCoord) < collisionRadius
                            && Math.abs(a.yCoord - b.yCoord) < collisionRadius) {
                        int damage_from_B = b.attack_weak - a.shield;
                        int damage_from_A = a.attack_strong - b.shield;
                        if (damage_from_A <= 0) {
                            b.shield -= a.attack_strong;
                        } else {
                            b.health -= damage_from_A;
                        }
                        if (damage_from_B <= 0) {
                            a.shield -= b.attack_weak;
                        } else {
                            a.health -= damage_from_B;
                        }
                        factions tempA = new factions();
                        tempA.velX = getRandomDirection(a.xCoord, b.xCoord, a.yCoord, b.yCoord, a.maxSpeed)[0];
                        tempA.velY = getRandomDirection(a.xCoord, b.xCoord, a.yCoord, b.yCoord, a.maxSpeed)[1];
                        tempA = transferProperties(tempA, a, b);
                        factions tempB = new factions();
                        tempB.velX = getRandomDirection(b.xCoord, a.xCoord, b.yCoord, a.yCoord, b.maxSpeed)[0];
                        tempB.velY = getRandomDirection(b.xCoord, a.xCoord, b.yCoord, a.yCoord, b.maxSpeed)[1];
                        tempB = transferProperties(tempB, b, a);
                        if (a.health <= 0) {
                            addWhiteImage(a, imageTeamA);
                            teamA.remove(a);
                            tempA.shield = tempA.maxShield;
                            tempA.health = tempA.maxHealth;
                            teamB.add(teamB.size(), tempA);
                        }
                        if (b.health <= 0) {
                            addWhiteImage(b, imageTeamB);
                            teamB.remove(b);
                            tempB.shield = tempB.maxShield;
                            tempB.health = tempB.maxHealth;
                            teamA.add(teamA.size(), tempB);
                        }
                        playSound(randomSound());
                        return;
                    }
                }
            }
            for (factions a : teamA) {
                for (factions c : teamC) {
                    if (Math.abs(a.xCoord - c.xCoord) < collisionRadius
                            && Math.abs(a.yCoord - c.yCoord) < collisionRadius) {
                        int damage_from_C = c.attack_strong - a.shield;
                        int damage_from_A = a.attack_weak - c.shield;
                        if (damage_from_A <= 0) {
                            c.shield -= a.attack_weak;
                        } else {
                            c.health -= damage_from_A;
                        }
                        if (damage_from_C <= 0) {
                            a.shield -= c.attack_strong;
                        } else {
                            a.health -= damage_from_C;
                        }
                        factions tempA = new factions();
                        tempA.velX = getRandomDirection(a.xCoord, c.xCoord, a.yCoord, c.yCoord, a.maxSpeed)[0];
                        tempA.velY = getRandomDirection(a.xCoord, c.xCoord, a.yCoord, c.yCoord, a.maxSpeed)[1];
                        tempA = transferProperties(tempA, a, c);
                        factions tempC = new factions();
                        tempC.velX = getRandomDirection(c.xCoord, a.xCoord, c.yCoord, a.yCoord, c.maxSpeed)[0];
                        tempC.velY = getRandomDirection(c.xCoord, a.xCoord, c.yCoord, a.yCoord, c.maxSpeed)[1];
                        tempC = transferProperties(tempC, c, a);
                        if (a.health <= 0) {
                            addWhiteImage(a, imageTeamA);
                            teamA.remove(a);
                            tempA.shield = tempA.maxShield;
                            tempA.health = tempA.maxHealth;
                            teamC.add(teamC.size(), tempA);
                        }
                        if (c.health <= 0) {
                            addWhiteImage(c, imageTeamC);
                            teamC.remove(c);
                            tempC.shield = tempC.maxShield;
                            tempC.health = tempC.maxHealth;
                            teamA.add(teamA.size(), tempC);
                        }
                        playSound(randomSound());
                        return;
                    }
                }
            }
            for (factions c : teamC) {
                for (factions b : teamB) {
                    if (Math.abs(c.xCoord - b.xCoord) < collisionRadius
                            && Math.abs(c.yCoord - b.yCoord) < collisionRadius) {
                        int damage_from_C = c.attack_weak - b.shield;
                        int damage_from_B = b.attack_strong - c.shield;
                        if (damage_from_B <= 0) {
                            c.shield -= b.attack_strong;
                        } else {
                            c.health -= damage_from_B;
                        }
                        if (damage_from_C <= 0) {
                            b.shield -= c.attack_weak;
                        } else {
                            b.health -= damage_from_C;
                        }
                        factions tempB = new factions();
                        tempB.velX = getRandomDirection(b.xCoord, c.xCoord, b.yCoord, c.yCoord, b.maxSpeed)[0];
                        tempB.velY = getRandomDirection(b.xCoord, c.xCoord, b.yCoord, c.yCoord, b.maxSpeed)[1];
                        tempB = transferProperties(tempB, b, c);
                        factions tempC = new factions();
                        tempC.velX = getRandomDirection(c.xCoord, b.xCoord, c.yCoord, b.yCoord, c.maxSpeed)[0];
                        tempC.velY = getRandomDirection(c.xCoord, b.xCoord, c.yCoord, b.yCoord, c.maxSpeed)[1];
                        tempC = transferProperties(tempC, c, b);
                        if (b.health <= 0) {
                            addWhiteImage(b, imageTeamB);
                            teamB.remove(b);
                            tempB.shield = tempB.maxShield;
                            tempB.health = tempB.maxHealth;
                            teamC.add(teamC.size(), tempB);
                        }
                        if (c.health <= 0) {
                            addWhiteImage(c, imageTeamC);
                            teamC.remove(c);
                            tempC.shield = tempC.maxShield;
                            tempC.health = tempC.maxHealth;
                            teamB.add(teamB.size(), tempC);
                        }
                        playSound(randomSound());
                        return;
                    }
                }
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        // captures keypress
        switch (e.getKeyCode()) {
            case 37:
                inputLeft = true;
                break;
            case 39:
                inputRight = true;
                break;
            case 38:
                inputUp = true;
                break;
            case 40:
                inputDown = true;
                break;
            case 32:
                if (myTimer.isRunning())
                    myTimer.stop();
                else
                    myTimer.start();
        }
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case 37:
                inputLeft = false;
                break;
            case 39:
                inputRight = false;
                break;
            case 38:
                inputUp = false;
                break;
            case 40:
                inputDown = false;
                break;
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    // create the paint method to show graphics
    public void paint(Graphics g) {
        double bar_width_scale = 0.4;
        int bar_height = 3;
        int bar_pos_x = 18;
        int bar_pos_y = 40;
        int shield_bar_pos_y = 44;
        offscreen = createImage(screenWidth, screenHeight);
        buffer = (Graphics2D) offscreen.getGraphics();
        buffer.setColor(new Color(0, 0, 0));
        buffer.fillRect(0, 0, screenWidth, screenHeight);
        for (animation i : animationImages) {
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, i.opacity);
            buffer.setComposite(ac);
            int x = (int) i.xCoord;
            int y = (int) i.yCoord;
            buffer.drawImage(i.imageIcon, x - (imageWidth / 2), y - (imageHeight / 2), null);
        }
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
        buffer.setComposite(ac);
        for (factions i : teamA) {
            int x = (int) i.xCoord;
            int y = (int) i.yCoord;
            buffer.drawImage(imageTeamA, x - (imageWidth / 2), y - (imageHeight / 2), null);
            if (rps_battle_mode == "custom") {
                buffer.setColor(Color.red);
                buffer.fillRect(x - bar_pos_x, y + bar_pos_y, (int) (i.health * bar_width_scale), bar_height);
                if (i.shield > 0) {
                    buffer.setColor(new Color(0, 255, 0, 80));
                    buffer.fillRect(x - bar_pos_x, y + shield_bar_pos_y, (int) (i.shield * bar_width_scale),
                            bar_height);
                }
            }
        }
        for (factions i : teamB) {
            int x = (int) i.xCoord;
            int y = (int) i.yCoord;
            buffer.drawImage(imageTeamB, x - (imageWidth / 2), y - (imageHeight / 2), null);
            if (rps_battle_mode == "custom") {
                buffer.setColor(Color.red);
                buffer.fillRect(x - bar_pos_x, y + bar_pos_y, (int) (i.health * bar_width_scale), bar_height);
                if (i.shield > 0) {
                    buffer.setColor(new Color(0, 255, 0, 80));
                    buffer.fillRect(x - bar_pos_x, y + shield_bar_pos_y, (int) (i.shield * bar_width_scale),
                            bar_height);
                }
            }
        }
        for (factions i : teamC) {
            int x = (int) i.xCoord;
            int y = (int) i.yCoord;
            buffer.drawImage(imageTeamC, x - (imageWidth / 2), y - (imageHeight / 2), null);
            if (rps_battle_mode == "custom") {
                buffer.setColor(Color.red);
                buffer.fillRect(x - bar_pos_x, y + bar_pos_y, (int) (i.health * bar_width_scale), bar_height);
                if (i.shield > 0) {
                    buffer.setColor(new Color(0, 255, 0, 80));
                    buffer.fillRect(x - bar_pos_x, y + shield_bar_pos_y, (int) (i.shield * bar_width_scale),
                            bar_height);
                }
            }
        }
        buffer.drawImage(imageTeamA, heroX, heroY, null);
        buffer.setColor(Color.red);
        buffer.setFont(new Font("Comic Sans MS", Font.PLAIN, 50));
        String timeLeft = String.valueOf(threeSecondStart / 60);
        if (threeSecondStart / 60 >= 0.0)
            buffer.drawString("Starts in " + timeLeft, screenWidth/ 2 - 100, screenHeight / 2);
        g.drawImage(offscreen, 0, 0, this);
    }

    public void Update(Graphics gr) {
        // call th
    }
    // buffer.setColor(Color.red);
    //
    // buffer.drawRect( x - (imageWidth / 2), y - (imageHeight / 2), imageWidth,
    // imageHeight);
}