# Setting Up App State in Appium

*Tests can be logically divided into actions which serve to **set up state**, **actions** which exercise the functionality in question, and **assertions**.*

<img width="600" src="https://user-images.githubusercontent.com/70295997/224533611-e0b52244-e41f-467f-9330-c38c9774af82.png">

#### Table of Contents
- [Waiting for app state: Theory](https://github.com/lana-20/appium-state-setup#waiting-for-app-state-theory)
- [Application State](https://github.com/lana-20/appium-state-setup#application-state)
- [Shortcut to State #1: Test Nexus](https://github.com/lana-20/appium-state-setup#shortcut-to-state-1-test-nexus)
- [Shortcut to State #2: Deep links](https://github.com/lana-20/appium-state-setup#shortcut-to-state-2-deep-links)
- [Shortcut to State #3: Application backdoors](https://github.com/lana-20/appium-state-setup#shortcut-to-state-3-application-backdoors)

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

## Shortcut to State #1: Test Nexus
*The dead simple "Test Nexus"*

The Test Nexus requires only standard Appium element searches and interactions from the test code perspective. Test Nexus portals are just buttons, which can be interacted with easily using the standard API.

Test Nexus |
---- |
Only in test build of the app |
Screen with one click links to all areas of the app |
Links can even populate data |

![image](https://user-images.githubusercontent.com/70295997/224533266-e6f88c56-8730-4f7f-a3f6-7fac6eea3896.png)

When we write Appium tests, we have to worry about application state on a number of levels. Let's talk about how and why to set up and wait for application state. 

First of all, let's talk about the point of testing. The main point of our test is to make some kind of **assertion** about the way our app behaves. Of course, to be in a place to make an assertion we need to **exercise the app** a bit. Using Appium, we do this by **interacting with the UI**. For example, filling in a text field and tapping a button. What often needs to happen for this to be successful though is for us to go through a *long process of setting up the correct state* in our application to be able to run the bit of functionality that defines our test. For example, if we're testing an e-commerce app our assertion might be something about the quantity of items in our shopping cart. To make the assertion, we need to add an item to our cart. And this is a core part of the test in question. 

But there are other requirements for this test that really have nothing to do with the test. For example, we might need to create a user account and log it in to be able to add an item to the cart. There are a couple frustrating consequences of this requirement of app state being necessary for our test. The first consequence is that we need to be sure *not to run any test interactions before our state is set up*. Otherwise, our assertions will probably fail. This is the case, for example, if we navigate to a certain view and try to perform actions on elements located within that view *before the view is fully loaded*. The solution to this kind of problem is to use **explicit waits**, which ensure we don't interact with the app until it's in a good spot and ready for us. 

Explicit waits don't help the other frustration though which is that setting up **application state through the UI can take a long time**. Imagine that running the test steps themselves and making the assertions only takes several seconds. If however it takes *dozens of seconds or even minutes to get the application into the correct state to interact with it*, then this can be a huge pain. Creating user accounts and logging in might take this long. And of course it's not just *time* that we *waste* when doing this but we also *add extra space for unreliability to creep into our test suite*. The problem doesn't stop there. We usually don't just have one test. If we have lots of tests that all need to go through the same state setup, we can end up wasting lots and lots of time. What we want to do then is figure out a way to *make our state setup really quick* so that it doesn't end up taking an inordinate amount of time in our test. And that's exactly what we'll look at how to achieve.

### Application State
*The woes and wherefoes*

<img width="600" src="https://user-images.githubusercontent.com/70295997/224533485-bf690041-b834-4ac1-99de-58c70f65f838.png">
<img width="600" src="https://user-images.githubusercontent.com/70295997/224533494-6984c02f-0709-4d86-ba6a-873b7ef38083.png">
<img width="600" src="https://user-images.githubusercontent.com/70295997/224533502-cd70c568-f9a9-41b6-ab58-8cc41fd20d53.png">
<img width="600" src="https://user-images.githubusercontent.com/70295997/224533509-1008e176-8675-4e1b-9e98-60bad7a8ef9b.png">

## Shortcut to State #2: Deep links

Deep Links |
---- |
Custom URL scheme registered with mobile OS |
Mobile OS opens your app, passing link content |
Link can be parsed using arbitrary format |
Action taken based on link parsing |

<img width="600" src="https://user-images.githubusercontent.com/70295997/224571371-6f65d27e-3cf9-4c2b-a470-ae8a0c958a63.png">

The second technique I recommend for taking a shortcut to app state is making use of *deep links*. Deep links are essentially URLs with a custom URL scheme that your app has registered with the mobile operating system. These *custom URL schemes* can be registered on both Android and iOS, as part of the application configuration, or manifest file. Imagine that your app has registered one of these custom schemes. Then, when a user tries to navigate to a URL beginning with your scheme, the mobile device will not try and load a web page with that URL, instead your *app will be opened, and it will be given the content of the link*, whatever it is. Now that your app is open, and has this link content, your app can *parse* this *link content* however it wants, just like a web server can parse a URL however it wants to support a REST API that has a certain format. 

So basically, any kind of data can be put into, and then extracted from, a URL in this way. Once that *data is parsed and extracted*, your *app can take any action you define, based on this data*. In our case, the action would be to set up some kind of app state, or navigate somewhere in the app, that would *normally take a long time using the UI*.

<img width="600" src="https://user-images.githubusercontent.com/70295997/224571362-1272ad59-fb27-48c3-af4d-ecc859826bf2.png">

Let's take a look at a simple example. Take a look at this URL. It doesn't start with <code>http</code>, or <code>https</code>, instead it starts with <code>theapp</code>. This is the custom scheme that my app has registered with the mobile OS. Now have look at the rest of the URL. This is what we would call the *path for the URL*. Notice that it starts with <code>login</code>, then has a placeholder for a <code>username</code> and a <code>password</code>, separated by a slash. This is the way that I chose to encode a login action with <code>username</code> and <code>password</code> parameters. Of course, I could have used, <code>query</code> parameters, or any other format instead. As a concrete example, what I would do with this format is construct a URL that looks like the one here, <code>theapp://login/alice/mypassword</code>. When I try to navigate to this URL on my device my app is launched. The app parses this URL for the user login information and then immediately logs the user in. Using this *deep link* means I get to *bypass the entire login process in the UI*, but I can still login as any user that I want, by passing in the user credentials via this deep link URL. 

The only question left is, how do we trigger such deep links in Appium? Assuming our app has registered the custom URL scheme with the OS, all we have to do is call <code>driver.get</code>, with the URL we want.

<img width="600" src="https://user-images.githubusercontent.com/70295997/224576678-30a58f56-6507-44a6-8640-8280d2979657.png">

### Deep Links Pros and Cons

A Deep Limk URL can contain arbitrary content. One benefit of it is that custom APIs can be developed using Deep Link URLs, allowing for parameters to be passed in. Since URLs are just a bunch of text, you can design a full-fledged API including passing in parameters to certain methods.

| Pros | Cons |
| ---- | ---- |
| 1 Appium command | Devs need to register scheme |
| Infinitely extensible | Devs need to build API |
| Cross-platform |  |

Let's look at the pros and cons of the deep link technique. 

#### Pros
1. The first great thing about using deep links is that from the Appium side, all we need to do is call <code>driver.get</code> to trigger the link. It's just one Appium command. 
2. The deep link technique is infinitely extensible, because it's just the implementation of an app-side API. You can get as creative as you want, and build an API with hundreds of actions in it. 
3. Because both Android and iOS support deep links this is a nice cross-platform way to opt out of a lot of state set-up within the UI. 

#### Cons
1. The main drawback here, which is not unique, is that the deep link support needs to be built by developers. 
2. The developers also need to build out the functionality, which is triggered by accessing a deep link. 


### Practical Example
Let's have a quick look at deep links in action. Because we've already looked at the single Appium command necessary for this to work, I'm just going to show how deep links can really simplify and speed up an Appium test. So now I'm looking at the file [Deep_Links.java](https://github.com/lana-20/appium-test-nexus/blob/main/Deep_Links.java). It has all our usual boilerplate up top, just starting an Android session on the emulator. 

    import io.appium.java_client.MobileBy;
    import io.appium.java_client.android.AndroidDriver;
    import java.net.URL;
    import java.util.concurrent.TimeUnit;
    import org.junit.After;
    import org.junit.Before;
    import org.junit.Test;
    import org.openqa.selenium.WebElement;
    import org.openqa.selenium.remote.DesiredCapabilities;
    import org.openqa.selenium.support.ui.ExpectedConditions;
    import org.openqa.selenium.support.ui.WebDriverWait;

    public class Deep_Links {
        private static final String APP_ANDROID = "https://github.com/cloudgrey-io/the-app/releases/download/v1.10.0/TheApp-v1.10.0.apk";
        private static final String APPIUM = "http://localhost:4723";

        private AndroidDriver driver;

        @Before
        public void setUp() throws Exception {
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability("platformName", "Android");
            caps.setCapability("platformVersion", "13");
            caps.setCapability("deviceName", "Android Emulator");
            caps.setCapability("automationName", "UiAutomator2");
            caps.setCapability("app", APP_ANDROID);
            driver = new AndroidDriver(new URL(APPIUM), caps);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        }

        @After
        public void tearDown() {
            if (driver != null) {
                driver.quit();
            }
        }

        @Test
        public void testLoginNormally() {
            WebDriverWait wait = new WebDriverWait(driver, 10);

            WebElement screen = wait.until(ExpectedConditions.presenceOfElementLocated(MobileBy.AccessibilityId("Login Screen")));
            screen.click();

            WebElement username = wait.until(ExpectedConditions.presenceOfElementLocated(MobileBy.AccessibilityId("username")));
            username.sendKeys("alice");

            WebElement password = driver.findElement(MobileBy.AccessibilityId("password"));
            password.sendKeys("mypassword");

            WebElement login = driver.findElement(MobileBy.AccessibilityId("loginBtn"));
            login.click();

            WebElement loginText = wait.until(ExpectedConditions.presenceOfElementLocated(
                MobileBy.xpath("//android.widget.TextView[contains(@text, 'You are logged in')]")));

            assert(loginText.getText().contains("alice"));
        }

        @Test
        public void testLoginWithDeepLink() {
            driver.get("theapp://login/alice/mypassword");

            WebDriverWait wait = new WebDriverWait(driver, 10);
            WebElement loginText = wait.until(ExpectedConditions.presenceOfElementLocated(
                MobileBy.xpath("//android.widget.TextView[contains(@text, 'You are logged in')]")));

            assert(loginText.getText().contains("alice"));
        }
    }

If we scroll down, we have two test cases here. One is called <code>testLoginNormally</code> and the implementation is the same as we have seen elsewhere in this course. It consists of the steps required to login as a certain user within the app, by clicking and typing in the UI. You can see it's about **20 lines of code**. 

The test method below is called <code>testloginWithDeepLink</code>, and it does the exact same thing as the previous test, only it uses a *deep link* to login, rather than using the UI. It makes the same verification, using the assert method at the end, but the test case is only **five or six lines** long. 

You can imagine that if we had many test cases, that all involved logging in at the beginning, having just one command here would save us lots of time, as well as lots of potentially duplicated code. 

Let's go ahead and run both of these tests, one after the other, so we can see for ourselves how much faster it is to use the deep link. So I'll go back up to the top, the test class. And run the entire test class here. We have our Android emulator open and the Appium server, starting sessions on the emulator. Then we're going to login normally using the UI. It's pretty quick, not too slow. And now we're going to try again, using deep links. The app is up and we logged in immediately. I didn't even have time to see what was going on. You can see though that we did log in as the correct user. So that's it. All you need to do for deep links is **build in the deep link API on the app side**, and it has the potential to make your testing life quite a bit easier.

## Shortcut to State #3: Application backdoors

The final strategy we're going to discuss for quickly setting up app state is actually directly calling methods in your application source code from Appium. Since this strategy is not available to users, we call it *backdooring* because you're getting into the app from the backdoor, so to speak. Let's take a look at how it works. 

The basic idea here is that you may have methods inside your app which are purely internal bits of code. Some of these bits of code might do useful things like adding a user or performing another action that could save you time as a tester. Typically, you would not be able to access these internal methods from Appium because Appium can only automate the UI from the outside, just like a user. However, if you use Appium's **Espresso driver**, which is the newest of Appium's Android drivers you'll be working with Google's Espresso technology under the hood. Espresso is designed in such a way that it has access to application internals, which means that Appium via this driver can also have access to methods inside your app. 

Of course, to call any methods inside your app you'll need to know a lot of information about that method. For example, its name, where it's defined, for example, on the application class or the main activity class, and what kinds of arguments it takes. Once you do know all this information, you can call that internal method using a special Appium command called <code>backdoor</code>. Let's take a look at how this command works. 

<img width="500" src="https://user-images.githubusercontent.com/70295997/224580544-ee07f77a-241a-4874-a12a-09909b1c84f3.png">

First of all, let's assume that in my Android app I have a method that looks like this on my main application class. It's called <code>raiseToast</code> and it takes a string as a parameter. What the app does with this string is create something called a toast, which is like a little temporary notice that shows up on the Android screen. Remember, this is code that is in my application itself, not in my test suite. I only know about this code because I can read the source of my application and see that it's there. It might not be in any way directly accessible to users. And by the way, this is what a toast looks like when it's raised on an Android device. 

<img width="350" src="https://user-images.githubusercontent.com/70295997/224580581-ae237274-3270-4669-9d6a-4809065cf662.png">

The argument that should be passed into the invocation of <code>driver.executeScript("mobile: backdoor")</code> is a Map defining a complex object specifying the name and details of the in-app method to call. This Map is turned into a JSON object and interpreted by the Appium server.

So if we call the <code>raiseToast</code> method this is what would happen on the screen. Ultimately, what our Appium Java client needs to do in order to make all of this happen is to encode all the necessary information about the <code>raiseToast</code> method in JSON and send it to the Appium server. So let's have a look at what needs to be sent. 

<img width="600" src="https://user-images.githubusercontent.com/70295997/224580681-deb83866-268f-41b1-be20-8feac7fc0aaa.png">

And this is an example of the JSON object that needs to be sent to Appium in order to trigger the raiseToast method. It describes where the method is hosted, namely on the <code>application</code>. We then have an opportunity to call a whole set of <code>methods</code> but we'll call just one. We have to provide the <code>name</code> which is <code>raiseToast</code> then we have to tell Appium what arguments to call this method with. The <code>args</code> value is therefore an array of other objects, each one of which is described as a Java <code>type</code> and has a <code>value</code>. In this case, we're trying to call the <code>raiseToast</code> method with the message, <code>"Hello from the test script!"</code> 

Thankfully, we don't have to construct this JSON by hand but we do have to create the equivalent structure in Java. So what we really want to call in our test code is something like this.

<img width="600" src="https://user-images.githubusercontent.com/70295997/224580689-bd291020-ec2c-462d-a0f2-49f193b72d16.png">

Here we define a series of lists and maps that encode all of the same information but as a Java object with multiple levels. Then we pass this object to the <code>mobile: backdoor</code> command as the argument to the <code>executeScript</code> call. 

Let's talk about the pros and cons of this approach. 

| Pros | Cons |
| ---- | ---- |
| No extra dev work | Android only |
| Total app access | Espresso only |
| Imstamt |  |

#### Pros
1. One great thing about backdooring is that as long as we can read the source ourselves we might not need to built any extra functionality into the app to support it. If the methods we need already exist we can can just call them. 
2. Secondly, we are not limited to what can be done from the UI. We have access to the app's internals, which gives us a lot of potential power. 
3. And this is also a very fast strategy because we're not going through the UI at all and it's just a single Appium command taking place. 

#### Cons
1. The main frustration here is that this is not a cross-platform solution. We don't have any way to access app internals from an iOS test context and this only works on Android.
2. Likewise this test method is only possible with the Espresso driver. It won't work with the older drivers. All that being said, let's have a quick look at how this works in practice. 

#### Practical Example

Let's take a look at the [App_Backdoor.java](https://github.com/lana-20/appium-test-nexus/blob/main/App_Backdoor.java) file illustrating the backdoor method. 

    import com.google.common.collect.ImmutableMap;
    import io.appium.java_client.android.AndroidDriver;
    import java.net.URL;
    import java.util.Arrays;
    import java.util.concurrent.TimeUnit;
    import org.junit.After;
    import org.junit.Before;
    import org.junit.Test;
    import org.openqa.selenium.remote.DesiredCapabilities;

    public class App_Backdoor {
        private static final String APP_ANDROID = "https://github.com/cloudgrey-io/the-app/releases/download/v1.9.0/TheApp-v1.10.0.apk";
        private static final String APPIUM = "http://localhost:4723";

        private AndroidDriver driver;

        @Before
        public void setUp() throws Exception {
            DesiredCapabilities caps = new DesiredCapabilities();
            caps.setCapability("platformName", "Android");
            caps.setCapability("platformVersion", "13");
            caps.setCapability("deviceName", "Android Emulator");
            caps.setCapability("automationName", "Espresso");
            caps.setCapability("app", APP_ANDROID);
            driver = new AndroidDriver(new URL(APPIUM), caps);
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        }

        @After
        public void tearDown() {
            if (driver != null) {
                driver.quit();
            }
        }

        @Test
        public void testBackdoor() throws InterruptedException {
            ImmutableMap<String, Object> scriptArgs = ImmutableMap.of(
                "target", "application",
                "methods", Arrays.asList(ImmutableMap.of(
                    "name", "raiseToast",
                    "args", Arrays.asList(ImmutableMap.of(
                        "value", "Hello from the test script!",
                        "type", "String"
                    ))
                ))
            );

            driver.executeScript("mobile: backdoor", scriptArgs);
            Thread.sleep(3000);
        }
    }

Most of the file is our usual setup for an Android test. The one important difference for setup is in the desired capabilities section. Look at line 23, where we've changed the automation name capability. Usually it's been <code>UiAutomator2</code> but now we've declared that it should be <code>Espresso</code>. And this tells Appium that we want to use the Espresso driver for this particular test. Remember, we're doing this because the *backdoor command is available only within the Espresso driver*. 

Now let's scroll down to the test method called <code>testBackdoor</code>. You can see that I basically pasted in the construction of our backdoor data using a bunch of maps and arrays declaring that we want to raise a toast with the message, "Hello from the test script." We then pass this data object into the call to the <code>mobile: backdoor</code> command and finally I've added a <code>Thread.sleep</code> here so that the app stays long enough for us to visually notice the toast message popping up. Well, let's give this test script a whirl. I'll load up my Android emulator and the Appium server logs, so we can watch the test proceeding. Session start will take a little bit longer because we're running a new driver, in this case, the Espresso driver. Once it launches, we'll see the "Hello from the test script" message and indeed we did. What this means of course is that we were able to successfully call a method written in the Java source code of our app all the way from our test script and that's pretty fun.

<img width="800" src="https://user-images.githubusercontent.com/70295997/224581354-11b6b220-3ac5-46f5-9c5b-39bc8dadd00d.png">


