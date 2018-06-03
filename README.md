# News2_android

This is the News App (part 2) for Udacity Android basics challenge, which is my seventh project.

## About the application

This App has a main screen that includes a list of relevant information related to news from the newspaper “The Guardian”. 
We obtain the news from the Guardian API and then, we parse the information to display each item.

**Application structure**

* Each list item has relevant information about the story: *Title*, *Category*, *Date*, *Author* and a *preview of the content* of the news.
* Clicking a story opens the URL of the specific news, using an intent.
* We used the `AsyncTaskLoader` to download the handle the `JSON` file.
* The news list is generated using an ArrayAdapter based on a `JSONObject`.
* Many errors are displayed properly, in case of no connection, corrupt data, etc.
* A `Settings Activity` has been included to allow the user to custimize its experience
  1. The Author information can be hidden.
  2. The published time can also be ommitted.
  2. It is also possible to show and hide every news preview.
  4. Finally, it is possible to select the desired categories that the user wants to display, for example only UK News and World News. If the users do not select any category, by default all the categories will be displayed without any filter.
* To access these settings, a `Navegation Drawer` is included only with the settings options.
