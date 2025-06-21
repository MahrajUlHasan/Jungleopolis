package Gui.Tile;

import org.example.GameEngine;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TileManager {
    GameEngine engine;
    Tile[] tile;
    int[][] mapTileNum;

    public TileManager(GameEngine engine) {
        this.engine = engine;
        tile = new Tile[10]; //  num of dirrent type of tiles
        mapTileNum = new int[engine.maxRowNUm][engine.maxColNUm];
        getTileImage();
        loadMap();
    }

    public void getTileImage() {
        try {
            tile[0] = new Tile();
            tile[0].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/grass.png"));

            tile[1] = new Tile();
            tile[1].image = ImageIO.read(getClass().getResourceAsStream("/Tiles/entranceExit.png"));
            tile[1].collision= true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap(){
        try{
            InputStream is = getClass().getResourceAsStream("/Tiles/map.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int col = 0;
            int row = 0;

            while (row < engine.maxRowNUm && col < engine.maxColNUm){

                String line = br.readLine();

                while (col < engine.maxColNUm){
                    String numbers [] = line.split(" ");

                    int num = Integer.parseInt(numbers[col]);

                    mapTileNum[row][col] = num;
                    col++;
                }
                if (col == engine.maxColNUm){
                    col = 0;
                    row++;
                }
            }
            br.close();

    }catch (Exception e){
        e.printStackTrace();
    }
    }

    public void draw(Graphics2D g2d) {

        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;

        while (col < engine.maxColNUm && row < engine.maxRowNUm) {
            int tileNum = mapTileNum[row][col];

            g2d.drawImage(tile[tileNum].image, x, y, engine.tileSize, engine.tileSize, null);
            col++;
            x += engine.tileSize;
            if (col == engine.maxColNUm) {
                col = 0;
                row++;
                y += engine.tileSize;
                x = 0;
            }


        }

    }
}
