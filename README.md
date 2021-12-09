# Add-on for Aerowinx PSX for taxing using OpenStreetMap

![workflow](https://github.com/jgracin/psxtaxi/actions/workflows/main.yaml/badge.svg)

This is a Java application that connects to the main server of [Aerowinx PSX](https://aerowinx.com) simulator 
and continuously shows aircraft's position on a map, using [OpenStreetMap](https://www.openstreetmap.org/) data and
[Mapsforge framework](https://github.com/mapsforge/mapsforge).

It is primarily intended to enable taxing around airports since PSX does not have taxiways. Of course, 
this is not as good as having a scenery generator, but it's much more lightweight and lighter 
on the hardware resources (i.e. CPU/GPU).

# Running

Download the latest release `.jar` file from Github (from [the Releases section](https://github.com/jgracin/psxtaxi/releases), e.g. `psxtaxi-1.0.1-b5.jar`).

In order to run it, you need Java version 8 or later.

Run the application:
```
java -jar psxtaxi-1.0.1-b5.jar
```

Replace psxtaxi-1.0.1-b5.jar with the exact version of the jar file that you've downloaded.

# Using

Here's a screenshot of taxing. The red circle indicates aircraft's position, the red line extending from the position 
circle  indicates current heading, while black tip indicates tiller input. The text next to the position circle
indicates speed. For technical reasons, it's IAS, not ground speed.

<img alt="snapshot" src="https://github.com/jgracin/psxtaxi/blob/master/docs/screenshot.jpg" width="800">

Known bug: Don't use Cmd+Q to quit the application on MacOS because that event is not properly handled by 
the application and might cause the connection to the main server to remain open.

# Configuration

If the directory where you run the application (i.e. where you started that java -jar command above) contains
a file called `psxtaxi.properties`, it will be read and used for configuring various application parameters.

Here's an example of a `psxtaxi.properties` file content:
```
showSpeed=true
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
Parameter `mapTileSize` is specific to Mapsforge. Try values such as 64, 128, 256, and 512 and see what works best for you.
Parameter `hostname` and `port` indicate PSX Main Server network address.


# License

Copyright © 2021 Josip Gracin

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
