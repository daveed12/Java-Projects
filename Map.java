import javafx.scene.control.TextArea;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;

public class Map implements Serializable {
    private String map_name;
    private transient Scanner buffer = null;
    private transient TextArea transcript;
    private int rows = 0;
    private int columns = 0;
    private char[][] map;
    private int [] pictureSize = new int[2];
    private String map_item_name;
    private HashMap<String,String> map_key = new HashMap<>();
    private ArrayList<String[]> itemsInMap = new ArrayList<>();

    public Map(String map,TextArea transcript){
        map_name = map;
        this.transcript = transcript;
        renderMap();
    }

    private void renderMap(){
        int increment = 0;
        //Try/Catch, exit program if it fails to open file
        try{
            buffer = new Scanner(new File(map_name));
        }catch(FileNotFoundException e){
            transcript.appendText("File not found" + "\n");
            return;
        }catch(Exception e){
            transcript.appendText("Error: " + e + "\n");
            return;
        }

        //Get dimensions for map
        String line = buffer.nextLine();
        String[] dimension = line.split(" ");

        //Creating map
        map = new char[Integer.parseInt(dimension[0])][Integer.parseInt(dimension[1])];
        rows = Integer.parseInt(dimension[0]);
        columns = Integer.parseInt(dimension[1]);

        //Filling in map
        while(buffer.hasNextLine()){
            if(buffer.hasNextInt()){
                String[] tmp = buffer.nextLine().split(" ");
                pictureSize[0] = Integer.parseInt(tmp[0]);
                pictureSize[1] = Integer.parseInt(tmp[1]);
                break;
            }
            line = buffer.nextLine();
            map[increment] = line.toCharArray();
            increment++;
        }
        map_item_name = buffer.nextLine();

        increment = 0;

        while(buffer.hasNextLine()){
            line = buffer.nextLine();
            String[] tmp = line.split(";");
            map_key.put(tmp[0], tmp[2]);
            increment++;
        }
        buffer.close();
        loadItems();
    }

    private void loadItems(){
        try{
            buffer = new Scanner(new File(map_item_name));
        }catch(FileNotFoundException e){
            transcript.appendText("Item file not found" + "\n");
            return;
        }catch(Exception e){
            transcript.appendText("Error: " + e + "\n");
            return;
        }
        String line;
        while(buffer.hasNextLine()){
            line = buffer.nextLine();
            itemsInMap.add(line.split(";"));
        }
    }

    public ArrayList<String[]> getItemsInMap(){
        return itemsInMap;
    }

    public void setItemsInMap(ArrayList<String[]> itemsInMap){
        this.itemsInMap = itemsInMap;
    }

    public int[] getPictureSize(){
        return pictureSize;
    }
    public HashMap getMapKey(){
        return map_key;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public char[][] getMap() {
        return map;
    }

    public void setTranscript(TextArea transcript){
        this.transcript = transcript;
    }
}
