package com.labs.wocom.android.compose

import android.app.Application
import co.touchlab.kermit.Kermit
import com.labs.wocom.feature_periodic_table.AKAFeaturePeriodicTable
import com.labs.wocom.feature_periodic_table.AKARendererPeriodicTable
import com.labs.wocom.feature_periodic_table.domain.IntentPeriodicTable
import com.labs.wocom.feature_periodic_table.domain.StatePeriodicTable
import com.labs.wocom.feature_periodic_table.koinModulePeriodicTable
import com.labs.wocom.feature_periodic_table.model.PeriodicTable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module
import thorib.io.core.AKAEventLogger
import thorib.io.core.DispatcherProvider
import thorib.io.core.featureBuilder
import kotlinx.serialization.decodeFromString

class DemoApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin{
            androidLogger()
            androidContext(this@DemoApplication)
            modules(appModule)
        }
    }
}

val appModule = module {

    single<Json> {
        Json {
            isLenient = true
            prettyPrint = true
            useAlternativeNames = true
            ignoreUnknownKeys = true
            encodeDefaults = true
            coerceInputValues = true
        }
    }

    single { Kermit() }

    single<DispatcherProvider> {
        object : DispatcherProvider {

            override val main : CoroutineDispatcher = Dispatchers.Main

            override val default : CoroutineDispatcher = Dispatchers.Default

            override val io : CoroutineDispatcher = Dispatchers.IO

            override val unconfined : CoroutineDispatcher =
                Dispatchers.Unconfined
        }
    }

    single<PeriodicTable> {
        get<Json>().decodeFromString(data)
    }

    factory<AKARendererPeriodicTable> { RendererPeriodicTable() }

    single<AKAEventLogger> {
        AKAEventLogger { counter -> ((counter % 1) == 0) }
    }

    factory<AKAFeaturePeriodicTable> {
        featureBuilder<IntentPeriodicTable, StatePeriodicTable>(get())
    }
    factory<StatePeriodicTable> {
        StatePeriodicTable()
    }

    single<MutableStateFlow<String>>{
        MutableStateFlow<String>("")
    }
} + koinModulePeriodicTable()
