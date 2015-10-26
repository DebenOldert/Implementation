/*
 * Feel free to copy/use it for your own project.
 * Keep in mind that it took me several days/weeks, beers and asperines to make this.
 * So be nice, and give me some credit, I won't bite and it won't hurt you.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Deben Oldert
 */
@WebServlet(urlPatterns = {"/APS"})
public class APS extends HttpServlet {
    
    ErrorCode code = new ErrorCode();
    Function function = new Function();
    int ErrCode;
    int ldapError;
    String req;
    String reqBody;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
        outputResult(response, 900, null, null);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
        
        try {
            
            reqBody = function.getBody(request.getReader());
            HashMap json = function.defragJSON(reqBody);
            LDAP ldap = new LDAP((String) json.get("username"), (String) json.get("password"));
            String requestId = (String) json.get("requestId");
            req = requestId;
            
            System.out.println("============= "+req+" ============= << APS");
        System.out.print(json);
        System.out.println("********************************************************");
            
            switch((String) json.get("function")) {
                case "authenticate":
                    System.out.println("##### APS >> AUTHENTICATE");
                    if((ldapError = ldap.userCheck()) == 0) {
                        String[] keys = {"serviceType",
                                         "serviceNumber",
                                         "apiKey",
                                         "deviceId",
                                         "mail"};
                        HashMap info = ldap.getUserInfo(keys);
                        if(info.get("serviceType") != null) {
                            info.put("serverURL", function.getURL("SAS"));
                            info.put("appURL", function.getURL("APP"));
                            info.put("storeURL", function.getURL("STORE"));
                            outputResult(response, 0, requestId, info);
                        } else {
                            outputResult(response, 0, requestId, null);
                        }
                    }
                    else {
                        outputResult(response, ldapError, requestId, null);
                    }
                    break;
                case "sendmail":
                    System.out.println("##### APS >> SENDMAIL");
                    if((ldapError = ldap.userCheck()) == 0) {
                        String[] keys = {"mail"};
                        MAIL mail = new MAIL();
                        HashMap info = ldap.getUserInfo(keys);
                        /*if(!info.get("mail").equals(json.get("email"))) {
                            outputResult(response, 51, requestId, null);
                            return;
                        }*/
                        if(!mail.check((String) info.get("mail"))) {
                            outputResult(response, 51, requestId, null);
                            return;
                        }
                        
                        if(mail.send((String) info.get("mail"), (String) json.get("subject"), (String) json.get("text"))) {
                            outputResult(response, 0, requestId, null);
                        }
                        else {
                            outputResult(response, 50, requestId, null);
                        }
                    }
                    else {
                        outputResult(response, ldapError, requestId, null);
                    }
                    break;
                case "register":
                    System.out.println("##### APS >> REGISTER");
                    if((ldapError = ldap.userCheck()) == 0) {
                        String[] keys = {"serviceType",
                                         "serviceNumber",
                                         "notificationId",
                                         "apiKey",
                                         "deviceId"};
                        boolean state;
                        for(String key : keys) {
                            if(!json.containsKey(key)) {
                                outputResult(response, 560, requestId, null);
                                return;
                            }
                            state = ldap.writeInfo(key, (String) json.get(key));
                            if(!state) {
                                outputResult(response, 82, requestId, null);
                                return;
                            }
                        }
                        outputResult(response, 0, requestId, null);
                    }
                    else {
                        outputResult(response, ldapError, requestId, null);
                    }
                    break;
            }
        } catch (ParseException | NamingException ex) {
            Logger.getLogger(APS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
private void outputResult(HttpServletResponse response, int errorCode, String requestId, HashMap extra) throws IOException {
        JSONObject array = new JSONObject();
        
        array.put("result", errorCode);
        array.put("resultText", code.getCodeText(errorCode));
        array.put("requestId", requestId);
        

        //extra.forEach((k, v) -> System.out.println("key: "+k+" value:"+v));
        if(extra != null) {
            JSONObject userinfo = new JSONObject();
            extra.forEach((k, v) -> userinfo.put(k, v));
            array.put("userInfo", userinfo);
        }
        System.out.println("============= "+req+" ============= >> APS");
        System.out.print(array);
        System.out.println("********************************************************");
         try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println(array);
        } 
    }
}
