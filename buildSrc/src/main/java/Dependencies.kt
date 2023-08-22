object Versions {
    const val kotlin = "1.7.0"
    const val compose_ui_version = "1.2.1"
    const val compose_activity_version = "1.5.1"
    const val hilt = "2.43.2"
    const val gradle_version = "7.0.2"
    const val kotlin_serialization_version = "1.4.0"
    const val android_core_version = "1.9.0"
    const val android_lifecycle_version = "2.5.1"
    const val compose_material_version = "1.2.1"
    const val retrofit_version = "2.9.0"
    const val okhttp_version = "4.9.1"
    const val chucker_version = "3.5.2"
    const val converter_version = "0.8.0"
    const val landscapist_version = "2.0.0"
    const val accompanist_version = "0.25.1"
    const val compose_destination_version = "1.7.22-beta"
    const val hilt_navigation = "1.0.0"
    const val map_compose_version = "2.8.0"
    const val map_play_service = "18.0.2"
    const val camerax_version = "1.1.0"
    const val permission_version = "0.27.0"
    const val lifecycle_alpha = "2.6.0-alpha03"
    const val map_utils_version = "3.4.0"
    const val splash_core_version = "1.0.0"
    const val image_cropper_version = "4.5.0"
    const val firebase_bom = "31.3.0"
    const val data_store = "1.0.0"
}

object Deps {
    const val compose_activity = "androidx.activity:activity-compose:${Versions.compose_activity_version}"
    const val compose_ui = "androidx.compose.ui:ui:${Versions.compose_ui_version}"
    const val compose_ui_preview = "androidx.compose.ui:ui-tooling-preview:${Versions.compose_ui_version}"
    const val compose_material = "androidx.compose.material:material:${Versions.compose_material_version}"
    const val compose_ui_tooling = "androidx.compose.ui:ui-tooling:${Versions.compose_ui_version}"
    const val compose_ui_manifest = "androidx.compose.ui:ui-test-manifest:${Versions.compose_ui_version}"
    const val compose_lifecycle_viewmodel = "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifecycle_alpha}"
    const val compose_lifecycle_viewmodel_ktx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle_alpha}"
    const val compose_lifecycle_runtime = "androidx.lifecycle:lifecycle-runtime-compose:${Versions.lifecycle_alpha}"
    const val android_core_ktx = "androidx.core:core-ktx:${Versions.android_core_version}"
    const val android_lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.android_lifecycle_version}"
    const val build_gradle_plugin = "com.android.tools.build:gradle:${Versions.gradle_version}"
    const val hilt_gradle_plugin = "com.google.dagger:hilt-android-gradle-plugin:${Versions.hilt}"
    const val hilt_navigation = "androidx.hilt:hilt-navigation-compose:${Versions.hilt_navigation}"
    const val kotlin_serialization_gradle =
        "org.jetbrains.kotlin:kotlin-serialization:${Versions.kotlin}"

    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit_version}"
    const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp_version}"
    const val okhttp_logging_interceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp_version}"
    const val retrofit_serialization_converter = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:${Versions.converter_version}"

    const val chucker_debug = "com.github.chuckerteam.chucker:library:${Versions.chucker_version}"
    const val chucker_release = "com.github.chuckerteam.chucker:library-no-op:${Versions.chucker_version}"

    const val hilt = "com.google.dagger:hilt-android:${Versions.hilt}"
    const val hilt_kapt = "com.google.dagger:hilt-compiler:${Versions.hilt}"

    const val kotlin_serialization =
        "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlin_serialization_version}"

    const val landscapis_glide =
        "com.github.skydoves:landscapist-glide:${Versions.landscapist_version}"
    const val accompanist_pager =
        "com.google.accompanist:accompanist-pager:${Versions.accompanist_version}"
    const val accompanist_pager_indicator =
        "com.google.accompanist:accompanist-pager-indicators:0.26.4-beta"

    // compose destinations
    const val compose_destination_core_animation =
        "io.github.raamcosta.compose-destinations:animations-core:${Versions.compose_destination_version}"
    const val compose_destination_ksp =
        "io.github.raamcosta.compose-destinations:ksp:${Versions.compose_destination_version}"
    const val compose_animation =
        "com.google.accompanist:accompanist-navigation-animation:${Versions.accompanist_version}"

    // maps
    const val google_map_compose =
        "com.google.maps.android:maps-compose:${Versions.map_compose_version}"
    const val map_gms_play_service =
        "com.google.android.gms:play-services-maps:${Versions.map_play_service}"
    const val google_map_utils =
        "com.google.maps.android:maps-utils-ktx:${Versions.map_utils_version}"

    // camera
    //  Camerax dependencies
    const val camerax_core = "androidx.camera:camera-core:${Versions.camerax_version}"
    const val camerax_camera2 =  "androidx.camera:camera-camera2:${Versions.camerax_version}"
    const val camerax_lifecycle =  "androidx.camera:camera-lifecycle:${Versions.camerax_version}"
    const val camerax_camera_view = "androidx.camera:camera-view:${Versions.camerax_version}"
    const val camerax_camera_extension = "androidx.camera:camera-extensions:${Versions.camerax_version}"

    // permission
    const val accompanist_permission = "com.google.accompanist:accompanist-permissions:${Versions.permission_version}"

    const val splash_screen_api = "androidx.core:core-splashscreen:${Versions.splash_core_version}"

    const val image_cropper =
        "com.vanniktech:android-image-cropper:${Versions.image_cropper_version}"

    const val firebase_bom = "com.google.firebase:firebase-bom:${Versions.firebase_bom}"
    const val firebase_messaging = "com.google.firebase:firebase-messaging-ktx:23.1.2"
    const val firebase_crashlytics = "com.google.firebase:firebase-crashlytics-ktx:18.3.6"
    const val firebase_analytics = "com.google.firebase:firebase-analytics-ktx:21.2.1"

    const val data_store = "androidx.datastore:datastore-preferences:${Versions.data_store}"
}