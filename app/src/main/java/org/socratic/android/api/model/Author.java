package org.socratic.android.api.model;

import com.stfalcon.chatkit.commons.models.IUser;

/**
 * Created by jessicaweinberg on 10/3/17.
 */

public class Author implements IUser {
    private String id;
    private String name;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return null;
    }

    public void setID(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}