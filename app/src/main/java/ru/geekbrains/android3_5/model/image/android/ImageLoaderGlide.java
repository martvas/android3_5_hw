package ru.geekbrains.android3_5.model.image.android;

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.util.Date;

import io.realm.Realm;
import ru.geekbrains.android3_5.model.common.NetworkStatus;
import ru.geekbrains.android3_5.model.common.Utils;
import ru.geekbrains.android3_5.model.entity.realm.RealmImageRepo;
import ru.geekbrains.android3_5.model.image.ImageLoader;
import timber.log.Timber;

public class ImageLoaderGlide implements ImageLoader<ImageView> {
    @Override
    public void loadInto(@Nullable String url, ImageView container) {
        if (NetworkStatus.isOffline()) {
            Realm realm = Realm.getDefaultInstance();
            RealmImageRepo realmImageRepo = realm.where(RealmImageRepo.class).equalTo("img_url", Utils.MD5(url)).findFirst();

            if (realmImageRepo != null) {
                Glide.with(container.getContext())
                        .load(new File(realmImageRepo.getPathToImg()))
                        .into(container);
            }
            realm.close();
        } else {
            GlideApp.with(container.getContext()).asBitmap().load(url).listener(new RequestListener<Bitmap>() {

                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                    Timber.e(e, "Failed to load image");
                    return false;
                }

                @Override
                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                    Realm realm = Realm.getDefaultInstance();
                    RealmImageRepo realmImageRepo = realm.where(RealmImageRepo.class).equalTo("img_url", Utils.MD5(url)).findFirst();

                    if (realmImageRepo == null) {
                        String newImgPath = container.getContext().getFilesDir().toString() + "/img" + new Date().getTime() + ".jpg";
                        CommonUtils.saveBitmapToDevice(resource, newImgPath);
                        realm.executeTransaction(innerRealm -> {
                            RealmImageRepo newRealmImgRepo = realm.createObject(RealmImageRepo.class, Utils.MD5(url));
                            newRealmImgRepo.setPathToImg(newImgPath);
                        });
                    }
                    return false;
                }
            }).into(container);
        }
    }
}
