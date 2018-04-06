package it.gruppoinfor.home2work.di.module

import android.content.Context
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import it.gruppoinfor.home2work.common.ImageLoader
import it.gruppoinfor.home2work.common.JobScheduler
import it.gruppoinfor.home2work.common.PicassoImageLoader
import javax.inject.Singleton

@Module
class AppModule constructor(context: Context) {

    private val appContext = context.applicationContext

    @Singleton
    @Provides
    fun provideAppContext(): Context {
        return appContext
    }

    @Singleton
    @Provides
    fun provideImageLoader(context: Context): ImageLoader {
        return PicassoImageLoader(Picasso.with(context))
    }

    @Singleton
    @Provides
    fun provideJobScheduler(context: Context): JobScheduler {
        return JobScheduler(context)
    }


}