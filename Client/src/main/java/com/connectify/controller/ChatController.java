package com.connectify.controller;

import com.connectify.Client;
import com.connectify.Interfaces.ServerAPI;
import com.connectify.dto.MessageSentDTO;
import com.connectify.mapper.MessageMapper;
import com.connectify.model.entities.Message;
import com.connectify.utils.ChatCardHandler;
import com.connectify.utils.CurrentUser;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.ResourceBundle;

public class ChatController implements Initializable {

    @FXML
    private ImageView attachmentImageView;

    @FXML
    private Text chatName;

    @FXML
    private ImageView htmlEditorImageView;

    @FXML
    private Text membersCount;

    @FXML
    private ListView<Message> messagesList;

    @FXML
    private Circle pictureClip;

    @FXML
    private ImageView pictureImageView;

    @FXML
    private TextField sendBox;

    @FXML
    private ImageView sendImageView;

    @FXML
    private Circle statusCircle;

    private ObservableList<Message> messages;

    private final int chatID;

    private final String name;

    private final Image image;

    private ServerAPI server;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chatName.setText(name);
        pictureImageView.setImage(image);
        setListViewCellFactory();
        messages = CurrentUser.getMessageList(chatID);
        messagesList.setItems(messages);
    }


    public ChatController(int chatID, String name, Image image){
        this.chatID = chatID;
        this.name = name;
        this.image = image;
        try {
            server = (ServerAPI) Client.getRegistry().lookup("server");
        } catch (RemoteException | NotBoundException e) {

        }
    }

    public void sendHandler(){
        if(!Objects.equals(sendBox.getText(), "")){
            try {
                MessageSentDTO messageSentDTO = new MessageSentDTO(Client.getConnectedUser().getPhoneNumber(),chatID,sendBox.getText(),new Timestamp(System.currentTimeMillis()), null);
                MessageMapper mapper = MessageMapper.INSTANCE;
                Message message =mapper.messageSentDtoTOMessage(messageSentDTO);
                ChatCardHandler.updateChatCard(message);
                server.sendMessage(messageSentDTO);
                //TODO render send message
                messages.add(message);
                sendBox.clear();
            } catch (RemoteException e) {
                System.err.println("Can't find server, details: "+e.getMessage());
            }
        }
    }


    public void attachmentHandler(){
        Stage stage = (Stage) sendBox.getScene().getWindow();
        FileChooser  fileChooser = new FileChooser();
        fileChooser.setTitle("Select file");
        File file = fileChooser.showOpenDialog(stage);
        if(file != null){
            try{
                MessageSentDTO messageSentDTO = new MessageSentDTO(Client.getConnectedUser().getPhoneNumber(), chatID, "", new Timestamp(System.currentTimeMillis()), file);
                server.sendAttachment(messageSentDTO);
            } catch (RemoteException e){
                System.err.println("Remote Exception: " + e.getMessage());
            }
        }
    }

    public void htmlEditorHandler(){

    }

    private void setListViewCellFactory(){
        messagesList.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>() {
            public ListCell<Message> call(ListView<Message> param) {
                return new ListCell<>(){
                     @Override
                    public void updateItem(Message message, boolean empty) {
                        super.updateItem(message, empty);
                        if (!empty) {
                            FXMLLoader loader;
                            if (message != null) {
                                try {
                                    if(Objects.equals(message.getSender(), Client.getConnectedUser().getPhoneNumber())){
                                        loader= new FXMLLoader(getClass().getResource("/views/SentMessageHBox.fxml"));
                                        //TODO add sender image here
                                        loader.setController(new SentMessageHBoxController(null,message.getContent()));
                                    }

                                    else{
                                        loader = new FXMLLoader(getClass().getResource("/views/ReceivedMessageHBox.fxml"));
                                        loader.setController(new ReceivedMessageHBoxController(null,message.getContent()));
                                    }

                                } catch (RemoteException e) {
                                    throw new RuntimeException(e);
                                }
                                HBox root;
                                try {
                                    root = loader.load();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                Platform.runLater(()->{
                                    setGraphic(root);
                                });
                            }
                        } else {
                            Platform.runLater(()->{
                                setText(null);
                                setGraphic(null);
                            });
                        }
                    }
                };
            }
        });
    }



}
