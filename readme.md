Here are the java classes I wrote for the mediaplayer. I will post a zip file to Basecamp with all of the external jar files that it needs as well. The java code is essentially finished, the only thing I really had left do do was clean it up a bit and remove the leftover methods that are still there from when we transfered from using a local webserver to salesforce for a backend.
The MediaPlayer class is the one you will want to run to start the media player. The DisplayImage and CustomFrame classes are what control the Jframes that make the images show up on the screen. The Config and Configsettings classes are what handle the player's dynamic configuration. Logging handles writing to the log file. Base64ToPng is what converts the Base64 strings sent by salesforce into images. Lastly, the ConnectToSalesForce class is what handles all of the SOAP calls to the salesforce backend. Also, it has a main method that will make a test connection to salesforce and attempt to get the schedule.
