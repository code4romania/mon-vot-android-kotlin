# Monitorizare Vot - Android Kotlin 

[![GitHub contributors](https://img.shields.io/github/contributors/code4romania/mon-vot-android-kotlin.svg?style=for-the-badge)](https://github.com/code4romania/mon-vot-android-kotlin/graphs/contributors) [![GitHub last commit](https://img.shields.io/github/last-commit/code4romania/mon-vot-android-kotlin.svg?style=for-the-badge)](https://github.com/code4romania/mon-vot-android-kotlin/commits/master) [![License: MPL 2.0](https://img.shields.io/badge/license-MPL%202.0-brightgreen.svg?style=for-the-badge)](https://opensource.org/licenses/MPL-2.0)

[See the project live](https://votemonitor.org/)

Monitorizare Vot is a mobile app for monitoring elections by authorized observers. They can use the app in order to offer a real-time snapshot on what is going on at polling stations and they can report on any noticeable irregularities. 

The NGO-s with authorized observers for monitoring elections have real time access to the data the observers are transmitting therefore they can report on how voting is evolving and they can quickly signal to the authorities where issues need to be solved. 

Moreover, where it is allowed, observers can also photograph and film specific situations and send the images to the NGO they belong to. 

The app also has a web version, available for every citizen who wants to report on election irregularities. Monitorizare Vot was launched in 2016 and it has been used for the Romanian parliamentary elections so far, but it is available for further use, regardless of the type of elections or voting process. 

[Contributing](#contributing) | [Built with](#built-with) | [Repos and projects](#repos-and-projects) | [Feedback](#feedback) | [License](#license) | [About Code4Ro](#about-code4ro)

## Contributing

This project is built by amazing volunteers and you can be one of them! Here's a list of ways in [which you can contribute to this project](.github/CONTRIBUTING.MD).

__IMPORTANT:__ Please follow the Code4Romania [WORKFLOW](.github/WORKFLOW.MD)

## Built With

* Android Studio 3.6
* Android SDK 29
* Kotlin
* RxJava, Retrofit2
* [Koin](https://insert-koin.io/)
* [Room](https://developer.android.com/reference/android/arch/persistence/room/RoomDatabase) database
* Firebase crashlytics, analytics & push notifications

Uses [Fastlane](https://fastlane.tools/) for automating builds & releases.

Uses the MVVM architectural pattern.

Relies on Firebase's RemoteConfig for remote settings.

The app is localized, meaning it's easier for any interested party to fork the project and use it in other countries, simply localizing the messages. Please see the [steps for app localization](https://github.com/code4romania/mon-vot-android-kotlin/wiki/Steps-for-app-localisation) in the wiki.

More info on redeploying and reusing the app can be found in the wiki: [Redeploy steps](https://github.com/code4romania/mon-vot-android-kotlin/wiki/Steps-for-redeploying---reusing-the-app) & [Google Play Deploy Steps](https://github.com/code4romania/mon-vot-android-kotlin/wiki/Google-Play-Deploy-Steps)

Swagger docs for the API are available [here](https://app-vmon-api-dev.azurewebsites.net/swagger/index.html).

## Repos and projects

![alt text](https://raw.githubusercontent.com/code4romania/mon-vot-android-kotlin/develop/vote_monitor_diagram.png)

- repo for the API - https://github.com/code4romania/monitorizare-vot
- repo for the iOS app - https://github.com/code4romania/monitorizare-vot-ios

Other related projects:
- https://github.com/code4romania/monitorizare-vot-ong

## Feedback

* Request a new feature on GitHub.
* Vote for popular feature requests.
* File a bug in GitHub Issues.
* Email us with other feedback contact@code4.ro

## License

This project is licensed under the MPL 2.0 License - see the [LICENSE](LICENSE) file for details

## About Code4Ro

Started in 2016, Code for Romania is a civic tech NGO, official member of the Code for All network. We have a community of over 500 volunteers (developers, ux/ui, communications, data scientists, graphic designers, devops, it security and more) who work pro-bono for developing digital solutions to solve social problems. #techforsocialgood. If you want to learn more details about our projects [visit our site](https://www.code4.ro/en/) or if you want to talk to one of our staff members, please e-mail us at contact@code4.ro.

Last, but not least, we rely on donations to ensure the infrastructure, logistics and management of our community that is widely spread across 11 timezones, coding for social change to make Romania and the world a better place. If you want to support us, [you can do it here](https://code4.ro/en/donate/).
