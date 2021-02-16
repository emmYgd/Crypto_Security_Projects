package EmmaFileCore

import javax.swing.*

trait ServerFileLoad{

        def ServerFileChoose = {
            //Using the FileChooser:set mode for multiple selection:
            JFileChooser fileChooser = new JFileChooser()
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES)

            //get result of the selection:
            File[] result
            result = fileChooser?.getSelectedFiles()

            return result
        }

        def choiceResult = {

            final def fileChoices = ServerFileChoose()
            fileChoices.each { File eachFile ->
                switch (eachFile) {
                    case eachFile?.isFile():
                        //call closure that handles file only.
                        return FilesOnly(eachFile)
                    break

                    case eachFile?.isDirectory():
                        //call closure that recurse through a Directory.
                        return DirOnly(eachFile)
                    break
                }
            }
        }

    def FilesOnly = { File candidateFile ->
        def eachFileRep = getInputStream(candidateFile)
        return eachFileRep
    }

    def getInputStream = {File candidateFile ->
        def verifiedFile = checkFile(candidateFile)

        def inStream = new FileInputStream(verifiedFile)
        def eachFileRep = new BufferedInputStream(inStream)
        return eachFileRep
    }.memoize()

    def getFileName = { File candidateFile ->
        def verifiedFile = checkFile(candidateFile)
        String FileName = verifiedFile?.getName()

        return FileName
    }.memoize()

    def checkFile = { File candidateFile ->
        //get file info:
        if (candidateFile?.exists()) {
            return candidateFile
        }else{
            JOptionPane.showMessageDialog(null, "Not a valid File, try again", "Invalid File Warning")
        }
    }.memoize()

    def DirOnly = { File eachDir ->
        def moveThroughDir = getTraverse(eachDir)
        return moveThroughDir
    }

    def getTraverse = { File eachDir ->
        def verifiedDir = VerifyDir(eachDir)
        final def traverse = verifiedDir.traverse(
                type: Object,

                preDir:{
                    excludeNameFilter: ~/.*txt.*/
                })
                {
                    if (it.isFile()) {
                        FilesOnly()
                    }
                    else if (it.isDirectory()) {
                        DirOnly()
                    }
                    else {
                        JOptionPane.showMessageDialog(null, "Sorry but this is not a valid file structure. Not acceptable")
                    }
                }
    }

    def getDirName = { File eachDir ->
        def verifiedDir = VerifyDir(eachDir)
        String DirName = verifiedDir?.getName()
        return DirName
    }

    def VerifyDir = { File eachDir ->
        if(eachDir.exists() && eachDir.isDirectory()){
            return eachDir
        }
    }.memoize()

}




