package com.drbrosdev.extractor.ui.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/*
Component based dev:
- component state as an interface
- default class implementation for state interface
- factory rememberFunction
- factory function for use outside of composition
- saver implementation

- composable that is public and takes in the State object
- composable that is private and takes in only primitive types
- preview for that private composable

## Problems
1. Interface state contract does not indicate that the values are stateful -> Fix with State<>
2. What happens if the component has actions?
    - Callback functions
    - channel/flow of events -> need sealed class
    - observe changes to values, other callbacks such as onSubmit/onDone/onClick handled outside state contract
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FooBar(
    modifier: Modifier = Modifier,
) {
    val foo = rememberDatePickerState()
    foo.selectedDateMillis = 1L
    DatePicker(state = foo)
}