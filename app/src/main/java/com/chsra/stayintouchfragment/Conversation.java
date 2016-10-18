package com.chsra.stayintouchfragment;

import java.io.Serializable;

/**
 * Created by chsra on 27-Apr-16.
 */
public class Conversation implements Serializable {
    private String key, deletedBy, user1, user2;
    private boolean archivedByUser1, archivedByUser2;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

    public boolean isArchivedByUser1() {
        return archivedByUser1;
    }

    public void setArchivedByUser1(boolean archivedByUser1) {
        this.archivedByUser1 = archivedByUser1;
    }

    public boolean isArchivedByUser2() {
        return archivedByUser2;
    }

    public void setIsArchivedByUser2(boolean isArchivedByUser2) {
        this.archivedByUser2 = isArchivedByUser2;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "key='" + key + '\'' +
                ", deletedBy='" + deletedBy + '\'' +
                ", user1='" + user1 + '\'' +
                ", user2='" + user2 + '\'' +
                ", archivedByUser1=" + archivedByUser1 +
                ", isArchivedByUser2=" + archivedByUser2 +
                '}';
    }

    public boolean isDeleted(User user){
        return (deletedBy != null && deletedBy.equals(user.getEmail()));
    }

    public boolean isHidden(User user){
        return (isDeleted(user) || isArchived(user));
    }

    public boolean isArchived(User user){
        if(
            (user1 != null && user1.equals(user.getEmail()) && archivedByUser1) ||
            (user2 != null && user2.equals(user.getEmail()) && archivedByUser2)){
            return true;
        } else {
            return false;
        }
    }

    public boolean isDisplayable(User user){
        if (
            ((user1 != null && getUser1().equals(user.getEmail())) || (user2 != null && getUser2().equals(user.getEmail()))) &&
            !isDeleted(user) &&
            !isArchived(user)) {
            return true;
        } else{
            return false;
        }
    }
}
