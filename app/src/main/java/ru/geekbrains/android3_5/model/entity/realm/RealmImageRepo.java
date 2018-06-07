package ru.geekbrains.android3_5.model.entity.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmImageRepo extends RealmObject {
    @PrimaryKey
    private String img_url;
    private String pathToImg;

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getPathToImg() {
        return pathToImg;
    }

    public void setPathToImg(String pathToImg) {
        this.pathToImg = pathToImg;
    }
}
