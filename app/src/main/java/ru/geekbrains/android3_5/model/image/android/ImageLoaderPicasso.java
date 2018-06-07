package ru.geekbrains.android3_5.model.image.android;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.Date;

import io.realm.Realm;
import ru.geekbrains.android3_5.model.common.NetworkStatus;
import ru.geekbrains.android3_5.model.common.Utils;
import ru.geekbrains.android3_5.model.entity.realm.RealmImageRepo;
import ru.geekbrains.android3_5.model.image.ImageLoader;
import timber.log.Timber;


public class ImageLoaderPicasso implements ImageLoader<ImageView> {
    String newImgPath;

    @Override
    public void loadInto(@Nullable String url, ImageView container) {
        if (NetworkStatus.isOffline()) {
            Realm realm = Realm.getDefaultInstance();
            RealmImageRepo realmImageRepo = realm.where(RealmImageRepo.class).equalTo("img_url", Utils.MD5(url)).findFirst();

            if (realmImageRepo != null) {
                Picasso.get().load(new File(realmImageRepo.getPathToImg())).into(container);
            }
            realm.close();
        } else {
            Picasso.get().load(url).into(new Target() {

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    Realm realm = Realm.getDefaultInstance();
                    RealmImageRepo realmImageRepo = realm.where(RealmImageRepo.class).equalTo("img_url", Utils.MD5(url)).findFirst();

                    if (realmImageRepo == null) {
                        newImgPath = container.getContext().getFilesDir().toString() + "/img" + new Date().getTime() + ".jpg";
                        CommonUtils.saveBitmapToDevice(bitmap, newImgPath);
                        realm.executeTransaction(innerRealm -> {
                            RealmImageRepo newRealmImgRepo = realm.createObject(RealmImageRepo.class, Utils.MD5(url));
                            newRealmImgRepo.setPathToImg(newImgPath);
                        });
                    }
                    realm.close();
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    Timber.d(e, "Bitmap Failed");
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                    Timber.d("Problem in image prepared load ");
                }
            });
            if (newImgPath != null) Picasso.get().load(new File(newImgPath)).into(container);
        }
    }
}
