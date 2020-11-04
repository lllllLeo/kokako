package com.example.kokako

import android.app.Application

// context를 전역으로 빼기위해. AppCompatActivity가 context를 가지고있는데 뷰홀더는 context를 가지고 있지 않음
// context를 가지고 오기 위해서
class App : Application() {

    // Singleton
    // Application레벨이 생성이 되었을때 instance를 자기자신으로 넣어줌 this.로
    // manifests에 이름 .App해야함
    companion object {
        lateinit var instance: App // 나중에 값을 넣는다. 자기사신을 가져옴. 썼던애들 가져온다.
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}