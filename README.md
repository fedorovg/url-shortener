# Url shortener

Point of this project was to get acquainted with the bespoke ecosystem of kotlin libraries such as Ktor and Exposed. 

This program allows you to register and generate shorter aliases for arbitrary links via http. Project is implemented as an API with JWT token authentication. 

### Build 
Everything is built with gradle. The only custom task in the project is fatJar (obviously used to build the fat jar).
