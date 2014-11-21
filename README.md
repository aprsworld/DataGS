# DataGS

## Summary
Gather data from TCP/IP (simple ASCII format) or serial port (WorldData format) and process and make available

## Channel Description File Format
The channel description file is in JSON format. It specifies channel names, descriptions, and other attributes for
a channel.

Example file with one element:
```
{
	"data": [
        {
            "id": "age_inverter",
            "title": "Inverter Age (255 indicates old data)",
            "description": "Inverter Age (255 indicates old data)",
            "units": "none",
            "precision": 2,
            "sortOrder": 240,
            "dayStats": "false",
            "log": "true",
            "historyByDay": "false",
            "recent": "false",
            "mode": "SAMPLE"
        }
	]
}
```

mode can be ```"SAMPLE"``` or ```"AVERAGE"```.

Note that channel id's will be single character letters for data received via TCP/IP / ASCII. Longer channel
names are possible for data that comes in via WorldDataProcessor via reflection.



## Data served via HTTP

### /data/json.html
Simple page with links to the different files. Read from file system.

### /data/history/YYYYMMDD.csv or /data/history/YYYYMMDD.txt 
Logged history file from file system. Return as MIME type `test/csv` if the URI ends with `.csv` or as 
`text/plain` if the URI ends with `.txt`. 
History files are stored in the log local directory which is set with the -w argument

### /data/channels.json
Channel description map as loaded from filesystem. File system location is set with the -c argument.
Returned as MIME type `application/json`.

### /data/now.json
Interval statistics or sample of the last batch of data processed. Data is process at interval
specified by the -i argument.

### /data/recent.json or /data/recent.dat
Time series data covering from now to the number of hours specified by the -H argument.

Returned as `application/json` if URI ends with `.json` or as `text/plain` if the URI ends with `.dat`

### /data/historyFiles.json or /data/historyFiles.dat
Listing of the log files available in the log local directory.

Returned as `application/json` if URI ends with `.json` or as `text/plain` if the URI ends with `.dat`

### /data/historyByDay.json or /data/historyByDay.dat
Daily statistics that summarize the values of all of the files in the log local directory. 
Statistics are generated on all of the columns that have `history` set to true in the channel description map.

Computing the results is done at startup and then continually updated. If the results aren't yet available, 
will return an HTTP response of `NO CONTENT` (HTTP result code 204).

Returned as `application/json` if URI ends with `.json` or as `text/plain` if the URI ends with `.dat`

### /data/dayStats.json or /data/dayStats.dat

Todo: fill in details

### /data/hostinfo.json or /data/hostinfo.dat

Hostname of server as `application/json` if URI ends with `.json` or as `text/plain` if the URI ends with `.dat`



## Command line arguments

### MySQL related arguments
```
options.addOption("d", "database", true, "MySQL database");
options.addOption("h", "host", true, "MySQL host");
options.addOption("p", "password", true, "MySQL password");
options.addOption("u", "user", true, "MySQL username");
```

### SQLite3 related arguments
```
options.addOption("s", "SQLite-URL",true,"SQLite URL (e.g. DataGS.db");
options.addOption("S", "SQLite-proto-URL",true,"SQLite prototype URL (e.g. DataGSProto.db");
```

### DataGSCollector related arguments
```
options.addOption("i", "interval", true, "Interval to summarize over (milliseconds)");
options.addOption("l", "listen-port", true, "DataGSCollector Listening Port");
options.addOption("t", "socket-timeout",true, "DataGSCollector connection socket timeout");
options.addOption("c", "channel-map", true, "Location of channel map JSON file");
options.addOption("a", "process-all-data",false,"Process all data, even if it isn't in channel map");
```

### Serial port data source arguments 
```
options.addOption("r", "serialPortWorldData",true,"Serial Port to listen for worldData packets");
options.addOption("R", "serialPortWorldDataSpeed",true,"Serial port speed");
```

### Data output (JSON) arguments
```
options.addOption("b", "http-document-root", true, "webserver document root directory");
options.addOption("j", "http-port", true, "webserver port, 0 to disable");
options.addOption("H", "json-history-hours", true, "hours of history data to make available, 0 to disable");
```

### Local Logging arguments 
```
options.addOption("w", "loglocal-directory", true, "directory for logging csv files");
```

## Starting the software

### Examples
```
#!/bin/bash
cd /home/aprs/DataGS
# for remote profiling and debugging
java 
	-DSERIAL_PORT_LIST=/dev/ttyAMA0 
	-cp .:jars:bin:jars/commons-cli-1.2.jar:jars/commons-lang3-3.3.2.jar:jars/commons-math3-3.3.jar:jars/gson-2.3.jar:jars/mysql-connector-java-5.1.7-bin.jar:jars/commons-collections4-4.0.jar:jars/jspComm.jar:jars/Serialio.jar:jars/json-lib-2.4-jdk15.jar:jars/commons-io-2.4.jar:jars/commons-csv-1.0.jar 
	dataGS.DataGS -j 8080 -l 4010 -i 10000 -c channelDescriptions/channels_magWebPro.json 
	-r /dev/ttyAMA0 -R 57600 -a -w /data/logLocal
```

### Enable remote profile via JMXREMOTE
``` 
-Djava.rmi.server.hostname=192.168.10.201
-Dcom.sun.management.jmxremote
-Dcom.sun.management.jmxremote.port=9010
-Dcom.sun.management.jmxremote.local.only=false 
-Dcom.sun.management.jmxremote.authenticate=false 
-Dcom.sun.management.jmxremote.ssl=false
```
Replace `192.168.10.201` with the IP of your public interface. Replace `9010` with a unique local port.

Use software such as VisualVM (from Oracle) for monitoring

### Specifying available serial ports under non-Windows operating systems

The serialio.com library we use for accessing serial ports doesn't have support for auto-detecting serial ports
under most operating systems. But we can tell the Java VM what serial ports are available. 

For example:

```
-DSERIAL_PORT_LIST=/dev/ttyUSB0
```

Multiple ports can be seperated with a colon. Example:

```
-DSERIAL_PORT_LIST=/dev/ttyUSB0:/dev/ttyUSB1
```

### Setting the classpath

Java needs to have the current directory, the bin directory, and the name of all the requires JAR files in the `-cp` 
argument. The program itself is then started with `packageName.className`. Or `dataGS.DataGS`.
