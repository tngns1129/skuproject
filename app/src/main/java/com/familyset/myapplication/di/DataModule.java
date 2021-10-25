package com.familyset.myapplication.di;

import com.familyset.myapplication.data.api.UsersService;
import com.familyset.myapplication.data.repo.UsersRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DataModule {

    @Singleton
    @Provides
    public static UsersRepository provideUsersRepository(UsersService usersService) {
        return new UsersRepository(usersService);
    }
}
