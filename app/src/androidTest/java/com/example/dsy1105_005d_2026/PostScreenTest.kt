package com.example.dsy1105_005d_2026

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.dsy1105_005d_2026.data.model.Post
import com.example.dsy1105_005d_2026.data.repository.PostRepository
import com.example.dsy1105_005d_2026.ui.catalogo.PostScreen
import com.example.dsy1105_005d_2026.viewmodel.PostViewModel
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

/** Prueba de UI (instrumentada): el catálogo muestra en pantalla los títulos que entrega el repositorio. */
class PostScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun el_titulo_de_post_debe_aparecer_en_pantalla() {
        val fakePosts = listOf(
            Post(userId = 1, id = 1, title = "Título 1", body = "Contenido 1"),
            Post(userId = 2, id = 2, title = "Título 2", body = "Contenido 2")
        )

        val fakeRepository = mockk<PostRepository>()
        coEvery { fakeRepository.getPosts() } returns fakePosts

        val fakeViewModel = PostViewModel(fakeRepository)

        composeRule.setContent {
            PostScreen(viewModel = fakeViewModel)
        }

        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.onAllNodes(hasText("Título: Título 1")).fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Título: Título 1").assertIsDisplayed()
        composeRule.onNodeWithText("Título: Título 2").assertIsDisplayed()
    }
}
