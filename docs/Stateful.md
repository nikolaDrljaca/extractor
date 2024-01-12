## Compose Components
Sometimes it is beneficial to create *stateful* components, or rather to encapsulate 
the state of a component in a record/data class holder.

In our opinionated architecture this is used for the `Molecule` level.

Especially the case when it comes to dealing with basically more than one `TextField`.

For these scenarios, follow this approach:
- Create a `class <some_component_name>State` as state holder.
- Make sure observable properties are backed by mutable states, and that there is a function that allows mutation.
  - With `kotlinx.coroutines`: `MutableStateFlow() and MutableStateFlow.update {...}`
  - With `androidx.compose.runtime`: `mutableStateOf()` and standard value assignment
- State class has a `Saver` implementation to work with `rememberSaveable`
- Factory `rememberSaveable*` or `remember*` function that wraps the constructor.
- Composable function for that widget which takes in parameter of the state holder type and exposes any needed events as callbacks.
  - Callbacks concerned with the state holder do not need to bubble up.
  - Events concerning calling components should bubble up.

For more details, take a look at the `/src/main/java/com.drbrosdev.extractor/ui/components/extractorsearchview` package.

### Short Example
```kotlin
@Stable
class FormState(initialValue: String) {
    var textValue by mutableStateOf(initialValue)
        private set
    
    fun update(block: String.() -> String) {
        textValue = block(textValue)
    }
    
    companion object {
        val Saver = Saver {/*...*/}
    }
}

fun FormState.textAsFlow() = snapshotFlow { textValue }

@Composable
fun rememberFormState(initial: String = ""): FormState {
    //also remember() {...} if state preservation is not necessary
    return rememberSaveable(saver = FormState.Saver()) {
        FormState(initial)
    }
}

//...

@Composable
fun FormView(formState: FormState = rememberFormState()) {
    //...
}
```

## A note on `rememberSaveable`
This function and the `Saver` API underneath bind to a `SavedStateRegistryOwner`(usually the `BackStackEntry` or better yet think `Activity`).
The point here is that large data(lists or large objects) should not be saved in this way to survive process death,
as the underlying `Bundle` is very limited in size. 

Save data that makes sense saving.

Example: A search bar and a list of content. Persist the search query in the bundle and perform the search again
to get the results as opposed to saving both the query and the whole list.