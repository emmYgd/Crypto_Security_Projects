/*File myDir = new File("C:\Users\user\Videos/")
return myDir*/

file_name = "cool.me"
 String strippedName = file_name.take(file_name.lastIndexOf("."))
 
 strippedExt = file_name - (strippedName + ".")
 return strippedExt