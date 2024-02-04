package com.connectify.utils;

import com.connectify.Interfaces.ConnectedUser;
import com.connectify.controller.ChatCardController;
import com.connectify.dto.MessageDTO;
import com.connectify.mapper.MessageMapper;
import com.connectify.model.entities.Message;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import org.controlsfx.control.Notifications;
import com.connectify.controller.IncomingFriendRequestController;
import com.connectify.dto.IncomingFriendInvitationResponse;
import com.connectify.loaders.IncomingFriendRequestCardLoader;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.Notifications;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class CurrentUser extends UnicastRemoteObject implements ConnectedUser, Serializable {

    private final String phoneNumber;

    private static final Map<Integer, ObservableList<Message>> chatListMessagesMap = new HashMap<>();

    public CurrentUser(String phoneNumber) throws RemoteException {
        super();
        this.phoneNumber = phoneNumber;
    }

    @Override
    public void receiveNotification(String title, String body) throws RemoteException {
        Platform.runLater(()->{
            Notifications.create()
                    .title(title)
                    .text(body)
                    .position(Pos.BOTTOM_RIGHT).showInformation();
        });
    }

    public String getPhoneNumber() throws RemoteException {
        return phoneNumber;
    }

    @Override
    public void receiveMessage(MessageDTO messageDTO) throws RemoteException {
        System.out.println(messageDTO.getContent());
        MessageMapper mapper = MessageMapper.INSTANCE;
        Message receivedMessage = mapper.messageDtoToMessage(messageDTO);
        ChatCardHandler.updateChatCard(receivedMessage);
        int chatID = messageDTO.getChatId();
        chatListMessagesMap.putIfAbsent(chatID, FXCollections.observableArrayList());
        chatListMessagesMap.get(chatID).add(receivedMessage);
    }



    public static ObservableList<Message> getMessageList(int chatID) {
        chatListMessagesMap.putIfAbsent(chatID, FXCollections.observableArrayList());
        return chatListMessagesMap.get(chatID);
    }

    @Override
    public void receiveFriendRequest(IncomingFriendInvitationResponse friendInvitation) throws RemoteException {
        AnchorPane newFriendRequestCard = IncomingFriendRequestCardLoader
                .loadNewIncomingFriendRequestCardPane(
                        friendInvitation.getName(), friendInvitation.getPhoneNumber(),
                        friendInvitation.getPicture(), friendInvitation.getInvitationId());

        ObservableList<AnchorPane> friendRequestList = IncomingFriendRequestController.getFriendRequestList();

        Platform.runLater(() -> {
            friendRequestList.add(newFriendRequestCard);
        });

        String title = "New Friend Request";
        String message = friendInvitation.getName() + " has sent you a friend request.";
        try {
            showNotification(title, message);
        } catch (RemoteException e) {
            System.err.println("Error receive Friend Request. case:" + e.getMessage());
        }
    }

    @Override
    public void showNotification(String title, String message) throws RemoteException {
        Platform.runLater(() -> {
            Notifications.create()
                    .title(title)
                    .text(message)
                    .darkStyle()
                    .threshold(3, Notifications.create().title("Collapsed Notification"))
                    .showInformation();
        });
    }


}
