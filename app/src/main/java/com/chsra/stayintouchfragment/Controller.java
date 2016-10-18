package com.chsra.stayintouchfragment;

public interface Controller {
    void goToSignup();
    void goToLogin();
    void goToConversations();
    void goToContacts();
    void goToProfile();
    void goToMessages(Conversation conversation);
    void goToContact(User contact);
    void closeCurrent();
}