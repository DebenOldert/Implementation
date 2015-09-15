/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
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
 * @author Deben
 */
@WebServlet(urlPatterns = {"/SAS"})
public class SAS extends HttpServlet {

    Function function = new Function();
    ErrorCode code = new ErrorCode();
    int ldapError;
    SQL sql;
    ResultSet result;
    final Object T3 = new Object();
    final Object T2 = new Object();
    final Object T1 = new Object();
    String req;
    String reqBody;
    int i;
    int timeout = 60;
    boolean finished = false;

    public SAS() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
        this.sql = (new SQL());
    }
    
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws java.sql.SQLException
     * @throws java.lang.InstantiationException
     * @throws java.lang.IllegalAccessException
     * @throws java.lang.ClassNotFoundException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
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
            response.setContentType("application/json;charset=UTF-8");
            
            JSONObject json = new JSONObject();
            json.put("result", 900);
            json.put("resultText", code.getCodeText(900));
            try (PrintWriter out = response.getWriter()) {
                out.println(json);
            } 
            
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            response.setContentType("application/json;charset=UTF-8");
            
            
            
        try {
            reqBody = function.getBody(request.getReader());
            HashMap json = function.defragJSON(reqBody);
            LDAP ldap = new LDAP((String) json.get("username"), (String) json.get("password"));
            String requestId = (String) json.get("requestId");
            String answer;
            req = requestId;
            
            
            
            
            System.out.println("============= "+req+" ============= << SAS");
        System.out.print(json);
        System.out.println("********************************************************");
            
            switch((String) json.get("function")) {
                case "authenticate":
                    sql.threadUpdate(requestId, "birth", null);
                    System.out.println("##### SAS >> AUTHENTICATE");
                    String tmpBody;
                    JSONObject tmpJSON = new JSONObject();
                        tmpJSON.put("function", "authenticate");
                        tmpJSON.put("requestId", requestId);
                        tmpJSON.put("username", (String) json.get("username"));
                        tmpJSON.put("password", (String) json.get("password"));
                    tmpBody = tmpJSON.toJSONString();

                    HashMap jsonAnswer = function.defragJSON(function.makeRequest("POST", function.getURL("APS"), tmpBody));
                    //System.out.print(jsonAnswer);
                    if(jsonAnswer.get("result").equals("0")) {
                        if(jsonAnswer.containsKey("serviceType")) {
                            System.out.println("SENDING PUSH NOTIFICATION");
                            tmpJSON = new JSONObject();
                                tmpJSON.put("function", "notify");
                                tmpJSON.put("requestId", requestId);
                                tmpJSON.put("serviceType", (String) jsonAnswer.get("serviceType"));
                                tmpJSON.put("serviceNumber", (String) jsonAnswer.get("serviceNumber"));
                                tmpJSON.put("apiKey", (String) jsonAnswer.get("apiKey"));
                                tmpJSON.put("deviceId", (String) jsonAnswer.get("deviceId"));
                            tmpBody = tmpJSON.toJSONString();
                            System.out.println("%%%%%%%%%%%%%%%");
                            System.out.println((String) jsonAnswer.get("serviceType"));
                            //function.defragJSON(function.makeRequest("POST", function.getURL((String) jsonAnswer.get("serviceType")), tmpBody));
                            sql.threadUpdate(requestId, "request", null);
                            for(i = 1; i<=timeout && !finished; i++) {
                                result = sql.stmt.executeQuery("SELECT `state`, `data` FROM `thread` WHERE `threadId`='"+requestId+"'");
                                //System.out.println("GOT RESULT");
                                if(result.first()) {
                                    //System.out.println("GOT VALID RESULT state: "+result.getString("state")+" || data: "+result.getString("data"));
                                    if(result.getString("state").equals("repley")) {
                                        //System.out.println("GOT REPLEY");
                                        
                                        if(result.getString("data") == null || result.getString("data").equals("") || result.getString("data").equals("null")) {
                                            outputResult(response, 601, requestId, null, true);
                                            finished = true;
                                        } else {
                                            outputResult(response, Integer.parseInt((String) jsonAnswer.get("result")), requestId, function.defragJSON(result.getString("data")), true);
                                            finished = true;
                                            break;
                                        }
                                    }
                                    else {
                                        System.out.println("WAITING "+i+" SECONDS");
                                        Thread.sleep(1000);
                                    }
                                }
                                else {
                                    //System.out.println("GOT INVALID RESULT");
                                    outputResult(response, 602, requestId, null, true);
                                    finished = true;
                                    break;
                                }
                                result = null;
                            }
                            System.out.println("WAITED "+i+" SECONDS");
                            if(i > timeout) {
                                outputResult(response, 950, requestId, null, true);
                            }
                            
                        } else {
                            System.out.println("SENDING EMAIL");
                            tmpJSON = new JSONObject();
                                tmpJSON.put("function", "sendmail");
                                tmpJSON.put("requestId", requestId);
                                tmpJSON.put("username", (String) json.get("username"));
                                tmpJSON.put("password", (String) json.get("password"));
                                tmpJSON.put("subject", "Enrollment for TFA");
                                tmpJSON.put("text", "Dear Employee,<br><br>"
                                    + "In order to login to our VPN server you need to download our app to verify your connection request.<br><br>"
                                    + "For Android:<br><ol>"
                                    + "<li>Download and install our app at: "+function.getURL("STORE")+"</li>"
                                    + "<li>Start it: "+function.getURL("APP")+"</li></ol>"
                                    + "We currently don't support iPhone. Sorry for the inconvience.<br><br>"
                                    + "Regards,<br>"
                                    + "The IT Security department");
                            tmpBody = tmpJSON.toJSONString();
                            jsonAnswer = function.defragJSON(function.makeRequest("POST", function.getURL("APS"), tmpBody));
                            if(jsonAnswer.get("result").equals("0")) {
                                    String state;
                                    String data;
                                    
                                    for(i=1; i<=timeout && !finished; i++) {
                                        result = sql.stmt.executeQuery("SELECT state,data FROM thread WHERE threadId='"+requestId+"'");
                                        if(result.first()) {
                                            state = result.getString("state");
                                            data = result.getString("data");
                                            if(state.equals("repley")) {
                                                answer = function.makeRequest("POST", function.getURL("APS"), data);
                                                outputResult(response, 0, requestId, null, true);
                                                finished = true;
                                            } else {
                                                System.out.println("WAITING "+i+" SECONDS");
                                                Thread.sleep(1000);
                                            }
                                            
                                        } else {
                                            outputResult(response, 602, requestId, null, true);
                                            finished = true;
                                        }
                                    }
                                    System.out.println("WAITED "+i+" SECONDS");
                                    if(i > timeout) {
                                        outputResult(response, 950, requestId, null, true);
                                    }
                            } else {
                                outputResult(response, (int) jsonAnswer.get("result"), requestId, null, true);
                                
                            }
                        }
                    } else {
                        outputResult(response, 1, requestId, null, true);
                    }
                    
                    break;
                case "register":
                    System.out.println("##### SAS >> REGISTER");
                    if((ldapError = ldap.userCheck()) == 0) {
                        sql.threadUpdate(requestId, "repley", reqBody);
                        outputResult(response, 0, requestId, null, false);
                        /*result = sql.stmt.executeQuery("SELECT state,data FROM thread WHERE threadId='"+requestId+"'");
                            if(result.first()) {
                                int resultCode = result.getInt("result");
                                outputResult(response, resultCode, requestId, null, false); 
                            }
                            else {
                                outputResult(response, 602, requestId, null, true);
                            }*/
                    } else {
                        outputResult(response, ldapError, requestId, null, true);
                    }
                    break;
                case "confirm":
                    System.out.println("##### SAS >> CONFIRM");
                    //System.out.println("Body: "+reqBody);
                    sql.threadUpdate(requestId, "repley", reqBody);
                    //sql.stmt.execute("UPDATE thread set `state`='repley',`data`="+function.getBody(request.getReader())+" WHERE `threadId`='"+requestId+"'");
                    outputResult(response, 0, requestId, null, false);
                    break;
            }
        
            
            
        } catch (ParseException | NamingException | SQLException | ClassNotFoundException | InterruptedException ex) {
            Logger.getLogger(SAS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    private void outputResult(HttpServletResponse response, int errorCode, String requestId, HashMap extra, boolean terminate) throws IOException, SQLException, ClassNotFoundException {
        JSONObject array = new JSONObject();
        
        array.put("result", errorCode);
        array.put("resultText", code.getCodeText(errorCode));
        array.put("requestId", requestId);
        

        //extra.forEach((k, v) -> System.out.println("key: "+k+" value:"+v));
        if(extra != null) {
            if(extra.containsKey("confirmation")) {
                array.put("confirmation", extra.get("confirmation"));
            } else {
                JSONObject userinfo = new JSONObject();
                extra.forEach((k, v) -> userinfo.put(k, v));
                array.put("userInfo", userinfo);  
            }
        }
        System.out.println("============= "+req+" ============= >> SAS");
        System.out.print(array);
        System.out.println("********************************************************");
        if(terminate) {
            sql.threadUpdate(requestId, "terminate", null);
        }
         try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println(array);
        } 
    }
}
