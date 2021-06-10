package server.utility_methods;

import java.io.File;

public class IsFileValid {
    public static boolean run(String path){
        File f = new File(path);
        if(f.exists() && f.canWrite() && f.canRead()){
            return true;
        }
        else
            return false;
    }
}
