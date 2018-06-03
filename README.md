# News2_android

This is the News App (part 2) for Udacity Android basics challenge, which is my sixth project.

## About the application

This App has a main screen. It includes 1 lists of relevant with the information that comes from the newspaper “The Guardian”. 
We obtain the news from the Guardian API and then, we parse the information to display each item.

**The activities structure is the following:**

* Each list item has relevant information about the story. For instance: Title, Name of section, Author and Date (if this information it is available).
* Clicking in a story uses an intent to open the new (URL) in the user’s browser.
* We used the AsyncTaskLoader  to download the handle the JSON file.
* The new list is generated using an ArrayAdapter(JSONObject).
* The app shows a message when there is not connection informing the user about it .
