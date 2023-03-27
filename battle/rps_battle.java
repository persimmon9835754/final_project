package battle;

import javax.swing.*;
import javax.xml.xpath.XPathEvaluationResult;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.event.*;

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
    private int unitCount = 30;
    private int heroX = 0;
    private int heroY = 0;
    Image imgRock;
    Image imgPaper;
    Image imgScissors;
    private int collisionRadius = 40;
    private int defaultCooldown = 30;
    private double maximumSpeed = 1.5;
    private int spawnWidth = 900;
    private int spawnBuffer = 50;
    private int spawnHeight = 700;
    private int threeSecondStart = 270;
    public static String rps_battle_mode = "";

    public rps_battle() {
        // starts timer on load, and adds key listener
        super("Button Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        addKeyListener(this);
        myTimer.start();
        imgRock = new ImageIcon("img/rock.png").getImage();
        imgPaper = new ImageIcon("img/paper.png").getImage();
        imgScissors = new ImageIcon("img/scissors.png").getImage();
        createTeams(unitCount, unitCount, unitCount);
    }

    // when you push the button it comes this method
    public void actionPerformed(ActionEvent event) {
        Object objSource = event.getSource();
        moveObjects();
        repaint();
        threeSecondStart--;
    }

    public factions generateUnit() {
        // this generates a new unit, the faction is determined by which arraylist the
        // unit is stored in
        factions newUnit = new factions();
        newUnit.xCoord = (int) (Math.random() * spawnWidth) + spawnBuffer;
        newUnit.yCoord = (int) (Math.random() * spawnHeight) + spawnBuffer;
        double x = Math.random() * 100 - 50;
        double y = Math.random() * 100 - 50;
        newUnit.velX = getRandomDirection(x, 0, y, 0)[0];
        newUnit.velY = getRandomDirection(x, 0, y, 0)[1];
        if (rps_battle_mode == "classic") {
            newUnit.health = 100;
            newUnit.maxHealth = 100;
            newUnit.attack_strong = 100;
            newUnit.attack_weak = 0;
            newUnit.shield = 0;
            newUnit.maxShield = 0;
        }
        if(rps_battle_mode == "health") {
            newUnit.health = 100;
            newUnit.maxHealth = 100;
            newUnit.attack_strong = 70;
            newUnit.attack_weak = 30;
            newUnit.shield = 0;
            newUnit.maxShield = 0;
        }
        return newUnit;
    }

    public void createTeams(int size1, int size2, int size3) {
        for (int i = 0; i < size1; i++) {
            teamA.add(teamA.size(), generateUnit());

        }
        for (int i = 0; i < size2; i++) {
            teamB.add(teamB.size(), generateUnit());
        }
        for (int i = 0; i < size3; i++) {
            teamC.add(teamC.size(), generateUnit());
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
                        teamA.add(teamA.size(), generateUnit());
                    }
                    while (teamB.size() < unitCount) {
                        teamB.add(teamB.size(), generateUnit());
                    }
                    while (teamC.size() < unitCount) {
                        teamC.add(teamC.size(), generateUnit());
                    }
                    checkCollisions();
                }
            } catch (Exception e) {
                System.out.println("Error");
            }
        }
        System.out
                .println("Number of units after collisions: " + teamA.size() + " " + teamB.size() + " " + teamC.size());

        /*
         * if(teamA.size() < unitCount || teamB.size() < unitCount || teamC.size() <
         * unitCount) {
         * teamA = new ArrayList<factions>();
         * teamB = new ArrayList<factions>();
         * teamC = new ArrayList<factions>();
         * createTeams(unitCount, unitCount, unitCount);
         * }
         */
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

    public void checkCollisions() {
        // bounces off the walls
        for (factions i : teamA) {
            // left and right wall collisions
            if (i.xCoord > getWidth() - 25 || i.xCoord < 25) {
                i.velX *= -1;
                i.xCoord += i.velX;
            }
            if (i.yCoord > getHeight() - 25 || i.yCoord < 0) {
                i.velY *= -1;
                i.yCoord += i.velY;
            }
        }
        for (factions i : teamB) {
            if (i.xCoord > getWidth() - 25 || i.xCoord < 25) {
                i.velX *= -1;
                i.xCoord += i.velX;
            }
            if (i.yCoord > getHeight() - 25 || i.yCoord < 0) {
                i.velY *= -1;
                i.yCoord += i.velY;
            }
        }
        for (factions i : teamC) {
            if (i.xCoord > getWidth() - 25 || i.xCoord < 25) {
                i.velX *= -1;
                i.xCoord += i.velX;
            }
            if (i.yCoord > getHeight() - 25 || i.yCoord < 0) {
                i.velY *= -1;
                i.yCoord += i.velY;
            }
        }
        if (threeSecondStart < 0 || threeSecondStart == 180) {
            for (factions a : teamA) {
                for (factions b : teamB) {
                    if (Math.abs(a.xCoord - b.xCoord) < collisionRadius
                            && Math.abs(a.yCoord - b.yCoord) < collisionRadius) {
                        factions temp = new factions();
                        temp.velX = getRandomDirection(a.xCoord, b.xCoord, a.yCoord, b.yCoord)[0];
                        temp.velY = getRandomDirection(a.xCoord, b.xCoord, a.yCoord, b.yCoord)[1];
                        temp.xCoord = a.xCoord;
                        temp.yCoord = a.yCoord;
                        teamA.remove(a);
                        teamB.add(teamB.size(), temp);
                        return;
                    }
                }
            }
            for (factions a : teamA) {
                for (factions c : teamC) {
                    if (Math.abs(a.xCoord - c.xCoord) < collisionRadius
                            && Math.abs(a.yCoord - c.yCoord) < collisionRadius) {
                        factions temp = new factions();
                        temp.velX = getRandomDirection(c.xCoord, a.xCoord, c.yCoord, a.yCoord)[0];
                        temp.velY = getRandomDirection(c.xCoord, a.xCoord, c.yCoord, a.yCoord)[1];
                        temp.xCoord = c.xCoord;
                        temp.yCoord = c.yCoord;
                        teamC.remove(c);
                        teamA.add(teamA.size(), temp);
                        return;
                    }
                }
            }
            for (factions c : teamC) {
                for (factions b : teamB) {
                    if (Math.abs(c.xCoord - b.xCoord) < collisionRadius
                            && Math.abs(c.yCoord - b.yCoord) < collisionRadius) {
                        factions temp = new factions();
                        temp.velX = getRandomDirection(b.xCoord, c.xCoord, b.yCoord, c.yCoord)[0];
                        temp.velY = getRandomDirection(b.xCoord, c.xCoord, b.yCoord, c.yCoord)[1];
                        temp.xCoord = b.xCoord;
                        temp.yCoord = b.yCoord;
                        teamB.remove(b);
                        teamC.add(teamC.size(), temp);
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
        offscreen = createImage(getSize().width, getSize().height);
        buffer = (Graphics2D) offscreen.getGraphics();
        buffer.setColor(new Color(0, 0, 0));
        buffer.fillRect(0, 0, getWidth(), getHeight());
        for (factions i : teamA) {
            int x = (int) i.xCoord;
            int y = (int) i.yCoord;
            buffer.drawImage(imgRock, x - 25, y - 17, null);
        }
        for (factions i : teamB) {
            int x = (int) i.xCoord;
            int y = (int) i.yCoord;
            buffer.drawImage(imgPaper, x - 25, y - 25, null);
        }
        for (factions i : teamC) {
            int x = (int) i.xCoord;
            int y = (int) i.yCoord;
            buffer.drawImage(imgScissors, x - 25, y + 14, null);
        }
        buffer.drawImage(imgRock, heroX, heroY, null);
        buffer.setColor(Color.red);
        buffer.setFont(new Font("Comic Sans MS", Font.PLAIN, 50));
        String timeLeft = String.valueOf(threeSecondStart / 60);
        if (threeSecondStart / 60 >= 0.0)
            buffer.drawString("Starts in " + timeLeft, getWidth() / 2 - 100, getHeight() / 2);
        g.drawImage(offscreen, 0, 0, this);
    }

    public void Update(Graphics gr) {
        // call the paint method
        paint(gr);
    }
}