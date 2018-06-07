package ru.geekbrains.android3_5.presenter;

import android.annotation.SuppressLint;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import io.reactivex.Scheduler;
import ru.geekbrains.android3_5.model.entity.User;
import ru.geekbrains.android3_5.model.repo.CacheRepo;
import ru.geekbrains.android3_5.model.repo.PaperUserRepo;
import ru.geekbrains.android3_5.view.MainView;
import ru.geekbrains.android3_5.view.RepoRowView;
import timber.log.Timber;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> implements IRepoListPresenter {
    private static final String USERNAME = "martvas";
    private Scheduler scheduler;
    private CacheRepo cacheRepo;
    private User user;

    public MainPresenter(Scheduler scheduler) {
        this.scheduler = scheduler;
        cacheRepo = new PaperUserRepo();
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().init();
        loadInfo();
    }

    @SuppressLint("CheckResult")
    public void loadInfo() {
        getViewState().showLoading();
        cacheRepo.getUser(USERNAME)
                .observeOn(scheduler)
                .subscribe(user -> {
                    this.user = user;
                    getViewState().hideLoading();
                    getViewState().showAvatar(user.getAvatarUrl());
                    getViewState().setUsername(user.getLogin());
                    cacheRepo.getUserRepos(user)
                            .observeOn(scheduler)
                            .subscribe(repositories -> {
                                this.user.setRepos(repositories);

                                getViewState().updateRepoList();
                            }, throwable -> {
                                Timber.e(throwable, "Failed to get user repos");
                                getViewState().showError(throwable.getMessage());
                            });


                }, throwable -> {
                    Timber.e(throwable, "Failed to get user");
                    getViewState().showError(throwable.getMessage());
                });
    }

    @Override
    public void bindRepoListRow(int pos, RepoRowView rowView) {
        if (user != null) {
            rowView.setTitle(user.getRepos().get(pos).getName());
        }
    }

    @Override
    public int getRepoCount() {
        return user == null || user.getRepos() == null ? 0 : user.getRepos().size();
    }
}
