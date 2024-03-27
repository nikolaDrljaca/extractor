## Compose Components

Most of the time, it is beneficial to create state holder classes or to model the state of a screen
using a class based approach.

Using domain modeling ideas about Product Types and Sum Types it's possible to create powerful
models
for state management. The only important thing is that they
utilize `androidx.compose.runtime.State`.

## Examples
### Approach 1 - Immutable Sum Types
```kotlin
sealed class ExtractorSearchScreenUiState {

  @Immutable
  data class Content(
    val images: List<Extraction>,
  ) : ExtractorSearchScreenUiState()

  data object Loading : ExtractorSearchScreenUiState()

  data object StillIndexing : ExtractorSearchScreenUiState()

  data class ShowSuggestions(
    val suggestedSearchState: ExtractorSuggestedSearchState
  ) : ExtractorSearchScreenUiState()

  data object Empty : ExtractorSearchScreenUiState()
}
```
In this approach, we utilize the sum type (current value can be one of the options) and sealed hierarchies
provide great support for this. In our UI code we can `when` on this and provide different components for 
each, or even use `AnimatedContent`. Useful for screen level states.

Expected to be used by wrapping with a `MutableStateFlow` or `MutableState`.

### Approach 2 - Immutable Product Type
```kotlin
@Immutable
data class ExtractorStatusDialogUiModel(
  val onDeviceCount: Int = 0,
  val inStorageCount: Int = 0,
  val isExtractionRunning: Boolean = false
) {
  val percentage = inStorageCount safeDiv onDeviceCount

  val percentageText = "${percentage.times(100).toInt()}%"

  val shouldAllowExtraction = when {
    isExtractionRunning -> false
    onDeviceCount == inStorageCount -> false
    else -> true
  }
}
```
Represented as simple data classes, product types (a value needs to have all listed properties) are useful 
for state modelling on simple screens and are a great pair with computed properties -> `percentage`.

Expected to be used by wrapping with a `MutableStateFlow` or `MutableState`.

### Approach 3 - Stable State Holder
```kotlin

@Stable
class ExtractorFeedbackState(
  initUserText: String = "",
  initIncludeEventLogs: Boolean = false
) {

    var userText by mutableStateOf(initUserText)
        private set

    var includeEventLogs by mutableStateOf(initIncludeEventLogs)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var enabled by mutableStateOf(true)
        private set
  
  //... omitted code ...
}
```
A more precise approach to modeling state, composing over observable properties for a more granular 
recomposition flow. Update functions are exposed to either update each property or update multiple ones
to achieve desired end state. 

In our opinionated architecture, these is used for the `Molecule` and `Organism` levels.

Especially the case when it comes to dealing with basically more than one `TextField`.

For these scenarios, follow this approach:

- Create a `class <some_component_name>State` as state holder.
- Make sure observable properties are backed by mutable states, and that there is a function that
  allows mutation.
    - With `kotlinx.coroutines`: `MutableStateFlow() and MutableStateFlow.update {...}`
    - With `androidx.compose.runtime`: `mutableStateOf()` and standard value assignment
- State class has a `Saver` implementation to work with `rememberSaveable`
- Factory `rememberSaveable*` or `remember*` function that wraps the constructor.
- Composable function for that widget which takes in parameter of the state holder type and exposes
  any needed events as callbacks.
    - Callbacks concerned with the state holder do not need to bubble up.
    - Events concerning calling components should bubble up.

For more details, take a look at
the `/src/main/java/com.drbrosdev.extractor/ui/components/extractorsearchview` package.

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
        val Saver = Saver {/*...*/ }
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

This function and the `Saver` API underneath bind to a `SavedStateRegistryOwner`(usually
the `BackStackEntry` or better yet think `Activity`).
The point here is that large data(lists or large objects) should not be saved in this way to survive
process death,
as the underlying `Bundle` is very limited in size.

Save data that makes sense saving.

Example: A search bar and a list of content. Persist the search query in the bundle and perform the
search again
to get the results as opposed to saving both the query and the whole list.