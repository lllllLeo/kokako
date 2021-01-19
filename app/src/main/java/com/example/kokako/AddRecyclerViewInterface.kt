package com.example.kokako

import android.view.View

// 커스텀 인터페이스
/*
* 리사이클러뷰어답터에서 클릭리스너 설정하면 비용이 많이 발생한다더라 그래서 인터페이스로 활용
* */
interface AddRecyclerViewInterface {
    fun onRemoveClicked(v : View, position: Int)
}
