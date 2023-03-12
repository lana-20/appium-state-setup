# Setting Up App State in Appium

*Tests can be logically divided into actions which serve to **set up state**, **actions** which exercise the functionality in question, and **assertions**.*

<img width="1665" alt="Screenshot 2023-03-12 at 12 36 23 AM" src="https://user-images.githubusercontent.com/70295997/224533611-e0b52244-e41f-467f-9330-c38c9774af82.png">


#### Waiting for app state: Theory

I'd like to introduce the first technique I recommend for speeding up state setup. It's called the *Test Nexus*. The Test Nexus is basically a special initial application view that only gets loaded during app testing. The Test Nexus should never be shipped to customers, of course. The main idea is that on this one view are lots and lots of buttons. Each button takes you to a different place in the application. So rather than tapping through the app like a regular user, you can just tap one button and you've transported magically to a far-away place in your app that might have taken a long time to get to if you were a regular user. The real value of the Test Nexus becomes clear when you realize that you can have buttons which not only take you somewhere new, but which also trigger various kinds of setup to happen for you automatically, that would normally take a lot of UI interaction. Imagine that the Test Nexus looks something like this. 

<img width="300" src="https://user-images.githubusercontent.com/70295997/224533132-0267e75c-c5b5-44ef-ad2a-9579d2e9c108.png">

You can see a ton of buttons. The buttons don't even have readable names in this case because the point is for our test script to tap them, not for a human being to read them. We're trying to cram as many buttons on the view as we can, so that we can maximize the number of direct portals to different places in the app. 

Basically as app developers, we can attach whatever functionality we'd like to these buttons to make that functionality instantly available to test authors. For example, tapping one might go to the login form. Whereas tapping two might go to the logged in user area with a user already logged in, totally bypassing the login form. 

We can create the possibility for the equivalent of variables here too. Tapping the A button might pop up a prompt for a username, and when entered, create a new user with that username but with all other details in their default value. Meaning we can create a custom new user without having to go through all the various signup forms. The possibilities are endless and limited only by your imagination and what the developers can build into the app at the tap of a button.

#### Test Nexus Pros and Cons

| Pros | Cons |
| ---- | ---- |
| Standard Appium API | Need a test only app |
| Easy to add buttons | Devs need to build functions |
| Quickest solution to build |  |



**Pros**:
1. One great thing is that from a test author perspective we're just dealing with tapping buttons using the standard Appium API, which makes using the Test Nexus dead simple for the automation. 
2. The Test Nexus is also really easy to expand. You just keep making the Nexus view longer, adding more buttons. 
3. In my opinion, Test Nexus approach is the quick and dirty way to cut out lots of time setting up app state. Just tap a button and you're there. 

**Cons**:
1. One drawback of this approach is that we'll need a test-only version of the app. You don't want to ship the Test Nexus to users. 
2. The other main drawback, which is shared with all the approaches in this chapter, is that the Test Nexus functionality needs to be built by developers. Without the source code and ability to modify it, test authors can't implement this on their own. 

The test app captioned below actually implements a version of the Test Nexus. All the links directly lead to different parts of the app. These happen to be human readable, which is intentional but is basically the same idea as what we've been considering here.

<img width=300 src="https://user-images.githubusercontent.com/70295997/224534139-e8508938-d8c5-44a7-b60f-84da8123ec48.png">

## Shortcut to State #1
*The dead simple "Test Nexus"*

Test Nexus |
---- |
Only in test build of the app |
Screen with one click links to all areas of the app |
Links can even populate data |

