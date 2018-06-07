package ru.geekbrains.android3_5.model.repo;

import java.util.List;

import io.reactivex.Observable;
import ru.geekbrains.android3_5.model.entity.Repository;
import ru.geekbrains.android3_5.model.entity.User;

public interface CacheRepo {
    Observable<User> getUser(String username);

    Observable<List<Repository>> getUserRepos(User user);
}
