package com.connectify.model.dao;

import com.connectify.dto.ChatCardsInfoDTO;
import com.connectify.model.entities.ChatMember;

import java.sql.SQLException;
import java.util.List;

public interface ChatMembersDAO extends DAO<ChatMember, ChatMember> {
    List<ChatMember> getAllUserChats(String userID) throws SQLException;

    List<ChatCardsInfoDTO> getAllUserChatsInfo(String userId) throws SQLException;

    List<ChatMember> getAllChatMembers(int chatID) throws SQLException;

}
