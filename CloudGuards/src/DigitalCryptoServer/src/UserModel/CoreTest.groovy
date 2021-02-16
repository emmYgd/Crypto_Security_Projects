package UserModel

class CoreTest {

   /* static void main(String[] args) {
        showMessageDialog(null, "Welcome to the Server Application prototype, Click OK to continue", "SERVER APPLICATION", INFORMATION_MESSAGE)
        showMessageDialog(null, "Please, choose the Server File that you want to protect", "", INFORMATION_MESSAGE)

        //call the File chooser:
        JFileChooser fileToBeEncrypted = new JFileChooser()
        fileToBeEncrypted.setCurrentDirectory(new File(System.getProperty("user.home")))
        fileToBeEncrypted.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES)

        //get selected File and its name:
        final JFrame FRAME_INFO_1 = new JFrame("PICK THE FILE TO BE ENCRYPTED!")
        FRAME_INFO_1.setSize(500, 500)
        FRAME_INFO_1.setVisible(true)
        int result = fileToBeEncrypted.showOpenDialog(FRAME_INFO_1)
        FRAME_INFO_1.setVisible(false)

        if (result == JFileChooser.APPROVE_OPTION) {
            File fileSelected = fileToBeEncrypted.getSelectedFile()
            String myFileName = fileSelected.getName()

            //create the File Location where the file will be stored:
            JFileChooser EncryptedFileLocation = new JFileChooser()
            EncryptedFileLocation.setCurrentDirectory(new File(System.getProperty("user.home")))
            EncryptedFileLocation.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY)

            final JFrame FRAME_INFO_2 = new JFrame("PICK THE LOCATION TO OUTPUT YOUR ENCRYPTED FILE")
            FRAME_INFO_2.setSize(500, 500)
            FRAME_INFO_2.setVisible(true)
            int myResult = EncryptedFileLocation.showOpenDialog(FRAME_INFO_2)
            FRAME_INFO_2.setVisible(false)

            if (myResult == JFileChooser.APPROVE_OPTION) {
                File writeEncryptedLocation = EncryptedFileLocation.getSelectedFile();

                //get the path of the dir:
                String DIRECTORY_PATH = writeEncryptedLocation.getPath()//.getAbsolutePath();
                //the file where the CipherStream writes to:
                String OutputEncFileLocation = DIRECTORY_PATH + "/" + myFileName + ".enc";


            }
        }
    }*/
}
