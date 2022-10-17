package com.xridwan.mystoryfinal

import com.xridwan.mystoryfinal.data.network.response.*

object DataDummy {

    fun dummyLoginResponse(): LoginResponse {
        return LoginResponse(
            LoginResult(
                "name",
                "id",
                "token"
            ),
            false,
            "token"
        )
    }

    fun dummyRegisterResponse(): RegisterResponse {
        return RegisterResponse(
            false,
            "success"
        )
    }

    fun dummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "created + $i",
                "name + $i",
                "description + $i",
                i.toDouble(),
                "id + $i",
                i.toDouble()
            )
            items.add(story)
        }
        return items
    }

    fun dummyStoryWithLocationResponse(): StoryResponse {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "created + $i",
                "name + $i",
                "description + $i",
                i.toDouble(),
                "id + $i",
                i.toDouble()
            )
            items.add(story)
        }
        return StoryResponse(
            items,
            false,
            "success"
        )
    }

    fun dummyAddStoryResponse(): FileUploadResponse {
        return FileUploadResponse(
            false,
            "success"
        )
    }
}