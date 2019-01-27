# 7tv Coding Challenge Application

## Architecture
- Basic principle is MVVM
- The view gets its data from the according ViewModel which in turn gets fed by
    - either its own DataStream<T, V> class
    - a DataSource<T, V> allowing for paginated access (see Android Pagination Lib) to a REST endpoint
- The ViewModel communicates with the View only via LiveData
- The ViewModel communicates with the data / domain layer only via RX
- Logic based operations are mostly based on the Try Monad allowing to capture potential failures and forward them to the end of the composed functions building the logic

## Shortcomings / Improvements

### TV Application
- The TV package is just a **placeholder**
- Therefore, all code from the mobile module needs to be moved to a *commons* lib for proper modularization
- However, all the necessary project setup steps are taken to allow for Android TV development

### Testing
- The project has poor test coverage due to time constraints
- However, all classes (except from the view package) are built in a composable way; some examples on how to test them on the JVM are given.
- Adding a DI framework (preferrably Koin) is a good idea to leverage testing comfort
- Mocking final classes is already possible

### Performance
- No explicit caching is implemented since both ViewModel, RecyclerViewAdapter and Picasso do all necessary caching. However, the HttpClient could be easily extended, holding a ConcurrentHashMap mapping from queries to results.

### UI/UX

- Recovery when a API request in the list fragment crashes is not implemented; it just informs the user
- No styles used (e.g. for headers in detail fragment)
- No proper color palette
- Layouts can be optimized (nested relative / linear layouts)
- Placeholder via Picasso as well as error fallback images should be set
- Animation between list and detail fragment should be implemented for improved UX
- RecyclerView item insert animation would be decent
- Error messages most of the time get swallowed (and only logged with the built in logging framework) - however, the architecture allows to easily add according implementation

### Misc

- Naming (including its consistency) can be vastly improved (prefixig everything with *GitHub* is probably not the best idea)
