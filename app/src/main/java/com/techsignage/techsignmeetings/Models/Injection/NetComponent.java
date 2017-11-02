package com.techsignage.techsignmeetings.Models.Injection;

import com.techsignage.techsignmeetings.MainActivity;

import javax.inject.Singleton;
import dagger.Component;

/**
 * Created by heat on 9/6/2017.
 */

@Singleton
@Component(modules={AppModule.class, NetModule.class})
public interface NetComponent {
    void inject(MainActivity activity);
}
