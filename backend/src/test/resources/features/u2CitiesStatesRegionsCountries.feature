Feature: U2 Cities, States/Regions, Countries

#  Modify the individual account to use an API to make it easier to enter parts of the home address (e.g. city, region, country). We want to reduce errors due to accidental spelling mistakes. It is up to the team to decide on the parts of the address (e.g. it may not be possible to help the user with street names).
#  The API that will be used for this is the Photon Komoot Geocoder - an open source system built on top of Open Street Map geodata. The source code (and usage documentation) can be found on the project’s GitHub page: https://github.com/komoot/photon. At this stage there is no need to create your own instance of the API - use the publicly hosted instance at https://photon.komoot.io/api/ instead.
#  AC1: As I type into a textbox that could be prefilled using the API, an autocomplete functionality gives me the best options to choose from to save me from typing the rest of the word(s). E.g. typing “Las” might give me Las Vegas, Las Pinas, Las Palmas de Gran Canaria. It might be possible to make the autocomplete cleverer (e.g. by choosing the order in which the address is filled in (e.g. the team might decide on this order country → region → city → street …), but this is for the team to decide. It should not be difficult for the user to enter their address.
#  AC2: The user should be able to override the autocomplete suggestions.
#  AC3: The team should choose when the autocomplete mechanism starts (e.g. after the user enters the third letter). The system should provide the best user experience even for users on lower powered devices with slower internet speeds.

  Scenario Outline: