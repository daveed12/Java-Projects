import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Adventure extends Application {

    String mapFile = null;
    static int [] position = new int[2];
    static GameChar character;
    final TextArea transcript = new TextArea();
    GridPane map = new GridPane();
    String mapPic = "";
    char[][] map_view;
    Map diagram;
    boolean isSaved = false;
    File file;



    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Adventure Game");

        Platform.setImplicitExit(false);

        final Button quitBtn = new Button("Quit");
        final Button saveBtn =  new Button("Save");
        final Button openBtn = new Button("Open");
        final Button executeCommandBtn = new Button("Execute");
        final TextField commandInputField = new TextField();
        final ScrollBar scBox = new ScrollBar();
        final Parameters params= getParameters();
        final FileChooser fileChooser = new FileChooser();


        //Takes in parameter of map file
        List<String> args = params.getRaw();
        if(!args.isEmpty()){
            mapFile = args.get(0);
        }

        //Initialize objects
        diagram = new Map(mapFile,transcript);
        character = new GameChar(diagram.getMap(),diagram.getRows(),diagram.getColumns(),transcript);
        //Get the current 5x5 view of map
        map_view = character.mapVisibility();
        //Set position to 0,0
        position = character.getCurrentPosition();

        //Quit button
        quitBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage) quitBtn.getScene().getWindow();
                transcript.appendText("Goodbye!" + "\n");
                stage.close();
            }
        });

        //Execute command button for textfield
        executeCommandBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String[] parsedInput = commandInputField.getText().split(" +");
                String command = parsedInput[0];
                if(command.length() > 0){
                    command(parsedInput);
                    transcript.appendText("\nYou are at location " + position[0] + "," + position[1]);
                     map_view = character.mapVisibility();
                     conjureMap(character.getMiniMapSize());
                }
            }
        });

        //Open save file
        openBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                file = fileChooser.showOpenDialog(primaryStage);
                if(file != null){
                    try{
                        // Reading the object from a file
                        FileInputStream f = new FileInputStream(file);
                        ObjectInputStream in = new ObjectInputStream(f);

                        // Method for deserialization of object
                        diagram = (Map)in.readObject();
                        character = (GameChar)in.readObject();
                        in.close();
                        f.close();

                    }catch(IOException ex){
                        transcript.appendText(FileChooser.class.getName() + ex + "\n");
                    }catch(Exception ex){
                        transcript.appendText(FileChooser.class.getName() + ex + "\n");
                    }
                }

                //Setting position back in
                map_view = character.mapVisibility();
                conjureMap(character.getMiniMapSize());
                int[] tmp = character.getCurrentPosition();
                position[0] = tmp[0];
                position[1] = tmp[1];
                //Adding transient transcript back in
                diagram.setTranscript(transcript);
                character.setTranscript(transcript);
                //Adding text to transcript
                transcript.appendText("\nYou are at location " + position[0] + "," + position[1]);
            }
        });

        //Save to file or save as if you haven't saved before
        saveBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                    if (isSaved == true) {
                        try{
                            FileOutputStream f = new FileOutputStream(file);
                            ObjectOutputStream out = new ObjectOutputStream(f);

                            out.writeObject(diagram);
                            out.writeObject(character);
                            out.close();
                            f.close();
                            transcript.appendText("Game has been saved\n");
                            isSaved = true;
                        }catch (IOException ex) {
                            transcript.appendText(FileChooser.class.getName() + ex + "\n");
                        } catch (Exception ex) {
                            transcript.appendText(FileChooser.class.getName() + ex + "\n");
                        }
                    } else {
                        file = fileChooser.showOpenDialog(primaryStage);
                        if (file != null) {
                            try {
                                FileOutputStream f = new FileOutputStream(file);
                                ObjectOutputStream out = new ObjectOutputStream(f);

                                out.writeObject(diagram);
                                out.writeObject(character);
                                out.close();
                                f.close();
                                transcript.appendText("Game has been saved\n");
                                isSaved = true;
                            } catch (IOException ex) {
                                transcript.appendText(FileChooser.class.getName() + ex + "\n");
                            } catch (Exception ex) {
                                transcript.appendText(FileChooser.class.getName() + ex + "\n");
                            }

                        }
                    }
            }
        });



        BorderPane root = new BorderPane();
        HBox optionsBar =  new HBox();
        VBox commandFrame = new VBox();

        conjureMap(character.getMiniMapSize());

        optionsBar.getChildren().addAll(openBtn,saveBtn,quitBtn);
        commandFrame.getChildren().addAll(commandInputField,executeCommandBtn);

        root.setAlignment(optionsBar, Pos.TOP_LEFT);
        root.setTop(optionsBar);
        root.setBottom(transcript);
        root.setRight(commandFrame);
        root.setCenter(map);


        Scene scene = new Scene(root, 700,700);

        primaryStage.setScene(scene);

        //Setup text receiving scrollbar
        scBox.setLayoutX(transcript.getWidth() - scBox.getWidth());
        scBox.setMin(0);
        scBox.setMax(700);
        scBox.setOrientation(Orientation.VERTICAL);
        scBox.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                transcript.setLayoutY(-newValue.doubleValue());
            }
        });

        transcript.appendText("Welcome to my Adventure game!" + "\n");
        transcript.appendText("Please enter a command in the textfield and hit execute to get started!" + "\n");



        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }

    public void command(String [] command){
        String tmp = command[0].toLowerCase();

        if(tmp.charAt(0) == 'g'){
            position = character.executeGo(command[1].toLowerCase());
        }
        else if(tmp.charAt(0) == 'i'){
            character.inventoryCommand();
        }
        else if(tmp.equals("take")){
            boolean itemPickedup = false;
            String item = "";

            for(int i = 1; i<command.length; i++){
                if(i == command.length - 1){
                    item += command[i];
                }
                else{
                    item += command[i] + " ";
                }
            }
            itemPickedup = checkIfItemExists(item);
            if(itemPickedup == true){
                character.setItemsInInventory(item);
            }
        }
        else if(tmp.equals("drop")){
            boolean itemDropped = false;
            String item = "";

            for(int i = 1; i<command.length; i++){
                if(i == command.length - 1){
                    item += command[i];
                }
                else{
                    item += command[i] + " ";
                }
            }
            itemDropped = checkIfItemInInventory(item);
            if(itemDropped == true){
                character.dropItemInInventory(item);
                String[] newItem = new String[] {Integer.toString(position[0]),Integer.toString(position[1]),item};
                ArrayList<String[]> tmp2 = diagram.getItemsInMap();
                tmp2.add(newItem);
                diagram.setItemsInMap(tmp2);
            }
        }
        else{
            transcript.appendText("Invalid command: " + command[0]);
        }
        checkIfItemFound();
    }
    public boolean checkIfItemInInventory(String item){
        ArrayList<String> items = character.getItemsInInventory();

        for(int i = 0; i<items.size(); i++){
            if(items.get(i).equals(item)){
                return true;
            }
        }
        transcript.appendText("Item was not found in your inventory.\n");
        return false;
    }
    public boolean checkIfItemExists(String item){
        ArrayList<String[]> items = diagram.getItemsInMap();

        for(int i = 0; i<items.size(); i++){
            if(items.get(i)[2].equals(item)){
                items.remove(i);
                diagram.setItemsInMap(items);
                return true;
            }
        }
        transcript.appendText("Item doesn't exist on this square of the map, make sure you also type it in exactly how it is.\n");
        return false;

    }
    public void checkIfItemFound(){
        for(int i = 0; i< diagram.getItemsInMap().size(); i++){
            for(int j = 0; j< diagram.getItemsInMap().get(i).length; j++){
                if(position[0] == Integer.parseInt(diagram.getItemsInMap().get(i)[0]) && position[1] ==  Integer.parseInt(diagram.getItemsInMap().get(i)[1])){
                    transcript.appendText("\n\"" + diagram.getItemsInMap().get(i)[2] +"\"" + " item found. Pick it up? Use the take command plus the name of item to do this" + "\n");
                    break;
                }
            }

        }
    }


    public void conjureMap(int size){
        for(int i = 0; i< map_view.length; i++){
            for(int j = 0; j<map_view[i].length; j++){
                mapPic = "file:./" + (diagram.getMapKey().get(String.valueOf(map_view[i][j]))).toString();
                Image image = new Image(mapPic);
                ImageView img = new ImageView(image);
                img.setFitWidth(diagram.getPictureSize()[0]);
                img.setFitHeight(diagram.getPictureSize()[1]);

                map.setRowIndex(img, i);
                map.setColumnIndex(img,j);
                map.getChildren().add(img);
                //The center of the minimap
                if(i == size/2 && j == size/2){
                    mapPic = "file:./" + diagram.getMapKey().get("1").toString();
                    Image image2 = new Image(mapPic);
                    ImageView img2 = new ImageView(image2);
                    img2.setFitWidth(diagram.getPictureSize()[0]);
                    img2.setFitHeight(diagram.getPictureSize()[1]);

                    map.setRowIndex(img2, i);
                    map.setColumnIndex(img2,j);
                    map.getChildren().add(img2);
                }
            }
        }

    }
}