![image](https://user-images.githubusercontent.com/70295997/224533266-e6f88c56-8730-4f7f-a3f6-7fac6eea3896.png)

When we write Appium tests, we have to worry about application state on a number of levels. Let's talk about how and why to set up and wait for application state. First of all, let's talk about the point of testing. The main point of our test is to make some kind of assertion about the way our app behaves. Of course, to be in a place to make an assertion we need to exercise the app a bit. Using Appium, we do this by interacting with the UI. For example, filling in a text field and tapping a button. What often needs to happen for this to be successful though is for us to go through a long process of setting up the correct state in our application to be able to run the bit of functionality that defines our test. For example, if we're testing an e-commerce app our assertion might be something about the quantity of items in our shopping cart. To make the assertion, we need to add an item to our cart. And this is a core part of the test in question. But there are other requirements for this test that really have nothing to do with the test. For example, we might need to create a user account and log it in to be able to add an item to the cart. There are a couple frustrating consequences of this requirement of app state being necessary for our test. The first consequence is that we need to be sure not to run any test interactions before our state is set up. Otherwise, our assertions will probably fail. This is the case, for example, if we navigate to a certain view and try to perform actions on elements located within that view before the view is fully loaded. The solution to this kind of problem is to use explicit weights, which ensure we don't interact with the app until it's in a good spot and ready for us. Explicit weights don't help the other frustration though which is that setting up application state through the UI can take a long time. Imagine that running the test steps themselves and making the assertions only takes several seconds. If however it takes dozens of seconds or even minutes to get the application into the correct state to interact with it, then this can be a huge pain. Creating user accounts and logging in might take this long. And of course it's not just time that we waste when doing this but we also add extra space for unreliability to creep into our test suite. The problem doesn't stop there. We usually don't just have one test. If we have lots of tests that all need to go through the same state setup, we can end up wasting lots and lots of time. What we want to do then is figure out a way to make our state setup really quick so that it doesn't end up taking an inordinate amount of time in our test. 

### Application State
*The woes and wherefoes*

<img width="800" src="https://user-images.githubusercontent.com/70295997/224533485-bf690041-b834-4ac1-99de-58c70f65f838.png">
<img width="800" src="https://user-images.githubusercontent.com/70295997/224533494-6984c02f-0709-4d86-ba6a-873b7ef38083.png">
<img width="800" src="https://user-images.githubusercontent.com/70295997/224533502-cd70c568-f9a9-41b6-ab58-8cc41fd20d53.png">
<img width="800" src="https://user-images.githubusercontent.com/70295997/224533509-1008e176-8675-4e1b-9e98-60bad7a8ef9b.png">

## State shortcut 2: Deep links

The second technique I recommend for taking a shortcut to app state is making use of deep links. Deep links are essentially URLs with a custom URL scheme that your app has registered with the mobile operating system. These custom URL schemes can be registered on both Android and iOS, as part of the application configuration, or manifest file. Imagine that your app has registered one of these custom schemes. Then, when a user tries to navigate to a URL beginning with your scheme, the mobile device will not try and load a web page with that URL, instead your app will be opened, and it will be given the content of the link, whatever it is. Now that your app is open, and has this link content, your app can parse this link content however it wants, just like a web server can parse a URL however it wants to support a REST API that has a certain format. So basically, any kind of data can be put into, and then extracted from, a URL in this way. Once that data is parsed and extracted, your app can take any action you define, based on this data. In our case, the action would be to set up some kind of app state, or navigate somewhere in the app, that would normally take a long time using the UI. Let's take a look at a simple example. Take a look at this URL. It doesn't start with http, or https, instead it starts with theapp. This is the custom scheme that my app has registered with the mobile OS. Now have look at the rest of the URL. This is what we would call the path for the URL. Notice that it starts with login, then has a placeholder for a username and a password, separated by a slash. This is the way that I chose to encode a login action with username and password parameters. Of course, I could have used, query parameters, or any other format instead. As a concrete example, what I would do with this format is construct a URL that looks like the one here, theapp://login/alice/my password. When I try to navigate to this URL on my device my app is launched. The app parses this URL for the user login information and then immediately logs the user in. Using this deep link means I get to bypass the entire login process in the UI, but I can still login as any user that I want, by passing in the user credentials via this deep link URL. The only question left is, how do we trigger such deep links in Appium? Assuming our app has registered the custom URL scheme with the OS, all we have to do is call driver.get, with the URL we want. Let's look at the pros and cons of the deep link technique. The first great thing about using deep links is that from the Appium side, all we need to do is call driver.get to trigger the link. It's just one Appium command. The deep link technique is infinitely extensible, because it's just the implementation of an app-side API. You can get as crazy as you want, and build an API with hundreds of actions in it if you want. Because both Android and iOS support deep links this is a nice cross-platform way to opt out of a lot of state set-up within the UI. The main drawback here, which is not unique, is that the deep link support needs to be built by developers. The developers also need to build out the functionality, which is triggered by accessing a deep link. Okay, let's have a quick look at deep links in action. Because we've already looked at the single Appium command necessary for this to work, I'm just going to show how deep links can really simplify and speed up an Appium test. So now I'm looking at the file Ch_04_03_Deep_Links.java. It has all our usual boilerplate up top, just starting an Android session on the emulator. If we scroll down, we have two test cases here. One is called testLoginNormally and the implementation is the same as we have seen elsewhere in this course. It consists of the steps required to login as a certain user within the app, by clicking and typing in the UI. You can see it's about 20 lines of code. The test method below is called testloginWithDeepLink, and it does the exact same thing as the previous test, only it uses a deep link to login, rather than using the UI. It makes the same verification, using the assert method at the end, but the test case is only five or six lines long. You can imagine that if we had many test cases, that all involved logging in at the beginning, having just one command here would save us lots of time, as well as lots of potentially duplicated code. Let's go ahead and run both of these tests, one after the other, so we can see for ourselves how much faster it is to use the deep link. So I'll go back up to the top, the test class. And run the entire test class here. We have our Android emulator open and the Appium server, starting sessions on the emulator. So now we're going to login normally using the UI. It's pretty quick. Not too slow. And now we're going to try again, using deep links. The app is up and we logged in immediately. I didn't even have time to say what was going on. You can see though that we did log in as the correct user. So that's it. All you need to do for deep links is build in the deep link API on the app side, and it has the potential to make your testing life quite a bit easier.



