# BI-KOT sem

The project is basically a url shortener in the form of web API.
After registering, users can create shortened versions of a link and then use them instead.

### Build
Everything is built with gradle. The only custom task in the project is fatJar (obviously used to build the fat jar).

### Manual tests
For manual testing and demo, I used IDEA's built-in http client.
All demo requests are stored inside `manual_tests.http`
