package EmmaFileCore

/**This represents the state of the file inside the system*/
/**This case handles when file is in the same environment as the program running..**/

trait ServerFileRep implements SandBoxFileTemplate {

    //create the file name for the new file:
    def FileAndDirRep = { String InitialFileAndDirNameWithExt ->

        def finalFileAndDir = super.createEmmaFiles(InitialFileAndDirNameWithExt)

    }

    //possibly convert this into interface:
    def EmmaFileCreateTemplates = { File dir_file, String newFileName ->

        FileTreeBuilder dir = new FileTreeBuilder((File) dir_file)

        dir {
            if (dir_file.isFile()) {
                def newF = "${newFileName}"()
                return newF
            }else if(dir_file.isDirectory()){
                dir.${dir_file.getName()}{
                    def newF = "${newFileName}"()
                    return newF
                }
            }
        }
    }
}




        /*protected OutputStream changeToBinRep(String fileName) throws IOException {

            URI newFileURL

            def myNewStr = null
            def fileRepBin = null
            try {
                newFileURL = new URI("/" + fileName + ".emma")

                myNewStr = Files.newOutputStream(Paths.get(newFileURL))
                fileRepBin = new ObjectOutputStream(myNewStr)

            } finally {
                myNewStr.close()
                fileRepBin.close()
            }

            return fileRepBin;
        }*/



