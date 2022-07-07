# Pass Vault
Rewriting [Pass Vault Java Android](https://github.com/justvinny/pass-vault-java-android) to Kotlin, incorporate modern Android features, and use recommended MVVM architecture.

I orginally made this application to store all my passwords using my Android phone as I got sick of getting locked out of important services such as StudyLink due to the increasing need of using unique and complex passwords. 

My main goal is to release an MVP to the playstore soon.

## Features
### Finished
- Viewing account passwords.
- Search your saved accounts using username or platform as a search criteria
- Copying usernames and passwords to the clipboard.
- Password generator that creates randomised and complex passwords.
- Saving passwords for different accounts.
- Login using a passcode to protect your passwords from people snooping on your phone. If a user is inactive for more than 10 minutes, you will be automatically logged out.
- All data is stored offline using a SQLite database.
- Databases are encrypted for security using [SQLCipher.](https://github.com/sqlcipher/android-database-sqlcipher)

### In-progress
- Import / Eport CSV feature.
- Forgot Password
- Adding non-intrusive ads. Big focus on **non-instrusive** as I hate all the annoying ads that plague freemium mobile applications with a passion.

### Features that I might add in the future (No timeline)
- Customisable font and font size
- Customisable application colour theme
- Tutorials
- More filter options
- Potentially integrate with user's Google Drive.
- Adding animations and gestures.

## Other Links
[Trello Board](https://trello.com/b/UGu9TQYG/password-manager-kotlin)

# Package Structure
- **activities** — Only contains one activity, which is the main activity as we are following the single activity architecture proposed by Google.
- **adapter** — Contains all Adapter classes, primariliy for RecyclerViews.
- **database** — Anything related to Databases live here. As of the moment, only Room SQLite related classes live here such as Entities, DAO, Main Room DB class. Repository pattern is being used to make it easy to plugin different types of databases in the future.
- **di** — Hilt dependency injection modules live here.
- **fragments** — All the different Fragments (Screens) live here along with the popup Dialogs.
- **models** — Non-database related classes live here.
- **utilities** — Object classes that are the equivalent of static classes in Java live here. Also includes constants.
- **viewmodels** — All the architecture component ViewModels live here.
  
# Testing
- **Unit Tests** — Uses JUnit4. Primarily tested ViewModels and Utility classes not related to the UI.
- **Instrumentation Tests** — Uses Espresso with Hamcrest. Tested all the UI related classes such as Activities and Fragments.
- **Hilt** — Used to swap the Database with a Fake using Room's in-memory database builder.

# Continuous Integration (CI)
- **GitHub Actions** — Uses GitHubActions to automatically run Unit and Instrumentation (API 26) tests during pull requests on non-main branches and merges to main.

# Screenshots


