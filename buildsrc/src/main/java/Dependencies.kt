object Ver {
    val support_lib = "28.0.0"
    val kotlin = "1.2.71"
    val anko = "0.10.1"
    val dagger = "2.16"
    val retrofit = "2.3.0"
    val rxjava = "2.1.9"
    val rxandroid = "2.0.1"
    val room = "1.1.1"
    val play_services = "15.0.1"
    val firebase = "16.0.0"
}

object Libs {
    // Support
    val support_annotations = "com.android.support:support-annotations:${Ver.support_lib}"
    val support_appcompat_v7 = "com.android.support:appcompat-v7:${Ver.support_lib}"
    val support_design = "com.android.support:design:${Ver.support_lib}"
    val support_cardview = "com.android.support:cardview-v7:${Ver.support_lib}"
    val support_support_v4 = "com.android.support:support-v4:${Ver.support_lib}"

    // Playservices
    val play_services_location = "com.google.android.gms:play-services-location:${Ver.play_services}"
    val play_services_maps = "com.google.android.gms:play-services-maps:${Ver.play_services}"

    // Firebase
    val firebase_config = "com.google.firebase:firebase-config:${Ver.firebase}"
    val firebase_core = "com.google.firebase:firebase-core:${Ver.firebase}"
    val firebase_messaging = "com.google.firebase:firebase-messaging:17.3.2"

    // Test
    val support_test = "com.android.support.test:runner:1.0.2"
    val support_test_espresso = "com.android.support.test.espresso:espresso-core:3.0.2"

    // Kotlin
    val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Ver.kotlin}"

    // Anko
    val anko_coroutines = "org.jetbrains.anko:anko-coroutines:${Ver.anko}"
    val anko_commons = "org.jetbrains.anko:anko-commons:${Ver.anko}"

    // JUnit
    val junit = "junit:junit:4.12"

    // Dagger
    val dagger = "com.google.dagger:dagger:${Ver.dagger}"
    val dagger_android = "com.google.dagger:dagger-android:${Ver.dagger}"
    val dagger_android_support = "com.google.dagger:dagger-android-support:${Ver.dagger}"
    val dagger_android_processor = "com.google.dagger:dagger-android-processor:${Ver.dagger}"
    val dagger_compiler = "com.google.dagger:dagger-compiler:${Ver.dagger}"

    // Retrofit
    val retrofit = "com.squareup.retrofit2:retrofit:${Ver.retrofit}"
    val retrofit_gson_converter = "com.squareup.retrofit2:converter-gson:${Ver.retrofit}"
    val retrofit_rxjava_adapter = "com.squareup.retrofit2:adapter-rxjava2:${Ver.retrofit}"

    // Rx
    val rxjava = "io.reactivex.rxjava2:rxjava:${Ver.rxjava}"
    val rxjava_android = "io.reactivex.rxjava2:rxandroid:${Ver.rxandroid}"

    // Room
    val room_runtime = "android.arch.persistence.room:runtime:${Ver.room}"
    val room_rxjava = "android.arch.persistence.room:rxjava2:${Ver.room}"
    val room_compiler = "android.arch.persistence.room:compiler:${Ver.room}"
    val room_testing = "android.arch.persistence.room:testing:${Ver.room}"

    // Eventbus
    val eventbus = "org.greenrobot:eventbus:3.1.1"

    // Chatkit
    val chatkit = "com.github.stfalcon:chatkit:0.2.2"


}