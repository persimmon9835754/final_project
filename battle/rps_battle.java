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
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;
import java.io.FilenameFilter;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;

public class rps_battle extends JFrame implements KeyListener, ActionListener {
    ArrayList<factions> teamA = new ArrayList<factions>();
    ArrayList<factions> teamB = new ArrayList<factions>();
    ArrayList<factions> teamC = new ArrayList<factions>();
    Timer myTimer = new Timer(16, this);
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
    private int screenWidth;
    private int screenHeight;
    private int threeSecondStart = 270;
    public static String rps_battle_mode = "";
    public static String teamA_unit = "";
    public static String teamB_unit = "";
    public static String teamC_unit = "";
    public static int imageWidth;
    public static int imageHeight;

    public rps_battle() {
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
            String imageFolder = "img/water-fire-tree";
            File fileFolder = new File(imageFolder);
            imageTeamA = ImageIO.read(fileFolder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith("a-");
                }
            })[0]);
            imageTeamB = ImageIO.read(fileFolder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith("b-");
                }
            })[0]);
            imageTeamC = ImageIO.read(fileFolder.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.startsWith("c-");
                }
            })[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toolkit tk = Toolkit.getDefaultToolkit();
        imageWidth = tk.getScreenSize().width / 25;
        imageHeight = imageWidth;
        collisionRadius = imageWidth;
        screenHeight = tk.getScreenSize().height - (imageHeight * 4);
        screenWidth = tk.getScreenSize().width - (imageWidth * 4);
        createTeams(unitCount, unitCount, unitCount);
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

    public void setMode(String mode) {
        this.rps_battle_mode = mode;
    }

    public static synchronized void playSound(final String url) {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                            rps_battle.class.getResourceAsStream("/sounds/" + url));
                            clip.addLineListener(event -> {
                                if(LineEvent.Type.STOP.equals(event.getType())) {
                                    clip.close();
                                }
                            });
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }

    public factions generateUnit(int health, int maxHealth, int attack_strong, int attack_weak, int shield,
            int maxShield) {
        // this generates a new unit, the faction is determined by which arraylist the
        // unit is stored in
        factions newUnit = new factions();
        newUnit.xCoord = (int) (Math.random() * screenWidth) + imageWidth * 2;
        newUnit.yCoord = (int) (Math.random() * screenHeight) + imageHeight * 2;
        double x = Math.random() * 100 - 50;
        double y = Math.random() * 100 - 50;
        newUnit.velX = getRandomDirection(x, 0, y, 0)[0];
        newUnit.velY = getRandomDirection(x, 0, y, 0)[1];
        newUnit.health = health;
        newUnit.maxHealth = maxHealth;
        newUnit.attack_strong = attack_strong;
        newUnit.attack_weak = attack_weak;
        newUnit.shield = shield;
        newUnit.maxShield = maxShield;
        return newUnit;
    }

    public factions getUnit(String type) {
        switch (type) {
            case "classic":
                return generateUnit(100, 100, 100, 0, 0, 0);
            case "chicken":
                return generateUnit(50, 50, 75, 75, 200, 200);
            case "custom":
                return generateUnit(100, 100, 70, 20, 100, 100);
        }
        return generateUnit(100, 100, 100, 0, 0, 0);
    }

    public void createTeams(int size1, int size2, int size3) {
        // health, maxHealth, attack_strong, attack_weak, shield, maxShield
        for (int i = 0; i < size1; i++) {
            teamA.add(teamA.size(), getUnit(teamA_unit));
        }
        for (int i = 0; i < size2; i++) {
            teamB.add(teamB.size(), getUnit(teamB_unit));

        }
        for (int i = 0; i < size3; i++) {
            teamC.add(teamC.size(), getUnit(teamC_unit));
        }
        System.out.println(
                "Number of units before collisions: " + teamA.size() + " " + teamB.size() + " " + teamC.size());
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
                        teamA.add(teamA.size(), getUnit(teamA_unit));
                    }
                    while (teamB.size() < unitCount) {
                        teamB.add(teamB.size(), getUnit(teamB_unit));
                    }
                    while (teamC.size() < unitCount) {
                        teamC.add(teamC.size(), getUnit(teamC_unit));
                    }
                    checkCollisions();
                }
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
        System.out
                .println("Number of units after collisions: " + teamA.size() + " " + teamB.size() + " " + teamC.size());
    }

    public double[] getRandomDirection(double x1, double x2, double y1, double y2) {
        double newVelocity[] = new double[10];
        double xDist = (x1 - x2) * (Math.random() * .4 + 0.8);
        double yDist = (y1 - y2) * (Math.random() * .4 + 0.8);
        // System.out.println(xDist + ", " + yDist);
        double slope = Math.sqrt(xDist * xDist + yDist * yDist);
        while (Math.abs(slope) > maximumSpeed) {
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
    }

    public factions transferProperties(factions newFaction, factions oldFactions) {
        newFaction.xCoord = oldFactions.xCoord;
        newFaction.yCoord = oldFactions.yCoord;
        newFaction.attack_strong = oldFactions.attack_strong;
        newFaction.attack_weak = oldFactions.attack_weak;
        newFaction.shield = oldFactions.shield;
        newFaction.maxShield = oldFactions.maxShield;
        newFaction.health = oldFactions.health;
        newFaction.maxHealth = oldFactions.maxHealth;
        return newFaction;
    }
    public String randomSound() {
        int randomPopSound = (int)(Math.random()*5)+1;
        System.out.println(randomPopSound);
        String soundFile = "pop" + randomPopSound + ".wav";
        return soundFile;
    }
    public void checkCollisions() {
        // bounces off the walls
        for (factions i : teamA) {
            // left and right wall collisions
            if (i.xCoord > getWidth() - (imageWidth / 2) || i.xCoord < (imageWidth / 2)) {
                i.velX *= -1;
                i.xCoord += i.velX;
            }
            if (i.yCoord > getHeight() - (imageHeight / 2) || i.yCoord < (imageHeight / 2)) {
                i.velY *= -1;
                i.yCoord += i.velY;
            }
        }
        for (factions i : teamB) {
            if (i.xCoord > getWidth() - (imageWidth / 2) || i.xCoord < (imageWidth / 2)) {
                i.velX *= -1;
                i.xCoord += i.velX;
            }
            if (i.yCoord > getHeight() - (imageHeight / 2) || i.yCoord < (imageHeight / 2)) {
                i.velY *= -1;
                i.yCoord += i.velY;
            }
        }
        for (factions i : teamC) {
            if (i.xCoord > getWidth() - (imageWidth / 2) || i.xCoord < (imageWidth / 2)) {
                i.velX *= -1;
                i.xCoord += i.velX;
            }
            if (i.yCoord > getHeight() - (imageHeight / 2) || i.yCoord < (imageHeight / 2)) {
                i.velY *= -1;
                i.yCoord += i.velY;
            }
        }
        if (threeSecondStart < 0 || threeSecondStart == 180) {
            for (factions a : teamA) {
                for (factions b : teamB) {
                    if (Math.abs(a.xCoord - b.xCoord) < collisionRadius
                            && Math.abs(a.yCoord - b.yCoord) < collisionRadius) {
                        int damage_from_B = b.attack_strong - a.shield;
                        int damage_from_A = a.attack_weak - b.shield;
                        if (damage_from_A <= 0) {
                            b.shield -= a.attack_weak;
                        } else {
                            b.health -= damage_from_A;
                        }
                        if (damage_from_B <= 0) {
                            a.shield -= b.attack_strong;
                        } else {
                            a.health -= damage_from_B;
                        }
                        factions tempA = new factions();
                        tempA.velX = getRandomDirection(a.xCoord, b.xCoord, a.yCoord, b.yCoord)[0];
                        tempA.velY = getRandomDirection(a.xCoord, b.xCoord, a.yCoord, b.yCoord)[1];
                        tempA = transferProperties(tempA, a);
                        factions tempB = new factions();
                        tempB.velX = getRandomDirection(b.xCoord, a.xCoord, b.yCoord, a.yCoord)[0];
                        tempB.velY = getRandomDirection(b.xCoord, a.xCoord, b.yCoord, a.yCoord)[1];
                        tempB = transferProperties(tempB, b);
                        if (a.health <= 0) {
                            teamA.remove(a);
                            tempA.shield = tempA.maxShield;
                            tempA.health = tempA.maxHealth;
                            teamB.add(teamB.size(), tempA);
                        }
                        if (b.health <= 0) {
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
                        int damage_from_C = c.attack_weak - a.shield;
                        int damage_from_A = a.attack_strong - c.shield;
                        if (damage_from_A <= 0) {
                            c.shield -= a.attack_strong;
                        } else {
                            c.health -= damage_from_A;
                        }
                        if (damage_from_C <= 0) {
                            a.shield -= c.attack_weak;
                        } else {
                            a.health -= damage_from_C;
                        }
                        factions tempA = new factions();
                        tempA.velX = getRandomDirection(a.xCoord, c.xCoord, a.yCoord, c.yCoord)[0];
                        tempA.velY = getRandomDirection(a.xCoord, c.xCoord, a.yCoord, c.yCoord)[1];
                        tempA = transferProperties(tempA, a);
                        factions tempC = new factions();
                        tempC.velX = getRandomDirection(c.xCoord, a.xCoord, c.yCoord, a.yCoord)[0];
                        tempC.velY = getRandomDirection(c.xCoord, a.xCoord, c.yCoord, a.yCoord)[1];
                        tempC = transferProperties(tempC, c);
                        if (a.health <= 0) {
                            teamA.remove(a);
                            tempA.shield = tempA.maxShield;
                            tempA.health = tempA.maxHealth;
                            teamC.add(teamC.size(), tempA);
                        }
                        if (c.health <= 0) {
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
                        int damage_from_C = c.attack_strong - b.shield;
                        int damage_from_B = b.attack_weak - c.shield;
                        if (damage_from_B <= 0) {
                            c.shield -= b.attack_weak;
                        } else {
                            c.health -= damage_from_B;
                        }
                        if (damage_from_C <= 0) {
                            b.shield -= c.attack_strong;
                        } else {
                            b.health -= damage_from_C;
                        }
                        factions tempB = new factions();
                        tempB.velX = getRandomDirection(b.xCoord, c.xCoord, b.yCoord, c.yCoord)[0];
                        tempB.velY = getRandomDirection(b.xCoord, c.xCoord, b.yCoord, c.yCoord)[1];
                        tempB = transferProperties(tempB, b);
                        factions tempC = new factions();
                        tempC.velX = getRandomDirection(c.xCoord, b.xCoord, c.yCoord, b.yCoord)[0];
                        tempC.velY = getRandomDirection(c.xCoord, b.xCoord, c.yCoord, b.yCoord)[1];
                        tempC = transferProperties(tempC, c);
                        if (b.health <= 0) {
                            teamB.remove(b);
                            tempB.shield = tempB.maxShield;
                            tempB.health = tempB.maxHealth;
                            teamC.add(teamC.size(), tempB);
                        }
                        if (c.health <= 0) {
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

        offscreen = createImage(getSize().width, getSize().height);
        buffer = (Graphics2D) offscreen.getGraphics();
        buffer.setColor(new Color(0, 0, 0));
        buffer.fillRect(0, 0, getWidth(), getHeight());
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
            buffer.drawString("Starts in " + timeLeft, getWidth() / 2 - 100, getHeight() / 2);
        g.drawImage(offscreen, 0, 0, this);
    }

    public void Update(Graphics gr) {
        // call th

    
    }
 
    // buffer.setColor(Color.red);
    // 
    //buffer.drawRect( x - (imageWidth / 2), y - (imageHeight / 2), imageWidth, imageHeight);

}