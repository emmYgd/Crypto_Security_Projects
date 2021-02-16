package EmmaFileCore;
    /**This case handles when file is not in the same host as the program running..**/

    trait ExternalFileLoad {

        //case 1:
        //A file containing enterprise file link outside the program host environment ..
        def collectFromURL = { File textFileContainingEntFileLinks ->
        try {
            //open each text file:
            textFile.eachLine { line ->
                    URL fileURL = (URL) line?.clone()
                if (fileURL instanceof URL) {
                    connectToIt(fileURL)
                }
            }
        } catch (Exception ex) {
            return singleURL()
        }/*finally{

        }*/
    }


        //case 2:
        //if the above fails execute this:
        def singleURL = {URL url_link ->
                //access file location online...
                connectToIt(fileURL)
        }

        def connectToIt = { URL fileURL ->

            URLConnection extFileResource = fileURL.openConnection()

            //get FileInfo:
            String contentType = extFileResource.getContentType()
            Long lastModified = extFileResource.getLastModified()
            Long fileLength = extFileResource.getContentLengthLong()

            //open the Input Stream to get resources:
            FileInputStream input = extFileResource?.getContent()
            def eachFileRep = new BufferedInputStream(input)

            //return
            //contentType
            //lastModified
            //fileLength
            return eachFileRep
    }
}

