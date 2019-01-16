import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

public class ChatClient extends Application {

    int port = 4688;
    String username = "Anonymous";
    final TextArea textBox = new TextArea();

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("ChatClient");

        Platform.setImplicitExit( false );

        final Button disconnectBtn = new Button("Disconnect");
        final Button sendBtn = new Button("Send");
        final TextArea textMessage = new TextArea();
        final Parameters params = getParameters();
        final ScrollBar scBox = new ScrollBar();
        final ScrollBar scMessage = new ScrollBar();

        //Adds port and username if not default
        List<String> args = params.getRaw();
        if(!args.isEmpty()) {
            username = args.get(0);
            if (args.size() > 1)
                port = Integer.parseInt(args.get(1));
        }
        //Create Socket & connect to the server
        Socket socket = new Socket("127.0.0.1",port);
        PrintStream p = new PrintStream(socket.getOutputStream());
        p.println("connect " + username);
       // DataOutputStream outputFromServer = new DataOutputStream(socket.getOutputStream());
        //outputFromServer.writeUTF("connect " + username);
        //Create thread to check for input in stream
        Thread server = new Thread(new HandleServer(socket));



        //Disconnect Button Press
        disconnectBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                /*try{
                    outputFromServer.writeUTF("disconnect " + username);
                }catch(IOException e){
                    e.printStackTrace();
                }*/
                p.println("disconnect " + username);
            }
        });

        //Send Button Press
        sendBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                p.println(textMessage.getText());
                addToTextBox(username + ": " + textMessage.getText());
            }
        });

        HBox hbox = new HBox();
        hbox.setAlignment(Pos.BOTTOM_RIGHT);
        hbox.getChildren().addAll(textMessage, sendBtn,disconnectBtn);

        BorderPane root = new BorderPane();
        root.setAlignment(hbox, Pos.CENTER);
        root.setBottom(hbox);
        root.setCenter(textBox);

        Scene scene = new Scene(root, 700, 700);

        primaryStage.setScene(scene);

        //Setup text receiving scrollbar
        scBox.setLayoutX(textBox.getWidth() - scBox.getWidth());
        scBox.setMin(0);
        scBox.setMax(700);
        scBox.setOrientation(Orientation.VERTICAL);
        scBox.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                textBox.setLayoutY(-newValue.doubleValue());
            }
        });

        //Setup text message scrollbar
        scMessage.setLayoutX(textMessage.getWidth() - scMessage.getWidth());
        scMessage.setMin(0);
        scMessage.setMax(150);
        scMessage.setOrientation(Orientation.VERTICAL);
        scMessage.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                textMessage.setLayoutY(-newValue.doubleValue());
            }
        });


        primaryStage.show();
        server.start();

    }


    public static void main(String[] args) {
        launch(args);
    }

    public synchronized void addToTextBox(String text){
        textBox.appendText(text + "\n");
    }

    class HandleServer implements Runnable {
        private Socket socket;

        public HandleServer(Socket socket){
            this.socket = socket;
        }

        public void run(){
            try{
                Scanner scanner = new Scanner(socket.getInputStream());

                while(true){
                    String tmp = scanner.nextLine();
                    Platform.runLater( () -> {
                        addToTextBox(tmp);
                    });
                }

            }catch(IOException e){
                e.printStackTrace();
            }
        }

    }
}
