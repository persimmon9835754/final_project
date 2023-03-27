import javax.swing.*;
import javax.xml.xpath.XPathEvaluationResult;

import battle.rps_battle;

import java.util.ArrayList;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.event.*;

public class startup extends JFrame implements KeyListener, ActionListener {

    private Graphics2D buffer;
    private Image offscreen;
    
    final int FRAME_WIDTH = 1000;
    final int FRAME_HEIGHT = 800;

    public startup() {
        // starts timer on load, and adds key listener
        super("Button Test");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        addKeyListener(this);

    }

    public static void main(String[] args) {
        // Place components on the applet panel
        final int FRAME_WIDTH = 1000;
        final int FRAME_HEIGHT = 800;
        startup frame = new startup();
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setVisible(true);
    }

    // when you push the button it comes this method
    public void actionPerformed(ActionEvent event) {
        Object objSource = event.getSource();
        repaint();
    }

    public void keyPressed(KeyEvent e) {
        // captures keypress
        switch (e.getKeyCode()) {
            case 49:
                rps_battle battle = new rps_battle();
                battle.setSize(FRAME_WIDTH, FRAME_HEIGHT);
                battle.setVisible(true);
                rps_battle.rps_battle_mode = "classic";
                break;
            case 50:
                rps_battle battle_health = new rps_battle();
                battle_health.setSize(FRAME_WIDTH, FRAME_HEIGHT);
                battle_health.setVisible(true);
                rps_battle.rps_battle_mode = "health";
                break;
        }
    }

    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void paint(Graphics g) {
        offscreen = createImage(400, 400);
        buffer = (Graphics2D) offscreen.getGraphics();
        buffer.setColor(new Color(0, 0, 0));
        buffer.fillRect(0, 0, 400, 400);
        g.drawImage(offscreen, 100, 100, this);
        buffer.setColor(new Color(0, 0, 255));
        buffer.fillRect(0, 0, 400, 400);        
        g.drawImage(offscreen, 300, 300, this);

    }

    public void Update(Graphics gr) {
        // call the paint method
        paint(gr);
    }
}
