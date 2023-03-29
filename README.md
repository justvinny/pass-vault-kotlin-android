# Pass Vault
Rewriting [Pass Vault Java Android](https://github.com/justvinny/pass-vault-java-android) to Kotlin, incorporate modern Android features, and use recommended MVVM architecture.

I orginally made this application to store all my passwords using my Android phone as I got sick of getting locked out of important services such as StudyLink due to the increasing need of using unique and complex passwords. 

My main goal is to release an MVP to the playstore once everything in [In-Progress](#in-progress) is completed.

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
- Import / Eport CSV feature.
- Forgot Password

### In-progress
- Adding non-intrusive ads. Big focus on **non-instrusive** as I hate all the annoying ads that plague freemium mobile applications with a passion.
- Migrate all screens to Compose.
- Allow username editing
- Fix Visual Bug - Can't see the entire password for long passwords.
- Fix Bug - Auto-logout feature when application is sent to the background does not work most of the time. 

### Features that I might add in the future (No timeline)
- Allow user to create Groups for their accounts for better organisation.
- More filter options
- Potentially integrate with user's Google Drive.
- Adding animations and gestures.
- Customisable font and font size
- Customisable application colour theme
- Tutorials

## Other Links
[Trello Board](https://trello.com/b/UGu9TQYG/password-manager-kotlin)

# Package Structure
**This is only applicabled to the old XML version of the application. I will be changing the structure during the Compose Migration and will update this section once I finish that.**

- **activities** — Only contains one activity, which is the main activity as we are following the single activity architecture proposed by Google.
- **adapter** — Contains all Adapter classes, primariliy for RecyclerViews.
- **database** — Anything related to Databases live here. As of the moment, only Room SQLite related classes live here such as Entities, DAO, Main Room DB class. Repository pattern is being used to make it easy to plugin different types of databases in the future.
- **di** — Hilt dependency injection modules live here.
- **fragments** — All the different Fragments (Screens) live here along with the popup Dialogs.
- **models** — Non-database related classes live here.
- **utilities** — Object classes that are the equivalent of static classes in Java live here. Also includes constants.
- **viewmodels** — All the architecture component ViewModels live here.
  
# Testing
- **Unit Tests** — Uses JUnit4. Primarily tested ViewModels and Utility classes not related to the UI. These is our most important tests especially once I finish the compose migration as I will be hosting state to the ViewModel for all compose screens. This makes it easier to unit test states related to the screen without tightly coupling them to the UI which relies on the Android API.
- **Instrumentation Tests** — Uses Espresso with Hamcrest. Tested all the UI related classes such as Activities and Fragments. Not as important as the ViewModel unit tests should cover most cases once the migration is over. 
- **Hilt** — Dependency injection framework. In our tests specifically, we use this to swap the Database with a Fake using Room's in-memory database builder as we do not want our production DB to be affected.

# Continuous Integration (CI)
- **GitHub Actions** — Uses GitHubActions to automatically run Unit tests during pull requests on non-main branches and merges to main. Did not include UI testing to the CI as running an Android Emulator takes a significant time which means it'll be expensive. This is undesirable as I am only using the free tier for GitHub actions which hard caps me to 2,000 minutes per month.

# Screenshots
**May not be accurate anymore due to compose migrations. Will update once the migration is finished.**
<img src="https://github.com/justvinny/pass-vault-kotlin-android/blob/main/screenshots/create-login.png" height="600" width="300" />

<img src="https://github.com/justvinny/pass-vault-kotlin-android/blob/main/screenshots/login-screen.png" height="600" />

<img src="https://github.com/justvinny/pass-vault-kotlin-android/blob/main/screenshots/account-list.png" height="600" />

<img src="https://github.com/justvinny/pass-vault-kotlin-android/blob/main/screenshots/individual-account-dialog.png" height="600" />

<img src="https://github.com/justvinny/pass-vault-kotlin-android/blob/main/screenshots/save-account.png" height="600" />

<img src="https://github.com/justvinny/pass-vault-kotlin-android/blob/main/screenshots/password-generator.png" height="600" />

<img src="https://github.com/justvinny/pass-vault-kotlin-android/blob/main/screenshots/overflow-menu.png" height="600" />

<img src="https://github.com/justvinny/pass-vault-kotlin-android/blob/main/screenshots/credits-dialog.png" height="600" />
