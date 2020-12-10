package GameState;
import Entity.*;
import Main.GamePanel;
import TileMap.Background;
import TileMap.TileMap;
import java.awt.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import Entity.Enemies.*;

public class Level1State extends LevelState
{
    private TileMap tileMap;
    private Background bg;
    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<Explosion> explosions;
    private HUD hud;
    
    public Level1State(GameStateManager gsm){
        this.gsm = gsm;
        init();
    }
    
    public void init() {
        tileMap = new TileMap(30);
        tileMap.loadTiles("/Backgrounds/grasstileset.gif");
        tileMap.loadMap("/Maps/level1-1.map");
        tileMap.setPosition(0, 0);
        tileMap.setTween(1);//camera speed following player

        bg = new Background("/Backgrounds/night.gif", 0.1);
        player = new Player(tileMap);
        player.setPosition(100,100);

        populateEnemies();
        explosions = new ArrayList<Explosion>();
        hud = new HUD(player);
    }
    private void populateEnemies(){
        //create enemy
        enemies = new ArrayList<Enemy>();

        Assignment assignment;
        Point[] points = new Point[] {
            new Point(200,100),
            new Point(860,200),
            new Point(1525, 200),
            new Point(1680, 200),
            new Point(1800, 200)
        };
        for(int i= 0; i< points.length; i++) {
            assignment = new Assignment(tileMap);
            assignment.setPosition(points[i].x,points[i].y);
            enemies.add(assignment);
        }

    }
    public void update() {
        if(player.getX() > tileMap.getTileSize()*tileMap.getNumCols() -15 ) {
            player.setPosition(100,100);
            System.out.println("you passed!");
            gsm.setState(GameStateManager.PASSSTATE);
            player.setPosition(100,100);
        }
        else if(player.getDead()){
            //player dies
            System.out.println("You were killed by the sheer amount of assingments, looks like you took on too much :(!");
            player.setDead(false);
            //idk why it says killed by enemies twice
            gsm.setState(GameStateManager.FAILURESTATE);
            // System.exit(0);
            //say that player died
            // go back to menu screen
        }
        else if (player.getY() > tileMap.getTileSize()*tileMap.getNumRows() -15){
            System.out.println("You fell into a hole of procrastination!");
            player.setDead(false);
            //idk why it says killed by enemies twice
            gsm.setState(GameStateManager.PROCRASTINATIONSTATE);
        }
        else {

            player.update();
            tileMap.setPosition(GamePanel.WIDTH/2-player.getX(), GamePanel.HEIGHT/2-player.getY());
            //set background
            bg.setPosition(tileMap.getX(), tileMap.getY());
            // attack enemies
            player.checkAttack(enemies);





            //update all enemies
            for(int i = 0; i < enemies.size(); i++){
                Enemy e = enemies.get(i);
                e.update();
                if(e.isDead()) {
                    enemies.remove(i);
                    i--;
                    explosions.add(
                            //idk why he didnt cast
                            new Explosion((int) e.getX(), (int) e.getY())
                    );
                }
            }
            //update explosions
            for(int i =0; i< explosions.size(); i++){
                Explosion e = explosions.get(i);
                e.update();
                if(e.shouldRemove()) {
                    explosions.remove(i);
                    i--;
                }
            }

        }


    }

    public void draw(Graphics2D g) {
        //draw bg
        bg.draw(g);

        // draw tilemap
        tileMap.draw(g);

        //draw player
        player.draw(g);

        //draw enemies
        for(int i=0; i<enemies.size(); i++){
            enemies.get(i).draw(g);
        }
        //draw explosions
        for(int i = 0; i< explosions.size(); i++) {
            explosions.get(i).setMapPosition((int)tileMap.getX(),(int)tileMap.getY());
            explosions.get(i).draw(g);
        }

        //draw hud
        hud.draw(g);

    }
    public void keyPressed(int k) {
        if(k == KeyEvent.VK_LEFT) player.setLeft(true);
        if(k == KeyEvent.VK_RIGHT) player.setRight(true);
        if(k == KeyEvent.VK_UP) player.setUp(true);
        if(k == KeyEvent.VK_DOWN) player.setDown(true);
        if(k == KeyEvent.VK_W) player.setJumping(true);
        if(k == KeyEvent.VK_E) player.setGliding(true);
        if(k == KeyEvent.VK_R) player.setScratching();
        if(k == KeyEvent.VK_F) player.setFiring();
    }
    public void keyReleased(int k) {
        if(k == KeyEvent.VK_LEFT) player.setLeft(false);
        if(k == KeyEvent.VK_RIGHT) player.setRight(false);
        if(k == KeyEvent.VK_UP) player.setUp(false);
        if(k == KeyEvent.VK_DOWN) player.setDown(false);
        if(k == KeyEvent.VK_W) player.setJumping(false);
        if(k == KeyEvent.VK_E) player.setGliding(false);
    }
    
}
