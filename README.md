# Add-on for Aerowinx PSX for taxing using OpenStreetMap

This is a Java application that connects to Aerowinx PSX simulator and draws aircraft's position on OpenStreetMap.
It is primarily intended to make taxing around airports possible, because PSX does not have taxiways. Of course, this
is not as good as having a scenery generator, but it's much more lightweight and lighter on the hardware resources (i.e. CPU/GPU).

# Running

Use the following:
```
java -jar psxtaxi.jar
```

Replace psxtaxi.jar with the exact version of the jar file that you've downloaded.

# Configuration

If the directory where you run the application (i.e. where you started that java -jar command above) contains
a file called psxtaxi.properties, it will be read and used for configuring various application parameters.

Here's an example of a psxtaxi.properties file content:
```
showSpeed=false
initialWindowWidth=1024
initialWindowHeight=768
cutoffSpeed=60
mapTileSize=256
hostname=localhost
port=10747
```

Parameter `showSpeed` can be true or false and indicates where aircraft's speed should be displayed.
Parameters `initialWindowWidth` and `initialWindowHeight` specify the size (in pixels) of the window when the application opens. 
Parameter `cutoffSpeed` is the speed above which the application stops rendering the map and aircraft because it assumes that you're not taxing any more.
Parameter `mapTileSize` is specific to Mapsforge. Try values such as 64, 128, 256, and 512 and see what works for you best.
Parameter `hostname` and `port` indicate PSX Main Server network address.