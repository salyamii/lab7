package utility_methods;

import java.io.File;

public class IsFileValid {
    public static boolean run(String path){
        File f = new File(path);
        if(f.exists() && f.canWrite() && f.canRead() &&
                (getFileExtension(path).equals("xml") | getFileExtension(path).equals("txt"))){
            return true;
        }
        else
            return false;
    }

    private static String getFileExtension(String path) {
        File file = new File(path);
        String fileName = file.getName();
        // если в имени файла есть точка и она не является первым символом в названии файла
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            // то вырезаем все знаки после последней точки в названии файла, то есть ХХХХХ.txt -> txt
            return fileName.substring(fileName.lastIndexOf(".")+1);
            // в противном случае возвращаем заглушку, то есть расширение не найдено
        else return "";
    }
}
