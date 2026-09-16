package com.crystal.kip.content;

public class Content {

    protected Long id;

    public Long getId() {
        return id;
    }

    public boolean equals(Content content) {
        if (this.id == content.id) {
            return true;
        }

        return false;
    }
}
