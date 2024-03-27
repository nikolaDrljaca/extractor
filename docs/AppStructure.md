# Application Structure

In this section we go over the **project folder structure** and the general application
architecture.

Choices made here are opinionated, as is the implementation behind the theoretical concepts of
different architectures/tech stacks/whatever(s).

They might not be the most optimal or industry standard, but they have been chosen because they work
or the authors know how to work with them.

These ideas are inspired by Adams
page [here](https://github.com/AdamMc331/TOA/blob/development/documentation/Architecture.md).
Give the page a read as most this are applicable to this architecture as well.

## Architecture

Overall, the application takes a mix of a layered and 'clean' architecture decisions, with some
domain
driven design.

In a general overview, there are 4 layers:

- data
- domain
- ui(presentation)
- framework

Visually, it looks something like this:
![architecture visual](arch.png "Architecture")

The architecture design is flexible, and it tries to establish patterns to handle things like:

- Separation of Concerns: A local `Dao` is only concerned with handling CRUD operations on its
  related entity. It does not concern itself with that entity's relations to others in the system.
  The layer is also not concerned about how its data is process downstream, or displayed in the UI.
- Encapsulation of Logic: Use Cases are created where they make sense, a piece of complex logic or
  logic that will be used in multiple places. It is hidden behind an interface contract to enforce
  a signature and allow implementation swapping.
- Atomic 'design patterns' for UI development. Jetpack Compose encourages breaking down complex UI
  flows
  and elements into smaller components. Distinguish between different levels of UI components and
  compose them together to build complex screens.

These are of course just examples for the terms.
Ideas behind each layer are explained in more detail below.

### Data Layer

Responsible for handling local and remote data sources. Structures responsible for representing
data in this layer are `Entities` and `Relations`. These are value objects, and their
respective `Daos`
(or in some cases `DataSource`) objects are service objects responsible for mostly CRUD operations
and mapping.

Read more [here](https://publicobject.com/2019/06/10/value-objects-service-objects-and-glue/).

For local data sources(specifically in our case the Room wrapper around an SQLite database), we have
`Entity` classes representing the rows/columns managed by the database. In this case, we don't need
additional mapping in this layer, as the data does not go out to an external source.

Example: `ExtractionEntity`, `ImageEmbeddingsRelation`, `ExtractionDao`.

A note on classes representing relationships. You might have noticed that the database APIs are
slightly
different compared to the standard `javax.persistence` APIs.
In JPA, usually database relationships are modelled directly in the entity classes(Object relational
Mapping).

Whilst this brings conveniences and an (subjectively) easier mental model, there are a few issues
with this approach:

- Object references: The classic circular trap with one-to-many relationships.
  An `Author` has many `Book`s(a list of them), and each `Book` has one `Author`. So, we fetch an
  author and his books,
  and each book has the same author, who has the same list etc etc. In cases where serialization is
  used
  this problem can usually be ignored, but it creates a data model that is not actually
  representative of
  the underlying schema.
- Lazy loading: Read in
  detail [here](https://developer.android.com/training/data-storage/room/referencing-data#understand-no-object-references)

So, for this application we use intermediate data classes to define and model explicitly the
relationships
between objects. Whilst this means we have to do more work to handle our data(there are no cascading
effects)
it also means we are explicit in our code and intentions.

Take a look at `ImageEmbeddingRelation` and above link for more info.

For remote data sources, as mentioned above, we might have `Entity` and `Dto` objects representing
the what the data layer is concerned about versus the data coming in from the external source.
Mappings
may be present here.

There are currently no remote data sources in the application.

In cases where necessary, `Dao`s can be composite -> they can take other `Dao`s as dependencies
to encapsulate service operations on related objects.

> It is recommended that composite DAOs make use of transactions. This can be provided via DI.
> Look at `TransactionProvider`.

Example:

```kotlin
class CompositeDaoImpl(
    private val firstDao: FirstDao,
    private val secondDao: SecondDao
) : CompositeDao {
    suspend fun foo() {
        firstDao.one()
        //...
        secondDao.two()
    }
}
```

### Domain Layer

Most important layer.
Represents the problem domain and its logic. Commonly referred to as the Domain Model and Business
Logic.
This is where most of our application business logic lives, if not all.

There are three main components:

- `model`: Contains domain models. This layer tries to employ some Domain Driven Design aspects in
  that
  by using language constructs we can create models that are self-documenting, self-validating and
  are
  representative of the problem domain.

Using exhaustive matching with restricted inheritance hierarchy constructs in Kotlin we can
effectively create
Sum types and Product types.

Read more [here](https://arrow-kt.io/learn/design/domain-modeling/).

It should be noted that while these concepts are present in this layer, they are not exclusive to
it.
Quite the contrary though, in the presentation layer a common pattern to use to model User Actions
or
events is to use sealed classes (Sum Types).

- `repository`: Generally used to aggregate data sources relevant as sources of domain models and
  perform the mappings. This is used in addition with `usecase` to complete what would generally be
  a
  `service`. This separation exists to separate the actual units of business logic from the concerns
  of
  domain model origin. Repositories here operate(return) domain models.

Furthermore, sometimes for UI consumers of the layer it is necessary to just fetch a model for
display or hand off
some domain model for processing. This is the place to deal with that.
> Most common are cases where a repository method has a `create` or `insert` method for a Domain
> type.
> For these situations we don't create Use Cases as they would be redundant, just forwarding to
> repository calls
> and increasing complexity.

We also make use of product types to encapsulate "payloads" for repository and use case functions.
This allows a sort of an open-closed principle where the interface contract cannot be changed, but
the
actual function input can by extending the payload type.

- `usecase`: Units of business logic. Classes and interface contracts that encapsulate the
  applications
  main logic. Can be hidden behind interfaces if there is a possibility of test doubles or different
  implementations.
  It should be noted that every use case has a single responsibility in the scope of its logic.

In `TextEmbeddingExtractor` as the name implies, the unit of logic is creation of text based
embeddings
from images. The interface defines the contract and there is an actual implementation next to it.

Modeling logic this way allows the use of composition -> Use cases can be composed to create more
complex
flows. For example: `Extractor` makes use of multiple other use cases to orchestrate its logic.

> Note that the naming convention should always make use of a verb to indicate an action,
> ex: `ValidateToken`.

Note however that use cases should not be used in situations where they lead to a data/domain layer
CRUD call. In these situations it is more appropriate to use the Repository package.
Use cases should be used where they make sense -> reusable units of logic.

### Presentation (ui) Layer

Android user interface layer based on Jetpack Compose.
For general Compose
guidelines [go here](https://developer.android.com/jetpack/compose/documentation).

In this part we will be establishing some guidelines and conventions that our code is following to
streamline development of user interfaces.

We will be loosely following principles from Atomic Design.

Read
more [here](https://github.com/thunderbird/thunderbird-android/blob/main/core/ui/compose/designsystem/README.md)
and [here](https://www.wearemobilefirst.com/blog/atomic-design)

From the concepts of Atomic Design, we are concerned with:

- Atoms: Stateless UI components such as custom buttons, text fields or any other custom elements.
  Example: Take a look into the `src/{package}/ui/components/shared/Buttons.kt`
- Molecules: Small collection of atoms that create a more complex component with its related state.
  As far as Composable functions go in this layer, they are stateless, but they are accompanied by a
  state definition and they make use of it. For more details, read [UI State](ComposeState.md).
- Organisms: Collection of molecules that compose into a page. A distinction is made here between an
  Organism and a Screen due to the fact that on larger form factors we might display more Organisms
  per screen.

**NOTE: For now in our application an Organism=Screen, eg: `@Composable fun HomeScreen() {..}` is an
organism and a screen**

An Organisms state is managed by the `androidx.viewmodel`. It composes over the various states of
molecules
and provides orchestration between then while also acting as a **glue** object between the `ui`
and `domain` layers.
This project is strictly Android based and we are not yet concerned with multiplatform capabilities.

For package structure, each screen/feature has a dedicated package with:

- (required) Main entrypoint: `<feature_name>NavTarget` -> Class or object to implement
  the `NavTarget` interface
  and thus marking as a routing destination. Acts as the top-level stateful composable function and
  a glue object
  to bind with view models and navigation providers.
- (required) Top level *stateless* composable: `<feature_name>Screen` -> Actual user interface code.
- `<feature_name>ViewModel` if applicable.
- `<feature_name>UiState` if applicable. **Should be annotated with `@Immutable` or `@Stable`.**
- `<feature_name>Events` sealed interface/class to represent possible UI events. **Should be
  annotated with `@Immutable`**

**ALWAYS** have a `@Preview` in this file - in `<feature_name>Screen` to preview the screen ui.

Components package contains a package per molecule.
Have a look at `ExtractorSearchView` and `Stateful.md`.

### Framework Layer

A bit of an outlier layer in what is generally accepted/considered 'clean' architecture.

The idea is to create configurations, repositories or any other concerns that come from external
libraries such as `koin` etc.

For now the layer encapsulates `koin` for Dependency Injection and the Android `MediaStore` that
interacts with the system image query engine.