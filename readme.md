# imt3673-lab_2-andregg

## Notes:
* I used a library called [Rome](https://github.com/rometools/rome) to parse the RSS and ATOM feeds provided by the user.
* The fetching is done in a service, however if the app is terminated (Closed shut by the OS or via the 'task manager') the fetching will stop. The fetching works correctly if the app is in the background.
* Feed items are stored in a SQLite database, making it possible to browse already fetched items offline
* My solution to endless scrolling might bring some problems if the SQLite database become very large, see the 'improvements' section for more information.

## Checklist: 

* [ ] The git repository URL is correctly provided, such that command works: git clone <url>

* [ ] The code is well, logically organised and structured into appropriate classes. Everything should be in a single package.

* [X] It is clear to the user what RSS feed formats are supported (RSS2.0 and/or Atom)

* [X] The user can go to Preferences and set the URL of the RSS feed.

* [X] The user can go to Preferences and set the feed item limit.

* [X] The user can go to Preferences and set the feed refresh frequency.

* [X] The user can see the list of items from the feed on the home Activity ListView.

* [X] The user can go to a particular item by clicking on it. The content will be displayed in newly open activity. The back button puts the user back onto the main ListView activity to select another item.

* [X] The user can press the back button from the main activity to quit the app.

* [X] When the content article has graphics, it is rendered correctly.

* [X] The Filter EditText works as expected.

* [ ] The app has JUnit Tests for testing the parsing, and the filtering functionality.

## Improvements:
* Right now the whole dataset is fetched from the SQLite db and stored in memory. An improvement is too only load X amount of items at a time.
* The UI could display pictures if there are any associated with the feed item
