package com.plcoding.cleanarchitecturenoteapp.feature_note.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.plcoding.cleanarchitecturenoteapp.core.util.TestTags.CONTEXT_TEXT_FIELD
import com.plcoding.cleanarchitecturenoteapp.core.util.TestTags.NOTE_ITEM
import com.plcoding.cleanarchitecturenoteapp.core.util.TestTags.TITLE_TEXT_FIELD
import com.plcoding.cleanarchitecturenoteapp.di.AppModule
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note.AddEditNoteScreen
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.notes.NotesScreen
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.util.Screens
import com.plcoding.cleanarchitecturenoteapp.ui.theme.CleanArchitectureNoteAppTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class NoteEndToEndTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @ExperimentalAnimationApi
    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.setContent {
            CleanArchitectureNoteAppTheme {

                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screens.NotesScreen.route
                ) {
                    composable(route = Screens.NotesScreen.route) {
                        NotesScreen(navController = navController)
                    }
                    composable(route = Screens.AddEditNoteScreen.route +
                            "?noteId={noteId}&noteColor={noteColor}",
                        arguments = listOf(
                            navArgument(
                                name = "noteId"
                            ) {
                                type = NavType.IntType
                                defaultValue = -1
                            },
                            navArgument(
                                name = "noteColor"
                            ) {
                                type = NavType.IntType
                                defaultValue = -1
                            }

                        )
                    ) {
                        val color = it.arguments?.getInt("noteColor") ?: -1
                        AddEditNoteScreen(
                            navController = navController,
                            noteColor = color
                        )
                    }
                }
            }
        }
    }

    @Test
    fun saveNewNote_editAfterwards() {
        composeRule.onNodeWithContentDescription("Add note").performClick()

        composeRule
            .onNodeWithTag(TITLE_TEXT_FIELD)
            .performTextInput("test-title")
        composeRule
            .onNodeWithTag(CONTEXT_TEXT_FIELD)
            .performTextInput("test-content")
        composeRule.onNodeWithContentDescription("Save note").performClick()

        composeRule.onNodeWithText("test-title").assertIsDisplayed()
        composeRule.onNodeWithText("test-title").performClick()

        composeRule
            .onNodeWithTag(TITLE_TEXT_FIELD)
            .assertTextEquals("test-title")
        composeRule
            .onNodeWithTag(CONTEXT_TEXT_FIELD)
            .assertTextEquals("test-content")
        composeRule
            .onNodeWithTag(TITLE_TEXT_FIELD)
            .performTextInput("2")
        composeRule
            .onNodeWithContentDescription("Save note")
            .performClick()

        composeRule.onNodeWithText("test-title2").assertIsDisplayed()


    }

    @Test
    fun saveNewNotes_orderByTitleDescending() {
        for (i in 1..3) {
            composeRule.onNodeWithContentDescription("Add note").performClick()

            composeRule
                .onNodeWithTag(TITLE_TEXT_FIELD)
                .performTextInput(i.toString())
            composeRule
                .onNodeWithTag(CONTEXT_TEXT_FIELD)
                .performTextInput(i.toString())
            composeRule.onNodeWithContentDescription("Save note").performClick()
        }

        composeRule.onNodeWithText("1").assertIsDisplayed()
        composeRule.onNodeWithText("2").assertIsDisplayed()
        composeRule.onNodeWithText("3").assertIsDisplayed()


        composeRule
            .onNodeWithContentDescription("Sort")
            .performClick()
        composeRule
            .onNodeWithContentDescription("Title")
            .performClick()
        composeRule
            .onNodeWithContentDescription("Descending")
            .performClick()

        composeRule.onAllNodesWithTag(NOTE_ITEM)[0]
            .assertTextContains("3")
        composeRule.onAllNodesWithTag(NOTE_ITEM)[1]
            .assertTextContains("2")
        composeRule.onAllNodesWithTag(NOTE_ITEM)[2]
            .assertTextContains("1")


    }

}