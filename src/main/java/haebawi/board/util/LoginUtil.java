package haebawi.board.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class LoginUtil {

    public static boolean isLogin(){
        boolean result = true;
        Object pricipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (pricipal instanceof  String){
            return false;
        }
        return result;
    }
}
