package com.example.dsy1105_005d_2026

import com.example.dsy1105_005d_2026.data.model.Post
import com.example.dsy1105_005d_2026.data.repository.PostRepository
import com.example.dsy1105_005d_2026.viewmodel.PostViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

/**
 * PRUEBA 3 - PostViewModel (catálogo externo con Retrofit).
 * Se MOCKEA el PostRepository: no se llama a internet, se simulan las respuestas.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest : StringSpec({

    val dispatcher = StandardTestDispatcher()

    beforeSpec { Dispatchers.setMain(dispatcher) }
    afterSpec { Dispatchers.resetMain() }

    "postList contiene los datos esperados después de fetchPosts()" {
        runTest(dispatcher) {
            val fakePosts = listOf(
                Post(userId = 1, id = 1, title = "Título 1", body = "Contenido 1"),
                Post(userId = 2, id = 2, title = "Título 2", body = "Contenido 2")
            )
            val fakeRepository = mockk<PostRepository>()
            coEvery { fakeRepository.getPosts() } returns fakePosts

            val testViewModel = PostViewModel(fakeRepository)
            advanceUntilIdle()

            testViewModel.postList.value shouldContainExactly fakePosts
            testViewModel.cargando.value shouldBe false
            testViewModel.error.value shouldBe null
        }
    }

    "si la API falla, la lista queda vacía y se informa el error" {
        runTest(dispatcher) {
            val fakeRepository = mockk<PostRepository>()
            coEvery { fakeRepository.getPosts() } throws RuntimeException("sin internet")

            val testViewModel = PostViewModel(fakeRepository)
            advanceUntilIdle()

            testViewModel.postList.value.shouldBeEmpty()
            testViewModel.cargando.value shouldBe false
            testViewModel.error.value!! shouldContain "sin internet"
        }
    }
})
