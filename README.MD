# SaferME API CLIENT LIBRARY
The SaferMe API Client Library can be used to communicate with the SaferMe API.

## Users Guide / Getting Started

### Adding to your project
Currently only Android Studio is supported.

Add the github maven repository to your build gradle:

	repositories {
	 	maven {
	         name = "GitHubPackages"
	         url = uri("https://maven.pkg.github.com/SaferMe/saferme-api-client-android")
	         credentials {
	             username = GITHUB_USERNAME
	             password = GITHUB_PASSWORD or ACCESS TOKEN
	         }
	     }
	 }

Add the most release release to your dependencies:

	implementation("me.safer.apilib:saferme-kotlin-release:VERSION")

### Create an instance of the client
Client Implementation can be injected using Dagger2 Library

	lateinit var client: SaferMeClient
	client = SaferMeClientService.getService().getClient()

### Making a request
Requests typically require a RequestParameters object with your credentials and, success and failure callbacks. Some requests also require a resource Object.

#### Create Task example
First, create a Task object that we wish to save on the server:

	val task = Task(
		uuid = UUID.randomUUID().toString(),
		assignee_id = ASSIGNEE,
		client_created_at = Date(),
		completed_at = null,
		completed_by_id = null,
		creator_id = CREATOR,
		title = "Task Test",
		report_id = REPORT_ID,
		description = "This is a test task"
	)
	
Create a RequestParameters Object with your credentials

	params = client.defaultParams().copy(
		credentials = SaferMeCredentials(
				API_KEY, INSTALL_ID, APP_ID, TEAM_ID)
	)

Call the create method on the Task Resource supplying the approproiate success and failure functions - note this must be run asynchronously - we recommend using Kotlin Coroutines for this.

        CoroutineScope(Dispatchers.IO).launch {

            client.tasks.create(
                params,
                task,
                { result ->
                    println("It Worked!"
                },
                { println("It Failed!") })
        }

### Next stepsh
Look at the interfaces that extend SaferMeResource to see all the resources that have been implemented. most resources will implement the standard methods index, create, read, update and delete.

Some Resources may have custom methods available

## Developers Guide
If you wish to develop this project you will need to get a good environment setup. This guide assumes you will be using Android Studio 3.5.x and has only been tested on OSX Mojave.

Other projects are dependent on this one, and the variable names in buildSrc/src/main/kotlin/Dependencies.kt are used by other projects - be VERY careful if editing this file, as it may cause other projects to break.

### Installation
* Clone the Repo: `git clone git@github.com:SaferMe/saferme-api-client-android.git`

* Open the project in Android studio - it should resolve all the required dependencies.

* It is recommended that you have Maven Installed: `brew install maven`

* If you are using Maven to import a build into another project you will need to make a copy of buildSrc/local.properties.example to buildSrc/local.properties

* You do not need to provide github credentials unless you plan on publishing artifacts to github.

* Test your setup by running a build from the commandline with `./gradlew clean build test assemble` and also try to build the `app` module from within android studio.

### Development
We use Gitflow and Test Driven development.

1. Checkout new feature branch
2. Write failing unit tests in the tests folder
3. Write code
4. Run tests and linter
5. Commit and push to Github
6. Raise a PR if you want your code merged into the master branch

### Code Style
This project is using the ktlint gradle plugin (see: https://github.com/jlleitschuh/ktlint-gradle)

To auto correct/format all kotling files run: 
`./gradlew ktlintFormat`

To check for formatting errors run
`./gradlew ktlintCheck`

There is also Android Studio IDE integration - use the menu option Analyze->Inspect Code to run the linter and Analyze->Code Cleanup to autofix issues.

Be sure to run the linter over your code BEFORE making a pull request as the CI will reject your PR if it fails.

## Testing & Coverage
All code should be covered by unit tests. you can run your tests within Android Studio by right clicking on a test file and choosing 'Run xxxxTest'. Currently Android Studio can not run more than one test file at a time.

To run the whole suite use the command line: `$ ./gradlew test`

To run the whole suite and generate a coverage report: `$ ./gradlew test jacocoTestReport`

Report will be output to `app/build/reports`

## Building & Publishing
Update version.txt in the base directory to specify the version NAME.
update builsSrc.local.properties value for 'version' to have the version BUILD_NUMBER
The published package will be NAME.BUILD_NUMBER
If you have changed any of the values in local.properties it is recommend you run the following with the --rerun-tasks flag, as gradle may not detect these changes and will use previously generated artifacts


##### Build AAR	
	$ ./gradlew build
Artifact will be generated in `app/build/output/aar`

##### Publish artifact to local Maven repository
	$ ./gradlew publishToMavenLocal

##### Publish (test) artifact to GitHub
	$ ./gradlew publishTestReleasePublicationToGitHubRepository
##### Publish (production) artifact to GitHub
	$ ./gradlew publishProductionReleasePublicationToGitHubRepository

## Licence & Contributing
We welcome the community to contribute to this project - If you notice any problems please raise them in Github. 

If you see an issue you can fix, feel free to create a pull request.

Email support@safer.me if you need any help

This library is covered by the standard MIT Licence:

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:
	
	The above copyright notice and this permission notice shall be included in all
	copies or substantial portions of the Software.
	
	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
	SOFTWARE.
