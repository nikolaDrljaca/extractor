## Compose Components
Sometimes it is beneficial to create *stateful* components, or rather to encapsulate 
the state of a component in a record/data class holder.

Especially the case when it comes to dealing with basically more than one `TextField`.

For these scenarios, follow this approach:
- Create a `data class` as state holder.
- Make sure observable properties are backed by mutable states, and that there is a function that allows mutation.
- Factory *smart* function eg. `fun MyStateHolder() = MyStateHolder(...)`
- Factory `remember*` function that wraps the one above
- State class has a `Saver` implementation to work with `rememberSaveable`
- Composable function for that widget which takes in parameter of the state holder type and exposes any needed events as callbacks.

## Example
For more details, take a look at `/src/main/java/com.drbrosdev.extractor/ui/components/extractorsearchview`

```kotlin
class FormState(initialValue: String) {
    var textValue by mutableStateOf(initialValue)
        private set
    
    fun update(block: String.() -> String) {
        textValue = block(textValue)
    }
    
    companion object {
        fun Saver() = Saver {/*...*/}
    }
}

fun FormState.textAsFlow() = snapshotFlow { textValue }

fun FormState(initialValue: String) = FormState(initialValue)

@Composable
fun rememberFormState(initial: String = ""): FormState {
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