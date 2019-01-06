import javafx.scene.control.TextArea;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class GameChar implements Serializable {

    private char[][] map;
    private int MAX_ROW;
    private int MAX_COL;
    private final int MIN_COL = 0;
    private final int MIN_ROW = 0;
    private int row=0;
    private int col=0;
    private transient TextArea transcript;
    //minimap size
    private int map_size = 7;
    private ArrayList<String> items = new ArrayList<>(Arrays.asList("brass lantern","rope","rations","staff"));

    public GameChar(char[][] map, int rows, int columns, TextArea transcript){
        this.map = map;
        MAX_ROW = rows -1;
        MAX_COL = columns -1;
        this.transcript = transcript;
    }
    public int[] executeGo(String direction){

        if (direction.charAt(0) == 'e') {
            if (col + 1 <= MAX_COL) {
                ++col;
                transcript.appendText("\nMoving east..." + "\n");

            } else {
                transcript.appendText("\nYou can't go that far east." + "\n");
            }
        } else if (direction.charAt(0) == 's') {
            if (row + 1 <= MAX_ROW) {
                ++row;
                transcript.appendText("\nMoving south..." + "\n");

            } else {
                transcript.appendText("\nYou can't go that far south." + "\n");

            }
        } else if (direction.charAt(0) == 'w') {
            if (col - 1 >= MIN_COL) {
                --col;
                transcript.appendText("\nMoving west..." + "\n");


            } else {
                transcript.appendText("\nYou can't go that far west." + "\n");
            }
        } else if (direction.charAt(0) == 'n') {
            if (row - 1 >= MIN_ROW) {
                --row;
                transcript.appendText("\nMoving north..." + "\n");
            } else {
                transcript.appendText("\nYou can't go that far north." + "\n");
            }
        }
        else {
            transcript.appendText("\nInvalid parameter: " + direction + " please only enter west, east, south or north." + "\n");
        }
        return new int []{row,col};
    }

    public char[][] mapVisibility(){
        char[][] map_sight = new char[map_size][map_size];
        int offset = map_size/(2);
        for(int i = -1*offset; i<=offset; i++){
            for(int j = -1*offset; j<=offset; j++){
                if(row + i < MIN_ROW || col + j < MIN_COL || row + i > MAX_ROW || col + j > MAX_COL){
                    map_sight[i + offset][j + offset] = '-';
                }
                else{
                    map_sight[i + offset][j + offset] = map[row + i][col + j];
                }
            }
        }
        return map_sight;
    }
    public void inventoryCommand(){
        transcript.appendText("\nYou are carrying:");
        for(int i =0; i<items.size(); i++){
            transcript.appendText("\n" + items.get(i));
        }
    }
    public int [] getCurrentPosition(){
        return new int []{row,col};
    }
    public int getMiniMapSize(){
        return map_size;
    }
    public void setItemsInInventory(String item){
        items.add(item);
    }
    public void dropItemInInventory(String item){
        items.remove(item);
    }
    public ArrayList<String> getItemsInInventory(){
        return items;
    }
    public void setTranscript(TextArea transcript){
        this.transcript = transcript;
    }
}
