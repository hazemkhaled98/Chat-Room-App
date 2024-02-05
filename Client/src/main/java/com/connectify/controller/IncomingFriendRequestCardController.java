package com.connectify.controller;

import com.connectify.Client;
import com.connectify.Interfaces.ServerAPI;
import com.connectify.dto.ChatCardsInfoDTO;
import com.connectify.loaders.ChatCardLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class IncomingFriendRequestCardController implements Initializable {

    @FXML
    private Button acceptFriendRequestButton;

    @FXML
    private Button cancelFriendRequestButton;

    @FXML
    private HBox messageHBox;

    @FXML
    private Circle senderImage;

    @FXML
    private Label senderNameLabel;

    @FXML
    private Label senderPhoneNumberLabel;

    private String name;
    private byte[] pictureBytes;
    private String phone;
    private int invitationId;

    private ServerAPI server;
    private static String currentUserPhone;

    public IncomingFriendRequestCardController() {

    }

    public IncomingFriendRequestCardController(String name, byte[] pictureBytes, String phone, int invitationId) {
        this.name = name;
        this.pictureBytes = pictureBytes;
        this.phone = phone;
        this.invitationId = invitationId;

        try {
            server = (ServerAPI) Client.getRegistry().lookup("server");
            currentUserPhone = "+20" + Client.getConnectedUser().getPhoneNumber();
        } catch (RemoteException e) {
            System.err.println("Remote Exception: " + e.getMessage());
        } catch (NotBoundException e) {
            System.err.println("NotBoundException: " + e.getMessage());
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setCardName(name);
        setCardPhone(phone);
        setImage(pictureBytes);
    }

    private void setImage(byte[] pictureBytes) {
        Image image = null;
        if (pictureBytes == null) {
            image = new Image(String.valueOf(ProfileController.class.getResource("/images/profile.png")));
        } else {
            image = new Image(new ByteArrayInputStream(pictureBytes));
        }
        senderImage.setFill(new ImagePattern(image));
    }

    private void setCardPhone(String phone) {
        senderPhoneNumberLabel.setText(phone);
    }

    private void setCardName(String name) {
        senderNameLabel.setText(name);
    }

    @FXML
    void handleAcceptPressed(ActionEvent event) {
        try {
            boolean friendRequestAccepted = server.acceptFriendRequest(invitationId);

            if (friendRequestAccepted) {
                ObservableList<AnchorPane> friendRequestList = IncomingFriendRequestController.getFriendRequestList();
                friendRequestList.removeIf(this::isControllerMatch);
            }
            try {
                ServerAPI server = (ServerAPI) Client.getRegistry().lookup("server");
                ChatCardsInfoDTO chat = server.getUserLastChatCardInfo(Client.getConnectedUser().getPhoneNumber());
                AnchorPane chatCard = ChatCardLoader.loadChatCardAnchorPane(chat.getChatID(),chat.getUnreadMessagesNumber(),chat.getName(),chat.getPicture(),chat.getLastMessage(),chat.getTimestamp());
                AllChatsPaneController.getChatsPanesList().add(chatCard);
            } catch (RemoteException | NotBoundException e) {
                throw new RuntimeException(e);
            }
        } catch (RemoteException e) {
            System.err.println("Accept Friend Request failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void handleCancelPressed(ActionEvent event) {
        try {
            boolean friendRequestCanceled = server.cancelFriendRequest(invitationId);

            if (friendRequestCanceled) {
                ObservableList<AnchorPane> friendRequestList = IncomingFriendRequestController.getFriendRequestList();

                friendRequestList.removeIf(this::isControllerMatch);
            }
        } catch (RemoteException e) {
            System.err.println("Cancel Friend Request failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isControllerMatch(AnchorPane anchorPane) {
        IncomingFriendRequestCardController controller = (IncomingFriendRequestCardController) anchorPane.getUserData();
        return controller == this;
    }

    public int getInvitationId() {
        return invitationId;
    }

}
