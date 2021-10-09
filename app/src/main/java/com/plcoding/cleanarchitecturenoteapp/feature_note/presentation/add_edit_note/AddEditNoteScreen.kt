package com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.plcoding.cleanarchitecturenoteapp.core.util.TestTags.CONTEXT_TEXT_FIELD
import com.plcoding.cleanarchitecturenoteapp.core.util.TestTags.TITLE_TEXT_FIELD
import com.plcoding.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.plcoding.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note.components.TransparentHintTextField
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun AddEditNoteScreen(
    navController: NavController,
    noteColor: Int,
    viewModel: AddEditViewModel = hiltViewModel()
) {
    val titleState = viewModel.noteTitle.value
    val contentState = viewModel.noteContent.value

    val scaffoldState = rememberScaffoldState()

    val noteBackgroundAnimatable = remember {
        Animatable(
            Color(if (noteColor != -1) noteColor else viewModel.noteColor.value)
        )
    }
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = true){
        viewModel.eventFlow.collectLatest { event ->
            when(event){
                is AddEditViewModel.UiEvent.ShowSnackbar ->{
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = event.message
                    )

                }
                is AddEditViewModel.UiEvent.SaveNote -> {
                    navController.navigateUp()
                }
            }
        }
    }

    Scaffold(
        floatingActionButton =
        {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(AddEditNoteEvent.SaveNote)
                },
                backgroundColor = MaterialTheme.colors.primary
            ) {
                Icon(imageVector = Icons.Default.Save, contentDescription = "Save note")
            }
        },
        scaffoldState = scaffoldState
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(noteBackgroundAnimatable.value)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Note.noteColors.forEach { color ->
                    val coloInt = color.toArgb()
                    Box(modifier = Modifier
                        .size(50.dp)
                        .shadow(15.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(color = color)
                        .border(
                            width = 3.dp,
                            color = if (viewModel.noteColor.value == coloInt) {
                                Color.Black
                            } else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable {
                            scope.launch {
                                noteBackgroundAnimatable.animateTo(
                                    targetValue = Color(color = coloInt),
                                    animationSpec = tween(
                                        durationMillis = 500
                                    )
                                )
                            }
                            viewModel.onEvent(AddEditNoteEvent.ChangeColor(color = coloInt))
                        })

                }

            }
            Spacer(modifier = Modifier.height(16.dp))
            TransparentHintTextField(
                text = titleState.text,
                hint = titleState.hint,
                onValueChange = {
                                viewModel.onEvent(AddEditNoteEvent.EnteredTitle(it))
                },
                onFocusChange ={
                    viewModel.onEvent(AddEditNoteEvent.ChangeTitleFocus(it))
                },
                isHintVisible = titleState.isVisible,
                singleLine = true,
                textStyle = MaterialTheme.typography.h5,
                testTag = TITLE_TEXT_FIELD

            )
            Spacer(modifier = Modifier.height(16.dp))
            TransparentHintTextField(
                text = contentState.text,
                hint = contentState.hint,
                onValueChange = {
                    viewModel.onEvent(AddEditNoteEvent.EnteredContent(it))
                },
                onFocusChange ={
                    viewModel.onEvent(AddEditNoteEvent.ChangeContentFocus(it))
                },
                isHintVisible = contentState.isVisible,
                textStyle = MaterialTheme.typography.body1,
                modifier = Modifier.fillMaxHeight(),
                testTag =  CONTEXT_TEXT_FIELD

            )

        }
    }
}
