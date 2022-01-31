# SmartTourismRGB

The App Idea was to get unique and more or less secret Places for Tourists in Regensburg, Germany. 

In the end there is a lot of unfinished construction sites in this code. 

For example: 
- Added Button so you can take a photo and add it to the placemark. No function behind it
- worked with AR Core to let Tourist see their created placemarks in AR Sight. Still problems with the
right angles of the phone. The math is right for placing places relative to the own location. Therefore it only works fine
when you are looking into the right direction.
- Placing of the Marker in AR Sight should be more far away. Aim is that they will spawn when you are in a certain distance. 
- Re creating of Goolge Maps inside the AR Activity. Sould reuse the already existing Map Activity. 
- initally get a list of places in Regensburg. ( only one placemark behind it...)


What does it. Or should do. Or what was the idea.

The idea would be that you can get a prefilled list of interesting places in Regensburg. In the list you can change between 
different tags like "park", "sightseeing" or nice "view" or something like that. Kind of pre-created routes through Regensburg.
Then you can select some of them (single placemarks or whole routes) to see them on the map, so you know where to go. And activate AR Mode to get more information about
the places already created. For example you walk near the cathedral and you start AR Mode you see a marker (better would be a small picture) "on" the building. 
In the Info Window you can see a small explanation with basic information about it and then you can be redirected to the wikipedia page about that place. (or a better site)

Then you have the option to save places for your self as well.

That said, the App can do only few things of that and I should have concentrated on one. 

- Splash Screen
- List from the labs, only added address that gets information from the Google geolocation API
- Show all markers on the map
- Show all existing markers in AR sight in the right direction from yourself
- Information page without information
