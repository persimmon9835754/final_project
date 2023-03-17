//Name: Andrew La  
//Date: 2/18/2022
//Project: Circle Bounce Array

import javax.swing.*;
import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.event.*;

public class Main extends JFrame implements KeyListener, ActionListener {

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

    private int unitCount = 100;
    private int heroX = 100;
    private int heroY = 100;

    Image imgRock;
    Image imgPaper;
    Image imgScissors;

    JButton myButton = new JButton("press");

    public Main() {
        // starts timer on load, and adds key listener
        super("Button Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        addKeyListener(this);
        myTimer.start();

        imgRock = new ImageIcon("img/rock.png").getImage();
        imgPaper = new ImageIcon("img/paper.png").getImage();
        imgScissors = new ImageIcon("img/scissors.png").getImage();
        createTeams((int) (Math.random() * 50) + 1, (int) (Math.random() * 50) + 1, (int) (Math.random() * 50) + 1);
    }

    public static void main(String[] args) {
        // Place components on the applet panel
        final int FRAME_WIDTH = 1000;
        final int FRAME_HEIGHT = 800;
        Main frame = new Main();
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setVisible(true);
    }

    // when you push the button it comes this method
    public void actionPerformed(ActionEvent event) {
        Object objSource = event.getSource();
        moveObjects();
        repaint();

    }

    public void createTeams(int size1, int size2, int size3) {
        for (int i = 0; i < size1; i++) {
            teamA.add(teamA.size(), new factions());
            teamA.get(i).xCoord = (int) (Math.random() * 1000);
            teamA.get(i).yCoord = (int) (Math.random() * 800);
            while (Math.abs(teamA.get(i).velX) < 1)
                teamA.get(i).velX = (Math.random() * 6) - 3;
            while (Math.abs(teamA.get(i).velY) < 1)
                teamA.get(i).velY = (Math.random() * 6) - 3;
        }
        for (int i = 0; i < size2; i++) {
            teamB.add(teamB.size(), new factions());
            teamB.get(i).xCoord = (int) (Math.random() * 1000);
            teamB.get(i).yCoord = (int) (Math.random() * 800);
            while (Math.abs(teamB.get(i).velX) < 1)
                teamB.get(i).velX = (Math.random() * 6) - 3;
            while (Math.abs(teamB.get(i).velY) < 1)
                teamB.get(i).velY = (Math.random() * 6) - 3;
        }
        for (int i = 0; i < size3; i++) {
            teamC.add(teamC.size(), new factions());
            teamC.get(i).xCoord = (int) (Math.random() * 1000);
            teamC.get(i).yCoord = (int) (Math.random() * 800);
            while (Math.abs(teamC.get(i).velX) < 1)
                teamC.get(i).velX = (Math.random() * 6) - 3;
            while (Math.abs(teamC.get(i).velY) < 1)
                teamC.get(i).velY = (Math.random() * 6) - 3;
        }

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
        checkCollisions();
    }

    public void checkCollisions() {

        for (factions i : teamA) {
            if (i.xCoord > getWidth() || i.xCoord < 0) {
                i.velX *= -1.00;
                i.xCoord += i.velX;
            }
            if (i.yCoord > getHeight() || i.yCoord < 0) {
                i.velY *= -1.00;
                i.yCoord += i.velY;

            }
        }
        for (factions i : teamB) {
            if (i.xCoord > getWidth() || i.xCoord < 0) {
                i.velX *= -1.00;
                i.xCoord += i.velX;

            }
            if (i.yCoord > getHeight() || i.yCoord < 0) {
                i.velY *= -1.00;
                i.yCoord += i.velY;

            }
        }
        for (factions i : teamC) {
            if (i.xCoord > getWidth() || i.xCoord < 0) {
                i.velX *= -1.00;
                i.xCoord += i.velX;

            }
            if (i.yCoord > getHeight() || i.yCoord < 0) {
                i.velY *= -1.00;
                i.yCoord += i.velY;
            }
        }
        System.out.println(teamA.get(2).velX);
        for (factions a : teamA) {
            for (factions b : teamB) {
                if (Math.abs(a.xCoord - b.xCoord) < 50 && Math.abs(a.yCoord - b.yCoord) < 50) {
                    teamB.add(teamB.size(), a);
                    teamA.remove(a);
                    return;
                }
            }
            for (factions c : teamC) {
                if (Math.abs(a.xCoord - c.xCoord) < 50 && Math.abs(a.yCoord - c.yCoord) < 50) {
                    teamA.add(teamA.size(), c);
                    teamB.remove(c);
                    return;
                }
            }
        }
        for (factions c : teamC) {
            for (factions b : teamB) {
                if (Math.abs(c.xCoord - b.xCoord) < 50 && Math.abs(c.yCoord - b.yCoord) < 50) {
                    teamC.add(teamC.size(), b);
                    teamB.remove(b);
                    return;
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
            buffer.drawImage(imgRock, (int) i.xCoord, (int) i.yCoord, null);
        }
        for (factions i : teamB) {
            buffer.drawImage(imgPaper, (int) i.xCoord, (int) i.yCoord, null);
        }
        for (factions i : teamC) {
            buffer.drawImage(imgScissors, (int) i.xCoord, (int) i.yCoord, null);
        }
        buffer.drawImage(imgRock, heroX, heroY, null);
        g.drawImage(offscreen, 0, 0, this);
    }

    public void Update(Graphics gr) {
        // call the paint method
        paint(gr);
    }
}