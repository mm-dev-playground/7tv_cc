# 7tv Coding Challenge Application

## Architecture
- Basic principle is MVVM
- The view gets its data from the according ViewModel which in turn gets fed by
    - either its own DataStream<T, V> class
    - a DataSource<T, V> allowing for paginated access (see Android Pagination Lib) to a REST endpoint
- The ViewModel communicates with the View only via LiveData
- The ViewModel communicates with the data / domain layer only via RX
- Package-wise the reference path is always view -> domain -> repo -> data -> http
- Logic based operations are mostly based on the Try Monad allowing to capture potential failures and forward them to the end of the composed functions building the logic

## Shortcomings / Improvements

### TV Application
- The TV package is just a **placeholder**
- Therefore, all code from the mobile module needs to be moved to a *commons* lib for proper modularization
- However, all the necessary project setup steps are taken to allow for Android TV development

### Testing
- The project has poor test coverage due to time constraints but the architecture allows for fast development (DI framework is suggested!)
- Some examples on how to test them on the JVM are already given
- Mocking final classes and consuming LiveData is already possible with according dependencies

### Performance
- No explicit caching is implemented since both ViewModel, RecyclerViewAdapter and Picasso do all necessary caching. However, the HttpClient could be easily extended, holding a ConcurrentHashMap mapping from queries to results.
- Layouts can be optimized (nested relative / linear layouts)

### UI/UX

- Recovery when a API request in the list fragment crashes is not implemented; the occasion of an error is just presented to the user
- No styles used (e.g. for headers in detail fragment)
- No proper color palette
- Placeholder via Picasso as well as error fallback images should be set
- Animation between list and detail fragment should be implemented for improved UX
- RecyclerView item insert animation would be decent
- Error messages most of the time get swallowed (and only logged with the built in logging framework) - however, the architecture allows to easily add according implementation
- The app icon is more than questionable ...

### Misc

- Naming (including its consistency) can be vastly improved (prefixing everything with *GitHub* is probably not the best idea)
