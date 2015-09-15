/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.HashMap;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;


/**
 *
 * @author Deben
 */
public class LDAP {
    private static DirContext ctx;
    private static String userName;
    private static String passWord;

    LDAP(String username, String password) throws NamingException {
        Hashtable<String, String> env = new Hashtable();
        
        env.put(LdapContext.CONTROL_FACTORIES, "com.sun.jndi.ldap.ControlFactory");
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://192.168.2.240:389");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, "VPN\\Administrator");
        env.put(Context.SECURITY_CREDENTIALS, "Magnesium12");
        
        try {
            ctx = new InitialDirContext(env);
        }
        catch(NamingException e) {
            int errCode= getErrorCode(e.getMessage());
            System.out.println("*** Conection with LDAP failed ("+username+", "+password+") Error: "+errCode+" ***");
        }
        userName = username;
        passWord = password;
    }
    
    public int userCheck() throws NamingException {
        Hashtable tmpEnv = (Hashtable) ctx.getEnvironment().clone();
        
        tmpEnv.put(Context.SECURITY_PRINCIPAL, userName);
        tmpEnv.put(Context.SECURITY_CREDENTIALS, passWord);
        
        try {
            new InitialDirContext(tmpEnv);
            return 0;
        }
        catch(NamingException e) {
            return getErrorCode(e.toString()) + 700;
        }
    }
    
    public HashMap getUserInfo(String[] keys) throws NamingException {
        SearchControls cons = new SearchControls();
        cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
        cons.setReturningAttributes(keys);
        
        HashMap<String, String> array = new HashMap();
        NamingEnumeration<SearchResult> answer = ctx.search("dc=vpn,dc=local", "sAMAccountName=" + userName, cons);
        
        if(answer.hasMore()) {
            Attributes attrs = answer.next().getAttributes();
            //System.out.println(attrs.get("serviceType").toString());
            //array.put("serviceType", attrs.get("serviceType").toString());
            
            String tmp;
            for(String key : keys) {
                if(attrs.get(key) != null) {
                    tmp = attrs.get(key).toString();
                    array.put(key, tmp.substring(tmp.indexOf(":") + 2));
                }
                else {
                    array.put(key, null);
                }
            }
            return array;
        }
        return null;
        
    }
    public boolean writeInfo(String key, String value) {
        ModificationItem[] mods = new ModificationItem[1];
        String name = "cn="+userName+",cn=Users,dc=vpn,dc=local";
        
        mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
          new BasicAttribute(key, value));
        try {
            ctx.modifyAttributes(name, mods);
        }
        catch(NamingException e){
            System.out.println(e);
            return false;
        }
        return true;
    }
    
    private int getErrorCode(final String exceptionMsg)
    {
        String pattern="-?\\d+";
        Pattern p=Pattern.compile(pattern);
        Matcher  m=p.matcher(exceptionMsg);
        if (m.find()) {
            return Integer.valueOf(m.group(0));
        }
        return -1;
    }

}
